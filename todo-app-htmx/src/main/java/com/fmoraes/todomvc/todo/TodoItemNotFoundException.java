package com.fmoraes.todomvc.todo;

public class TodoItemNotFoundException extends RuntimeException {

    public TodoItemNotFoundException(final Long id) {
        super("TodoItem with id %s not found".formatted(id));
    }
}
