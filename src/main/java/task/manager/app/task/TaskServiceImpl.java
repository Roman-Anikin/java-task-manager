package task.manager.app.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import task.manager.app.epictask.EpicTaskService;
import task.manager.app.exception.ObjectNotFoundException;
import task.manager.app.history.TaskHistoryService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TaskServiceImpl implements TaskService<Task> {

    private final EpicTaskService epicService;
    private final TaskHistoryService historyService;
    private final TaskRepository repository;

    @Override
    public Task addTask(Task task) {
        setTaskTime(task);
        Task savedTask = repository.save(task);
        log.info("Добавлена задача {}", savedTask);
        return savedTask;
    }

    @Override
    public Task updateTask(Task task, Long taskId) {
        Task newTask = checkTask(taskId);
        Optional.ofNullable(task.getName()).ifPresent(newTask::setName);
        Optional.ofNullable(task.getDescription()).ifPresent(newTask::setDescription);
        Optional.ofNullable(task.getStatus()).ifPresent(newTask::setStatus);
        Optional.ofNullable(task.getDuration()).ifPresent(newTask::setDuration);
        Optional.ofNullable(task.getStartTime()).ifPresent(newTask::setStartTime);
        setTaskTime(newTask);
        repository.save(newTask);
        log.info("Обновлена задача {}", newTask);
        return newTask;
    }

    @Override
    public void removeById(Long taskId) {
        Task task = checkTask(taskId);
        repository.delete(task);
        log.info("Удалена задача {}", task);
    }

    @Override
    public void removeAll() {
        repository.removeAll();
        log.info("Все задачи удалены");
    }

    @Override
    public Task getById(Long taskId) {
        Task task = checkTask(taskId);
        log.info("Получена задача {}", task);
        historyService.add(task);
        return task;
    }

    @Override
    public List<Task> getAll() {
        List<Task> tasks = repository.findAll();
        log.info("Получен список задач {}", tasks);
        return tasks;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = repository.getHistory();
        log.info("Получена история просмотров задач {}", history);
        return history;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        epicService.getAll();
        List<Task> tasks = repository.findByOrderByStartTime();
        log.info("Получен список задач {}", tasks);
        return tasks;
    }

    @Override
    public Task checkTask(Long taskId) {
        return repository.findById(taskId).orElseThrow(() ->
                new ObjectNotFoundException("Задание с id " + taskId + " не найдено"));
    }
}
