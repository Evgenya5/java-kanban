package api;

import com.sun.net.httpserver.HttpExchange;
import data.Task;
import logic.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        statusCode = 200;
        method = exchange.getRequestMethod();
        response = "";
        path = exchange.getRequestURI().getPath();
        System.out.println("Началась обработка /tasks запроса от клиента." + method);
        switch (method) {
            case "POST":
                InputStream inputStream = exchange.getRequestBody(); // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Тело запроса:\n" + body);
                Task task = gson.fromJson(body, Task.class);
                int returnId = 0;
                if (task.getId() >= 1) {
                    returnId = taskManager.updateTask(task);
                } else {
                    returnId = taskManager.createTask(task);
                }
                if (returnId < 0) {
                    response = "Задача пересекается с другой по времени. Обновление/создание не выполнено.";
                    statusCode = 406;
                } else {
                    statusCode = 201;
                }
                break;
            case "GET": {
                String[] params = path.split("/");
                if (params.length >= 3) {
                    int id = Integer.parseInt(params[2]);
                    Task taskById = taskManager.getTaskById(id);
                    if (taskById == null) {
                        response = "Задача с таким id не найдена.";
                        statusCode = 404;
                    } else {
                        response = gson.toJson(taskById);
                    }
                } else {
                    response = taskManager.getTaskList().stream()
                            .map(t -> gson.toJson(t))
                            .collect(Collectors.joining("\n"));
                }
                break;
            }
            case "DELETE": {
                String[] params = path.split("/");
                if (params.length >= 3) {
                    int id = Integer.parseInt(params[2]);
                    taskManager.deleteTaskById(id);
                } else {
                    taskManager.deleteAllTasks();
                }
                break;
            }
            default:
                response = "Некорректный метод!";
                statusCode = 405;
        }
        sendText(exchange,response,statusCode);
    }
}