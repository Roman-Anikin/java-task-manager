package task.manager.app.tests;

import org.junit.jupiter.api.Test;
import task.manager.app.manager.InMemoryTaskManager;
import task.manager.app.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager = new InMemoryTaskManager();

    // Тест создания задачи
    @Test
    public void createTask() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        assertEquals("Test task", task.getName(), "Name not equal");
        assertEquals("test disc", task.getDescription(), "Disc not equal");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Status not equal");

        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus(), "Status not equal");
    }

    // Тест добавления задачи
    @Test
    public void addTask() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        taskManager.createNewTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "task not found");
        assertEquals(task, savedTask, "tasks not equal");

        List<Task> taskList = taskManager.getAllTaskList();
        assertNotNull(taskList, "task not found");

        assertEquals(1, taskList.size(), "Invalid number of tasks");
        assertEquals(task, taskList.get(0), "tasks not equal");
        assertEquals(task, taskManager.getPrioritizedTasks().get(0), "Tasks not equal");
    }

    // Тест обновления задачи
    @Test
    public void updateTask() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        taskManager.createNewTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, savedTask, "tasks not equal");

        Task task2 = new Task("Test task2", "test disc2", TaskStatus.IN_PROGRESS);
        task2.setId(task.getId());
        taskManager.updateTask(task2);
        assertEquals(task2, taskManager.getAllTaskList().get(0), "Tasks not equal");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Invalid size");
        assertEquals(task2, taskManager.getPrioritizedTasks().get(0), "Tasks not equal");
    }

    // Тест удаления задачи
    @Test
    public void deleteTask() {
        // удаление задачи по id
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2022, 1, 1, 4, 0));
        task.setDuration(120);
        taskManager.createNewTask(task);

        taskManager.deleteTaskById(task.getId());
        List<Task> taskList = taskManager.getAllTaskList();
        assertEquals(0, taskList.size(), "Invalid number of tasks");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Invalid size");

        // удаление всех задач
        taskManager.createNewTask(task);

        taskManager.deleteAllTasks();
        taskList = taskManager.getAllTaskList();
        assertEquals(0, taskList.size(), "Invalid number of tasks");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Invalid size");
    }

    // Тест добавления эпика
    @Test
    public void addEpic() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);
        assertEquals(0, epicTask.getEpicSubtasks().size(), "Invalid number of subtasks");
        assertEquals(TaskStatus.NEW, epicTask.getStatus(), "Wrong status");

        EpicTask savedEpic = taskManager.getEpicById(epicTask.getId());

        assertNotNull(savedEpic, "epic not found");
        assertEquals(epicTask, savedEpic, "epics not equal");

        List<EpicTask> epicList = taskManager.getAllEpicList();
        assertNotNull(epicList, "epic not found");

        assertEquals(1, epicList.size(), "Invalid number of epics");
        assertEquals(epicTask, epicList.get(0), "epics not equal");
    }

    // Тест удаления эпика
    @Test
    public void deleteEpic() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);
        Subtask subtask = new Subtask("Test subtask", "subtask disc", TaskStatus.NEW);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);
        // удаление по id
        taskManager.deleteEpicById(epicTask.getId());
        List<EpicTask> epicList = taskManager.getAllEpicList();
        assertEquals(0, epicTask.getEpicSubtasks().size(), "Invalid number of subtasks");
        assertEquals(0, epicList.size(), "Invalid number of epics");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Invalid size");

        taskManager.createNewEpic(epicTask);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);
        // удаление всех эпиков
        taskManager.deleteAllEpics();
        List<Subtask> subtaskList = taskManager.getAllSubtaskList();
        epicList = taskManager.getAllEpicList();
        assertEquals(0, epicTask.getEpicSubtasks().size(), "Invalid number of subtasks");
        assertEquals(0, epicList.size(), "Invalid number of epics");
        assertEquals(0, subtaskList.size(), "Invalid number of subtasks");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Invalid size");
    }

    // Тест добавления подзадачи
    @Test
    public void addSubtask() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);

        Subtask subtask = new Subtask("Test subtask", "subtask disc", TaskStatus.NEW);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);

        Subtask savedSub = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(savedSub, "Subtask not found");
        assertEquals(subtask, savedSub, "Subtasks not equal");

        List<Subtask> subtaskList = taskManager.getAllSubtaskList();
        assertNotNull(subtaskList, "Subtask not found");

        assertEquals(1, subtaskList.size(), "Invalid number of tasks");
        assertEquals(subtask, subtaskList.get(0), "Subtasks not equal");
        assertEquals(subtask, taskManager.getSubtaskListByEpic(epicTask).first(), "Subtasks not equal");
        assertEquals(subtask, epicTask.getEpicSubtasks().first(), "Invalid subtask");
        assertEquals(subtask, taskManager.getPrioritizedTasks().get(0), "Invalid subtask");

    }

    // Тест обновления подзадачи
    @Test
    public void updateSubtask() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);

        Subtask subtask = new Subtask("Test subtask", "subtask disc", TaskStatus.NEW);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);

        Subtask subtask1 = new Subtask("Test subtask1", "subtask docs", TaskStatus.IN_PROGRESS);
        subtask1.setId(subtask.getId());
        subtask1.setEpicId(epicTask.getId());
        taskManager.updateSubtask(subtask1);

        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()), "Subtasks not equal");
        assertEquals(1, epicTask.getEpicSubtasks().size(), "Invalid size");
        assertEquals(1, taskManager.getAllSubtaskList().size(), "Invalid size");
        assertEquals(subtask1, epicTask.getEpicSubtasks().first(), "Subtasks not equal");
        assertEquals(subtask1, taskManager.getPrioritizedTasks().get(0), "Invalid subtask");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Invalid size");

    }

    // Тест удаления подзадачи
    @Test
    public void deleteSubtask() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);

        Subtask subtask = new Subtask("Test subtask", "subtask disc", TaskStatus.NEW);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());
        assertEquals(0, taskManager.getAllSubtaskList().size(), "Invalid number of subtasks");
        assertEquals(0, epicTask.getEpicSubtasks().size(), "Subtask found");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Invalid size");

        taskManager.createNewSubtask(subtask);

        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtaskList().size(), "Invalid number of subtasks");
        assertEquals(0, epicTask.getEpicSubtasks().size(), "Invalid number of subtasks");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Invalid size");
    }

    // Тест рассчета статуса эпика
    @Test
    public void calculateEpicStatus() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);
        assertEquals(TaskStatus.NEW, epicTask.getStatus(), "Wrong status");
        // 2 подзадачи NEW
        Subtask subtask1 = new Subtask("Test subtask1", "subtask1 disc", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Test subtask2", "subtask2 disc", TaskStatus.NEW);
        subtask1.setEpicId(epicTask.getId());
        subtask2.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epicTask.getStatus(), "Wrong status");
        // 2 подзадачи DONE
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epicTask.getStatus(), "Wrong status");
        // 2 подзадачи NEW DONE
        subtask1.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus(), "Wrong status");
        // 2 подзадачи IN_PROGRESS
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus(), "Wrong status");
    }

    // Тест по рассчету продолжительности
    @Test
    public void taskDuration() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2022, 4, 26, 12, 0));
        assertEquals(LocalDateTime.of(2022, 4, 26, 12, 0), task.getStartTime(),
                "Date or time not equals");

        task.setDuration(600);
        assertEquals(600, task.getDuration());
        assertEquals(LocalDateTime.of(2022, 4, 26, 22, 0), task.getEndTime(),
                "Date or time not equals");
    }

    // Тест по рассчету продолжительности эпика
    @Test
    public void epicStartDurationEndTime() {
        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);

        Subtask subtask1 = new Subtask("Test subtask1", "subtask1 disc", TaskStatus.NEW);
        subtask1.setEpicId(epicTask.getId());
        subtask1.setStartTime(LocalDateTime.of(2022, 4, 26, 12, 0));
        subtask1.setDuration(300);
        taskManager.createNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask2", "subtask1 disc", TaskStatus.NEW);
        subtask2.setEpicId(epicTask.getId());
        subtask2.setStartTime(LocalDateTime.of(2022, 4, 26, 16, 0));
        subtask2.setDuration(120);
        taskManager.createNewSubtask(subtask2);

        assertEquals(LocalDateTime.of(2022, 4, 26, 12, 0), epicTask.getStartTime(),
                "Date or time not equals");
        assertEquals(LocalDateTime.of(2022, 4, 26, 18, 0), epicTask.getEndTime(),
                "Date or time not equals");
        assertEquals(420, epicTask.getDuration(), "Wrong duration");
    }

    // Тест для рассчета приоритета задач
    @Test
    public void getPrioritized() {
        // задача без старта
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        taskManager.createNewTask(task);

        Task task1 = new Task("Test task1", "test disc", TaskStatus.IN_PROGRESS);
        task1.setStartTime(LocalDateTime.of(2022, 1, 1, 4, 0));
        task1.setDuration(120);
        taskManager.createNewTask(task1);

        Task task2 = new Task("Test2 task", "test disc", TaskStatus.DONE);
        task2.setStartTime(LocalDateTime.of(2022, 1, 1, 1, 0));
        task2.setDuration(120);
        taskManager.createNewTask(task2);

        List<Task> taskList = taskManager.getPrioritizedTasks();
        assertEquals(task2, taskList.get(0), "Invalid task");
        assertEquals(task1, taskList.get(1), "Invalid task");
        assertEquals(task, taskList.get(2), "Invalid task");
    }

    // Тест по поиску пересечений
    @Test
    public void findIntersections() {
        // задача не пересекается
        Task task1 = new Task("Test task1", "test disc", TaskStatus.IN_PROGRESS);
        task1.setStartTime(LocalDateTime.of(2022, 1, 1, 1, 0));
        task1.setDuration(120);
        taskManager.createNewTask(task1);

        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        taskManager.createNewEpic(epicTask);

        // задача пересекается с task
        Subtask subtask = new Subtask("Test subtask", "test disc", TaskStatus.DONE);
        subtask.setStartTime(LocalDateTime.of(2022, 1, 1, 2, 0));
        subtask.setDuration(120);
        subtask.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask);

        // задача не пересекается
        Subtask subtask1 = new Subtask("Test subtask1", "test disc", TaskStatus.NEW);
        subtask1.setStartTime(LocalDateTime.of(2022, 1, 1, 4, 0));
        subtask1.setDuration(120);
        subtask1.setEpicId(epicTask.getId());
        taskManager.createNewSubtask(subtask1);

        List<Task> taskList = taskManager.getPrioritizedTasks();
        assertEquals(task1, taskList.get(0), "Invalid task");
        assertEquals(subtask1, taskList.get(1), "Invalid task");
        assertEquals(2, taskList.size(), "Invalid size");

        // Обновление задачи с тем же временем
        Task task2 = new Task("Test task2", "test disc", TaskStatus.NEW);
        task2.setStartTime(task1.getStartTime());
        task2.setDuration(task1.getDuration());
        task2.setId(task1.getId());
        taskManager.updateTask(task2);

        // Обновление подзадачи с другим временем
        Subtask subtask2 = new Subtask("Test subtask2", "test disc", TaskStatus.NEW);
        subtask2.setStartTime(LocalDateTime.of(2022, 1, 1, 18, 0));
        subtask2.setDuration(120);
        subtask2.setId(subtask1.getId());
        subtask2.setEpicId(epicTask.getId());
        taskManager.updateSubtask(subtask2);

        taskList = taskManager.getPrioritizedTasks();
        assertEquals(task2, taskList.get(0), "Invalid task");
        assertEquals(subtask2, taskList.get(1), "Invalid task");
        assertEquals(2, taskList.size(), "Invalid size");

    }
}
