import data.Task;
import logic.HistoryManager;
import logic.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    Task task1;
    Task task2;
    Task task3;
    private static final int HISTORY_MAX_SIZE = 10;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("task", "task");
        task.setId(0);
        task1 = new Task("t1", "t1");
        task1.setId(1);
        task2 = new Task("t2", "t2");
        task2.setId(2);
        task3 = new Task("t3", "t3");
        task3.setId(3);
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
    void checkAddOneTaskTwice() { //проверяем что одна и та же задача не добавляется дважды
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
    void checkDeleteFirstTask() { //проверяем удаление из истории
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1, task2,task3), history, "История не верна");
        historyManager.remove(task1.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task2,task3), history, "История не верна");
    }

    @Test
    void checkDeleteMiddleTask() { //проверяем удаление из истории
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1, task2,task3), history, "История не верна");
        historyManager.remove(task2.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1,task3), history, "История не верна");
    }

    @Test
    void checkDeleteLastTask() { //проверяем удаление из истории
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1, task2,task3), history, "История не верна");
        historyManager.remove(task3.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1,task2), history, "История не верна");
    }

    @Test
    void checkGetTaskListOrder() { //проверяем порядок элементов в истории
        ArrayList<Task> history;

        history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
        historyManager.add(task1);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1), history, "История не верна");
        historyManager.add(task2);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task1, task2), history, "История не верна");
        historyManager.add(task1);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(List.of(task2, task1), history, "История не верна");
    }
}