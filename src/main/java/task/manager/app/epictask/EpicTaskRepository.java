package task.manager.app.epictask;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface EpicTaskRepository extends JpaRepository<EpicTask, Long> {

    String graph = "EpicTask.subtasks";

    @EntityGraph(value = graph)
    @NonNull
    Optional<EpicTask> findById(@NonNull Long epicId);

    @EntityGraph(value = graph)
    @NonNull
    List<EpicTask> findAll();

}
