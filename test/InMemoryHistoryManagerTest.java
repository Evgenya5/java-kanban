import data.Task;
import logic.HistoryManager;
import logic.Managers;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    private static final int historyMaxSize = 10;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();
        task = new Task("task1", "task2");
        task.setId(taskManager.createTask(task));
    }

    @Test
    void add() {
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void checkEmptyHistory() {
        final ArrayList<Task> emptyArray = new ArrayList<>();
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(history, emptyArray, "История не пустая.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void checkMaxSizeHistory() {
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        for (int i = 1; i <= historyMaxSize; i++) {
            historyManager.add(task);
        }
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(historyMaxSize, history.size(), "История не содержит максимальное кол-во элементов");
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(historyMaxSize, history.size(), "История не содержит максимальное кол-во элементов");
    }
}