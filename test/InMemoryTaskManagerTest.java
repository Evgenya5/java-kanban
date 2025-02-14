import logic.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest  extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager(); //инициализируем T taskManager реализацией InMemoryTaskManager
        initTasks(taskManager);
    }

}