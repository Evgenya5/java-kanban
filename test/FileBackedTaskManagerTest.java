import data.Epic;
import data.Subtask;
import data.Task;
import logic.FileBackedTaskManager;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    TaskManager taskManager;
    Epic epic;
    Subtask subtask;
    Task task;
    int taskId;
    int subtaskId;
    int epicId;
    File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("autotest.csv");
        taskManager = new FileBackedTaskManager(file.getName());
        epic = new Epic("Test", "Test description");
        epicId = taskManager.createTask(epic);
        subtask = new Subtask("Test", "Test description", epicId);
        subtaskId = taskManager.createTask(subtask);
        task = new Task("Test", "Test description");
        taskId = taskManager.createTask(task);
    }

    @Test
    void loadTasksFromFile() {
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager2.getTaskList(), taskManager.getTaskList(), "Список задач не совпадает.");
        assertEquals(taskManager2.getSubtaskList(), taskManager.getSubtaskList(), "Список подзадач не совпадает.");
        assertEquals(taskManager2.getEpicList(), taskManager.getEpicList(), "Список эпиков не совпадает.");
    }
}