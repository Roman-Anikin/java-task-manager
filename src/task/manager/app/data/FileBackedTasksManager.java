package task.manager.app.data;

import task.manager.app.manager.*;
import task.manager.app.model.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static Path path;
    private static LinkedHashMap<Long, Task> tasks = new LinkedHashMap<>();

    public FileBackedTasksManager(Path path) {
        FileBackedTasksManager.path = path;
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

    //Метод для удаления набора ключей
    private void removeAll(Set<Long> keys) {
        for (Long l : keys) {
            tasks.remove(l);
        }
    }

    // Метод для сохранения в файл
    private void save() {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            bf.write("id,type,name,status,description,epic" + "\n");
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
        if (task.getClass().equals(EpicTask.class)) {
            type = String.valueOf(TaskType.EPIC);
        } else if (task.getClass().equals(Subtask.class)) {
            type = String.valueOf(TaskType.SUBTASK);
        } else {
            type = String.valueOf(TaskType.TASK);
        }
        StringBuilder result = new StringBuilder(String.join(",", String.valueOf(task.getId()), type,
                task.getName(), String.valueOf(task.getStatus()), task.getDescription())).append(",");
        if (task.getClass().equals(Subtask.class)) {
            result.append(((Subtask) task).getEpicId());
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
                ((Subtask) task).setEpicId(Long.valueOf(line[5]));
                subtask = (Subtask) task;
                EpicTask epicTask = (EpicTask) tasks.get(subtask.getEpicId());
                epicTask.getEpicSubtasks().add(subtask);
                break;
            default:
                task = new Task(line[2], line[4], TaskStatus.valueOf(line[3]));
                task.setId(Long.valueOf(line[0]));
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
    private static FileBackedTasksManager loadFromFile(Path path) {
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
                    fileBacked.historyManager.add(tasks.get(l));
                }
            } else {
                Task task = fileBacked.taskFromString(lines.get(i));
                tasks.put(task.getId(), task);
            }
        }
        return fileBacked;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fb = new FileBackedTasksManager(Path.of("src/task/manager/app/data/tasks.csv"));

        Task task = new Task("task", "task disc", TaskStatus.DONE);
        fb.createNewTask(task);

        EpicTask epicTask = new EpicTask("epic", "epic disc", TaskStatus.DONE);
        fb.createNewEpic(epicTask);

        Subtask subtask = new Subtask("sub", "sub disc", TaskStatus.IN_PROGRESS);
        subtask.setEpicId(epicTask.getId());
        fb.createNewSubtask(subtask);

        Task task1 = new Task("task2", "disc2", TaskStatus.IN_PROGRESS);
        fb.createNewTask(task1);

        Subtask subtask1 = new Subtask("sub2", "disc 2", TaskStatus.NEW);
        subtask1.setEpicId(epicTask.getId());
        fb.createNewSubtask(subtask1);

        fb.getTaskById(task.getId());
        fb.getSubtaskById(subtask.getId());
        fb.getEpicById(epicTask.getId());

        FileBackedTasksManager fb1 = loadFromFile(Path.of("src/task/manager/app/tasks.csv"));

        for (Task t : tasks.values()) {
            System.out.println(t);
        }
        System.out.println();

        List<Task> taskLst = fb1.historyManager.getHistory();
        for (Task t : taskLst) {
            System.out.println(t);
        }
    }
}
