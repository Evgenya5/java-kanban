package data;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;
    LocalDateTime endTime;

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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getEndTimeFormated() {
        if (endTime == null) {
            return "";
        }
        return endTime.format(dateTimeFormater);
    }

    public LocalDateTime setEndTimeFromString(String endTimeStr) {
        return LocalDateTime.parse(endTimeStr,dateTimeFormater);
    }

    @Override
    public String toString() {
        return getId() + ", " + TaskType.EPIC + ", " + getName() + ", " + getStatus() + ", " + getDescription()
                + ", " + getFormatedStartTime() + ", " + getDuration().toMinutes()  + ", " + getEndTimeFormated();
    }
}