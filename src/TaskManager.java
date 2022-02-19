import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, EpicTask> epicList = new HashMap<>();
    private HashMap<Integer, ArrayList<Subtask>> subtaskList = new HashMap<>();
    private int id;

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
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (ArrayList<Subtask> arrayList : subtaskList.values()) {
            subtasks.addAll(arrayList);
        }
        return subtasks;
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

    //Метод для удаления всех подзадач
    public void deleteAllSubtasks() {
        subtaskList.clear();

    }

    //Метод для получения задачи по ИД
    public Task getTaskById(int id) {
        return taskList.get(id);
    }

    //Метод для получения эпика по ИД
    public EpicTask getEpicById(int id) {
        return epicList.get(id);
    }

    //Метод для получения подзадачи по ИД
    public Subtask getSubtaskById(int id) {
        Subtask subtask = null;
        OUTER:
        for (ArrayList<Subtask> subtaskArrayList : subtaskList.values()) {
            for (Subtask s : subtaskArrayList) {
                if (s.getId() == id) {
                    subtask = s;
                    break OUTER;
                }
            }
        }
        return subtask;
    }

    //Метод для создания нового задания
    public void createNewTask(Task task) {
        task.setId(id++);
        taskList.put(task.getId(), task);
    }

    //Метод для создания нового эпика
    public void createNewEpic(EpicTask epic) {
        epic.setId(id++);
        epicList.put(epic.getId(), epic);
    }

    //Метод для создания новой подзадачи
    public void createNewSubtask(Subtask subtask, int epicId) {
        ArrayList<Subtask> subtasks;
        if (subtaskList.get(epicId) == null) {
            subtasks = new ArrayList<>();
        } else {
            subtasks = subtaskList.get(epicId);
        }
        subtask.setId(id++);
        subtasks.add(subtask);
        subtaskList.put(epicId, subtasks);
    }

    //Метод для обновления задачи
    public void updateTask(Task task) {
        for (Task t : taskList.values()) {
            if (t.getName().equals(task.getName())) {
                task.setId(t.getId());
                taskList.put(t.getId(), task);
                break;
            }
        }
    }

    //Метод для обновления эпика
    private void updateEpic(EpicTask epic) {
        epicList.put(epic.getId(), epic);
    }

    //Метод для обновления подзадачи
    public void updateSubtask(Subtask subtask) {
        for (ArrayList<Subtask> subtaskArrayList : subtaskList.values()) {
            for (int i = 0; i < subtaskArrayList.size(); i++) {
                if (subtaskArrayList.get(i).getName().equals(subtask.getName())) {
                    subtask.setId(subtaskArrayList.get(i).getId());
                    subtaskArrayList.set(i, subtask);
                    break;
                }
            }
        }
        EpicTask epic = null;
        for (Integer i : subtaskList.keySet()) {
            ArrayList<Subtask> subtasks = subtaskList.get(i);
            if (subtasks.contains(subtask)) {
                epic = epicList.get(i);
            }
        }
        ArrayList<Subtask> subtaskArrayList = subtaskList.get(epic.getId());
        int newCount = 0;
        int doneCount = 0;
        for (Subtask value : subtaskArrayList) {
            if (value.getStatus().equals("NEW")) {
                newCount++;
            } else if (value.getStatus().equals("DONE")) {
                doneCount++;
            }
        }
        if (newCount == subtaskArrayList.size()) {
            epic.setStatus("NEW");
        } else if (doneCount == subtaskArrayList.size()) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
        new TaskManager().updateEpic(epic);

    }

    //Метод для удаления задачи по ИД
    public void deleteTaskById(int id) {
        taskList.remove(id);
    }

    //Метод для удаления эпика по ИД
    public void deleteEpicById(int id) {
        epicList.remove(id);
        subtaskList.remove(id);
    }

    //Метод для удаления подзадачи по ИД
    public void deleteSubtaskById(int id) {
        int index = 0;
        Subtask s = null;
        OUTER:
        for (Integer i : subtaskList.keySet()) {
            ArrayList<Subtask> subtasks = subtaskList.get(i);
            for (Subtask subtask : subtasks) {
                if (subtask.getId() == id) {
                    index = i;
                    s = subtask;
                    break OUTER;
                }
            }
        }
        ArrayList<Subtask> subtaskArrayList = subtaskList.get(index);
        subtaskArrayList.remove(s);
    }

    //Метод для получения списка подзадач по эпику
    public ArrayList<Subtask> getSubtaskListByEpic(EpicTask epic) {
        return subtaskList.get(epic.getId());
    }
}