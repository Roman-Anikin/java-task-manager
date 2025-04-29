package task.manager.app.subtask;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import task.manager.app.task.TaskService;

import java.util.List;

@RestController
@RequestMapping(path = "/tasks/subtask")
@AllArgsConstructor
public class SubtaskController {

    private final TaskService<Subtask> service;

    @PostMapping
    public Subtask addSubtask(@Valid @RequestBody Subtask subtask) {
        return service.addTask(subtask);
    }

    @PatchMapping(path = "/{subtaskId}")
    public Subtask updateSubtask(@Valid @RequestBody Subtask subtask, @PathVariable Long subtaskId) {
        return service.updateTask(subtask, subtaskId);
    }

    @DeleteMapping(path = "/{subtaskId}")
    public void removeById(@PathVariable Long subtaskId) {
        service.removeById(subtaskId);
    }

    @DeleteMapping
    public void removeAll() {
        service.removeAll();
    }

    @GetMapping(path = "/{subtaskId}")
    public Subtask getById(@PathVariable Long subtaskId) {
        return service.getById(subtaskId);
    }

    @GetMapping
    public List<Subtask> getAll() {
        return service.getAll();
    }
}
