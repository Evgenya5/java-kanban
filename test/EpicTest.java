import data.Epic;
import data.Subtask;
import data.Task;
import logic.InMemoryTaskManager;
import logic.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void addNewEpic() { //Можем создавать и получать эпик
        Epic epic = new Epic("Test", "Test description");
        final int taskId = inMemoryTaskManager.createTask(epic);

        final Epic savedTask = inMemoryTaskManager.getEpicById(taskId);

        assertNotNull(savedTask, "Эпик не найдена.");
        assertEquals(epic, savedTask, "Эпики не совпадают.");

        final List<Epic> epics = inMemoryTaskManager.getEpicList();
        final Epic epicById = inMemoryTaskManager.getEpicById(taskId);
        assertNotNull(epicById, "Эпик не возвращается по ИД.");
        assertEquals(taskId, epicById.getId(), "Неверное ID эпика.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

    }

    @Test
    public void EpicWithSameIdShouldBeEquals() {
        int epicId = 1;
        Epic epic1 = new Epic("epic1", "epic1");
        epic1.setId(epicId);
        Epic epic2 = new Epic("epic2", "epic2");
        epic2.setId(epicId);
        assertEquals(epic1, epic2, "Эпики не равны");
    }

    @Test
    public void EpicCantBeSubtaskForYourself() {
        Epic epic1 = new Epic("epic1", "epic1");
        int epicId = inMemoryTaskManager.createTask(epic1);
        Subtask subtask = new Subtask("st", "st", epicId);
        subtask.setId(epicId);
        int subId = inMemoryTaskManager.createTask(subtask);
        Subtask subtaskByID = inMemoryTaskManager.getSubtaskById(subId);
        assertNotNull(subtaskByID, "Подзадача не найдена.");
        assertNotEquals(subtaskByID.getId(), epic1.getId(), "ID равны");
        assertEquals(subtaskByID.getEpicId(), epic1.getId(), "Epic ID не равны");
    }

    @Test
    public void EpicIdGenerateAutomatically() {
        Epic epic1 = new Epic("epic1", "epic1");
        int epicId1 = inMemoryTaskManager.createTask(epic1);
        Epic epic2 = new Epic("epic2", "epic2");
        epic2.setId(epicId1);
        int epicId2 = inMemoryTaskManager.createTask(epic2);
        assertNotEquals(epic2.getId(), epic1.getId(), "ID равны");
    }

    @Test
    public void EpicNotChange() {
        Epic epic1 = new Epic("ep1", "ep1");
        int epicId1 = inMemoryTaskManager.createTask(epic1);
        Epic epicById = inMemoryTaskManager.getEpicById(epicId1);
        assertEquals(epic1.getName(), epicById.getName(), "Name не равны");
        assertEquals(epic1.getDescription(), epicById.getDescription(), "Description не равны");
    }
}