import logic.InMemoryTaskManager;
import logic.Managers;
import  data.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager)Managers.getDefault();
        Task task1 = new Task("task1", "task1desc");
        Task task2 = new Task("task2", "task2desc");
        Task task3 = new Task("task3", "task3desc");
        ArrayList<Integer> subtasks1 = new ArrayList<>();
        Epic epic1 = new Epic("epic1", "epic1desc");
        int epic1Id = inMemoryTaskManager.createTask(epic1);
        Epic epic2 = new Epic("epic2", "epic2desc");
        int epic2Id = inMemoryTaskManager.createTask(epic2);
        Subtask subtask1 = new Subtask("sub1", "sub1desc", epic1Id);
        Subtask subtask2 = new Subtask("sub2", "sub2desc", epic1Id);
        Subtask subtask3 = new Subtask("sub3", "sub3desc", epic1Id);
        Subtask subtask4 = new Subtask("sub4", "sub4desc", epic2Id);
        Subtask subtask5 = new Subtask("sub5", "sub5desc", epic2Id);
        inMemoryTaskManager.createTask(subtask1);
        inMemoryTaskManager.createTask(subtask2);
        inMemoryTaskManager.createTask(subtask3);
        inMemoryTaskManager.createTask(subtask4);
        inMemoryTaskManager.createTask(subtask5);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        System.out.println("Вывели все subtask by epic1");
        System.out.println(inMemoryTaskManager.getSubtaskListByEpic(epic1Id));
        System.out.println("Вывели все subtask by epic2");
        System.out.println(inMemoryTaskManager.getSubtaskListByEpic(epic2Id));
        System.out.println("Вывели все subtask by 100");
        System.out.println(inMemoryTaskManager.getSubtaskListByEpic(100));
        System.out.println("Вывели все");
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println(inMemoryTaskManager.getTaskList());
        subtask5.setStatus(TaskStatus.DONE);
        subtask4.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask5);
        inMemoryTaskManager.updateSubtask(subtask4);
        //taskManager.deleteEpicById(1);
        System.out.println("Вывели все");
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        inMemoryTaskManager.deleteSubtaskById(2);
        System.out.println("Вывели все");
        System.out.println(inMemoryTaskManager.getEpicList());
        inMemoryTaskManager.deleteSubtaskById(4);
        //inMemoryTaskManager.deleteAllSubtasks();
        System.out.println("Вывели все");
        System.out.println(inMemoryTaskManager.getEpicList());

        System.out.println(inMemoryTaskManager.getHistory().size());
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getEpicById(0);
        inMemoryTaskManager.getSubtaskById(6);
        inMemoryTaskManager.getTaskById(8);
        inMemoryTaskManager.getEpicById(1);
        inMemoryTaskManager.getSubtaskById(2);
        inMemoryTaskManager.getTaskById(7);
        inMemoryTaskManager.getEpicById(1);
        inMemoryTaskManager.getSubtaskById(6);
        inMemoryTaskManager.getTaskById(8);
        inMemoryTaskManager.getEpicById(0);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getSubtaskById(6);
        inMemoryTaskManager.getTaskById(7);

        System.out.println(inMemoryTaskManager.getHistory().size());
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getEpicById(100);
        inMemoryTaskManager.getSubtaskById(60);
        inMemoryTaskManager.getTaskById(80);
        System.out.println(inMemoryTaskManager.getHistory().size());

        System.out.println(inMemoryTaskManager.getHistory());

    }
}
