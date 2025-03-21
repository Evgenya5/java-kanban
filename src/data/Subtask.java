package data;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, LocalDateTime startDateTime) {
        super(name, description, startDateTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return getId() + ", " + TaskType.SUBTASK + ", " + getName() + ", " + getStatus() + ", " + getDescription()   + ", " + getFormatedStartTime()
                + ", " + getDuration().toMinutes() +  ", " + getEndTimeFormated() + ", " + getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
