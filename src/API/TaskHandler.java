package API;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import data.Task;
import logic.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        System.out.println("Началась обработка /tasks запроса от клиента." + method);
        switch (method) {
            case "POST":
                InputStream inputStream = exchange.getRequestBody(); // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Тело запроса:\n" + body);
                GsonBuilder gsonBuilder = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                        .registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
                Gson gson = gsonBuilder.create();
                Task task = gson.fromJson(body, Task.class);
                int returnId = 0;
                if (task.getId() >= 1) {
                    returnId = taskManager.updateTask(task);
                } else {
                    returnId = taskManager.createTask(task);
                }
                if (returnId < 0) {
                    response = "Задача пересекается с другой по времени. Обновление/создание не выполнено.";
                    sendHasInteractions(exchange,response);
                    return;
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
                        sendNotFound(exchange,response);
                        return;
                    } else {
                        response = taskById.toString();
                    }
                } else {
                    response = taskManager.getTaskList().stream()
                            .map(Task::toString)
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
                statusCode = 400;
        }
        sendText(exchange,response,statusCode);
    }
}