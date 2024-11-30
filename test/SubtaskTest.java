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

    @Test
    public void SubtaskWithSameIdShouldBeEquals() {
        int subtaskId = 1;
        Subtask subtask1 = new Subtask("st1", "st1", 0);
        subtask1.setId(subtaskId);
        Subtask subtask2 = new Subtask("st2", "st2", 0);
        subtask2.setId(subtaskId);
        assertEquals(subtask1, subtask2, "Подзадачи не равны");
    }
}