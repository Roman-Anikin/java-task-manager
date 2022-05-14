package task.manager.app.tests;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import task.manager.app.api.HttpTaskManager;
import task.manager.app.api.HttpTaskServer;
import task.manager.app.api.KVServer;
import task.manager.app.model.EpicTask;
import task.manager.app.model.Subtask;
import task.manager.app.model.Task;
import task.manager.app.model.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HttpTaskManagerTest {

    private static final String URL = "http://localhost:8080";
    private static Gson gson = new Gson();
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void createTask() throws IOException, InterruptedException {
        Task task = new Task("test task", "test desc", TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);
        HttpResponse<String> response = postRequest(URL + "/tasks/task", json);
        task.setId(0L);
        assertEquals(201, response.statusCode(), "Invalid code");

        Task task2 = new Task("test task2", "test desc2", TaskStatus.NEW);
        json = gson.toJson(task2);
        response = postRequest(URL + "/tasks/task", json);
        task2.setId(1L);
        assertEquals(201, response.statusCode(), "Invalid code");

        response = getRequest(URL + "/tasks/task/?id=0");
        assertEquals(200, response.statusCode());

        String res = response.body();
        Task task3 = gson.fromJson(res, Task.class);
        assertEquals(task, task3);

        response = getRequest(URL + "/tasks/task");
        assertEquals(200, response.statusCode(), "Invalid code");

        Type list = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> taskList = gson.fromJson(response.body(), list);

        assertEquals(2, taskList.size(), "Invalid size");
        assertEquals(task, taskList.get(0), "Invalid task");
        assertEquals(task2, taskList.get(1), "Invalid task");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task = new Task("test task", "test desc", TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);
        postRequest(URL + "/tasks/task", json);
        task.setId(0L);

        Task task2 = new Task("test task2", "test desc2", TaskStatus.NEW);
        task2.setId(0L);
        json = gson.toJson(task2);
        HttpResponse<String> response = postRequest(URL + "/tasks/task", json);

        assertEquals(201, response.statusCode(), "Invalid code");

        response = getRequest(URL + "/tasks/task/?id=0");
        assertEquals(200, response.statusCode());

        String res = response.body();
        Task task3 = gson.fromJson(res, Task.class);

        assertEquals(task2, task3, "Invalid tasks");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("test task", "test desc", TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);
        postRequest(URL + "/tasks/task", json);
        task.setId(0L);

        HttpResponse<String> response = deleteRequest(URL + "/tasks/task/?id=0");
        assertEquals(200, response.statusCode());

        response = getRequest(URL + "/tasks/task/?id=0");
        assertEquals(404, response.statusCode());
        assertEquals("", response.body());

        response = postRequest(URL + "/tasks/task", json);
        assertEquals(201, response.statusCode());

        response = deleteRequest(URL + "/tasks/task");
        assertEquals(200, response.statusCode());

        response = getRequest(URL + "/tasks/task");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void createEpic() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        String json = gson.toJson(epicTask);
        HttpResponse<String> response = postRequest(URL + "/tasks/epic", json);
        epicTask.setId(0L);
        assertEquals(201, response.statusCode(), "Invalid code");

        EpicTask epicTask2 = new EpicTask("test epic2", "epic desc2", TaskStatus.NEW);
        json = gson.toJson(epicTask2);
        response = postRequest(URL + "/tasks/epic", json);
        epicTask2.setId(1L);
        assertEquals(201, response.statusCode(), "Invalid code");

        response = getRequest(URL + "/tasks/epic/?id=0");
        assertEquals(200, response.statusCode());

        String res = response.body();
        EpicTask epicTask3 = gson.fromJson(res, EpicTask.class);
        assertEquals(epicTask, epicTask3);

        response = getRequest(URL + "/tasks/epic");
        assertEquals(200, response.statusCode(), "Invalid code");

        Type list = new TypeToken<ArrayList<EpicTask>>() {
        }.getType();
        List<EpicTask> epicTaskList = gson.fromJson(response.body(), list);

        assertEquals(2, epicTaskList.size(), "Invalid size");
        assertEquals(epicTask, epicTaskList.get(0), "Invalid epic");
        assertEquals(epicTask2, epicTaskList.get(1), "Invalid epic");

    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        String json = gson.toJson(epicTask);
        postRequest(URL + "/tasks/epic", json);
        epicTask.setId(0L);

        HttpResponse<String> response = deleteRequest(URL + "/tasks/epic/?id=0");
        assertEquals(200, response.statusCode());

        response = getRequest(URL + "/tasks/epic/?id=0");
        assertEquals(404, response.statusCode());
        assertEquals("", response.body());

        response = postRequest(URL + "/tasks/epic", json);
        assertEquals(201, response.statusCode());

        response = deleteRequest(URL + "/tasks/epic");
        assertEquals(200, response.statusCode());

        response = getRequest(URL + "/tasks/epic");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void createSubtask() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        String json = gson.toJson(epicTask);
        postRequest(URL + "/tasks/epic", json);
        epicTask.setId(0L);

        Subtask subtask = new Subtask("subtask name", "subtask desc", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        json = gson.toJson(subtask);
        HttpResponse<String> response = postRequest(URL + "/tasks/subtask", json);
        subtask.setId(1L);

        assertEquals(201, response.statusCode());

        response = getRequest(URL + "/tasks/subtask/?id=1");
        assertEquals(200, response.statusCode());

        String res = response.body();
        Subtask subtask2 = gson.fromJson(res, Subtask.class);
        assertEquals(subtask, subtask2);

        Subtask subtask3 = new Subtask("subtask3 name", "subtask3 desc", TaskStatus.NEW);
        subtask3.setEpicId(epicTask.getId());
        json = gson.toJson(subtask3);
        response = postRequest(URL + "/tasks/subtask", json);
        subtask3.setId(2L);

        assertEquals(201, response.statusCode());

        response = getRequest(URL + "/tasks/subtask");
        assertEquals(200, response.statusCode(), "Invalid code");

        Type list = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), list);

        assertEquals(2, subtasks.size(), "Invalid size");
        assertEquals(subtask, subtasks.get(0), "Invalid epic");
        assertEquals(subtask3, subtasks.get(1), "Invalid epic");

        response = getRequest(URL + "/tasks/subtask/epic/?id=0");
        assertEquals(200, response.statusCode(), "Invalid code");

        Type list2 = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> epicSubtasks = gson.fromJson(response.body(), list2);
        assertEquals(2, epicSubtasks.size(), "Invalid size");

    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        String json = gson.toJson(epicTask);
        postRequest(URL + "/tasks/epic", json);
        epicTask.setId(0L);

        Subtask subtask = new Subtask("subtask name", "subtask desc", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        json = gson.toJson(subtask);
        postRequest(URL + "/tasks/subtask", json);
        subtask.setId(1L);

        Subtask subtask2 = new Subtask("subtask2 name", "subtask2 desc", TaskStatus.NEW);
        subtask2.setEpicId(epicTask.getId());
        subtask2.setId(1L);

        json = gson.toJson(subtask2);
        HttpResponse<String> response = postRequest(URL + "/tasks/subtask", json);

        assertEquals(201, response.statusCode(), "Invalid code");

        response = getRequest(URL + "/tasks/subtask/?id=1");
        assertEquals(200, response.statusCode());

        String res = response.body();
        Subtask subtask3 = gson.fromJson(res, Subtask.class);

        assertEquals(subtask2, subtask3, "Invalid tasks");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        String json = gson.toJson(epicTask);
        postRequest(URL + "/tasks/epic", json);
        epicTask.setId(0L);

        Subtask subtask = new Subtask("subtask name", "subtask desc", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        json = gson.toJson(subtask);
        postRequest(URL + "/tasks/subtask", json);
        subtask.setId(1L);

        HttpResponse<String> response = deleteRequest(URL + "/tasks/subtask/?id=1");
        assertEquals(200, response.statusCode());

        response = getRequest(URL + "/tasks/subtask/?id=1");
        assertEquals(404, response.statusCode());
        assertEquals("", response.body());

        response = postRequest(URL + "/tasks/subtask", json);
        assertEquals(201, response.statusCode());

        response = deleteRequest(URL + "/tasks/subtask");
        assertEquals(200, response.statusCode());

        response = getRequest(URL + "/tasks/subtask");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void history() throws IOException, InterruptedException {
        Task task = new Task("test task", "test desc", TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);
        postRequest(URL + "/tasks/task", json);
        task.setId(0L);

        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        json = gson.toJson(epicTask);
        postRequest(URL + "/tasks/epic", json);
        epicTask.setId(1L);

        Subtask subtask = new Subtask("subtask name", "subtask desc", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        json = gson.toJson(subtask);
        postRequest(URL + "/tasks/subtask", json);
        subtask.setId(2L);

        getRequest(URL + "/tasks/task/?id=0");
        getRequest(URL + "/tasks/epic/?id=1");
        getRequest(URL + "/tasks/subtask/?id=2");

        HttpResponse<String> response = getRequest(URL + "/tasks/history");
        assertEquals(200, response.statusCode(), "Invalid code");

        Type list = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), list);
        assertEquals(3, tasks.size(), "Invalid size");

        response = getRequest(URL + "/tasks");
        assertEquals(200, response.statusCode(), "Invalid code");

        list = new TypeToken<TreeSet<Task>>() {}.getType();
        TreeSet<Task> tasks2 = gson.fromJson(response.body(), list);
        assertEquals(2, tasks2.size(), "Invalid size");
    }


    @Test
    public void loadKVServer() throws IOException, InterruptedException {
        Task task = new Task("test task", "test desc", TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);
        postRequest(URL + "/tasks/task", json);
        task.setId(0L);

        EpicTask epicTask = new EpicTask("test epic", "epic desc", TaskStatus.NEW);
        json = gson.toJson(epicTask);
        postRequest(URL + "/tasks/epic", json);
        epicTask.setId(1L);

        Subtask subtask = new Subtask("subtask name", "subtask desc", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        json = gson.toJson(subtask);
        postRequest(URL + "/tasks/subtask", json);
        subtask.setId(2L);

        String load = new HttpTaskManager(new URL("http://localhost:8078")).getTaskClient().load("task");
        HttpTaskManager taskManager = gson.fromJson(load, HttpTaskManager.class);

        assertEquals(1, taskManager.getAllTaskList().size(), "Invalid size");
        assertEquals(1, taskManager.getAllSubtaskList().size(), "Invalid size");
        assertEquals(1, taskManager.getAllTaskList().size(), "Invalid size");

    }


    private HttpResponse<String> getRequest(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        return httpClient.send(request, handler);
    }

    private HttpResponse<String> postRequest(String url, String json) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .POST(body)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        return httpClient.send(request, handler);
    }

    private HttpResponse<String> deleteRequest(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        return httpClient.send(request, handler);
    }
}
