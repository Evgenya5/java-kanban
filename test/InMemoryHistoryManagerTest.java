import data.Task;
import logic.HistoryManager;
import logic.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    private static final int HISTORY_MAX_SIZE = 10;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("task1", "task2");
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
    void checkMaxSizeHistory() { //теперь проверяем что история не имеет макс ограничения
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        for (int i = 1; i <= HISTORY_MAX_SIZE; i++) {
            task = new Task("task"+i, "task"+i);
            task.setId(i);
            historyManager.add(task);
        }
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(HISTORY_MAX_SIZE, history.size(), "История не равна старому ограничению размера");
        task = new Task("task", "task");
        task.setId(HISTORY_MAX_SIZE+1);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertTrue(HISTORY_MAX_SIZE < history.size(), "История не превысило старое ограничение размера");
    }

    @Test
    void checkAddOneTaskTwice() { //теперь проверяем что история не имеет макс ограничения
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "История не равна 1");
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "История не равна 1");
    }

    @Test
    void checkDeleteTask() { //теперь проверяем что история не имеет макс ограничения
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "История не равна 1");
        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void checkgetTaskListOrder() { //теперь проверяем что история не имеет макс ограничения
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        int firstId = 1;
        task.setId(firstId);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "История не равна 1");
        task = new Task("t2", "t2");
        int secondId = 2;
        task.setId(secondId);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(2, history.size(), "История не равна 2");
        assertEquals(firstId, history.getFirst().getId(), "Id first task != firstId");
        assertEquals(secondId, history.getLast().getId(), "Id last task != secondId");
    }
}