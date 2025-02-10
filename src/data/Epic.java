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
        return getId() + ", " + TaskType.EPIC + ", " + getName() + ", " + getStatus() + ", " + getDescription();
    }
}