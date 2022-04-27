package task.manager.app.data;

import task.manager.app.manager.*;
import task.manager.app.model.*;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private Path path;
    private LinkedHashMap<Long, Task> tasks = new LinkedHashMap<>();

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void createNewEpic(EpicTask epic) {
        super.createNewEpic(epic);
        tasks.put(epic.getId(), epic);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        tasks.put(subtask.getId(), subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        tasks.put(subtask.getId(), subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        removeAll(taskList.keySet());
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        removeAll(epicList.keySet());
        removeAll(subtaskList.keySet());
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        removeAll(subtaskList.keySet());
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(Long id) {
        super.deleteTaskById(id);
        tasks.remove(id);
        save();
    }

    @Override
    public void deleteEpicById(Long id) {
        Set<Long> keys = new HashSet<>();
        for (Map.Entry<Long, Task> entry : tasks.entrySet()) {
            if (entry.getValue().getClass().equals(Subtask.class)) {
                if (((Subtask) entry.getValue()).getEpicId().equals(id)) {
                    keys.add(entry.getKey());
                }
            }
        }
        removeAll(keys);
        tasks.remove(id);
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Long id) {
        super.deleteSubtaskById(id);
        tasks.remove(id);
        save();
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicById(Long id) {
        EpicTask epicTask = super.getEpicById(id);
        save();
        return epicTask;
    }

    @Override
    public Subtask getSubtaskById(Long id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    public LinkedHashMap<Long, Task> getTasks() {
        return tasks;
    }

    //Метод для удаления набора ключей
    private void removeAll(Set<Long> keys) {
        for (Long l : keys) {
            tasks.remove(l);
        }
    }

    // Метод для сохранения в файл
    private void save() {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            bf.write("id,type,name,status,description,epic,start,duration,end" + "\n");
            for (Task t : tasks.values()) {
                bf.write(toString(t) + "\n");
            }
            bf.write("\n" + toString(historyManager));
        } catch (IOException ioException) {
            throw new ManagerSaveException();
        }
    }

    // Метод для перевода задачи в строку
    private String toString(Task task) {
        String type = "";
        StringBuilder result;
        if (task.getClass().equals(EpicTask.class)) {
            type = String.valueOf(TaskType.EPIC);
            result = new StringBuilder(String.join(",", String.valueOf(task.getId()),
                    type, task.getName(), String.valueOf(task.getStatus()), task.getDescription())).append(",");
            if (!((EpicTask) task).getEpicSubtasks().isEmpty()) {
                result.append(task.getStartTime().toString()).append(",").append(task.getDuration()).append(",")
                        .append(task.getEndTime().toString()).append(",");
            }
        } else if (task.getClass().equals(Subtask.class)) {
            type = String.valueOf(TaskType.SUBTASK);
            result = new StringBuilder(String.join(",", String.valueOf(task.getId()),
                    type, task.getName(), String.valueOf(task.getStatus()), task.getDescription(),
                    task.getStartTime().toString(), String.valueOf(task.getDuration()), task.getEndTime().toString()))
                    .append(",").append(((Subtask) task).getEpicId());
        } else {
            type = String.valueOf(TaskType.TASK);
            result = new StringBuilder(String.join(",", String.valueOf(task.getId()),
                    type, task.getName(), String.valueOf(task.getStatus()), task.getDescription(),
                    task.getStartTime().toString(), String.valueOf(task.getDuration()), task.getEndTime().toString()))
                    .append(",");
        }
        return result.toString();
    }

    //Метод для перевода истории в строку
    private static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        if (manager.getHistory().size() > 0) {
            List<Task> taskList = manager.getHistory();
            for (Task t : taskList) {
                sb.append(t.getId()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    //Метод для получения задачи из строки
    private Task taskFromString(String value) {
        Task task;
        Subtask subtask;
        String[] line = value.split(",");
        switch (TaskType.valueOf(line[1])) {
            case EPIC:
                task = new EpicTask(line[2], line[4], TaskStatus.valueOf(line[3]));
                task.setId(Long.valueOf(line[0]));
                break;
            case SUBTASK:
                task = new Subtask(line[2], line[4], TaskStatus.valueOf(line[3]));
                task.setId(Long.valueOf(line[0]));
                ((Subtask) task).setEpicId(Long.valueOf(line[8]));
                subtask = (Subtask) task;
                EpicTask epicTask = (EpicTask) tasks.get(subtask.getEpicId());
                epicTask.getEpicSubtasks().add(subtask);
                task.setStartTime(LocalDateTime.parse(line[5]));
                task.setDuration(Long.parseLong(line[6]));
                break;
            default:
                task = new Task(line[2], line[4], TaskStatus.valueOf(line[3]));
                task.setId(Long.valueOf(line[0]));
                task.setStartTime(LocalDateTime.parse(line[5]));
                task.setDuration(Long.parseLong(line[6]));
                break;
        }
        return task;
    }

    //Метод для получения ид задач из строки
    private static List<Long> idFromString(String value) {
        String[] line = value.split(",");
        List<Long> id = new ArrayList<>();
        for (String s : line) {
            id.add(Long.parseLong(s));
        }
        return id;
    }

    //Метод для загрузки задач и истории из файла
    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager fileBacked = new FileBackedTasksManager(path);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)))) {
            while (br.ready()) {
                String line = br.readLine();
                lines.add(line);
            }
        } catch (IOException q) {
            throw new ManagerSaveException();
        }
        for (int i = 1; i < lines.size() - 1; i++) {
            if (lines.get(i).equals("")) {
                List<Long> id = idFromString(lines.get(i + 1));
                for (Long l : id) {
                    fileBacked.historyManager.add(fileBacked.tasks.get(l));
                }
            } else {
                Task task = fileBacked.taskFromString(lines.get(i));
                fileBacked.tasks.put(task.getId(), task);
            }
        }
        return fileBacked;
    }
}
