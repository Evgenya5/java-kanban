import api.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import logic.FileBackedTaskManager;
import logic.TaskManager;
import java.io.File;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8081;
    final TaskManager taskManager;
    private HttpServer httpServer;
    //private  static Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        //HttpServer httpServer;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("test.csv");
        final TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager1);
        httpTaskServer.start();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                .registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        return gsonBuilder.create();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager)); // связываем путь и обработчик
        httpServer.createContext("/tasks", new TaskHandler(taskManager)); // связываем путь и обработчик
        httpServer.createContext("/epics", new EpicHandler(taskManager)); // связываем путь и обработчик
        httpServer.createContext("/history", new HistoryHandler(taskManager)); // связываем путь и обработчик
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager)); // связываем путь и обработчик
        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }


    public void stop() {
        httpServer.stop(1);
    }
}
