package com.fmoraes.inlineediting.issue.web;

import com.fmoraes.inlineediting.issue.Issue;
import com.fmoraes.inlineediting.issue.usecase.GetIssueUseCase;
import com.fmoraes.inlineediting.issue.usecase.ReorderSubtasksUseCase;
import com.fmoraes.inlineediting.issue.usecase.UpdateSummaryUseCase;
import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@Path("/issues")
public class IssueController extends HxController {

    private static final Logger LOG = Logger.getLogger(IssueController.class);
    private final GetIssueUseCase getIssueUseCase;
    private final UpdateSummaryUseCase updateSummaryUseCase;
    private final ReorderSubtasksUseCase reorderSubtasksUseCase;

    public IssueController(final GetIssueUseCase getIssueUseCase, final UpdateSummaryUseCase updateSummaryUseCase,
        final ReorderSubtasksUseCase reorderSubtasksUseCase) {
        this.getIssueUseCase = getIssueUseCase;
        this.updateSummaryUseCase = updateSummaryUseCase;
        this.reorderSubtasksUseCase = reorderSubtasksUseCase;
    }

    @CheckedTemplate
    public static class Templates {

        public static native TemplateInstance issue(Issue issue);
        public static native TemplateInstance issue$subtaskItems(Issue issue);

        public static native TemplateInstance fragments$issueSummaryView(Issue issue);

        public static native TemplateInstance fragments$issueSummaryEdit(Issue issue, SummaryUpdateFormData formData,
            Map<String, String> errors);
    }

    @Path("/{key}")
    @GET
    public TemplateInstance showIssue(@PathParam("key") String key) {
        final var issue = this.getIssueUseCase.execute(key);

        return Templates.issue(issue);
    }

    @Path("/{key}/summary/inline-edit-form")
    @GET
    public TemplateInstance summaryInlineEditForm(@PathParam("key") String key) {
        final var issue = this.getIssueUseCase.execute(key);

        final var summaryUpdateFormData = new SummaryUpdateFormData();
        summaryUpdateFormData.setSummary(issue.getSummary());

        return Templates.fragments$issueSummaryEdit(issue, summaryUpdateFormData, Map.of());
    }

    @GET
    @Path("/{key}/summary")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance summaryView(@PathParam("key") String key) {
        final var issue = getIssueUseCase.execute(key);
        return Templates.fragments$issueSummaryView(issue);
    }

    @PUT
    @Path("/{key}/summary")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance summaryUpdate(@PathParam("key") String key,
        @Valid @BeanParam SummaryUpdateFormData formData) {
        if (validation.hasErrors()) {
            final var issue = getIssueUseCase.execute(key);
            return Templates.fragments$issueSummaryEdit(issue, formData,
                Map.of("summary", validation.getError("summary")));
        }

        final var issue = updateSummaryUseCase.execute(key, formData.getSummary());
        return Templates.fragments$issueSummaryView(issue);
    }

    @PUT
    @Path("/{key}/subtasks")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance reorderSubtasks(@PathParam("key") String key,
        @FormParam("subTaskOrder") List<Integer> subTaskOrder) {
        final var issue = reorderSubtasksUseCase.execute(key, subTaskOrder);

        return Templates.issue$subtaskItems(issue);
    }
}
