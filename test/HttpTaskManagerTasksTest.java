import com.google.gson.Gson;
import data.Epic;
import data.Subtask;
import data.Task;
import data.TaskStatus;
import logic.InMemoryTaskManager;
import logic.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    private static final String URL_BASE = "http://localhost:8081/";
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testPostTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = task;
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        /*Проверяем обновление*/
        task = tasksFromManager.getFirst();
        task.setName("new_name");
        taskJson = gson.toJson(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new_name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        /*Проверяем создание пересекающейся задачи*/
        taskJson = gson.toJson(task1);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new_name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025,2,19,16,20));
        manager.createTask(task);
        manager.createTask(task1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTaskList();
        assertEquals(response.body(), tasksFromManager.stream().map(t -> gson.toJson(t)).collect(Collectors.joining("\n")), "Некорректное количество задач");
        url = URI.create(URL_BASE + "tasks/" + tasksFromManager.getFirst().getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(tasksFromManager.getFirst()), "Некорректная задача");
        url = URI.create(URL_BASE + "tasks/" + 100);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task1 = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025,2,19,16,20));
        Task task2 = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025,1,19,16,20));
        manager.createTask(task);
        manager.createTask(task1);
        manager.createTask(task2);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        List<Task> tasksFromManager = manager.getTaskList();
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
        URI url = URI.create(URL_BASE + "tasks/" + tasksFromManager.getFirst().getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getTaskList();
        assertEquals(200, response.statusCode());
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create(URL_BASE + "tasks");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getTaskList();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testPostSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic");
        int epicId = manager.createTask(epic);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        Subtask task1 = task;
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        /*Проверяем обновление*/
        task = tasksFromManager.getFirst();
        task.setName("new_Subtask_name");
        taskJson = gson.toJson(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getSubtaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new_Subtask_name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        /*Проверяем создание пересекающейся задачи*/
        taskJson = gson.toJson(task1);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        tasksFromManager = manager.getSubtaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new_Subtask_name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("epic", "epic");
        int epicId = manager.createTask(epic);
        Subtask task = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        Subtask task1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025,2,19,16,20), epicId);
        manager.createTask(task);
        manager.createTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtaskList();
        assertEquals(response.body(), tasksFromManager.stream().map(t -> gson.toJson(t)).collect(Collectors.joining("\n")), "Некорректное количество задач");
        url = URI.create(URL_BASE + "subtasks/" + tasksFromManager.getFirst().getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(tasksFromManager.getFirst()), "Некорректная задача");
        url = URI.create(URL_BASE + "subtasks/" + 100);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("epic", "epic");
        int epicId = manager.createTask(epic);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        Subtask task1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025,2,19,16,20), epicId);
        Subtask task2 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025,1,19,16,20), epicId);
        manager.createTask(task);
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        List<Subtask> tasksFromManager = manager.getSubtaskList();
        URI url = URI.create(URL_BASE + "subtasks/" + tasksFromManager.getFirst().getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getSubtaskList();
        assertEquals(200, response.statusCode());
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create(URL_BASE + "subtasks");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getSubtaskList();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testPostEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpicList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        /*Проверяем обновление*/
        task = tasksFromManager.getFirst();
        task.setName("new_name");
        taskJson = gson.toJson(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getEpicList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new_name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 1", "Testing task 1");
        Epic task1 = new Epic("Test 2", "Testing task 2");
        manager.createTask(task);
        manager.createTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> tasksFromManager = manager.getEpicList();
        assertEquals(response.body(), tasksFromManager.stream().map(t -> gson.toJson(t)).collect(Collectors.joining("\n")), "Некорректное количество задач");
        url = URI.create(URL_BASE + "epics/" + tasksFromManager.getFirst().getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(tasksFromManager.getFirst()), "Некорректная задача");
        url = URI.create(URL_BASE + "epics/" + 100);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        Subtask subtask = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), tasksFromManager.getFirst().getId());
        Subtask subtask1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 19, 16, 20), tasksFromManager.getFirst().getId());
        manager.createTask(subtask);
        manager.createTask(subtask1);
        url = URI.create(URL_BASE + "epics/" + 100 + "/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        url = URI.create(URL_BASE + "epics/" + tasksFromManager.getFirst().getId() + "/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), manager.getSubtaskListByEpic(tasksFromManager.getFirst().getId()).stream()
                .map(t -> gson.toJson(t)).collect(Collectors.joining("\n")), "Некорректный список подзадач");
    }

    @Test
    public void testDeleteEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 1", "Testing task 1");
        Epic task1 = new Epic("Test 2", "Testing task 2");
        Epic task2 = new Epic("Test 2", "Testing task 2");
        manager.createTask(task);
        manager.createTask(task1);
        manager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        List<Epic> tasksFromManager = manager.getEpicList();
        URI url = URI.create(URL_BASE + "epics/" + tasksFromManager.getFirst().getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getEpicList();
        assertEquals(200, response.statusCode());
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create(URL_BASE + "epics");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = manager.getEpicList();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int epicId = manager.createTask(epic);
        int taskId = manager.createTask(task);
        Subtask subtask = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        Subtask subtask1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 19, 16, 20), epicId);
        manager.createTask(subtask);
        int subId = manager.createTask(subtask1);
        manager.getTaskById(taskId);
        manager.getSubtaskById(subId);
        manager.getEpicById(epicId);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> historyFromManager = manager.getHistory();
        assertEquals(response.body(), historyFromManager.stream().map(t -> gson.toJson(t)).collect(Collectors.joining("\n")), "Некорректное количество задач");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int epicId = manager.createTask(epic);
        int taskId = manager.createTask(task);
        Subtask subtask = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        Subtask subtask1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 19, 16, 20), epicId);
        manager.createTask(subtask);
        int subId = manager.createTask(subtask1);
        manager.getTaskById(taskId);
        manager.getSubtaskById(subId);
        manager.getEpicById(epicId);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL_BASE + "prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Set<Task> prioritizedFromManager = manager.getPrioritizedTasks();
        assertEquals(response.body(), prioritizedFromManager.stream().map(t -> gson.toJson(t)).collect(Collectors.joining("\n")), "Некорректное количество задач");
    }
}