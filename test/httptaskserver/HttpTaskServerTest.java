package httptaskserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import manager.Managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {

    public final InMemoryTaskManager manager = Managers.getDefault();
    public final HttpTaskServer server = new HttpTaskServer(manager);
    public final Gson gson = server.getGson();

    public HttpResponse<String> response;
    public HttpRequest request;
    public String requestBodyJson;

    public LocalDateTime startTask_0;
    public LocalDateTime startSubtask_2;
    public LocalDateTime startSubtask_3;
    public LocalDateTime startForTest;
    public Duration duration_10m;
    public Duration duration_30m;
    Task task_0;
    Epic epic_1;
    Subtask subtask_2;
    Subtask subtask_3;

    @BeforeEach
    public void beforeEach() {
        startTask_0 = LocalDateTime.of(1, 1, 1, 0, 0, 0);
        startSubtask_2 = LocalDateTime.of(2, 2, 2, 0, 0, 0);
        startSubtask_3 = LocalDateTime.of(3, 3, 3, 0, 0, 0);
        startForTest = LocalDateTime.of(4, 4, 4, 0, 0, 0);

        duration_10m = Duration.ofMinutes(10);
        duration_30m = Duration.ofMinutes(30);
        task_0 = manager.createNewTask(new Task("Task_0", startTask_0, duration_10m));
        epic_1 = manager.createNewEpic(new Epic("Epic_1"));
        subtask_2 = manager.createNewSubtask(new Subtask(
                "Subtask_2", startSubtask_2, duration_10m, 1));
        subtask_3 = manager.createNewSubtask(new Subtask(
                "Subtask_2", startSubtask_3, duration_10m, 1));

        server.start();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
        request = null;
        response = null;
        manager.clearEpics();
        manager.clearTasks();
    }

    @Test
    void start() {
    }

    @Test
    void stop() {
    }

    public <T> void makeRequest(String URL, int id, boolean getSubtasks, T body, String method) {
        String num;
        String s = "";
        if (id >= 0) {
            num = String.valueOf(id);
            if (getSubtasks) s = "subtasks";
        } else {
            num = "";
        }
        String address = String.format("http://localhost:8080/%s/%s/%s", URL, num, s);

        if (body == null && method.equals("POST")) throw new NotFoundException("body null");
        requestBodyJson = gson.toJson(body);

        switch (method) {
            case "GET":
                request = HttpRequest.newBuilder()
                        .uri(URI.create(address))
                        .header("Content-type", "application/json")
                        .GET().build();
                break;
            case "POST":
                request = HttpRequest.newBuilder()
                        .uri(URI.create(address))
                        .header("Content-type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8)).build();
                break;
            case "DELETE":
                request = HttpRequest.newBuilder()
                        .uri(URI.create(address))
                        .header("Content-type", "application/json")
                        .DELETE().build();
                break;
            default:
                throw new NotFoundException("Неверный метод");
        }
    }

    public void sendRequest() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("sendRequest error");
        }
    }

    public <T> List<T> deserialization(Class<T> clazz) {
        String r = response.body();
        if (r.startsWith("{")) return List.of(gson.fromJson(r, clazz));

        return gson.fromJson(r, TypeToken.getParameterized(List.class, clazz).getType());
    }

    @Test
    public void ShouldReturnCode200AndTheTaskWhenGetMethodWithId() {
        makeRequest("tasks", 0, false, null, "GET");
        sendRequest();
        Task task = deserialization(Task.class).getFirst();

        assertEquals(200, response.statusCode(), "не возвращает код 200");
        assertEquals(task_0, task, "возвращает неправильный объект");
    }

    @Test
    public void ShouldReturnCode200AndTheTaskListWhenGetMethodWithId() {
        makeRequest("tasks", -1, false, null, "GET");
        sendRequest();

        assertEquals(200, response.statusCode(), "не возвращает код 200");
        assertEquals(List.of(task_0), deserialization(Task.class), "возвращает неправильный список");
    }

    @Test
    public void ShouldReturnCode404NotFoundWhenGetMethodWithWrongId() {
        makeRequest("tasks", 1, false, null, "GET");
        sendRequest();

        assertEquals(404, response.statusCode(), "не возвращает код 404");
        assertEquals("Not Found", response.body(), "возвращает неправильную ошибку");
    }

    @Test
    public void ShouldReturnCode201AndAddTaskWhenPOSTWithOutId() {
        makeRequest("tasks", -1, false, new Task("", startForTest, duration_10m), "POST");
        int mapTasksSize = manager.getListTasks().size();
        sendRequest();

        assertEquals(201, response.statusCode(), "не возвращает код 201");
        assertNotEquals(mapTasksSize, manager.getListTasks().size(), "Задача не добавляется");
    }

    @Test
    public void ShouldReturnCode201AndUpDateTaskWhenPOSTWithId() {
        makeRequest("tasks", 0, false, new Task("", startForTest, duration_10m), "POST");
        assertNotEquals(startForTest, manager.getTaskByIdentifier(0).getStartTime(), "Задачи одинаковые");

        sendRequest();
        assertEquals(201, response.statusCode(), "не возвращает код 201");
        assertEquals(startForTest, manager.getTaskByIdentifier(0).getStartTime(), "Задача не изменилась");
    }

    @Test
    public void ShouldReturnCode404WhenPOSTWithWrongId() {
        makeRequest("tasks", 1, false, new Task("", startForTest, duration_10m), "POST");
        sendRequest();

        assertEquals(404, response.statusCode(), "не возвращает код 404");
        assertEquals("Not Found", response.body(), "возвращает неправильную ошибку");
    }

    @Test
    public void ShouldReturnCode406WhenPOSTWithOutIdAddingTaskCrossingTime() {
        makeRequest("tasks", -1, false, new Task("", startTask_0, duration_10m), "POST");
        sendRequest();

        System.out.println(response.statusCode());
        System.out.println(response.body());
        assertEquals(startTask_0, manager.getTaskByIdentifier(0).getStartTime(), "разное время старта");
        assertEquals(406, response.statusCode(), "не возвращает код 406");
        assertEquals("Not Acceptable", response.body(), "возвращает неправильную ошибку");
    }

    @Test
    public void ShouldReturnCode201WhenPOSTWithIdUpDatingTaskWithTheSameTime() {
        makeRequest("tasks", 0, false, new Task("new", startForTest, duration_10m), "POST");
        assertNotEquals(startForTest, manager.getTaskByIdentifier(0).getStartTime(), "одинаковое время старта");
        assertNotEquals("new", manager.getTaskByIdentifier(0).getName(), "одинаковое имя");
        sendRequest();

        assertEquals(201, response.statusCode(), "не возвращает код 201");
        assertEquals(startForTest, manager.getTaskByIdentifier(0).getStartTime(), "разное время старта");
        assertEquals("new", manager.getTaskByIdentifier(0).getName(), "разное имя");
    }

    @Test
    public void ShouldReturnCode201WhenDeleteTaskWithId() {
        makeRequest("tasks", 0, false, null, "DELETE");
        int size = manager.getListTasks().size();
        sendRequest();

        assertNotEquals(size, manager.getListTasks().size(), "количество задач не изменилось");
    }

    @Test
    public void ShouldReturnCode200AndTheEpicSubtasksWhenGetMethodWithId() {
        makeRequest("epics", 1, true, null, "GET");
        List<Subtask> arr = manager.getEpicSubtasks(1);
        System.out.println(arr);
        sendRequest();

        assertEquals(200, response.statusCode(), "не возвращает код 200");
        assertEquals(arr, deserialization(Subtask.class), "возвращает неправильный список");
    }

    @Test
    public void ShouldReturnCode200AndHistoryWhenGetMethod() {
        makeRequest("history", -1, false, null, "GET");
        manager.getTaskByIdentifier(0);
        manager.getEpicByIdentifier(1);
        manager.getSubtaskByIdentifier(2);
        int historySize = manager.getHistory().size();
        sendRequest();

        assertEquals(200, response.statusCode(), "не возвращает код 200");
        assertEquals(historySize, deserialization(Task.class).size(), "возвращает неправильный список");
    }

    @Test
    public void ShouldReturnCode200AndPrioritizedWhenGetMethod() {
        makeRequest("prioritized", -1, false, null, "GET");
        int prioritizedSize = manager.getPrioritizedTasks().size();
        sendRequest();

        assertEquals(200, response.statusCode(), "не возвращает код 200");
        assertEquals(prioritizedSize, deserialization(Task.class).size(), "возвращает неправильный список");
    }
}