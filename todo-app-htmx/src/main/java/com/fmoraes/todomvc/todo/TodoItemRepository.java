package com.fmoraes.todomvc.todo;

import java.util.List;
import java.util.Optional;

public interface TodoItemRepository {

    TodoItem save(TodoItem todoItem);
    Optional<TodoItem> find(Long id);
    List<TodoItem> all();
    void delete(Long id);
    long quatity();
    long countAllByCompleted(boolean completed);
    List<TodoItem> findAllByCompleted(boolean completed);

}
