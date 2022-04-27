package task.manager.app.tests;

import org.junit.jupiter.api.Test;
import task.manager.app.manager.InMemoryHistoryManager;
import task.manager.app.model.Task;
import task.manager.app.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void addTaskToHistory() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "Wrong number of tasks");
    }

    @Test
    public void addDuplicateToHistory() {
        Task task = new Task("Test task", "test disc", TaskStatus.NEW);
        task.setId(0L);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Wrong number of tasks");
    }

    @Test
    public void deleteTaskFromHistory() {
        List<Task> taskList = new ArrayList<>(List.of(
                new Task("Test task", "test disc", TaskStatus.NEW),
                new Task("Test task2", "test disc", TaskStatus.IN_PROGRESS),
                new Task("Test task3", "test disc", TaskStatus.IN_PROGRESS),
                new Task("Test task4", "test disc", TaskStatus.NEW),
                new Task("Test task5", "test disc", TaskStatus.IN_PROGRESS)));
        Long id = 0L;
        for (Task t : taskList) {
            t.setId(id);
            id++;
            historyManager.add(t);
        }
        assertEquals(5, historyManager.getHistory().size(), "Wrong number of tasks");
        historyManager.remove(taskList.get(0).getId());
        historyManager.remove(taskList.get(2).getId());
        historyManager.remove(taskList.get(4).getId());
        taskList.remove(0);
        taskList.remove(1);
        taskList.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(taskList, history, "Wrong number of tasks");
    }
}
