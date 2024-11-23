import logic.TaskManager;
import  data.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Subtask subtask1 = new Subtask("sub1", "sub1desc", -1);
        Subtask subtask2 = new Subtask("sub2", "sub2desc", -1);
        Subtask subtask3 = new Subtask("sub3", "sub3desc", -1);
        Subtask subtask4 = new Subtask("sub4", "sub4desc", -1);
        Subtask subtask5 = new Subtask("sub5", "sub5desc", -1);
        Task task1 = new Task("task1", "task1desc");
        Task task2 = new Task("task2", "task2desc");
        Task task3 = new Task("task3", "task3desc");
        ArrayList<Integer> subtasks1 = new ArrayList<>();
        Epic epic1 = new Epic("epic1", "epic1desc");
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(subtask3);
        subtasks1.add(subtask1.getId());
        subtasks1.add(subtask3.getId());
        epic1.setSubtasks(subtasks1);
        taskManager.createTask(epic1);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println("Вывели все");
        System.out.println(taskManager.getAllTaskList());
        subtask5.setEpicId(3);
        subtask5.setStatus(TaskStatus.DONE);
        subtask5.setId(2);
        subtask4.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask5);
        subtask5.setEpicId(3);
        subtask4.setId(0);
        taskManager.updateSubtask(subtask4);
        //taskManager.deleteSubtaskById(2);
        System.out.println("Вывели все");
        System.out.println(taskManager.getAllTaskList());

        Epic epic2  = new Epic("epic1update", "epic1descupdate");
        System.out.println("1 epic id 3 = " + taskManager.getEpicById(3));
        ArrayList<Integer> subtasks = new ArrayList<>();
        subtasks.add(0);
        subtasks.add(1);
        epic2.setSubtasks(subtasks);
        epic2.setId(3);
        taskManager.updateEpic(epic2);
        System.out.println("Вывели все ");
        System.out.println(taskManager.getAllTaskList());
        taskManager.deleteTaskById(task1.getId());
        System.out.println("Удалили таск 1");
        System.out.println(taskManager.getTaskList());
        taskManager.deleteAllTasks();
        System.out.println("Удалили все таски");
        System.out.println(taskManager.getTaskList());
        taskManager.deleteTaskById(task3.getId());
        System.out.println(taskManager.getTaskList());
    }
}
