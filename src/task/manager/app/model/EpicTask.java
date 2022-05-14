package task.manager.app.model;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.TreeSet;

public class EpicTask extends Task {

    private TreeSet<Subtask> epicSubtasks = new TreeSet<>();

    public EpicTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public TreeSet<Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }

    private long calculateEpicDuration() {
        long sum = 0;
        if (epicSubtasks != null) {
            for (Subtask s : epicSubtasks) {
                sum += s.getDuration();
            }
        }
        return sum;
    }

    public LocalDateTime getStartTime() {
        if (epicSubtasks == null || epicSubtasks.isEmpty()) {
            return null;
        }
        return epicSubtasks.first().getStartTime();
    }

    public long getDuration() {
        return calculateEpicDuration();
    }

    public LocalDateTime getEndTime() {
        Iterator<Subtask> subtaskIterator = epicSubtasks.descendingIterator();
        while (subtaskIterator.hasNext()) {
            Subtask subtask = subtaskIterator.next();
            if (subtask.getStartTime() != null) {
                return subtask.getEndTime();
            }
        }
        return null;
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