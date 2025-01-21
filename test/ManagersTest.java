import logic.HistoryManager;
import logic.Managers;
import logic.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void createTaskManagerDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager не создается.");
    }

    @Test
    void createHistoryManagerDefault() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "historyManager не создается.");
    }
}