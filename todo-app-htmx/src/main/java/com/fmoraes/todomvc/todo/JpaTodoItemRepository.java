package com.fmoraes.todomvc.todo;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JpaTodoItemRepository implements TodoItemRepository, PanacheRepository<TodoItem> {

    @Override
    public TodoItem save(final TodoItem todoItem) {
        persist(todoItem);
        return todoItem;
    }

    @Override
    public Optional<TodoItem> find(final Long id) {
        return findByIdOptional(id);
    }

    @Override
    public List<TodoItem> all() {
        return findAll().stream().toList();
    }

    @Override
    public void delete(final Long id) {
        deleteById(id);
    }

    @Override
    public long quatity() {
        return count();
    }

    @Override
    public long countAllByCompleted(final boolean completed) {
        return count("completed = ?1", completed);
    }

    @Override
    public List<TodoItem> findAllByCompleted(final boolean completed) {
        return find("completed = ?1", completed).list();
    }
}
