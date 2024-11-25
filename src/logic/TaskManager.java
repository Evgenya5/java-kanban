package logic;

import data.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCount = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int generateId() {
        return idCount++;
    }

    public int createTask(Task task) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return taskId;
    }

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

    public int createTask(Epic epic) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        epic.setId(taskId);
        epics.put(taskId, epic);
        return taskId;
    }

    public ArrayList<Task> getTaskList() { //Получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtaskList() { //Получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

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

    public ArrayList<Epic> getEpicList() { //Получение списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() { //Удаление всех задач
        tasks.clear();
    }

    public void deleteAllSubtasks() { //Удаление всех подзадач
        subtasks.clear();
        for (Epic epic : epics.values()) { //очищаем сабтаски в эпике
            epic.getSubtasks().clear();
            changeEpicStatus(epic);
        }
    }

    public void deleteAllEpics() { //Удаление всех эпиков
        subtasks.clear();
        epics.clear();
    }

    public Task getTaskById(int id) { //Получение задачи по идентификатору.
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) { //Получение подзадачи по идентификатору.
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) { //Получение эпика по идентификатору.
        return epics.get(id);
    }

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

    public void updateTask(Task task) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (taskExist(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public void deleteTaskById(Integer id) { //Удаление по идентификатору.
        tasks.remove(id);
    }

    public void deleteSubtaskById(Integer id) { //Удаление по идентификатору.
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        if (epicExist(epicId)) {
            epics.get(epicId).deleteSubtask(id);
            changeEpicStatus(epics.get(epicId));
        }
    }

    public void deleteEpicById(Integer id) { //Удаление по идентификатору.
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (int subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
        }
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
