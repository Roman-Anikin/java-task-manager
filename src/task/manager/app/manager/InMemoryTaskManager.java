package task.manager.app.manager;

import task.manager.app.model.*;
import task.manager.app.utility.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Long, Task> taskList = new HashMap<>();
    protected HashMap<Long, EpicTask> epicList = new HashMap<>();
    protected HashMap<Long, Subtask> subtaskList = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private static Long id = 0L;
    private Set<Task> prioritizedTasks = new TreeSet<>();

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void findIntersections(Task task) {
        List<Task> tasks = getPrioritizedTasks();
        if (tasks.size() == 0 || task.getStartTime() == null || tasks.get(0).getStartTime() == null) {
            prioritizedTasks.add(task);
            return;
        }
        for (Task value : tasks) {
            if (value.getStartTime() != null && (value.getEndTime().isBefore(task.getStartTime())
                    || value.getStartTime().isAfter(task.getEndTime()))) {
                prioritizedTasks.add(task);
            }
        }
    }

    //Метод для получения списка всех задач
    @Override
    public ArrayList<Task> getAllTaskList() {
        return new ArrayList<>(taskList.values());
    }

    //Метод для получения списка всех эпиков
    @Override
    public ArrayList<EpicTask> getAllEpicList() {
        return new ArrayList<>(epicList.values());
    }

    //Метод для получения списка всех подзадач
    @Override
    public ArrayList<Subtask> getAllSubtaskList() {
        return new ArrayList<>(subtaskList.values());
    }

    //Метод для удаления всех задач
    @Override
    public void deleteAllTasks() {
        for (Long l : taskList.keySet()) {
            prioritizedTasks.remove(taskList.get(l));
            historyManager.remove(l);
        }
        taskList.clear();
    }

    //Метод для удаления всех эпиков
    @Override
    public void deleteAllEpics() {
        for (Long l : subtaskList.keySet()) {
            prioritizedTasks.remove(subtaskList.get(l));
            historyManager.remove(l);
        }
        for (Long l : epicList.keySet()) {
            prioritizedTasks.remove(epicList.get(l));
            epicList.get(l).getEpicSubtasks().clear();
            historyManager.remove(l);
        }
        epicList.clear();
        subtaskList.clear();
    }

    //Метод для удаления всех подзадач (также происходит удаление из списка эпика и обновление статуса эпика)
    @Override
    public void deleteAllSubtasks() {
        for (EpicTask epicTask : epicList.values()) {
            for (Subtask s : epicTask.getEpicSubtasks()) {
                prioritizedTasks.remove(s);
                historyManager.remove(s.getId());
            }
            epicTask.getEpicSubtasks().clear();
            changeEpicStatus(epicTask);
        }
        subtaskList.clear();
    }

    //Метод для получения задачи по ИД
    @Override
    public Task getTaskById(Long id) {
        historyManager.add(taskList.get(id));
        return taskList.get(id);
    }

    //Метод для получения эпика по ИД
    @Override
    public EpicTask getEpicById(Long id) {
        historyManager.add(epicList.get(id));
        return epicList.get(id);
    }

    //Метод для получения подзадачи по ИД
    @Override
    public Subtask getSubtaskById(Long id) {
        historyManager.add(subtaskList.get(id));
        return subtaskList.get(id);
    }

    //Метод для создания нового задания
    @Override
    public void createNewTask(Task task) {
        if (!taskList.containsKey(task.getId())) {
            task.setId(generateId());
        }
        taskList.put(task.getId(), task);
        findIntersections(task);
    }

    //Метод для создания нового эпика
    @Override
    public void createNewEpic(EpicTask epic) {
        if (!epicList.containsKey(epic.getId())) {
            epic.setId(generateId());
        }
        changeEpicStatus(epic);
        epicList.put(epic.getId(), epic);
    }

    //Метод для создания новой подзадачи (также происходит добавление подзадачи в список эпика и обновление статуса)
    @Override
    public void createNewSubtask(Subtask subtask) {
        if (!subtaskList.containsKey(subtask.getId())) {
            subtask.setId(generateId());
        }
        epicList.get(subtask.getEpicId()).getEpicSubtasks().add(subtask);
        subtaskList.put(subtask.getId(), subtask);
        changeEpicStatus(epicList.get(subtask.getEpicId()));
        findIntersections(subtask);
    }

    //Метод для обновления задачи
    @Override
    public void updateTask(Task task) {
        prioritizedTasks.remove(taskList.get(task.getId()));
        taskList.put(task.getId(), task);
        findIntersections(task);
    }

    //Метод для обновления эпика
    private void updateEpic(EpicTask epic) {
        epicList.put(epic.getId(), epic);
    }

    //Метод для обновления подзадачи (также происходит обновление статуса эпика)
    @Override
    public void updateSubtask(Subtask subtask) {
        prioritizedTasks.remove(subtaskList.get(subtask.getId()));
        subtaskList.put(subtask.getId(), subtask);
        EpicTask epicTask = epicList.get(subtask.getEpicId());
        for (Subtask s : epicTask.getEpicSubtasks()) {
            if (s.getId().equals(subtask.getId())) {
                epicTask.getEpicSubtasks().remove(s);
                epicTask.getEpicSubtasks().add(subtask);
                break;
            }
        }
        changeEpicStatus(epicTask);
        findIntersections(subtask);
    }

    //Метод для удаления задачи по ИД
    @Override
    public void deleteTaskById(Long id) {
        prioritizedTasks.remove(taskList.get(id));
        taskList.remove(id);
        historyManager.remove(id);
    }

    //Метод для удаления эпика по ИД (также происходит удаление подзадач эпика)
    @Override
    public void deleteEpicById(Long id) {
        ArrayList<Long> keys = new ArrayList<>();
        for (Long l : subtaskList.keySet()) {
            Subtask subtask = subtaskList.get(l);
            if (subtask.getEpicId().equals(id)) {
                epicList.get(id).getEpicSubtasks().remove(subtask);
                keys.add(l);
            }
        }
        for (Long l : keys) {
            prioritizedTasks.remove(subtaskList.get(l));
            subtaskList.remove(l);
            historyManager.remove(l);
        }
        prioritizedTasks.remove(epicList.get(id));
        epicList.remove(id);
        historyManager.remove(id);
    }

    //Метод для удаления подзадачи по ИД (также происходит удаление из списка эпика)
    @Override
    public void deleteSubtaskById(Long id) {
        prioritizedTasks.remove(subtaskList.get(id));
        Subtask subtask = subtaskList.get(id);
        EpicTask epicTask = epicList.get(subtask.getEpicId());
        epicTask.getEpicSubtasks().remove(subtask);
        subtaskList.remove(id);
        changeEpicStatus(epicTask);
        historyManager.remove(id);
    }

    //Метод для получения списка подзадач по эпику
    @Override
    public TreeSet<Subtask> getSubtaskListByEpic(EpicTask epic) {
        return subtaskList.isEmpty() ? new TreeSet<>() : epic.getEpicSubtasks();
    }

    //Метод для генерации ИД
    private static Long generateId() {
        return id++;
    }

    //Метод для изменения статуса эпика
    private void changeEpicStatus(EpicTask epicTask) {
        int newCount = 0;
        int doneCount = 0;
        for (Subtask subtask : epicTask.getEpicSubtasks()) {
            if (subtask.getStatus().equals(TaskStatus.NEW)) {
                newCount++;
            } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                doneCount++;
            }
        }
        if (newCount == epicTask.getEpicSubtasks().size() || epicTask.getEpicSubtasks().isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
        } else if (doneCount == epicTask.getEpicSubtasks().size()) {
            epicTask.setStatus(TaskStatus.DONE);
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
        updateEpic(epicTask);
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }
}