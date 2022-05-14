package task.manager.app.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.sun.net.httpserver.HttpServer;
import task.manager.app.manager.TaskManager;
import task.manager.app.model.EpicTask;
import task.manager.app.model.Subtask;
import task.manager.app.model.Task;
import task.manager.app.model.TaskStatus;
import task.manager.app.utility.Managers;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class HttpTaskServer {

    private static Gson gson = new Gson();
    private static TaskManager manager = Managers.getDefault();
    private static final int PORT = 8080;
    private HttpServer server;


    public HttpTaskServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/tasks/history", new HistoryHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/subtask", new SubtaskHandler());
    }

    public void start() {
        System.out.println("Запускаем TaskServer на порту " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Останавливаем TaskServer на порту " + PORT);
        server.stop(0);
    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] url = exchange.getRequestURI().toString().split("/");
            String res = "";
            if (method.equals("GET") && url.length == 2) {
                exchange.sendResponseHeaders(200, 0);
                res = gson.toJson(manager.getPrioritizedTasks());
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(res.getBytes());
            }
        }
    }

    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] url = exchange.getRequestURI().toString().split("/");
            String res = "";
            if (method.equals("GET") && url.length == 3) {
                exchange.sendResponseHeaders(200, 0);
                res = gson.toJson(manager.history());
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(res.getBytes());
            }
        }
    }

    static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] url = exchange.getRequestURI().toString().split("/");
            String res = "";
            switch (method) {
                case "GET":
                    if (url.length == 3) {
                        exchange.sendResponseHeaders(200, 0);
                        res = gson.toJson(manager.getAllTaskList());
                    } else if (url.length == 4 && url[3].startsWith("?id=")) {
                        Long id = Long.valueOf(url[3].split("=")[1]);
                        String json = gson.toJson(manager.getTaskById(id));
                        if (!json.equals("null")) {
                            exchange.sendResponseHeaders(200, 0);
                            res = json;
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    if (url.length == 3) {
                        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Task.class);
                        if (task != null && task.getId() == null) {
                            manager.createNewTask(task);
                            exchange.sendResponseHeaders(201, 0);
                        } else if (task != null) {
                            manager.updateTask(task);
                            exchange.sendResponseHeaders(201, 0);
                        } else {
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "DELETE":
                    if (url.length == 3) {
                        exchange.sendResponseHeaders(200, 0);
                        manager.deleteAllTasks();
                    } else if (url.length == 4 && url[3].startsWith("?id=")) {
                        Long id = Long.valueOf(url[3].split("=")[1]);
                        manager.deleteTaskById(id);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(res.getBytes());
            }
        }
    }

    static class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] url = exchange.getRequestURI().toString().split("/");
            String res = "";
            switch (method) {
                case "GET":
                    if (url.length == 3) {
                        exchange.sendResponseHeaders(200, 0);
                        res = gson.toJson(manager.getAllEpicList());
                    } else if (url.length == 4 && url[3].startsWith("?id=")) {
                        Long id = Long.valueOf(url[3].split("=")[1]);
                        String json = gson.toJson(manager.getEpicById(id));
                        if (!json.equals("null")) {
                            exchange.sendResponseHeaders(200, 0);
                            res = json;
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    if (url.length == 3) {
                        EpicTask epicTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                EpicTask.class);
                        if (epicTask != null && epicTask.getId() == null) {
                            manager.createNewEpic(epicTask);
                            exchange.sendResponseHeaders(201, 0);
                        } else {
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "DELETE":
                    if (url.length == 3) {
                        exchange.sendResponseHeaders(200, 0);
                        manager.deleteAllEpics();
                    } else if (url.length == 4 && url[3].startsWith("?id=")) {
                        Long id = Long.valueOf(url[3].split("=")[1]);
                        manager.deleteEpicById(id);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(res.getBytes());
            }
        }
    }

    static class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] url = exchange.getRequestURI().toString().split("/");
            String res = "";
            switch (method) {
                case "GET":
                    if (url.length == 3) {
                        exchange.sendResponseHeaders(200, 0);
                        res = gson.toJson(manager.getAllSubtaskList());
                    } else if (url.length == 4 && url[3].startsWith("?id=")) {
                        Long id = Long.valueOf(url[3].split("=")[1]);
                        String json = gson.toJson(manager.getSubtaskById(id));
                        if (!json.equals("null")) {
                            exchange.sendResponseHeaders(200, 0);
                            res = json;
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else if (url.length == 5 && url[4].startsWith("?id=")) {
                        Long id = Long.valueOf(url[4].split("=")[1]);
                        EpicTask epicTask = new EpicTask(null, null, TaskStatus.NEW);
                        List<EpicTask> epics = manager.getAllEpicList();
                        for (EpicTask e : epics) {
                            if (e.getId().equals(id)) {
                                epicTask = e;
                                break;
                            }
                        }
                        if (epicTask.getId() != null) {
                            String json = gson.toJson(manager.getSubtaskListByEpic(epicTask));
                            exchange.sendResponseHeaders(200, 0);
                            res = json;
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    if (url.length == 3) {
                        Subtask subtask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Subtask.class);
                        if (subtask != null && subtask.getId() == null) {
                            manager.createNewSubtask(subtask);
                            exchange.sendResponseHeaders(201, 0);
                        } else if (subtask != null) {
                            manager.updateSubtask(subtask);
                            exchange.sendResponseHeaders(201, 0);
                        } else {
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "DELETE":
                    if (url.length == 3) {
                        exchange.sendResponseHeaders(200, 0);
                        manager.deleteAllSubtasks();
                    } else if (url.length == 4 && url[3].startsWith("?id=")) {
                        Long id = Long.valueOf(url[3].split("=")[1]);
                        manager.deleteSubtaskById(id);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(res.getBytes());
            }
        }
    }
}
