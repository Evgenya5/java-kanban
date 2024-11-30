import data.Epic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void EpicWithSameIdShouldBeEquals() {
        int epicId = 1;
        Epic epic1 = new Epic("epic1", "epic1");
        epic1.setId(epicId);
        Epic epic2 = new Epic("epic2", "epic2");
        epic2.setId(epicId);
        assertEquals(epic1, epic2, "Эпики не равны");
    }
}