package API;

import com.sun.net.httpserver.HttpExchange;
import data.Task;
import logic.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        System.out.println("Началась обработка /history запроса от клиента." + method);
        switch (method) {
            case "GET": {
                response = taskManager.getHistory().stream()
                        .map(Task::toString)
                        .collect(Collectors.joining("\n"));
                break;
            }
            default:
                response = "Некорректный метод!";
                statusCode = 400;
        }
        sendText(exchange,response,statusCode);
    }
}
