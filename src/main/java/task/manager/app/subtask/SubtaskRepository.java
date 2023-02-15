package task.manager.app.subtask;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    String graph = "Subtask.epicTask";

    @EntityGraph(value = graph)
    Optional<Subtask> findById(Long subtaskId);

    @EntityGraph(value = graph)
    List<Subtask> findAll();

}
