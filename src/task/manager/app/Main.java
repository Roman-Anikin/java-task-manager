package task.manager.app;

import task.manager.app.manager.*;
import task.manager.app.model.*;
import task.manager.app.utility.Managers;

import java.util.List;
/*
Привет, Наталья!
Разобраться в последней части задания "Сделайте историю задач интерфейсом" до конца не удалось.
Надеюсь ошибок не очень много.
*/
public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        Task task2 = new Task("task2", "task2", TaskStatus.IN_PROGRESS);

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        EpicTask epicTask = new EpicTask("epic1", "epic1", TaskStatus.DONE);
        taskManager.createNewEpic(epicTask);

        Subtask subtask = new Subtask("sub1", "sub1", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getEpicById(epicTask.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getEpicById(epicTask.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getEpicById(epicTask.getId());

        List<Task> taskList = taskManager.history();
        for (Task task : taskList) {
            System.out.println(task);
        }
    }
}

