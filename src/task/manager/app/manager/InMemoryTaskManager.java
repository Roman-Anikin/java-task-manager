package task.manager.app.manager;

import task.manager.app.model.*;
import task.manager.app.utility.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Long, Task> taskList = new HashMap<>();
    private HashMap<Long, EpicTask> epicList = new HashMap<>();
    private HashMap<Long, Subtask> subtaskList = new HashMap<>();
    private static Long id = 0L;


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
        taskList.clear();

    }

    //Метод для удаления всех эпиков
    @Override
    public void deleteAllEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    //Метод для удаления всех подзадач (также происходит удаление из списка эпика и обновление статуса эпика)
    @Override
    public void deleteAllSubtasks() {
        for (EpicTask epicTask : epicList.values()) {
            epicTask.getEpicSubtasks().clear();
            changeEpicStatus(epicTask);
        }
        subtaskList.clear();
    }

    //Метод для получения задачи по ИД
    @Override
    public Task getTaskById(Long id) {
        Managers.getDefaultHistory().add(taskList.get(id));
        return taskList.get(id);
    }

    //Метод для получения эпика по ИД
    @Override
    public EpicTask getEpicById(Long id) {
        Managers.getDefaultHistory().add(epicList.get(id));
        return epicList.get(id);
    }

    //Метод для получения подзадачи по ИД
    @Override
    public Subtask getSubtaskById(Long id) {
        Managers.getDefaultHistory().add(subtaskList.get(id));
        return subtaskList.get(id);
    }

    //Метод для создания нового задания
    @Override
    public void createNewTask(Task task) {
        task.setId(generateId());
        taskList.put(task.getId(), task);
    }

    //Метод для создания нового эпика
    @Override
    public void createNewEpic(EpicTask epic) {
        epic.setId(generateId());
        updateEpic(epic);
        epicList.put(epic.getId(), epic);
    }

    //Метод для создания новой подзадачи (также происходит добавление подзадачи в список эпика и обновление статуса)
    @Override
    public void createNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        epicList.get(subtask.getEpicId()).getEpicSubtasks().add(subtask);
        subtaskList.put(subtask.getId(), subtask);
        changeEpicStatus(epicList.get(subtask.getEpicId()));
    }

    //Метод для обновления задачи
    @Override
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    //Метод для обновления эпика
    private void updateEpic(EpicTask epic) {
        epicList.put(epic.getId(), epic);
    }

    //Метод для обновления подзадачи (также происходит обновление статуса эпика)
    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        EpicTask epicTask = epicList.get(subtask.getEpicId());
        for (int i = 0; i < epicTask.getEpicSubtasks().size(); i++) {
            if (epicTask.getEpicSubtasks().get(i).getId().equals(subtask.getId())) {
                epicTask.getEpicSubtasks().set(i, subtask);
                break;
            }
        }
        changeEpicStatus(epicTask);
    }

    //Метод для удаления задачи по ИД
    @Override
    public void deleteTaskById(Long id) {
        taskList.remove(id);
    }

    //Метод для удаления эпика по ИД (также происходит удаление подзадач эпика)
    @Override
    public void deleteEpicById(Long id) {
        ArrayList<Long> keys = new ArrayList<>();
        for (Long l : subtaskList.keySet()) {
            Subtask subtask = subtaskList.get(l);
            if (subtask.getEpicId().equals(id)) {
                keys.add(l);
            }
        }
        for (Long l : keys) {
            subtaskList.remove(l);
        }
        epicList.remove(id);
    }

    //Метод для удаления подзадачи по ИД (также происходит удаление из списка эпика)
    @Override
    public void deleteSubtaskById(Long id) {
        Subtask subtask = subtaskList.get(id);
        EpicTask epicTask = epicList.get(subtask.getEpicId());
        epicTask.getEpicSubtasks().remove(subtask);
        subtaskList.remove(id);
        changeEpicStatus(epicTask);
    }

    //Метод для получения списка подзадач по эпику
    @Override
    public ArrayList<Subtask> getSubtaskListByEpic(EpicTask epic) {
        return subtaskList.isEmpty() ? new ArrayList<>() : epic.getEpicSubtasks();
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
    }

    @Override
    public List<Task> history() {
        return Managers.getDefaultHistory().getHistory();
    }
}
