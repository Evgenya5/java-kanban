package logic;

import data.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;
    File file;

    public FileBackedTaskManager(String fileName) throws ManagerSaveException {
        this.fileName = fileName;
        file = new File(fileName);
        System.out.println(file);
    }

    @Override
    public int createTask(Subtask task) {
        int id = super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public int createTask(Epic task) {
        int id = super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public void deleteAllTasks() { //Удаление всех задач
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllSubtasks() { //Удаление всех задач
        super.deleteAllSubtasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllEpics() { //Удаление всех задач
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(Task task) {
        return task.getId() + ", " + TaskType.TASK + ", " + task.getName() + ", " + task.getStatus() + ", " + task.getDescription();
    }

    private String toString(Subtask task) {
        return task.getId() + ", " + TaskType.SUBTASK + ", " + task.getName() + ", " + task.getStatus() + ", " + task.getDescription() + ", " + task.getEpicId();
    }

    private String toString(Epic task) {
        return task.getId() + ", " + TaskType.EPIC + ", " + task.getName() + ", " + task.getStatus() + ", " + task.getDescription();
    }

    private Task fromString(String value) {
        String[] words = value.split(", ");
        if (words.length == 5) {
            if (TaskType.TASK.equals(TaskType.valueOf(words[1]))) {
                Task task = new Task(words[2],words[4]);
                task.setId(Integer.parseInt(words[0]));
                task.setStatus(TaskStatus.valueOf(words[3]));
                return task;
            } else if (TaskType.EPIC.equals(TaskType.valueOf(words[1]))) {
                Epic task = new Epic(words[2],words[4]);
                task.setId(Integer.parseInt(words[0]));
                task.setStatus(TaskStatus.valueOf(words[3]));
                return task;
            }
        } else if (words.length == 6) {
            if (TaskType.SUBTASK.equals(TaskType.valueOf(words[1]))) {
                Subtask task = new Subtask(words[2],words[4],Integer.parseInt(words[5]));
                task.setId(Integer.parseInt(words[0]));
                task.setStatus(TaskStatus.valueOf(words[3]));
                return task;
            }
        }
        return null;
    }

    private void save() throws ManagerSaveException { //сохранять текущее состояние менеджера в указанный файл
        try {
            Writer fileWriter = new FileWriter(fileName);
            fileWriter.write("id, type, name, status, description, epic \n");
            for (Task task : getTaskList()) {
                fileWriter.write(toString(task) + "\n");
            }
            for (Subtask subtask : getSubtaskList()) {
                fileWriter.write(toString(subtask) + "\n");
            }
            for (Epic epic : getEpicList()) {
                fileWriter.write(toString(epic) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при работе с файлом!");
        }
    }



    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException { //будет восстанавливать данные менеджера из файла при запуске программы
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getName());
        List<String> tasks;
        try {
            tasks = Files.lines(file.toPath())
                    .filter(value -> value.contains("TASK") || value.contains("SUBTASK") || value.contains("EPIC"))
                    .toList();
            System.out.println(Files.readString(file.toPath()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при работе с файлом!");
        }
        for (String line:tasks) {
            fileBackedTaskManager.createTask(fileBackedTaskManager.fromString(line));
        }
        return fileBackedTaskManager;
    }
}
