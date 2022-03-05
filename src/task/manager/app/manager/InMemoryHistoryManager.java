package task.manager.app.manager;

import task.manager.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static List<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}