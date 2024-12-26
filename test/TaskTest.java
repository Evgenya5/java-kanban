import data.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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