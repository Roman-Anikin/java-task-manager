package task.manager.app.history;

import task.manager.app.task.Task;

public interface TaskHistoryService {

    void add(Task task);

    void remove(Long taskId);

}
