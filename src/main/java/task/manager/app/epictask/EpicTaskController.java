package task.manager.app.epictask;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import task.manager.app.subtask.Subtask;

import java.util.List;

@RestController
@RequestMapping(path = "/tasks/epic")
@AllArgsConstructor
public class EpicTaskController {

    private final EpicTaskService service;

    @PostMapping
    public EpicTask addEpic(@Valid @RequestBody EpicTask epicTask) {
        return service.addTask(epicTask);
    }

    @PatchMapping(path = "/{epicId}")
    public EpicTask updateEpic(@Valid @RequestBody EpicTask epicTask, @PathVariable Long epicId) {
        return service.updateTask(epicTask, epicId);
    }

    @DeleteMapping(path = "/{epicId}")
    public void removeById(@PathVariable Long epicId) {
        service.removeById(epicId);
    }

    @DeleteMapping
    public void removeAll() {
        service.removeAll();
    }

    @GetMapping(path = "/{epicId}")
    public EpicTask getById(@PathVariable Long epicId) {
        return service.getById(epicId);
    }

    @GetMapping
    public List<EpicTask> getAll() {
        return service.getAll();
    }

    @GetMapping("/{epicId}/subtasks")
    public List<Subtask> getEpicSubtasks(@PathVariable Long epicId) {
        return service.getEpicSubtasks(epicId);
    }
}
