import java.util.ArrayList;

public class EpicTask extends Task {

    private ArrayList<Subtask> epicSubtasks;

    public EpicTask(String name, String description, String status) {
        super(name, description, status);
        epicSubtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }
}