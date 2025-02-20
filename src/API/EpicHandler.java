package API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import data.Epic;
import data.Subtask;
import logic.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        System.out.println("Началась обработка /epics запроса от клиента." + method);
        switch (method) {
            case "POST":
                InputStream inputStream = exchange.getRequestBody(); // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Тело запроса:\n" + body);
                GsonBuilder gsonBuilder = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                        .registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
                Gson gson = gsonBuilder.create();
                Epic epic = gson.fromJson(body, Epic.class);
                int returnId = 0;
                if (epic.getId() >= 1) {
                    returnId = taskManager.updateEpic(epic);
                } else {
                    returnId = taskManager.createTask(epic);
                }
                if (returnId < 0) {
                    response = "Обновление/создание не выполнено.";
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
                    Epic epicById = taskManager.getEpicById(id);
                    if (epicById == null) {
                        response = "Задача с таким id не найдена.";
                        sendNotFound(exchange,response);
                        return;
                    } else {
                        if (params.length >= 4){
                            response = taskManager.getSubtaskListByEpic(epicById.getId()).stream()
                                    .map(Subtask::toString)
                                    .collect(Collectors.joining("\n"));
                        } else {
                            response = epicById.toString();
                        }
                    }
                } else {
                    response = taskManager.getEpicList().stream()
                            .map(Epic::toString)
                            .collect(Collectors.joining("\n"));
                }
                break;
            }
            case "DELETE": {
                String[] params = path.split("/");
                if (params.length >= 3) {
                    int id = Integer.parseInt(params[2]);
                    taskManager.deleteEpicById(id);
                } else {
                    taskManager.deleteAllEpics();
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
