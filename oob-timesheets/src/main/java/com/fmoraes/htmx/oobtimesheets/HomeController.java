package com.fmoraes.htmx.oobtimesheets;

import com.fmoraes.htmx.oobtimesheets.project.Project;
import com.fmoraes.htmx.oobtimesheets.project.ProjectService;
import com.fmoraes.htmx.oobtimesheets.timeregistration.TimeRegistrationService;
import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Path("/")
public class HomeController extends HxController {

    private final ProjectService projectService;
    private final TimeRegistrationService timeRegistrationService;

    public HomeController(final ProjectService projectService, final TimeRegistrationService timeRegistrationService) {
        this.projectService = projectService;
        this.timeRegistrationService = timeRegistrationService;
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index(List<Project> projects, List<LocalDate> days, Duration total,
            Map<String, Duration> daysTotal);

        public static native TemplateInstance index$overallTotal(final Duration total);
        public static native TemplateInstance index$dayTotal(final LocalDate day, final Map<String, Duration> daysTotal);
    }

    @GET
    @Path("/")
    public TemplateInstance index(HttpServerRequest request) {

        final var locale = request.getHeader("Accept-Language").split(",")[0];
        final var days = getDaysOfCurrentWeek(Locale.forLanguageTag(locale));

        final var daysTotal = days.stream()
            .collect(Collectors.toMap(
                    day -> "dayTotal_" + day.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    day -> getTotal(List.of(day))
                )
            );
        return Templates.index(projectService.getProjects(), days, getTotal(days), daysTotal);
    }

    @PUT
    @Path("/projects/{projectId}/{date}")
    public TemplateInstance updateTimeRegistration(HttpServerRequest request, @PathParam("projectId") int projectId,
        @PathParam("date") LocalDate date, @FormParam("value") Double value) {

        final var duration = value == null ? Duration.ZERO : Duration.ofMinutes((long) (value * 60.0));
        timeRegistrationService.addOrUpdateRegistration(projectId, date, duration);

        final var locale = request.getHeader("Accept-Language").split(",")[0];

        return concatTemplates(
            Templates.index$overallTotal(getTotal(getDaysOfCurrentWeek(Locale.forLanguageTag(locale)))),
            Templates.index$dayTotal(date, Map.of("dayTotal_" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                getTotal(List.of(date))))
        );
    }

    private Duration getTotal(List<LocalDate> daysOfCurrentWeek) {
        final var projectIds = projectService.getProjects()
            .stream()
            .map(Project::id)
            .collect(Collectors.toSet());

        return timeRegistrationService.getTotal(projectIds, Set.copyOf(daysOfCurrentWeek));
    }

    private List<LocalDate> getDaysOfCurrentWeek(Locale locale) {
        final var now = LocalDate.now();
        final var firstDayOfWeek = WeekFields.of(locale).getFirstDayOfWeek();
        final var firstDay = now.with(firstDayOfWeek);

        return Stream.iterate(firstDay, date -> date.plusDays(1))
            .limit(7)
            .toList();
    }
}
