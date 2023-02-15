package task.manager.app.history;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import task.manager.app.task.Task;

@Service
@Slf4j
@AllArgsConstructor
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository repository;

    @Override
    public void add(Task task) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        if (repository.findById(task.getId()).isPresent()) {
            remove(task.getId());
        }
        repository.save(history);
        log.info("В историю просмотра задач сохранена задача {}", task);
    }

    @Override
    public void remove(Long taskId) {
        repository.deleteById(taskId);
        log.info("Из истории просмотра задач удалена задача с id {}", taskId);
    }
}
