package hexlet.code.repository;

import hexlet.code.model.QTask;
import hexlet.code.model.Task;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long>, QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {
    Optional<Task> findById(Long id);
    List<Task> findAll();

    @Override
    default void customize(QuerydslBindings bindings, QTask root) {

    }
}
