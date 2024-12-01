import data.Epic;
import data.Subtask;
import data.Task;
import logic.Managers;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    TaskManager taskManager;
    Epic epic;
    Subtask subtask;
    Task task;
    int taskId;
    int subtaskId;
    int epicId;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("Test", "Test description");
        epicId = taskManager.createTask(epic);
        subtask = new Subtask("Test", "Test description", epicId);
        subtaskId = taskManager.createTask(subtask);
        task = new Task("Test", "Test description");
        taskId = taskManager.createTask(task);
    }

    @Test
    void addHistoryAndGetHistoryList() {
        ArrayList<Task> histories = new ArrayList<>();
        Task taskById = taskManager.getTaskById(taskId);
        Subtask subtaskById = taskManager.getSubtaskById(subtaskId);
        Epic epicById = taskManager.getEpicById(epicId);

        histories.add(taskById);
        histories.add(subtaskById);
        histories.add(epicById);
        epicById.setName("newEpName");
        taskManager.updateEpic(epicById);
        assertEquals(histories, taskManager.getHistory(), "История не совпадает.");
    }

    @Test
    void addNewEpic() { //Можем создавать и получать эпик
        final Epic savedTask = taskManager.getEpicById(epicId);
        assertNotNull(savedTask, "Эпик не найдена.");
        assertEquals(epic, savedTask, "Эпики не совпадают.");
        final List<Epic> epics = taskManager.getEpicList();
        final Epic epicById = taskManager.getEpicById(epicId);
        assertNotNull(epicById, "Эпик не возвращается по ИД.");
        assertEquals(epicId, epicById.getId(), "Неверное ID эпика.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void epicCantBeSubtaskForYourself() {
        Subtask subtaskByID = taskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskByID, "Подзадача не найдена.");
        assertNotEquals(subtaskByID.getId(), epic.getId(), "ID равны");
        assertEquals(subtaskByID.getEpicId(), epic.getId(), "Epic ID не равны");
    }

    @Test
    public void epicIdGenerateAutomatically() {
        Epic epic2 = new Epic("epic2", "epic2");
        epic2.setId(epicId);
        taskManager.createTask(epic2);
        assertNotEquals(epic2.getId(), epic.getId(), "ID равны");
    }

    @Test
    public void epicNotChange() {
        Epic epicById = taskManager.getEpicById(epicId);
        assertEquals(epic.getName(), epicById.getName(), "Name не равны");
        assertEquals(epic.getDescription(), epicById.getDescription(), "Description не равны");
    }

    @Test
    void updateEpic() { //Можем обновлять эпик
        Epic changedEpic = taskManager.getEpicById(epicId);
        assertNotNull(changedEpic, "Эпик не найдена");
        changedEpic.setName("newName");
        changedEpic.setDescription("newDesc");
        taskManager.updateTask(changedEpic);
        Epic epicById = taskManager.getEpicById(epicId);
        assertNotNull(epicById, "Эпик не найдена");
        final List<Epic> epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпик не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epicById.getDescription(), changedEpic.getDescription(),"Description не обновлен");
        assertEquals(epicById.getName(), changedEpic.getName(),"Name не обновлен");
        assertEquals(epicById.getSubtasks(), changedEpic.getSubtasks(),"Subtasks list изменился");
    }

    @Test
    void canGetEpicList() { //Можем получать список всех эпиков
        final List<Epic> epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void canDeleteEpicById() { //Можем удалить эпик по ид
        List<Epic> epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
        List<Subtask> subtasks = taskManager.getSubtaskListByEpic(epicId);
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
        taskManager.deleteEpicById(epicId);
        epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
        subtasks = taskManager.getSubtaskListByEpic(epicId);
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void canDeleteAllEpic() { //Можем удалить все эпики
        List<Epic> epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
        List<Subtask> subtasks = taskManager.getSubtaskListByEpic(epicId);
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
        taskManager.deleteAllEpics();
        epics = taskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
        subtasks = taskManager.getSubtaskListByEpic(epicId);
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
        subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void addNewSubtask() { //Можем создавать и получать подзадачу
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        final List<Subtask> subtasks = taskManager.getSubtaskList();
        final Subtask subtaskById = taskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskById, "Подзадача не возвращается по ИД.");
        assertEquals(subtaskId, subtaskById.getId(), "Неверное ID подзадачи.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    public void subtaskCantBeEpicForYourself() {
        Subtask subtaskByID = taskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskByID, "Подзадача не найдена.");
        subtaskByID.setEpicId(subtaskId);
        taskManager.updateSubtask(subtaskByID);
        assertEquals(subtaskByID.getEpicId(), subtaskByID.getId(), "ID равны");
    }

    @Test
    public void subtaskIdGenerateAutomatically() {
        Subtask subtask2 = new Subtask("st2", "st2", 0);
        subtask2.setId(subtaskId);
        taskManager.createTask(subtask2);
        assertNotEquals(subtask2.getId(), subtask.getId(), "ID равны");
    }

    @Test
    public void subtaskNotChange() {
        Subtask subtaskById = taskManager.getSubtaskById(subtaskId);
        assertEquals(subtask.getName(), subtaskById.getName(), "Name не равны");
        assertEquals(subtask.getDescription(), subtaskById.getDescription(), "Description не равны");
        assertEquals(subtask.getEpicId(), subtaskById.getEpicId(), "Epic не равны");
    }

    @Test
    void updateSubtask() { //Можем обновлять подзадачу
        Subtask changedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(changedSubtask, "Подзадача не найдена");
        changedSubtask.setName("newName");
        changedSubtask.setDescription("newDesc");
        taskManager.updateTask(changedSubtask);
        Subtask subtaskById = taskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskById, "Подзадача не найдена");
        final List<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtaskById.getDescription(), changedSubtask.getDescription(),"Description не обновлен");
        assertEquals(subtaskById.getName(), changedSubtask.getName(),"Name не обновлен");
        assertEquals(subtaskById.getEpicId(), changedSubtask.getEpicId(),"EpicID изменился");
    }

    @Test
    void canGetSubtaskList() { //Можем получать список всех подзадач
        final List<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void canDeleteSubtaskById() { //Можем удалить подзадачу по ид
        List<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
        taskManager.deleteSubtaskById(subtaskId);
        subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void canDeleteAllSubtask() { //Можем удалить все подзадачи
        List<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
        taskManager.deleteAllSubtasks();
        subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void addNewTask() { //Можем создавать и получать задачу
        final Task taskById = taskManager.getTaskById(taskId);
        assertNotNull(taskById, "Задача не найдена");
        assertEquals(taskId, taskById.getId(), "Неверное ID задачи.");
        assertEquals(task, taskById, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void taskIdGenerateAutomatically() {
        Task task2 = new Task("t2", "t2");
        task2.setId(taskId);
        taskManager.createTask(task2);
        assertNotEquals(task2.getId(), task.getId(), "ID равны");
    }

    @Test
    public void taskNotChange() {
        Task taskById = taskManager.getTaskById(taskId);
        assertEquals(task.getName(), taskById.getName(), "Name не равны");
        assertEquals(task.getDescription(), taskById.getDescription(), "Description не равны");
    }

    @Test
    void updateTask() { //Можем обновлять задачу
        Task changedTask = taskManager.getTaskById(taskId);
        assertNotNull(changedTask, "Задача не найдена");
        changedTask.setName("newName");
        changedTask.setDescription("newDesc");
        taskManager.updateTask(changedTask);
        Task taskById = taskManager.getTaskById(taskId);
        assertNotNull(taskById, "Задача не найдена");
        final List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskById.getDescription(),changedTask.getDescription(),"Description не обновлен");
        assertEquals(taskById.getName(),changedTask.getName(),"Name не обновлен");
    }

    @Test
    void canGetTaskList() { //Можем получать список всех задач
        final List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void canDeleteTaskById() { //Можем удалить задачу по ид
        List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.deleteTaskById(taskId);
        tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void canDeleteAllTask() { //Можем удалить все задачи
        List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.deleteAllTasks();
        tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }
}