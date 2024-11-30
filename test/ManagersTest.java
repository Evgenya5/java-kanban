import data.Epic;
import data.Subtask;
import data.Task;
import logic.HistoryManager;
import logic.Managers;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    Epic epic;
    Subtask subtask;
    Task task;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("Test", "Test description");
        subtask = new Subtask("Test", "Test description", 0);
        task = new Task("Test", "Test description");
    }

    @Test
    void createTaskManagerDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager не создается.");
        int epicId = taskManager.createTask(epic);
        subtask.setEpicId(epicId);
        taskManager.createTask(subtask);
        taskManager.createTask(task);
        assertNotNull(taskManager.getEpicList(), "taskManager не работает.");
        assertEquals(1, taskManager.getEpicList().size(), "Неверное количество эпиков.");
        assertNotNull(taskManager.getSubtaskList(), "taskManager не работает.");
        assertEquals(1, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertNotNull(taskManager.getTaskList(), "taskManager не работает.");
        assertEquals(1, taskManager.getTaskList().size(), "Неверное количество задач.");
    }

    @Test
    void createHistoryManagerDefault() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "historyManager не создается.");
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertNotNull(historyManager.getHistory(), "historyManager не работает.");
        assertEquals(3, historyManager.getHistory().size(), "Неверное количество истории.");
    }
}