package logic;

import data.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected int idCount = 1;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public int createTask(Task task) { //Создание. Сам объект должен передаваться в качестве параметра
        if (startDatesOfTasksOverlap(task)) {
            return -1;
        }
        int taskId = generateId();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);
        tasks.put(taskId, task);
        addPrioritizedTask(task);
        return taskId;
    }

    @Override
    public int createTask(Subtask subtask) { //Создание. Сам объект должен передаваться в качестве параметра
        if (startDatesOfTasksOverlap(subtask)) {
            return -1;
        }
        if (!epicExist(subtask.getEpicId())) {
            return -1;
        }
        int taskId = generateId();
        subtask.setId(taskId);
        subtasks.put(taskId, subtask);
        epics.get(subtask.getEpicId()).addSubtask(taskId);
        changeEpicStatus(epics.get(subtask.getEpicId()));
        changeEpicDateParams(epics.get(subtask.getEpicId()));
        addPrioritizedTask(subtask);
        return taskId;
    }

    @Override
    public int createTask(Epic epic) { //Создание. Сам объект должен передаваться в качестве параметра
        int taskId = generateId();
        epic.setId(taskId);
        epics.put(taskId, epic);
        return taskId;
    }

    @Override
    public ArrayList<Task> getTaskList() { //Получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() { //Получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskListByEpic(int epicId) { //Получение списка всех подзадач по эпику
        ArrayList<Subtask> subtaskByEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return subtaskByEpic;
        }
        for (int subtask : epic.getSubtasks()) {
            subtaskByEpic.add(subtasks.get(subtask));
        }
        return subtaskByEpic;
    }

    @Override
    public ArrayList<Epic> getEpicList() { //Получение списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() { //Удаление всех задач
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            removePrioritizedTask(task);
        });
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() { //Удаление всех подзадач
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            removePrioritizedTask(subtask);
        });
        subtasks.clear();
        epics.values().forEach(epic -> { //очищаем сабтаски в эпике
            epic.getSubtasks().clear();
            changeEpicStatus(epic);
            changeEpicDateParams(epic);
        });
    }

    @Override
    public void deleteAllEpics() { //Удаление всех эпиков
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            removePrioritizedTask(subtask);
        });
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) { //Получение задачи по идентификатору.
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) { //Получение подзадачи по идентификатору.
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) { //Получение эпика по идентификатору.
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int updateSubtask(Subtask subtask) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (startDatesOfTasksOverlap(subtask)) {
            return -1;

        }
        Subtask savedSubtask = subtasks.get(subtask.getId());
        removePrioritizedTask(savedSubtask);
        if (savedSubtask == null) {
            return -1;
        }
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        savedSubtask.setStatus(subtask.getStatus());
        savedSubtask.setStartTime(subtask.getStartTime());
        savedSubtask.setDuration(subtask.getDuration().toMinutes());
        if (epicExist(savedSubtask.getEpicId())) {
            changeEpicStatus(epics.get(savedSubtask.getEpicId()));
            changeEpicDateParams(epics.get(savedSubtask.getEpicId()));
        }
        addPrioritizedTask(savedSubtask);
        return savedSubtask.getId();
    }

    @Override
    public int updateTask(Task task) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (startDatesOfTasksOverlap(task)) {
            return -1;
        }
        if (taskExist(task.getId())) {
            removePrioritizedTask(task);
            tasks.put(task.getId(), task);
            addPrioritizedTask(task);
            return task.getId();
        }
        return -1;
    }

    @Override
    public int updateEpic(Epic epic) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return -1;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        return savedEpic.getId();
    }

    @Override
    public void deleteTaskById(Integer id) { //Удаление по идентификатору.
        Task task = tasks.remove(id);
        removePrioritizedTask(task);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) { //Удаление по идентификатору.
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        removePrioritizedTask(subtask);
        int epicId = subtask.getEpicId();
        historyManager.remove(id);
        if (epicExist(epicId)) {
            epics.get(epicId).deleteSubtask(id);
            changeEpicStatus(epics.get(epicId));
            changeEpicDateParams(epics.get(epicId));
        }
    }

    @Override
    public void deleteEpicById(Integer id) { //Удаление по идентификатору.
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        historyManager.remove(id);
        for (int subtaskId : epic.getSubtasks()) {
            historyManager.remove(subtaskId);
            Subtask subtask = subtasks.remove(subtaskId);
            if (subtask != null) {
                removePrioritizedTask(subtask);
            }
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean startDatesOfTasksOverlap(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        if (getPrioritizedTasks().isEmpty()) {
            return false;
        }
        return getPrioritizedTasks().stream()
                .anyMatch(t -> (task.getId() != t.getId()) && task.getStartTime().isBefore(t.getEndTime()) && task.getEndTime().isAfter(t.getStartTime()));
    }

    private int generateId() {
        return idCount++;
    }

    private boolean subtaskExist(int id) {
        return subtasks.containsKey(id);
    }

    private boolean taskExist(int id) {
        return tasks.containsKey(id);
    }

    private boolean epicExist(int id) {
        return epics.containsKey(id);
    }

    protected void addPrioritizedTask(Task task) {
        if (task == null) {
            return;
        }
        if (task.getStartTime() == null) {
            return;
        }
        if (startDatesOfTasksOverlap(task)) {
            return;
        }
        prioritizedTasks.add(task);
    }

    private void removePrioritizedTask(Task task) {
        if (task == null) {
            return;
        }
        if (task.getStartTime() == null) {
            return;
        }
        prioritizedTasks.remove(task);
    }

    private Epic changeEpicStatus(Epic epic) {

        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            if (getSubtaskListByEpic(epic.getId()).stream()
                    .filter(Objects::nonNull)
                    .allMatch(s -> s.getStatus().equals(TaskStatus.DONE))) {
                epic.setStatus(TaskStatus.DONE);
            } else if (getSubtaskListByEpic(epic.getId()).stream()
                    .filter(Objects::nonNull)
                    .allMatch(s -> s.getStatus().equals(TaskStatus.NEW))) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
        return epic;
    }

    private Epic changeEpicDateParams(Epic epic) {

        Stream<LocalDateTime> streamStartDate = getSubtaskListByEpic(epic.getId()).stream()
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime);
        LocalDateTime minStartDate = streamStartDate.filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Long duration = getSubtaskListByEpic(epic.getId()).stream()
                .mapToLong(u -> u.getDuration().toMinutes())
                .sum();
        Stream<LocalDateTime> streamEndDate = getSubtaskListByEpic(epic.getId()).stream()
                .filter(Objects::nonNull)
                .map(Subtask::getEndTime);

        LocalDateTime maxEndDate = streamEndDate.filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(minStartDate);
        epic.setDuration(duration);
        epic.setEndTime(maxEndDate);
        return epic;
    }
}
