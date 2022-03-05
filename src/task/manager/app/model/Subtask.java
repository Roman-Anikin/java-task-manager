package task.manager.app.model;

public class Subtask extends Task {

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
}