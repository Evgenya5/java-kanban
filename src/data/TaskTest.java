package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TaskTest {
    private int id;
    private String name;
    private String description;
    //private TaskStatus status;
    private LocalDateTime startTime;
    protected static final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public TaskTest(String name, String description) {
        this.name = name;
        this.description = description;
        //this.status = TaskStatus.NEW;
    }

    public TaskTest(String name, String description, LocalDateTime startDateTime) {
        this.name = name;
        this.description = description;
        //this.status = TaskStatus.NEW;
        //this.duration = Duration.ofMinutes(0);
        this.startTime = startDateTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return id + ", " + name + ", " + description + ", " + ", ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskTest task)) return false;
        TaskTest t = (TaskTest) o;
        return t.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
