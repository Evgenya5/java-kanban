import data.Epic;
import data.Subtask;
import data.Task;
import logic.InMemoryTaskManager;
import logic.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void addNewSubtask() { //Можем создавать и получать подзадачу
        Subtask subtask = new Subtask("Test", "Test description", 0);
        final int taskId = inMemoryTaskManager.createTask(subtask);

        final Subtask savedTask = inMemoryTaskManager.getSubtaskById(taskId);

        assertNotNull(savedTask, "Подзадача не найдена.");
        assertEquals(subtask, savedTask, "Подзадачи не совпадают.");

        final List<Subtask> epics = inMemoryTaskManager.getSubtaskList();
        final Subtask subtaskById = inMemoryTaskManager.getSubtaskById(taskId);
        assertNotNull(subtaskById, "Подзадача не возвращается по ИД.");
        assertEquals(taskId, subtaskById.getId(), "Неверное ID подзадачи.");
        assertNotNull(epics, "Подзадачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество подзадач.");
        assertEquals(subtask, epics.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void SubtaskWithSameIdShouldBeEquals() {
        int subtaskId = 1;
        Subtask subtask1 = new Subtask("st1", "st1", 0);
        subtask1.setId(subtaskId);
        Subtask subtask2 = new Subtask("st2", "st2", 0);
        subtask2.setId(subtaskId);
        assertEquals(subtask1, subtask2, "Подзадачи не равны");
    }

    @Test
    public void SubtaskCantBeEpicForYourself() {
        Subtask subtask = new Subtask("epic", "epic", -10);
        int subtaskId = inMemoryTaskManager.createTask(subtask);
        Subtask subtaskByID = inMemoryTaskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskByID, "Подзадача не найдена.");
        subtaskByID.setEpicId(subtaskId);
        inMemoryTaskManager.updateSubtask(subtaskByID);
        assertEquals(subtaskByID.getEpicId(), subtaskByID.getId(), "ID равны");
    }

    @Test
    public void SubtaskIdGenerateAutomatically() {
        Subtask subtask1 = new Subtask("st1", "st1", 0);
        int subtaskId1 = inMemoryTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("st2", "st2", 0);
        subtask2.setId(subtaskId1);
        int subtaskId2 = inMemoryTaskManager.createTask(subtask2);
        assertNotEquals(subtask2.getId(), subtask1.getId(), "ID равны");
    }

    @Test
    public void SubtaskNotChange() {
        Epic epic1 = new Epic("epic1", "epic1");
        int epicId1 = inMemoryTaskManager.createTask(epic1);
        Subtask subtask1 = new Subtask("t1", "t1", epicId1);
        int subtaskId1 = inMemoryTaskManager.createTask(subtask1);
        Subtask subtaskById = inMemoryTaskManager.getSubtaskById(subtaskId1);
        assertEquals(subtask1.getName(), subtaskById.getName(), "Name не равны");
        assertEquals(subtask1.getDescription(), subtaskById.getDescription(), "Description не равны");
        assertEquals(subtask1.getEpicId(), subtaskById.getEpicId(), "Epic не равны");
    }
}