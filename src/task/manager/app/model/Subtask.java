package task.manager.app.model;

public class Subtask extends Task implements Comparable<Task> {

    private Long epicId;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() + ", epicID=" + epicId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask task = (Subtask) o;
        return getName().equals(task.getName()) && getDescription().equals(task.getDescription())
                && getStatus().equals(task.getStatus()) && getId().equals(task.getId())
                && getEpicId().equals(task.getEpicId());
    }
}