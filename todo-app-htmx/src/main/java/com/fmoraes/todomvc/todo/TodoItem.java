package com.fmoraes.todomvc.todo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class TodoItem {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String title;
    private boolean completed;

    protected TodoItem() {
    }

    public TodoItem(final String title, final boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(final boolean completed) {
        this.completed = completed;
    }
}
