package task.manager.app.epictask;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import task.manager.app.subtask.Subtask;
import task.manager.app.task.Task;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedEntityGraph(name = "EpicTask.subtasks",
        attributeNodes = @NamedAttributeNode("subtasks"))
@DiscriminatorValue("2")
public class EpicTask extends Task {

    @OneToMany(mappedBy = "epicTask",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @OrderBy("start_time")
    private List<Subtask> subtasks = new ArrayList<>();

    @Override
    public String toString() {
        return "EpicTask{" +
                "subtasks=" + subtasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
