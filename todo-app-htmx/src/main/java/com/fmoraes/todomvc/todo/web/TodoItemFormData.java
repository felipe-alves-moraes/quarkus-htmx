package com.fmoraes.todomvc.todo.web;

import jakarta.validation.constraints.NotBlank;

public class TodoItemFormData {
    @NotBlank
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
