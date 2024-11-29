import data.Subtask;
import data.Task;
import logic.InMemoryTaskManager;
import logic.Managers;
import logic.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void addNewTask() { //Можем создавать и получать задачу
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = inMemoryTaskManager.createTask(task);

        final Task savedTask = inMemoryTaskManager.getTaskById(taskId);
        final Task taskById = inMemoryTaskManager.getTaskById(taskId);
        assertNotNull(taskById, "Задача не возвращается по ИД.");
        assertEquals(taskId, taskById.getId(), "Неверное ID задачи.");
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = inMemoryTaskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void taskWithSameIdShouldBeEquals() {
        int taskId = 1;
        Task task1 = new Task("task1", "task1");
        task1.setId(taskId);
        Task task2 = new Task("task2", "task2");
        task2.setId(taskId);
        assertEquals(task1, task2, "Задачи не равны");
    }

    @Test
    public void TaskIdGenerateAutomatically() {
        Task task1 = new Task("t1", "t1");
        int taskId1 = inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("t2", "t2");
        task2.setId(taskId1);
        int taskId2 = inMemoryTaskManager.createTask(task2);
        assertNotEquals(task2.getId(), task1.getId(), "ID равны");
    }

    @Test
    public void TaskNotChange() {
        Task task1 = new Task("t1", "t1");
        int taskId1 = inMemoryTaskManager.createTask(task1);
        Task taskById = inMemoryTaskManager.getTaskById(taskId1);
        assertEquals(task1.getName(), taskById.getName(), "Name не равны");
        assertEquals(task1.getDescription(), taskById.getDescription(), "Description не равны");
    }
}