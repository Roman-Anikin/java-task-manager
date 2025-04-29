package task.manager.app.subtask;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    String graph = "Subtask.epicTask";

    @EntityGraph(value = graph)
    @NonNull
    Optional<Subtask> findById(@NonNull Long subtaskId);

    @EntityGraph(value = graph)
    @NonNull
    List<Subtask> findAll();

}
