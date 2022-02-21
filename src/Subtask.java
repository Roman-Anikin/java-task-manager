public class Subtask extends Task {

    private Long epicId;

    public Subtask(String name, String description, String status) {
        super(name, description, status);
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }
}