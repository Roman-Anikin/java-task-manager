package task.manager.app.task;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService<Task> service;

    @PostMapping(path = "/task")
    public Task addTask(@Valid @RequestBody Task task) {
        return service.addTask(task);
    }

    @PatchMapping(path = "/task/{taskId}")
    public Task updateTask(@Valid @RequestBody Task task, @PathVariable Long taskId) {
        return service.updateTask(task, taskId);
    }

    @DeleteMapping(path = "/task/{taskId}")
    public void removeById(@PathVariable Long taskId) {
        service.removeById(taskId);
    }

    @DeleteMapping(path = "/task")
    public void removeAll() {
        service.removeAll();
    }

    @GetMapping(path = "/task/{taskId}")
    public Task getById(@PathVariable Long taskId) {
        return service.getById(taskId);
    }

    @GetMapping(path = "/task")
    public List<Task> getAll() {
        return service.getAll();
    }

    @GetMapping(path = "/history")
    public List<Task> getHistory() {
        return service.getHistory();
    }

    @GetMapping
    public List<Task> getPrioritizedTasks() {
        return service.getPrioritizedTasks();
    }
}
