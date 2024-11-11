import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String command = scanner.nextLine();

            switch (command) {
                case "1" -> {
                    System.out.println("Что вы хотите создать: T - Task, ST - Subtask, E - Epic?");
                    String taskType = scanner.nextLine();
                    System.out.println("Введите наименование задачи");
                    String taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи");
                    String taskDescription = scanner.nextLine();
                    if (taskType.equals("E")) {
                        Epic epic = new Epic(taskName, taskDescription, TaskStatus.NEW);
                        System.out.println("Введите ид подзадач через Enter, которые нужно добавить к эпику. Для завершения ввода введите '-1'");
                        int nextId = scanner.nextInt();
                        while (nextId>=0) {
                            if (taskManager.subtaskExist(nextId)) {
                                epic.addSubtasks((Subtask) taskManager.getTaskById(nextId));
                            } else {
                                System.out.println("Нет такой подзадачи, введи ИД еще раз");
                            }
                            nextId = scanner.nextInt();
                        }
                        boolean result = taskManager.createTask(epic);
                        System.out.println("Эпик " + (result?" создан": " не создан"));
                    } else if (taskType.equals("ST")) {
                        Subtask subtask = new Subtask(taskName, taskDescription, TaskStatus.NEW);
                        boolean result = taskManager.createTask(subtask);
                        System.out.println("Подзадача " + (result?" создана": " не создана"));
                    } else {
                        Task task = new Task(taskName, taskDescription, TaskStatus.NEW);
                        boolean result = taskManager.createTask(task);
                        System.out.println("Задача " + (result?" создана": " не создана"));
                    }
                }
                case "2" -> taskManager.getTaskList();
                case "3" -> {
                    System.out.println("Введите id задачи");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();
                    boolean result = taskManager.deleteTaskById(taskId);
                    System.out.println("задача с id: " + taskId + (result?" удалена": " не удалена"));
                }
                case "4" -> {
                    taskManager.deleteAllTasks();
                    System.out.println("Все задачи удалены");
                }
                case "5" -> {
                    System.out.println("Введите id задачи");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите наименование задачи");
                    String taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи");
                    String taskDescription = scanner.nextLine();
                    if (taskManager.taskExist(taskId)) {
                        System.out.println("Введите статус задачи из списка NEW, IN_PROGRESS, DONE: ");
                        String status = scanner.next();
                        TaskStatus taskStatus = TaskStatus.valueOf(status);
                        Task task = (Task) taskManager.getTaskById(taskId);
                        task.setName(taskName);
                        task.setDescription(taskDescription);
                        task.setStatus(taskStatus);
                        taskManager.updateTask(taskId, task);
                        System.out.println("Задача обновлена" + task);
                    } else if (taskManager.subtaskExist(taskId)) {
                        System.out.println("Введите статус задачи из списка NEW, IN_PROGRESS, DONE: ");
                        String status = scanner.next();
                        TaskStatus taskStatus = TaskStatus.valueOf(status);
                        Subtask subtask = (Subtask) taskManager.getTaskById(taskId);
                        subtask.setName(taskName);
                        subtask.setDescription(taskDescription);
                        subtask.setStatus(taskStatus);
                        taskManager.updateTask(taskId,subtask);
                        System.out.println("Подзадача обновлена" + subtask);
                    } else if (taskManager.epicExist(taskId)) {
                        Epic epic = (Epic) taskManager.getTaskById(taskId);
                        epic.setName(taskName);
                        epic.setDescription(taskDescription);
                        System.out.println("Добавить сабтаски? Y / N");
                        String answer = scanner.nextLine();
                        if (answer.equals("Y")) {
                            System.out.println("Введите ид подзадач через Enter, которые нужно добавить к эпику. Для завершения ввода введите '-1'");
                            int nextId = scanner.nextInt();
                            while (nextId>=0) {
                                if (taskManager.subtaskExist(nextId)) {
                                    epic.addSubtasks((Subtask) taskManager.getTaskById(nextId));
                                } else {
                                    System.out.println("Нет такой подзадачи, введи ИД еще раз");
                                }
                                nextId = scanner.nextInt();
                            }
                        }
                        taskManager.updateTask(taskId, epic);
                    }
                }
                case "6" -> {
                    System.out.println("Введите id задачи");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();
                    if (taskManager.subtaskExist(taskId)) {
                        Subtask subtask = (Subtask) taskManager.getTaskById(taskId);
                        System.out.println("задача с id: " + taskId + " " + subtask);
                    } else if (taskManager.taskExist(taskId)) {
                        Task task = (Task) taskManager.getTaskById(taskId);
                        System.out.println("задача с id: " + taskId + " " + task);
                    } else if (taskManager.epicExist(taskId)) {
                        Epic epic = (Epic) taskManager.getTaskById(taskId);
                        System.out.println("задача с id: " + taskId + " " + epic);
                    }
                }
                case "7" -> {
                    System.out.println("Вы вышли из программы. Хорошего дня! :)");
                    return;
                }
                default -> System.out.println("Такой команды нет! Повторите ввод.");
            }
        }



    }

    private static void printMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Получить список всех задач");
        System.out.println("3 - Удалить задачу по ид");
        System.out.println("4 - Удалить все задачи");
        System.out.println("5 - Изменить задачу по ид");
        System.out.println("6 - Получить задачу по ид");
        System.out.println("7 - Выход");
    }
}
