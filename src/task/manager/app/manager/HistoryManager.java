package task.manager.app.manager;

import task.manager.app.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(Long id);

    List<Task> getHistory();

}
