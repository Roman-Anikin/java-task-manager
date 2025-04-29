package task.manager.app.subtask;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.manager.app.epictask.EpicTask;
import task.manager.app.epictask.EpicTaskService;
import task.manager.app.exception.ObjectNotFoundException;
import task.manager.app.history.TaskHistoryService;
import task.manager.app.task.TaskService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class SubtaskServiceImpl implements TaskService<Subtask> {

    private final EpicTaskService epicService;
    private final TaskHistoryService historyService;
    private final SubtaskRepository repository;

    @Override
    @Transactional
    public Subtask addTask(Subtask subtask) {
        EpicTask epic = epicService.checkTask(subtask.getEpicTask().getId());
        setTaskTime(subtask);
        Subtask savedSubtask = repository.save(subtask);
        epic.getSubtasks().add(savedSubtask);
        savedSubtask.setEpicTask(epic);
        log.info("Добавлена подзадача {}", savedSubtask);
        epicService.setEpicStatus(epic);
        epicService.setTaskTime(epic);
        return savedSubtask;
    }

    @Override
    public Subtask updateTask(Subtask subtask, Long subtaskId) {
        EpicTask epic = epicService.checkTask(subtask.getEpicTask().getId());
        Subtask newSubtask = checkTask(subtaskId);
        Optional.ofNullable(subtask.getName()).ifPresent(newSubtask::setName);
        Optional.ofNullable(subtask.getDescription()).ifPresent(newSubtask::setDescription);
        Optional.ofNullable(subtask.getStatus()).ifPresent(newSubtask::setStatus);
        Optional.ofNullable(subtask.getDuration()).ifPresent(newSubtask::setDuration);
        Optional.ofNullable(subtask.getStartTime()).ifPresent(newSubtask::setStartTime);
        setTaskTime(newSubtask);
        repository.save(newSubtask);
        log.info("Обновлена подзадача {}", newSubtask);
        epicService.setEpicStatus(epic);
        epicService.setTaskTime(epic);
        return newSubtask;
    }

    @Override
    public void removeById(Long subtaskId) {
        Subtask subtask = checkTask(subtaskId);
        EpicTask epic = epicService.checkTask(subtask.getEpicTask().getId());
        repository.delete(subtask);
        log.info("Удалена подзадача {}", subtask);
        epicService.setEpicStatus(epic);
        epicService.setTaskTime(epic);
    }

    @Override
    public void removeAll() {
        repository.deleteAllInBatch();
        log.info("Все подзадачи удалены");
        epicService.getAll().forEach(epicTask -> {
            epicService.setEpicStatus(epicTask);
            epicService.setTaskTime(epicTask);
        });
    }

    @Override
    public Subtask getById(Long subtaskId) {
        Subtask subtask = checkTask(subtaskId);
        log.info("Получена подзадача {}", subtask);
        historyService.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAll() {
        List<Subtask> subtasks = repository.findAll();
        log.info("Получен список подзадач {}", subtasks);
        return subtasks;
    }

    @Override
    public Subtask checkTask(Long subtaskId) {
        return repository.findById(subtaskId).orElseThrow(() ->
                new ObjectNotFoundException("Подзадача с id " + subtaskId + " не найдена"));
    }
}
