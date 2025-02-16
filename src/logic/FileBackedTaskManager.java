package logic;

import data.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(String fileName) {
        file = new File(fileName);
        System.out.println(file);
    }

    @Override
    public int createTask(Subtask task) {
        int id = super.createTask(task);
        save();
        return id;

    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createTask(Epic task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void deleteAllTasks() { //Удаление всех задач
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() { //Удаление всех задач
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() { //Удаление всех задач
        super.deleteAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    private Task fromString(String value) {
        String[] words = value.split(",");
        int id = Integer.parseInt(words[0].trim());
        TaskType type = TaskType.valueOf(words[1].trim());
        String name = words[2];
        TaskStatus status = TaskStatus.valueOf(words[3].trim());
        String description = words[4].trim();
        String startTime = words[5].trim();
        Long durationInMin = Long.parseLong(words[6].trim());
        setIdCount(id);
        switch (type) {
            case SUBTASK: {
                int epicId = Integer.parseInt(words[8].trim());
                Subtask task = new Subtask(name, description, epicId);
                task.setId(id);
                task.setStatus(status);
                task.setStartTimeFromString(startTime);
                task.setDuration(durationInMin);
                return task;
            }
            case TASK: {
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                task.setStartTimeFromString(startTime);
                task.setDuration(durationInMin);
                return task;
            }
            case EPIC: {
                Epic task = new Epic(name, description);
                task.setId(id);
                task.setStatus(status);
                task.setStartTimeFromString(startTime);
                task.setDuration(durationInMin);
                String endTime = words[7].trim();
                if (!Objects.equals(endTime, "")) {
                    task.setEndTimeFromString(endTime);
                }
                return task;
            }
            default: {
                return new Task(name, description);
            }
        }
    }

    private void setIdCount(int id) {
        if (id > idCount) {
            idCount = id;
        }
    }

    private void save() { //сохранять текущее состояние менеджера в указанный файл
        try {
            Writer fileWriter = new FileWriter(file.getName());
            fileWriter.write("id, type, name, status, description, startTime, duration, endTime, epic \n");
            for (Task task : getTaskList()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Subtask subtask : getSubtaskList()) {
                fileWriter.write(subtask.toString() + "\n");
            }
            for (Epic epic : getEpicList()) {
                fileWriter.write(epic.toString() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при работе с файлом!");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) { //будет восстанавливать данные менеджера из файла при запуске программы

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getName());
        List<String> tasksList;

        if (!file.exists()) {
            return fileBackedTaskManager;
        }
        try {
            tasksList = Files.lines(file.toPath())
                    .skip(1)
                    .toList();
            System.out.println(Files.readString(file.toPath()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при работе с файлом!");
        }
        for (String line : tasksList) {
            if (line.contains(TaskType.EPIC.toString())) {
                Epic epic = (Epic) fileBackedTaskManager.fromString(line);
                fileBackedTaskManager.epics.put(epic.getId(), epic);
            } else if (line.contains(TaskType.SUBTASK.toString())) {
                Subtask subtask = (Subtask) fileBackedTaskManager.fromString(line);
                fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                fileBackedTaskManager.addPrioritizedTask(subtask);
            } else if (line.contains(TaskType.TASK.toString())) {
                Task task = fileBackedTaskManager.fromString(line);
                fileBackedTaskManager.tasks.put(task.getId(), task);
                fileBackedTaskManager.addPrioritizedTask(task);
            }
        }
        for (Subtask subtask : fileBackedTaskManager.subtasks.values()) {
            fileBackedTaskManager.epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
        }
        return fileBackedTaskManager;
    }
}
