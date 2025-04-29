package task.manager.app.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER,
        name = "task_type_id", columnDefinition = "INTEGER")
@DiscriminatorValue(value = "1")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_gen")
    @SequenceGenerator(name = "task_gen", sequenceName = "task_seq", allocationSize = 1)
    @Column(name = "task_id", nullable = false)
    protected Long id;

    @NotEmpty(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    protected String name;

    protected String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Статус не может быть пустым")
    protected TaskStatus status;

    @Column(name = "duration")
    @JsonFormat(pattern = "HH:mm")
    protected LocalTime duration = LocalTime.of(0, 0);

    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    protected LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    protected LocalDateTime endTime;

}
