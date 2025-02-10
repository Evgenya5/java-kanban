package logic;

import data.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    protected int idCount = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int createTask(Task task) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return taskId;
    }

    @Override
    public int createTask(Subtask subtask) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        subtask.setId(taskId);
        subtasks.put(taskId, subtask);
        if (subtask.getEpicId() >= 0) {
            if (epicExist(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).addSubtask(taskId);
                changeEpicStatus(epics.get(subtask.getEpicId()));
            }
        }
        return taskId;
    }

    @Override
    public int createTask(Epic epic) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        epic.setId(taskId);
        epics.put(taskId, epic);
        return taskId;
    }

    @Override
    public ArrayList<Task> getTaskList() { //Получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() { //Получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskListByEpic(int epicId) { //Получение списка всех подзадач по эпику
        ArrayList<Subtask> subtaskByEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return subtaskByEpic;
        }
        for (int subtask:epic.getSubtasks()) {
            subtaskByEpic.add(subtasks.get(subtask));
        }
        return subtaskByEpic;
    }

    @Override
    public ArrayList<Epic> getEpicList() { //Получение списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() { //Удаление всех задач
        for (Task task: tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() { //Удаление всех подзадач
        for (Subtask subtask: subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) { //очищаем сабтаски в эпике
            epic.getSubtasks().clear();
            changeEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() { //Удаление всех эпиков
        for (Subtask subtask: subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Epic epic: epics.values()) {
            historyManager.remove(epic.getId());
        }
        subtasks.clear();
        epics.clear();

    }

    @Override
    public Task getTaskById(int id) { //Получение задачи по идентификатору.
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) { //Получение подзадачи по идентификатору.
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) { //Получение эпика по идентификатору.
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateSubtask(Subtask subtask) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Subtask savedSubtask = subtasks.get(subtask.getId());

        if (savedSubtask == null) {
            return;
        }
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        savedSubtask.setStatus(subtask.getStatus());
        if (epicExist(savedSubtask.getEpicId())) {
            changeEpicStatus(epics.get(savedSubtask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Task task) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (taskExist(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTaskById(Integer id) { //Удаление по идентификатору.
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) { //Удаление по идентификатору.
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        historyManager.remove(id);
        if (epicExist(epicId)) {
            epics.get(epicId).deleteSubtask(id);
            changeEpicStatus(epics.get(epicId));
        }
    }

    @Override
    public void deleteEpicById(Integer id) { //Удаление по идентификатору.
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        historyManager.remove(id);
        for (int subtaskId : epic.getSubtasks()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return idCount++;
    }

    private boolean subtaskExist(int id) {
        return subtasks.containsKey(id);
    }

    private boolean taskExist(int id) {
        return tasks.containsKey(id);
    }

    private boolean epicExist(int id) {
        return epics.containsKey(id);
    }

    private Epic changeEpicStatus(Epic epic) {

        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            boolean allDone = true;
            boolean allNew = true;
            for (Integer subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (!subtask.getStatus().equals(TaskStatus.DONE)) {
                    allDone = false;
                }
                if (!subtask.getStatus().equals(TaskStatus.NEW)) {
                    allNew = false;
                }
            }
            if (allDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (allNew) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
        return epic;
    }
}
