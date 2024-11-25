package data;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Integer subtaskId) {
        if (subtaskId >= 0) {
            if (!subtasks.contains(subtaskId)) {
                subtasks.add(subtaskId);
            }
        }
    }

    public void deleteSubtask(Integer subtaskId) {
        if (subtasks.contains(subtaskId)) {
            subtasks.remove(subtaskId);
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
            result = result + ", subtasks=" + subtasks.toString();
        }
        return result + "}";
    }
}