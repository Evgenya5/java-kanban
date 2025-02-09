import logic.FileBackedTaskManager;
import logic.Managers;
import  data.*;
import logic.TaskManager;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {

        System.out.println("Поехали!");

        final TaskManager taskManager = new FileBackedTaskManager("test.csv");
        //File file = new File("test.csv");
        //final TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        Task task1 = new Task("task1", "task1desc");
        Task task2 = new Task("task2", "task2desc");
        Task task3 = new Task("task3", "task3desc");
        ArrayList<Integer> subtasks1 = new ArrayList<>();
        Epic epic1 = new Epic("epic1", "epic1desc");
        int epic1Id = taskManager.createTask(epic1);
        Epic epic2 = new Epic("epic2", "epic2desc");
        int epic2Id = taskManager.createTask(epic2);
        Subtask subtask1 = new Subtask("sub1", "sub1desc", epic1Id);
        Subtask subtask2 = new Subtask("sub2", "sub2desc", epic1Id);
        Subtask subtask3 = new Subtask("sub3", "sub3desc", epic1Id);
        Subtask subtask4 = new Subtask("sub4", "sub4desc", epic2Id);
        Subtask subtask5 = new Subtask("sub5", "sub5desc", epic2Id);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(subtask3);
        taskManager.createTask(subtask4);
        taskManager.createTask(subtask5);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println("Вывели все subtask by epic1");
        System.out.println(taskManager.getSubtaskListByEpic(epic1Id));
        System.out.println("Вывели все subtask by epic2");
        System.out.println(taskManager.getSubtaskListByEpic(epic2Id));
        System.out.println("Вывели все subtask by 100");
        System.out.println(taskManager.getSubtaskListByEpic(100));
        System.out.println("Вывели все");
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getTaskList());
        subtask5.setStatus(TaskStatus.DONE);
        subtask4.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask5);
        taskManager.updateSubtask(subtask4);
        //taskManager.deleteEpicById(1);
        System.out.println("Вывели все");
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        //taskManager.deleteSubtaskById(2);
        System.out.println("Вывели все");
        System.out.println(taskManager.getEpicList());
        taskManager.deleteSubtaskById(4);
        //inMemoryTaskManager.deleteAllSubtasks();
        System.out.println("Вывели все with histor");
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getHistory().size());
        System.out.println(taskManager.getHistory());
        System.out.println("start get by id");
        taskManager.getEpicById(0);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(8);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getTaskById(7);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(8);
        taskManager.getEpicById(0);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(7);
        System.out.println(taskManager.getHistory().size());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(100);
        taskManager.getSubtaskById(60);
        taskManager.getTaskById(80);
        System.out.println(taskManager.getHistory().size());
        System.out.println(taskManager.getHistory());
        //taskManager.deleteAllTasks();

    }
}
