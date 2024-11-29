import data.Epic;
import data.Subtask;
import data.Task;
import logic.InMemoryTaskManager;
import logic.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void AddHistoryAndGetHistoryList() {
        Epic epic = new Epic("Test", "Test description");
        final int epicId = inMemoryTaskManager.createTask(epic);
        Subtask subtask = new Subtask("Test", "Test description", epicId);
        final int subtaskId = inMemoryTaskManager.createTask(subtask);
        Task task = new Task("Test", "Test description");
        final int taskId = inMemoryTaskManager.createTask(task);
        ArrayList<Task> histories = new ArrayList<>();
        Task taskById = inMemoryTaskManager.getTaskById(taskId);
        histories.add(taskById);
        Subtask subtaskById = inMemoryTaskManager.getSubtaskById(subtaskId);
        histories.add(subtaskById);
        Epic epicById = inMemoryTaskManager.getEpicById(epicId);
        histories.add(epicById);
        epicById.setName("newEpName");
        inMemoryTaskManager.updateEpic(epicById);
        assertEquals(histories, inMemoryTaskManager.getHistory(), "История не совпадает.");
    }

}