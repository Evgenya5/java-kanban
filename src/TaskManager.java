import java.util.HashMap;

public class TaskManager {
    private static int idCount = 0;
    private HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<Integer, Subtask>();
    private HashMap<Integer, Epic> epics = new HashMap<Integer, Epic>();

    public static int generateId() {
        return idCount++;
    }

    public boolean createTask(Task task) { //Создание. Сам объект должен передаваться в качестве параметра
        for (Task t : tasks.values()) {
            if (t.equals(task)) {
                return false;
            }
        }
        TaskStatus taskStatus = TaskStatus.NEW;
        int taskId = generateId();
        task.setStatus(taskStatus);
        task.setId(taskId);
        tasks.put(taskId, task);
        return true;
    }

    public boolean createTask(Subtask subtask) { //Создание. Сам объект должен передаваться в качестве параметра
        for (Subtask st : subtasks.values()) {
            if (st.equals(subtask)) {
                return false;
            }
        }
        TaskStatus taskStatus = TaskStatus.NEW;
        int taskId = generateId();
        subtask.setStatus(taskStatus);
        subtask.setId(taskId);
        subtasks.put(taskId, subtask);
        return true;
    }

    public boolean createTask(Epic epic) { //Создание. Сам объект должен передаваться в качестве параметра
        for (Epic e : epics.values()) {
            if (e.equals(epic)) {
                return false;
            }
        }
        TaskStatus taskStatus = TaskStatus.NEW;
        int taskId = generateId();
        epic.setStatus(taskStatus);
        epic.setId(taskId);
        for (Subtask subtask : epic.getSubtasks().values()) {
            subtask.setEpicId(taskId);
            subtasks.get(subtask.getId()).setEpicId(taskId);
        }

        epics.put(taskId, epic);
        return true;
    }

    public void getTaskList() { //Получение списка всех задач

        for (Task task : tasks.values()) {
            System.out.println(task);
        }
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() < 0) {
                System.out.println(subtask);
            }
        }
    }

    public void deleteAllTasks() { //Удаление всех задач
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public Object getTaskById(int id) { //Получение по идентификатору.
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public Subtask updateTask(int id, Subtask subtask) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).changeStatus();
        return subtasks.get(id);
    }

    public Task updateTask(int id, Task task) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        tasks.put(id, task);
        return tasks.get(id);
    }

    public Epic updateTask(int id, Epic epic) { //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        epic.changeStatus();
        for (int subtaskId : epic.getSubtasks().keySet()) {
            subtasks.get(subtaskId).setEpicId(epic.getId());
        }
        epics.put(id, epic);
        return epics.get(id);
    }

    public boolean deleteTaskById(int id) { //Удаление по идентификатору.
        if (taskExist(id)) {
            tasks.remove(id);
            return true;
        } else if (epicExist(id)) {
            for (int subtaskId : epics.get(id).getSubtasks().keySet()) {
                subtasks.get(subtaskId).setEpicId(-1);
            }
            epics.remove(id);
            return true;
        } else if (subtaskExist(id)) {
            if (subtasks.get(id).getEpicId() >= 0) {
                epics.get(subtasks.get(id).getEpicId()).getSubtasks().remove(id);
            }
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean subtaskExist(int id) {
        return subtasks.containsKey(id);
    }

    public boolean taskExist(int id) {
        return tasks.containsKey(id);
    }

    public boolean epicExist(int id) {
        return epics.containsKey(id);
    }
}
