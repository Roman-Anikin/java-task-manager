package task.manager.app.subtask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import task.manager.app.epictask.EpicTask;
import task.manager.app.task.Task;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonDeserialize(using = SubtaskDeserializer.class)
@NamedEntityGraph(name = "Subtask.epicTask",
        attributeNodes = @NamedAttributeNode("epicTask"))
@DiscriminatorValue("3")
public class Subtask extends Task {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Отсутствует id эпика")
    @JsonIgnoreProperties("subtasks")
    private EpicTask epicTask;

    @Override
    public String toString() {
        return "Subtask{" +
                "epicTask=" + epicTask.getId() +
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
