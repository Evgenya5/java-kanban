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
        if (subtask.getEpicId() >= 0) {
            if (epicExist(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).addSubtask(taskId);
            }
        }
        subtasks.put(taskId, subtask);
        return taskId;
    }

    public int createTask(Epic epic) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        epic.setId(taskId);
        Epic copyEpic = epic;
        for (Integer index:epic.getSubtasks()){
            if (subtasks.containsKey(index)) {
                subtasks.get(index).setEpicId(taskId);
            } else {
                copyEpic.deleteSubtask(index);
            }
        }
        changeEpicStatus(copyEpic);
        epics.put(taskId, copyEpic);
        return taskId;
    }

    public ArrayList<String> getAllTaskList() { //Получение списка всех задач (включая эпики и сабтаски)
        ArrayList<String> taskList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskList.add(task.toString());
        }
        for (Epic epic : epics.values()) {
            taskList.add(epic.toString());
        }
        for (Subtask subtask : subtasks.values()) {
            taskList.add(subtask.toString());
        }
        return taskList;
    }

    public ArrayList<String> getTaskList() { //Получение списка всех задач
        ArrayList<String> taskList = new ArrayList<>();

        for (Task task : tasks.values()) {
            taskList.add(task.toString());
        }
        return taskList;
    }

    public ArrayList<String> getSubtaskList() { //Получение списка всех подзадач
        ArrayList<String> taskList = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            taskList.add(subtask.toString());
        }
        return taskList;
    }

    public ArrayList<String> getEpicList() { //Получение списка всех эпиков
        ArrayList<String> taskList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            taskList.add(epic.toString());
        }
        return taskList;
    }

    public void deleteAllTasks() { //Удаление всех задач
        tasks.clear();
    }

    public void deleteAllSubtasks() { //Удаление всех подзадач
        subtasks.clear();
        for (Epic epic: epics.values()) { //очищаем сабтаски в эпике
            ArrayList<Integer> newSubtasks = new ArrayList<>();
            epic.setSubtasks(newSubtasks);
        }
    }

    public void deleteAllEpics() { //Удаление всех эпиков
        subtasks.clear();
        epics.clear();
    }

    public Task getTaskById(int id) { //Получение задачи по идентификатору.
        if (taskExist(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Subtask getSubtaskById(int id) { //Получение подзадачи по идентификатору.
        if (subtaskExist(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public Epic getEpicById(int id) { //Получение эпика по идентификатору.
        if (epicExist(id)) {
            return epics.get(id);
        }
        return null;
    }

    public Subtask updateSubtask(Subtask subtask) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (subtaskExist(subtask.getId())) {
            Integer oldEpicId = subtasks.get(subtask.getId()).getEpicId();

            if (oldEpicId != subtask.getEpicId()) {
                if (epicExist(oldEpicId)) {
                    Epic oldEpic = epics.get(oldEpicId);
                    oldEpic.deleteSubtask(subtask.getId());
                    updateEpic(oldEpic);
                }
            }
            if (epicExist(subtask.getEpicId())) {
                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtask(subtask.getId()); //добавляем сабтаску к эпику, если она была - не добавиться, если не было то добавим
                updateEpic(epic); //обновляем эпик
            } else {
                subtask.setEpicId(-1);
            }
            subtasks.put(subtask.getId(), subtask);
            return subtasks.get(subtask.getId());
        }
        return null;
    }

    public Task updateTask(Task task) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (taskExist(task.getId())) {
            tasks.put(task.getId(), task);
            return tasks.get(task.getId());
        }
        return null;
    }

    public Epic updateEpic(Epic epic) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (epicExist(epic.getId())) {
            Epic newEpic = epic;
            ArrayList<Integer> newSubtasks = new ArrayList<>();
            for (Integer index : epic.getSubtasks()) {
                if (subtaskExist(index)) {
                    subtasks.get(index).setEpicId(epic.getId());
                    newSubtasks.add(index);
                }
            }
            for (Integer subtaskId : epics.get(epic.getId()).getSubtasks()) { //Проверяем были ли убраны какие то сабтаски из эпика
                if (!newSubtasks.contains(subtaskId)) {
                    if (subtaskExist(subtaskId)) {
                        subtasks.get(subtaskId).setEpicId(-1);
                    }
                }
            }
            newEpic.setSubtasks(newSubtasks);
            changeEpicStatus(newEpic);
            epics.put(epic.getId(), newEpic);
            return epics.get(epic.getId());
        }
        return null;
    }

    public boolean deleteTaskById(Integer id) { //Удаление по идентификатору.
        if (taskExist(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean deleteSubtaskById(Integer id) { //Удаление по идентификатору.
        if (subtaskExist(id)) {
            if (epicExist(subtasks.get(id).getEpicId())) {
                epics.get(subtasks.get(id).getEpicId()).deleteSubtask(id);
            }
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean deleteEpicById(Integer id) { //Удаление по идентификатору.
        if (epicExist(id)) {
            for (int subtaskId : epics.get(id).getSubtasks()) {
                if (subtaskExist(subtaskId)) {
                    subtasks.remove(subtaskId);
                }
            }
            epics.remove(id);
            return true;
        }
        return false;
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
