package com.fmoraes.htmx.oobtimesheets.timeregistration;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public class TimeRegistration {
    private int projectId;
    private LocalDate date;
    private Duration duration;

    public TimeRegistration(final int projectId, final LocalDate date, final Duration duration) {
        this.projectId = projectId;
        this.date = date;
        this.duration = duration;
    }

    public int getProjectId() {
        return projectId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final TimeRegistration that)) {
            return false;
        }
        return projectId == that.projectId && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, date);
    }
}
