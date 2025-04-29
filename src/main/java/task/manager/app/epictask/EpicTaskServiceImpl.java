package task.manager.app.epictask;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.manager.app.exception.ObjectNotFoundException;
import task.manager.app.history.TaskHistoryService;
import task.manager.app.subtask.Subtask;
import task.manager.app.task.TaskStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class EpicTaskServiceImpl implements EpicTaskService {

    private final TaskHistoryService historyService;
    private final EpicTaskRepository repository;

    @Override
    public EpicTask addTask(EpicTask epicTask) {
        epicTask.setStatus(TaskStatus.NEW);
        setTaskTime(epicTask);
        EpicTask savedEpic = repository.save(epicTask);
        log.info("Добавлен эпик {}", savedEpic);
        return savedEpic;
    }

    @Override
    public EpicTask updateTask(EpicTask epicTask, Long epicId) {
        EpicTask newEpic = checkTask(epicId);
        Optional.ofNullable(epicTask.getName()).ifPresent(newEpic::setName);
        Optional.ofNullable(epicTask.getDescription()).ifPresent(newEpic::setDescription);
        setEpicStatus(newEpic);
        repository.save(newEpic);
        log.info("Обновлен эпик {}", newEpic);
        return newEpic;
    }

    @Override
    public void removeById(Long epicId) {
        EpicTask epic = checkTask(epicId);
        repository.delete(epic);
        log.info("Удален эпик {}", epic);
    }

    @Override
    public void removeAll() {
        repository.deleteAllInBatch();
        log.info("Все эпики удалены");
    }

    @Override
    public EpicTask getById(Long epicId) {
        EpicTask epic = checkTask(epicId);
        log.info("Получен эпик {}", epic);
        historyService.add(epic);
        return epic;
    }

    @Override
    public List<EpicTask> getAll() {
        List<EpicTask> epics = repository.findAll();
        log.info("Получен список эпиков {}", epics);
        return epics;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Long epicId) {
        EpicTask epic = checkTask(epicId);
        log.info("Получен список подзадач эпика с id {}: {}", epicId, epic.getSubtasks());
        return epic.getSubtasks();
    }

    @Override
    @Transactional
    public void setEpicStatus(EpicTask epicTask) {
        List<Subtask> subtasks = epicTask.getSubtasks();
        if (subtasks.isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }
        int newCount = 0;
        int doneCount = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                epicTask.setStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (subtask.getStatus().equals(TaskStatus.NEW)) {
                newCount++;
            } else {
                doneCount++;
            }
        }
        if (newCount == subtasks.size()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        } else if (doneCount == subtasks.size()) {
            epicTask.setStatus(TaskStatus.DONE);
            return;
        }
        epicTask.setStatus(TaskStatus.IN_PROGRESS);
    }

    @Override
    @Transactional
    public void setTaskTime(EpicTask epicTask) {
        List<Subtask> subtasks = epicTask.getSubtasks();
        epicTask.setDuration(LocalTime.of(0, 0));
        if (subtasks.isEmpty()) {
            epicTask.setStartTime(null);
            epicTask.setEndTime(null);
            return;
        }
        boolean isFilled = false;
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() == null) {
                continue;
            }
            if (!isFilled) {
                epicTask.setDuration(epicTask.getDuration()
                        .plusMinutes(subtask.getDuration().getMinute())
                        .plusHours(subtask.getDuration().getHour()));
                epicTask.setStartTime(subtask.getStartTime());
                epicTask.setEndTime(subtask.getEndTime());
                isFilled = true;
                continue;
            }
            if ((!epicTask.getStartTime().equals(subtask.getStartTime())
                    && epicTask.getDuration().toNanoOfDay() < subtask.getDuration().toNanoOfDay())
                    || (!epicTask.getStartTime().equals(subtask.getStartTime())
                    && !epicTask.getEndTime().equals(subtask.getEndTime()))) {
                epicTask.setDuration(epicTask.getDuration()
                        .plusMinutes(subtask.getDuration().getMinute())
                        .plusHours(subtask.getDuration().getHour()));
            }
            if (epicTask.getStartTime().equals(subtask.getStartTime())
                    && epicTask.getDuration().toNanoOfDay() < subtask.getDuration().toNanoOfDay()) {
                epicTask.setDuration(subtask.getDuration());
            }
            if (epicTask.getStartTime() == null) {
                epicTask.setStartTime(subtask.getStartTime());
                epicTask.setEndTime(subtask.getEndTime());
            }
            if (epicTask.getStartTime().isAfter(subtask.getStartTime())) {
                epicTask.setStartTime(subtask.getStartTime());
            }
            if (epicTask.getEndTime().isBefore(subtask.getEndTime())) {
                epicTask.setEndTime(subtask.getEndTime());
            }
        }
    }

    @Override
    public EpicTask checkTask(Long epicId) {
        return repository.findById(epicId).orElseThrow(() ->
                new ObjectNotFoundException("Эпик с id " + epicId + " не найден"));
    }
}
