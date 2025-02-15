import logic.InMemoryTaskManager;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest  extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager(); //инициализируем T taskManager реализацией InMemoryTaskManager
        initTasks();
    }

}