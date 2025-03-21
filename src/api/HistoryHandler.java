package api;

import com.sun.net.httpserver.HttpExchange;
import logic.TaskManager;
import java.io.IOException;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        statusCode = 200;
        method = exchange.getRequestMethod();
        response = "";
        path = exchange.getRequestURI().getPath();;
        System.out.println("Началась обработка /history запроса от клиента." + method);
        switch (method) {
            case "GET": {
                response = taskManager.getHistory().stream()
                        .map(t -> gson.toJson(t))
                        .collect(Collectors.joining("\n"));
                break;
            }
            default:
                response = "Некорректный метод!";
                statusCode = 405;
        }
        sendText(exchange,response,statusCode);
    }
}
