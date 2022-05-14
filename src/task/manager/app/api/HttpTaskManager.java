package task.manager.app.api;

import com.google.gson.*;
import task.manager.app.data.FileBackedTasksManager;

import java.io.IOException;
import java.net.URL;

public class HttpTaskManager extends FileBackedTasksManager {

    private KVTaskClient taskClient;

    public HttpTaskManager(URL url) throws IOException, InterruptedException {
        super(null);
        taskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        Gson gson = new Gson();
        HttpTaskManager manager = this;
        try {
            taskClient.put("task", gson.toJson(manager));
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    public KVTaskClient getTaskClient() {
        return taskClient;
    }

}
