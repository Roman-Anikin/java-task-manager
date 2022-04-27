package task.manager.app.tests;

import org.junit.jupiter.api.Test;
import task.manager.app.data.FileBackedTasksManager;
import task.manager.app.model.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest {

    private Path path = Path.of("src/task/manager/app/files/tasksTest.csv");
    private FileBackedTasksManager fb = new FileBackedTasksManager(Path.of(String.valueOf(path)));

    @Test
    public void saveToFile() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2022, 4, 26, 12, 0));
        task.setDuration(300);
        fb.createNewTask(task);

        EpicTask epicTask = new EpicTask("Test epic", "test disc", TaskStatus.IN_PROGRESS);
        fb.createNewEpic(epicTask);

        Subtask subtask = new Subtask("Test subtask", "subtask disc", TaskStatus.NEW);
        subtask.setEpicId(epicTask.getId());
        subtask.setStartTime(LocalDateTime.of(2022, 4, 26, 8, 0));
        subtask.setDuration(600);
        fb.createNewSubtask(subtask);

        LinkedHashMap<Long, Task> tasks = FileBackedTasksManager.loadFromFile(path).getTasks();
        assertNotNull(tasks, "Tasks not found");
        assertEquals(3, tasks.size(), "Invalid number of tasks");

        fb.getTaskById(task.getId());
        fb.getEpicById(epicTask.getId());
        fb.getSubtaskById(subtask.getId());

        fb = FileBackedTasksManager.loadFromFile(path);
        assertNotNull(fb.history(), "history is empty");
        assertEquals(3, fb.history().size(), "Invalid number of tasks");
    }

    @Test
    public void loadFromFile() {
        List<Task> tasks = new ArrayList<>(FileBackedTasksManager.loadFromFile(path).getTasks().values());
        assertNotNull(tasks, "Tasks not found");
        assertEquals(3, tasks.size(), "Invalid number of tasks");

        Task task = tasks.get(0);
        assertEquals(LocalDateTime.of(2022, 4, 26, 12, 0), task
                .getStartTime(), "Invalid date or time");
        assertEquals(300, task.getDuration(), "Invalid duration");
        assertEquals(task.getStartTime().plusMinutes(300), task.getEndTime(),
                "Invalid date or time");

        fb = FileBackedTasksManager.loadFromFile(path);
        assertNotNull(fb.history(), "history is empty");
        assertEquals(3, fb.history().size(), "Invalid number of tasks");
    }
}
