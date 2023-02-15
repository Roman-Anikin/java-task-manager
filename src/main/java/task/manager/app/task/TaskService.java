package task.manager.app.task;

import java.util.List;
import java.util.Optional;

public interface TaskService<T extends Task> {

    T addTask(T task);

    T updateTask(T task, Long taskId);

    void removeById(Long taskId);

    void removeAll();

    T getById(Long taskId);

    List<T> getAll();

    T checkTask(Long taskId);

    default List<T> getHistory() {
        return null;
    }

    default void setTaskTime(T task) {
        Optional.ofNullable(task.getStartTime()).ifPresent(
                time -> task.setEndTime(task.getStartTime().plusNanos(task.getDuration().toNanoOfDay())));
    }

    default List<T> getPrioritizedTasks() {
        return null;
    }
}
