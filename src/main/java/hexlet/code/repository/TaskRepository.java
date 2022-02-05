package hexlet.code.repository;

import hexlet.code.model.Task;
import hexlet.code.model.QTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@SuppressWarnings("checkstyle:RegexpSingleline")
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {

    // Используем дефолтное поведение, т.е. проверка атрибутов на равенство
    // Если требуется изменить условие, то раскомментировать customize
    @Override
    default void customize(QuerydslBindings bindings, QTask task) {
       /*
        // взять 1-й по счёту параметр 'taskStatus' и проверить на равенство
        bindings.bind(task.taskStatus.id).first(
                (path, value) -> path.eq(value));

        bindings.bind(task.executor.id).first(
                (path, value) -> path.eq(value));

        bindings.bind(task.labels.any().id).first(
                (path, value) -> path.eq(value));

        bindings.bind(task.author.id).first(
                (path, value) -> path.eq(value));*/
    }
}
