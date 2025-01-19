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
    void checkDeleteTask() { //проверяем удаление из истории
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
        task = new Task("t1", "t2");
        task.setId(1);
        historyManager.add(task);
        task = new Task("t2", "t2");
        task.setId(2);
        historyManager.add(task);
        task = new Task("t3", "t2");
        task.setId(3);
        historyManager.add(task);
        task = new Task("t4", "t2");
        task.setId(4);
        historyManager.add(task);
        task = new Task("t5", "t2");
        task.setId(5);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(5, history.size(), "История не равна 5");
        historyManager.remove(3);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(4, history.size(), "История не равна 4");
        assertEquals(1, history.get(0).getId(), "ИД первого элемента неверно");
        assertEquals(2, history.get(1).getId(), "ИД второго элемента неверно");
        assertEquals(4, history.get(2).getId(), "ИД третьего элемента неверно");
        assertEquals(5, history.get(3).getId(), "ИД четвертого элемента неверно");
        historyManager.remove(5);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(3, history.size(), "История не равна 3");
        assertEquals(1, history.get(0).getId(), "ИД первого элемента неверно");
        assertEquals(2, history.get(1).getId(), "ИД второго элемента неверно");
        assertEquals(4, history.get(2).getId(), "ИД третьего элемента неверно");
        historyManager.remove(1);
        history = historyManager.getHistory();
        assertNotNull(history, "История null.");
        assertEquals(2, history.size(), "История не равна 2");
        assertEquals(2, history.get(0).getId(), "ИД первого элемента неверно");
        assertEquals(4, history.get(1).getId(), "ИД второго элемента неверно");
    }

    @Test
    void checkGetTaskListOrder() { //проверяем порядок элементов в истории
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