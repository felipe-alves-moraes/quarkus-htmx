package com.fmoraes.todomvc.todo.web;

import com.fmoraes.todomvc.todo.TodoItem;
import com.fmoraes.todomvc.todo.TodoItemNotFoundException;
import com.fmoraes.todomvc.todo.TodoItemRepository;
import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateEnum;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;

@ApplicationScoped
@Path("/todo")
public class TodoItemController extends HxController {

    private final TodoItemRepository repository;

    @CheckedTemplate
    public static class Templates {

        public static native TemplateInstance index(TodoItemFormData item, ListFilter filter,
            List<TodoItemDto> todos, Long totalNumberOfItems, Long numberOfActiveItems, Long numberOfCompletedItems);

        public static native TemplateInstance fragments$todoItem(TodoItemDto item, Long totalNumberOfItems);
        public static native TemplateInstance fragments$activeItemCount(Long numberOfActiveItems);
        public static native TemplateInstance index$todoListItems(List<TodoItemDto> todos);
    }

    public TodoItemController(final TodoItemRepository repository) {
        this.repository = repository;
    }

    @GET
    @Path("/")
    public TemplateInstance index() {
        return Templates.index(new TodoItemFormData(), ListFilter.ALL, getTodoItems(ListFilter.ALL),
            repository.quatity(), getNumberOfActiveItems(), getNumberOfCompletedItems());
    }

    @GET
    @Path("/active")
    public TemplateInstance indexActive() {
        return Templates.index(new TodoItemFormData(), ListFilter.ACTIVE, getTodoItems(ListFilter.ACTIVE),
            repository.quatity(), getNumberOfActiveItems(), getNumberOfCompletedItems());
    }

    @GET
    @Path("/completed")
    public TemplateInstance indexCompleted() {
        return Templates.index(new TodoItemFormData(), ListFilter.COMPLETED, getTodoItems(ListFilter.COMPLETED),
            repository.quatity(), getNumberOfActiveItems(), getNumberOfCompletedItems());
    }

    @POST
    @Path("/")
    public TemplateInstance addNewTodoItem(@Valid @FormParam("title") @NotBlank String title) {
        onlyHxRequest();
        final var todoItem = repository.save(new TodoItem(title, false));

        hx(HxResponseHeader.TRIGGER, "itemAdded");

        return Templates.fragments$todoItem(new TodoItemDto(todoItem.getId(),
            todoItem.getTitle(),
            todoItem.isCompleted()), repository.quatity());
    }

    @PUT
    @Path("/{id}/toggle")
    public TemplateInstance toggleSelection(@PathParam("id") Long id) {
        onlyHxRequest();
        final var todoItem = repository.find(id)
            .orElseThrow(() -> new TodoItemNotFoundException(id));

        todoItem.setCompleted(!todoItem.isCompleted());
        repository.save(todoItem);

        hx(HxResponseHeader.TRIGGER, "itemCompletionToggle");

        return Templates.fragments$todoItem(new TodoItemDto(todoItem.getId(),
            todoItem.getTitle(),
            todoItem.isCompleted()), repository.quatity());
    }

    @PUT
    @Path("/toggle-all")
    public TemplateInstance toggleAll() {
        repository.all()
            .forEach(todoItem -> {
                todoItem.setCompleted(!todoItem.isCompleted());
                repository.save(todoItem);
            });

        return Templates.index$todoListItems(getTodoItems(ListFilter.ALL));
    }

    @DELETE
    @Path("/{id}")
    public String deleteTodoItem(@PathParam("id") Long id) {
        onlyHxRequest();
        repository.delete(id);

        hx(HxResponseHeader.TRIGGER, "itemDeleted");
        return "";
    }

    @DELETE
    @Path("/completed")
    public String deleteCompletedItems() {
        onlyHxRequest();
        repository.findAllByCompleted(true)
        .forEach(todoItem -> repository.delete(todoItem.getId()));

        hx(HxResponseHeader.TRIGGER, "itemDeleted");
        return "";
    }

    @GET
    @Path("/active-items-count")
    public TemplateInstance htmxActiveItemsCount() {
        onlyHxRequest();

        return Templates.fragments$activeItemCount(getNumberOfActiveItems());
    }

    private List<TodoItemDto> getTodoItems(ListFilter filter) {
        return switch (filter) {
            case ALL -> convertToDto(repository.all());
            case ACTIVE -> convertToDto(repository.findAllByCompleted
                (false));
            case COMPLETED -> convertToDto(repository.
                findAllByCompleted(true));
        };
    }

    private List<TodoItemDto> convertToDto(List<TodoItem> todoItems) {
        return todoItems
            .stream()
            .map(todoItem -> new TodoItemDto(todoItem.getId(),
                todoItem.getTitle(),
                todoItem.isCompleted()))
            .toList();
    }

    private long getNumberOfActiveItems() {
        return repository.countAllByCompleted(false);
    }

    private long getNumberOfCompletedItems() {
        return repository.countAllByCompleted(true);
    }

    public record TodoItemDto(long id, String title, boolean completed) {

    }

    @TemplateEnum
    public enum ListFilter {
        ALL,
        ACTIVE,
        COMPLETED;
    }
}
