package task.manager.app.model;

import java.util.ArrayList;

public class EpicTask extends Task {

    private ArrayList<Subtask> epicSubtasks;

    public EpicTask(String name, String description, TaskStatus status) {
        super(name, description, status);
        epicSubtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() + ", epicSubtasks=" + epicSubtasks.toString() +
                '}';
    }
}