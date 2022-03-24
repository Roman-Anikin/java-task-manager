package task.manager.app;

import task.manager.app.manager.*;
import task.manager.app.model.*;
import task.manager.app.utility.Managers;

import java.util.List;

/* Здарвствуйте, Александр.
Спасибо за подробные комментарии. Убрал статичность, протестировал. Добавил переменную HistoryManager
в InMemoryTaskManager.
*/

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        Task task2 = new Task("task2", "task2", TaskStatus.IN_PROGRESS);

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        EpicTask epicTask1 = new EpicTask("epic1", "epic1", TaskStatus.DONE);
        taskManager.createNewEpic(epicTask1);

        EpicTask epicTask2 = new EpicTask("epic2", "epic2", TaskStatus.DONE);
        taskManager.createNewEpic(epicTask2);

        Subtask subtask1 = new Subtask("sub1", "sub1", TaskStatus.IN_PROGRESS);
        subtask1.setEpicId(epicTask2.getId());
        taskManager.createNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("sub2", "sub2", TaskStatus.IN_PROGRESS);
        subtask2.setEpicId(epicTask2.getId());
        taskManager.createNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("sub3", "sub3", TaskStatus.IN_PROGRESS);
        subtask3.setEpicId(epicTask2.getId());
        taskManager.createNewSubtask(subtask3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epicTask2.getId());
        taskManager.getEpicById(epicTask2.getId());
        taskManager.getEpicById(epicTask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epicTask1.getId());
        taskManager.deleteSubtaskById(subtask1.getId());

        List<Task> taskList = taskManager.history();
        for (Task task : taskList) {
            System.out.println(task);
        }
        System.out.println();

        TaskManager taskManager1 = Managers.getDefault();

        Task task3 = new Task("task3", "task3", TaskStatus.NEW);
        Task task4 = new Task("task4", "task4", TaskStatus.IN_PROGRESS);

        taskManager1.createNewTask(task3);
        taskManager1.createNewTask(task4);

        taskManager1.getTaskById(task3.getId());
        taskManager1.getTaskById(task4.getId());

        List<Task> taskList1 = taskManager1.history();
        for (Task task : taskList1) {
            System.out.println(task);
        }
    }
}