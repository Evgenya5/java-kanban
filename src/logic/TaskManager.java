package logic;

import data.Epic;
import data.Subtask;
import data.Task;

import java.util.ArrayList;

public interface TaskManager {
    private int generateId() {
        return 0;
    }

    int createTask(Task task);

    int createTask(Subtask subtask);

    int createTask(Epic epic);

    ArrayList<Task> getTaskList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Subtask> getSubtaskListByEpic(int epicId);

    ArrayList<Epic> getEpicList();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteEpicById(Integer id);

    ArrayList<Task> getHistory();
}
