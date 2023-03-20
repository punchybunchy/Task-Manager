package hexlet.code.app.repository;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends CrudRepository<TaskStatus, Long> {
    Optional<TaskStatus> findByName(String name);
    List<TaskStatus> findAll();
}
