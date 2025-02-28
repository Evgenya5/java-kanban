package api;

import com.sun.net.httpserver.HttpExchange;
import data.Epic;
import logic.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        statusCode = 200;
        method = exchange.getRequestMethod();
        response = "";
        path = exchange.getRequestURI().getPath();
        System.out.println("Началась обработка /epics запроса от клиента." + method);
        switch (method) {
            case "POST":
                InputStream inputStream = exchange.getRequestBody(); // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Тело запроса:\n" + body);
                Epic epic = gson.fromJson(body, Epic.class);
                int returnId = 0;
                if (epic.getId() >= 1) {
                    returnId = taskManager.updateEpic(epic);
                } else {
                    returnId = taskManager.createTask(epic);
                }
                if (returnId < 0) {
                    response = "Обновление/создание не выполнено.";
                    statusCode = 406;
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
                        statusCode = 404;
                    } else {
                        if (params.length >= 4) {
                            response = taskManager.getSubtaskListByEpic(epicById.getId()).stream()
                                    .map(t -> gson.toJson(t))
                                    .collect(Collectors.joining("\n"));
                        } else {
                            response = gson.toJson(epicById);
                        }
                    }
                } else {
                    response = taskManager.getEpicList().stream()
                            .map(t -> gson.toJson(t))
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
                statusCode = 405;
        }
        sendText(exchange,response,statusCode);
    }
}
