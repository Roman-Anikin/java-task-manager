package task.manager.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeSet;

public class EpicTask extends Task {

    private ArrayList<Subtask> epicSubtasks = new ArrayList<>();
    private TreeSet<Subtask> subtasksTree;


    public EpicTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public ArrayList<Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }

    private long calculateEpicDuration() {
        long sum = 0;
        for (Subtask s : epicSubtasks) {
            sum += s.getDuration();
        }
        return sum;
    }

    public LocalDateTime getStartTime() {
        subtasksTree = new TreeSet<>(epicSubtasks);
        return subtasksTree.first().getStartTime();
    }


    public long getDuration() {
        return calculateEpicDuration();
    }

    public LocalDateTime getEndTime() {
        subtasksTree = new TreeSet<>(epicSubtasks);
        return subtasksTree.last().getEndTime();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpicTask task = (EpicTask) o;
        return getName().equals(task.getName()) && getDescription().equals(task.getDescription())
                && getStatus().equals(task.getStatus()) && getId().equals(task.getId())
                && getEpicSubtasks().equals(task.getEpicSubtasks());
    }

}