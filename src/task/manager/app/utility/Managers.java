package task.manager.app.utility;

import task.manager.app.api.HttpTaskManager;
import task.manager.app.manager.*;

import java.io.IOException;
import java.net.URL;

public class Managers {

    public static TaskManager getDefault() {
        try {
            return new HttpTaskManager(new URL("http://localhost:8078"));
        } catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
