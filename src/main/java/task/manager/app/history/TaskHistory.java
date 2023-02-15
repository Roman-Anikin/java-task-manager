package task.manager.app.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import task.manager.app.task.Task;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "task_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Task task;

    @Override
    public String toString() {
        return "TaskHistory{" +
                "task=" + task +
                '}';
    }
}
