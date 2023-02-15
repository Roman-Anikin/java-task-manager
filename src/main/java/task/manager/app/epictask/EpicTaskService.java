package task.manager.app.epictask;

import task.manager.app.subtask.Subtask;
import task.manager.app.task.TaskService;

import java.util.List;

public interface EpicTaskService extends TaskService<EpicTask> {

    List<Subtask> getEpicSubtasks(Long epicId);

    void setEpicStatus(EpicTask epicTask);

}
