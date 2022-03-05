package task.manager.app.manager;

import task.manager.app.model.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTaskList();

    ArrayList<EpicTask> getAllEpicList();

    ArrayList<Subtask> getAllSubtaskList();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(Long id);

    EpicTask getEpicById(Long id);

    Subtask getSubtaskById(Long id);

    void createNewTask(Task task);

    void createNewEpic(EpicTask epic);

    void createNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(Long id);

    void deleteEpicById(Long id);

    void deleteSubtaskById(Long id);

    ArrayList<Subtask> getSubtaskListByEpic(EpicTask epic);

    List<Task> history();
}