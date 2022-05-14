package task.manager.app.data;

import task.manager.app.manager.*;
import task.manager.app.model.*;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewEpic(EpicTask epic) {
        super.createNewEpic(epic);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(Long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Long id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Long id) {
        super.deleteSubtaskById(id);
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


    // Метод для сохранения в файл
    protected void save() {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            bf.write("id,type,name,status,description,epic,start,duration,end" + "\n");
            for (Task t : taskList.values()) {
                bf.write(toString(t) + "\n");
            }
            for (Task t : epicList.values()) {
                bf.write(toString(t) + "\n");
            }
            for (Task t : subtaskList.values()) {
                bf.write(toString(t) + "\n");
            }
            bf.write("\n" + toString(historyManager));
        } catch (IOException ioException) {
            throw new ManagerSaveException();
        }
    }

    // Метод для перевода задачи в строку
    private String toString(Task task) {
        String type;
        StringBuilder result;
        if (task.getClass().equals(EpicTask.class)) {
            type = String.valueOf(TaskType.EPIC);
        } else if (task.getClass().equals(Subtask.class)) {
            type = String.valueOf(TaskType.SUBTASK);
        } else {
            type = String.valueOf(TaskType.TASK);
        }
        result = new StringBuilder(String.join(",", String.valueOf(task.getId()), type, task.getName(),
                String.valueOf(task.getStatus()), task.getDescription())).append(",");
        if (task.getStartTime() == null) {
            result.append(" ").append(",").append(task.getDuration()).append(",").append(" ").append(",");
        } else {
            result.append(task.getStartTime().toString()).append(",").append(task.getDuration()).append(",")
                    .append(task.getEndTime().toString()).append(",");
        }
        if (task.getClass().equals(Subtask.class)) {
            result.append(((Subtask) task).getEpicId());
        }
        return result.toString();
    }

    //Метод для перевода истории в строку
    private static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        if (!manager.getHistory().isEmpty()) {
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
                EpicTask epicTask = epicList.get(subtask.getEpicId());
                epicTask.getEpicSubtasks().add(subtask);
                if (!line[5].equals(" ")) {
                    task.setStartTime(LocalDateTime.parse(line[5]));
                }
                task.setDuration(Long.parseLong(line[6]));
                break;
            default:
                task = new Task(line[2], line[4], TaskStatus.valueOf(line[3]));
                task.setId(Long.valueOf(line[0]));
                if (!line[5].equals(" ")) {
                    task.setStartTime(LocalDateTime.parse(line[5]));
                }
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
                    if (fileBacked.taskList.containsKey(l)) {
                        fileBacked.historyManager.add(fileBacked.taskList.get(l));
                    } else if (fileBacked.epicList.containsKey(l)) {
                        fileBacked.historyManager.add(fileBacked.epicList.get(l));
                    } else {
                        fileBacked.historyManager.add(fileBacked.subtaskList.get(l));
                    }
                }
            } else {
                Task task = fileBacked.taskFromString(lines.get(i));
                if (task.getClass().equals(Task.class)) {
                    fileBacked.taskList.put(task.getId(), task);
                } else if (task.getClass().equals(EpicTask.class)) {
                    fileBacked.epicList.put(task.getId(), (EpicTask) task);
                } else {
                    fileBacked.subtaskList.put(task.getId(), (Subtask) task);
                }
                generateId();
            }
        }
        return fileBacked;
    }
}
