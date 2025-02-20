package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;
    protected static final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(String name, String description, LocalDateTime startDateTime) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ofMinutes(0);
        this.startTime = startDateTime;
    }

    public Task(String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
        this.description = description;
        this.status = taskStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getFormatedStartTime() {
        if (startTime == null) {
            return "";
        }
        return startTime.format(dateTimeFormater);
    }

    public void setStartTimeFromString(String formatedTime) {
        if (!Objects.equals(formatedTime, "") && formatedTime != null) {
            this.startTime = LocalDateTime.parse(formatedTime,dateTimeFormater);
        }
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(String startTime) {
        if (startTime == "") {
            return;
        }
        this.startTime = LocalDateTime.parse(startTime,dateTimeFormater);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(long durationInMin) {
        if (durationInMin >= 0) {
            this.duration = Duration.ofMinutes(durationInMin);
        }
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return startTime;
        }

        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getEndTimeFormated() {
        if (startTime == null) {
            return "";
        }
        return getEndTime().format(dateTimeFormater);
    }

    @Override
    public String toString() {
        return id + ", " + TaskType.TASK + ", " + name + ", " + status + ", " + description + ", " + getFormatedStartTime() + ", "
                + duration.toMinutes() + ", " + getEndTimeFormated();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        Task t = (Task) o;
        return t.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
