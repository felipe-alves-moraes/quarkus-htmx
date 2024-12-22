package com.fmoraes.inlineediting.issue.usecase;

import com.fmoraes.inlineediting.issue.Issue;
import com.fmoraes.inlineediting.issue.repository.IssueRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetIssueUseCase {
    private final IssueRepository issueRepository;

    public GetIssueUseCase(final IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Issue execute(String key) {
        return issueRepository.getIssue(key);
    }
}
