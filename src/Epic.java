import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtasks(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        changeStatus();
    }

    public void changeStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
        } else {
            boolean allDone = true;
            boolean allNew = true;
            for (Subtask subtask : subtasks.values()) {
                if (!subtask.getStatus().equals(TaskStatus.DONE)) {
                    allDone = false;
                }
                if (!subtask.getStatus().equals(TaskStatus.NEW)) {
                    allNew = false;
                }
            }
            if (allDone) {
                setStatus(TaskStatus.DONE);
            } else if (allNew) {
                setStatus(TaskStatus.NEW);
            } else {
                setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus();

        if (subtasks.isEmpty()) {
            return result + ", subtasks=[null]}";
        } else {
            result = result + ", subtasks=[";
            int count = 0;
            for (Subtask subtask : subtasks.values()) {
                count++;
                result = result + subtask.toString();
                if (count != subtasks.size()) {
                    result = result + ", \n";
                }
            }
        }
        return result + "]}";
    }
}
