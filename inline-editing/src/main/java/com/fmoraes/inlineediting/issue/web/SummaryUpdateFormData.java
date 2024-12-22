package com.fmoraes.inlineediting.issue.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;

public class SummaryUpdateFormData {

    @NotBlank
    @FormParam("summary")
    private String summary;

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }
}
