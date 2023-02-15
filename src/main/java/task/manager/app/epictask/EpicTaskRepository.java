package task.manager.app.epictask;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpicTaskRepository extends JpaRepository<EpicTask, Long> {

    String graph = "EpicTask.subtasks";

    @EntityGraph(value = graph)
    Optional<EpicTask> findById(Long epicId);

    @EntityGraph(value = graph)
    List<EpicTask> findAll();

}
