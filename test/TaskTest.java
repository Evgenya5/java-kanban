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

    @Test
    public void taskWithSameIdShouldBeEquals() {
        int taskId = 1;
        Task task1 = new Task("task1", "task1");
        task1.setId(taskId);
        Task task2 = new Task("task2", "task2");
        task2.setId(taskId);
        assertEquals(task1, task2, "Задачи не равны");
    }
}