package task.manager.app.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tasks", nativeQuery = true)
    void removeAll();

    @Query(value = "SELECT * FROM tasks t " +
            "JOIN task_history th ON t.task_id = th.task_id", nativeQuery = true)
    List<Task> getHistory();

    List<Task> findByOrderByStartTime();

}
