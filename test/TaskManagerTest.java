import data.Epic;
import data.Subtask;
import data.Task;
import logic.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager; //параметризованное поле taskManager, которое мы можем использовать в наследниках и инициализировать его конкретной реализацией таск менеджера
    protected Epic epic;
    protected Subtask subtask;
    protected Subtask subtaskWithDate1;
    protected Subtask subtaskWithDate2;
    protected Task task;
    protected Task taskWithDate;
    protected int taskId;
    protected int subtaskId;
    protected int epicId;

    protected void initTasks(T tm) {
        taskManager = tm;
        epic = new Epic("Test", "Test description");
        epicId = taskManager.createTask(epic);
        subtask = new Subtask("Test", "Test description", epicId);
        subtaskId = taskManager.createTask(subtask);
        subtaskWithDate1 = new Subtask("Test", "Test description", epicId, LocalDateTime.of(2025,1, 14, 14,0));
        subtaskWithDate2 = new Subtask("Test", "Test description", epicId, LocalDateTime.of(2025,2, 14, 14,0));
        task = new Task("Test", "Test description");
        taskId = taskManager.createTask(task);
        taskWithDate = new Task("Test", "Test description", LocalDateTime.of(2025,3, 14, 14,0));
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
    void checkEpicStartAndEndDate() { //Проставляем правильные даты для эпика
        subtaskWithDate1.setDuration(60);
        int subId = taskManager.createTask(subtaskWithDate1);
        final Subtask savedSubtask1 = taskManager.getSubtaskById(subId);
        Epic epicBySub = taskManager.getEpicById(savedSubtask1.getEpicId());
        assertEquals(epicBySub.getStartTime(), savedSubtask1.getStartTime(),"epic StartDate не обновлен");
        assertEquals(epicBySub.getEndTime(), savedSubtask1.getEndTime(),"epic EndDate не обновлен 1");
        assertEquals(epicBySub.getDuration(), savedSubtask1.getDuration(),"epic Duration не обновлен");
        subtaskWithDate2.setDuration(20);
        int subId2 = taskManager.createTask(subtaskWithDate2);
        final Subtask savedSubtask2 = taskManager.getSubtaskById(subId2);
        epicBySub = taskManager.getEpicById(savedSubtask2.getEpicId());
        assertEquals(epicBySub.getStartTime(), savedSubtask1.getStartTime(),"epic StartDate не обновлен");
        assertEquals(epicBySub.getDuration(), savedSubtask2.getDuration().plus(savedSubtask1.getDuration()),"epic Duration не обновлен");
        assertEquals(epicBySub.getEndTime(), savedSubtask2.getEndTime(),"epic EndDate не обновлен 2");
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
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        taskManager.getEpicById(epicId);
        assertEquals(1, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.getSubtaskById(subtaskId);
        assertEquals(2, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.deleteEpicById(epicId);
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
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
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        taskManager.getEpicById(epicId);
        assertEquals(1, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.getSubtaskById(subtaskId);
        assertEquals(2, taskManager.getHistory().size(), "История не равна 2.");
        taskManager.deleteEpicById(epicId);
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
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
    void addNewSubtaskWithDate() { //Можем создавать и получать подзадачу с датой
        int subId = taskManager.createTask(subtaskWithDate1);
        final Subtask savedSubtask = taskManager.getSubtaskById(subId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtaskWithDate1, savedSubtask, "Подзадачи не совпадают.");
        Epic epicBySub = taskManager.getEpicById(savedSubtask.getEpicId());
        assertEquals(epicBySub.getStartTime(), savedSubtask.getStartTime(),"epic StartDate не обновлен");
        assertEquals(epicBySub.getEndTime(), savedSubtask.getEndTime(),"epic EndDate не обновлен");
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime endTime = dateTime.plusMinutes(60L);
        assertNotNull(changedSubtask, "Подзадача не найдена");
        changedSubtask.setName("newName");
        changedSubtask.setDescription("newDesc");
        changedSubtask.setStartTime(dateTime);
        changedSubtask.setDuration(60L);
        taskManager.updateSubtask(changedSubtask);
        Subtask subtaskById = taskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskById, "Подзадача не найдена");
        final List<Subtask> subtasks = taskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtaskById.getDescription(), changedSubtask.getDescription(),"Description не обновлен");
        assertEquals(subtaskById.getName(), changedSubtask.getName(),"Name не обновлен");
        assertEquals(subtaskById.getEpicId(), changedSubtask.getEpicId(),"EpicID изменился");
        assertEquals(subtaskById.getStartTime(), dateTime,"StartDate не обновлен");
        assertEquals(subtaskById.getEndTime(), endTime,"EndDate не обновлен");
        Epic epicBySub = taskManager.getEpicById(changedSubtask.getEpicId());
        assertEquals(epicBySub.getStartTime(), dateTime,"epic StartDate не обновлен");
        assertEquals(epicBySub.getEndTime(), endTime,"epic EndDate не обновлен");
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
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        taskManager.getSubtaskById(subtaskId);
        assertEquals(1, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.deleteSubtaskById(subtaskId);
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
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
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        taskManager.getSubtaskById(subtaskId);
        assertEquals(1, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
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
    void addNewTaskWithDate() { //Можем создавать и получать задачу с датой
        int tId = taskManager.createTask(taskWithDate);
        final Task savedTask = taskManager.getTaskById(tId);
        assertNotNull(savedTask, "задача не найдена.");
        assertEquals(taskWithDate, savedTask, "задачи не совпадают.");
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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime endTime = dateTime.plusMinutes(60L);
        assertNotNull(changedTask, "Задача не найдена");
        changedTask.setName("newName");
        changedTask.setDescription("newDesc");
        changedTask.setStartTime(dateTime);
        changedTask.setDuration(60L);
        taskManager.updateTask(changedTask);
        Task taskById = taskManager.getTaskById(taskId);
        assertNotNull(taskById, "Задача не найдена");
        final List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskById.getDescription(),changedTask.getDescription(),"Description не обновлен");
        assertEquals(taskById.getName(),changedTask.getName(),"Name не обновлен");
        assertEquals(taskById.getStartTime(), dateTime,"StartDate не обновлен");
        assertEquals(taskById.getEndTime(), endTime,"EndDate не обновлен");
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
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        taskManager.getTaskById(taskId);
        assertEquals(1, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.deleteTaskById(taskId);
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
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
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        taskManager.getTaskById(taskId);
        assertEquals(1, taskManager.getHistory().size(), "История не равна 1.");
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getHistory().size(), "История не пустая.");
        tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void canGetPrioritizedTasks() { //Можем получать список всех задач
        Subtask subtaskWithDate3 = subtaskWithDate2;
        int tId = taskManager.createTask(taskWithDate);
        int stId1 = taskManager.createTask(subtaskWithDate1);
        int stId2 = taskManager.createTask(subtaskWithDate2);
        final List<Task> taskList = List.of(taskManager.getSubtaskById(stId1),taskManager.getSubtaskById(stId2),taskManager.getTaskById(tId));
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Задачи не возвращаются.");
        assertEquals(3, prioritizedTasks.size(), "Неверное количество задач.");
        assertEquals(taskList, prioritizedTasks.stream().toList(), "Задачи не по порядку.");
        taskManager.createTask(subtaskWithDate3);
        prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Задачи не возвращаются.");
        assertEquals(3, prioritizedTasks.size(), "Неверное количество задач.");
        assertEquals(taskList, prioritizedTasks.stream().toList(), "Задачи не по порядку.");
        Task t1 = taskWithDate;
        t1.setStartTime(LocalDateTime.of(2025, 7, 14, 14, 0));
        t1.setId(tId);
        taskManager.updateTask(t1);
        subtaskWithDate3.setStartTime(LocalDateTime.of(2025, 10, 14, 14, 0));
        subtaskWithDate3.setId(stId1);
        taskManager.updateSubtask(subtaskWithDate3);
        prioritizedTasks = taskManager.getPrioritizedTasks();
        final List<Task> taskListChange = List.of(taskManager.getSubtaskById(stId2),taskManager.getTaskById(tId),taskManager.getSubtaskById(stId1));
        assertNotNull(prioritizedTasks, "Задачи не возвращаются.");
        assertEquals(3, prioritizedTasks.size(), "Неверное количество задач.");
        assertNotEquals(taskList, prioritizedTasks.stream().toList(), "Задачи не по порядку.");
        assertEquals(taskListChange, prioritizedTasks.stream().toList(), "Задачи не по порядку.");
    }
}
