package com.fmoraes.inlineediting.issue.usecase;

import com.fmoraes.inlineediting.issue.Issue;
import com.fmoraes.inlineediting.issue.repository.IssueRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateSummaryUseCase {

    private final IssueRepository repository;

    public UpdateSummaryUseCase(final IssueRepository repository) {
        this.repository = repository;
    }

    public Issue execute(String key, String summary) {
        final var issue = repository.getIssue(key);
        issue.setSummary(summary);
        repository.saveIssue(issue);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return issue;
    }
}
