import logic.FileBackedTaskManager;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    FileBackedTaskManager taskManager;
    File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("autotest.csv");
        taskManager = new FileBackedTaskManager(file.getName());
        initTasks(taskManager);
    }

    @Test
    void loadTasksFromFile() {
        taskManager.createTask(subtaskWithDate1);
        taskManager.createTask(taskWithDate);
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager2.getTaskList(), taskManager.getTaskList(), "Список задач не совпадает.");
        assertEquals(taskManager2.getSubtaskList(), taskManager.getSubtaskList(), "Список подзадач не совпадает.");
        assertEquals(taskManager2.getEpicList(), taskManager.getEpicList(), "Список эпиков не совпадает.");
        assertNotNull(taskManager2.getPrioritizedTasks(),"Список PrioritizedTasks пуст");
        assertEquals(taskManager2.getPrioritizedTasks(), taskManager.getPrioritizedTasks(), "Список PrioritizedTasks не совпадает.");
    }
}