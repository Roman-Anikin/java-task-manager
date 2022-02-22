import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Long, Task> taskList = new HashMap<>();
    private HashMap<Long, EpicTask> epicList = new HashMap<>();
    private HashMap<Long, Subtask> subtaskList = new HashMap<>();
    private static Long id = 0L;

    //Метод для получения списка всех задач
    public ArrayList<Task> getAllTaskList() {
        return new ArrayList<>(taskList.values());
    }

    //Метод для получения списка всех эпиков
    public ArrayList<EpicTask> getAllEpicList() {
        return new ArrayList<>(epicList.values());
    }

    //Метод для получения списка всех подзадач
    public ArrayList<Subtask> getAllSubtaskList() {
        return new ArrayList<>(subtaskList.values());
    }

    //Метод для удаления всех задач
    public void deleteAllTasks() {
        taskList.clear();

    }

    //Метод для удаления всех эпиков
    public void deleteAllEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    //Метод для удаления всех подзадач (также происходит удаление из списка эпика и обновление статуса эпика)
    public void deleteAllSubtasks() {
        for (EpicTask epicTask : epicList.values()) {
            epicTask.getEpicSubtasks().clear();
            changeEpicStatus(epicTask);
        }
        subtaskList.clear();
    }

    //Метод для получения задачи по ИД
    public Task getTaskById(Long id) {
        return taskList.get(id);
    }

    //Метод для получения эпика по ИД
    public EpicTask getEpicById(Long id) {
        return epicList.get(id);
    }

    //Метод для получения подзадачи по ИД
    public Subtask getSubtaskById(Long id) {
        return subtaskList.get(id);
    }

    //Метод для создания нового задания
    public void createNewTask(Task task) {
        task.setId(generateId());
        taskList.put(task.getId(), task);
    }

    //Метод для создания нового эпика
    public void createNewEpic(EpicTask epic) {
        epic.setId(generateId());
        epicList.put(epic.getId(), epic);
    }

    //Метод для создания новой подзадачи (также происходит добавление подзадачи в список эпика и обновление статуса)
    public void createNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        epicList.get(subtask.getEpicId()).getEpicSubtasks().add(subtask);
        subtaskList.put(subtask.getId(), subtask);
        changeEpicStatus(epicList.get(subtask.getEpicId()));
    }

    //Метод для обновления задачи
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    //Метод для обновления эпика
    private void updateEpic(EpicTask epic) {
        epicList.put(epic.getId(), epic);
    }

    //Метод для обновления подзадачи (также происходит обновление статуса эпика)
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
    public void deleteTaskById(Long id) {
        taskList.remove(id);
    }

    //Метод для удаления эпика по ИД (также происходит удаление подзадач эпика)
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
    public void deleteSubtaskById(Long id) {
        Subtask subtask = subtaskList.get(id);
        EpicTask epicTask = epicList.get(subtask.getEpicId());
        epicTask.getEpicSubtasks().remove(subtask);
        subtaskList.remove(id);
        changeEpicStatus(epicTask);
    }

    //Метод для получения списка подзадач по эпику
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
            if (subtask.getStatus().equals(TaskStatus.NEW.getTitle())) {
                newCount++;
            } else if (subtask.getStatus().equals(TaskStatus.DONE.getTitle())) {
                doneCount++;
            }
        }
        if (newCount == epicTask.getEpicSubtasks().size() || epicTask.getEpicSubtasks().isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW.getTitle());
        } else if (doneCount == epicTask.getEpicSubtasks().size()) {
            epicTask.setStatus(TaskStatus.DONE.getTitle());
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS.getTitle());
        }
        updateEpic(epicTask);
    }
}