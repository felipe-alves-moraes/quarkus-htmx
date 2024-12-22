package com.fmoraes.inlineediting.issue.usecase;

import com.fmoraes.inlineediting.issue.Issue;
import com.fmoraes.inlineediting.issue.SubTask;
import com.fmoraes.inlineediting.issue.repository.IssueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ReorderSubtasksUseCase {
    private final IssueRepository issueRepository;

    public ReorderSubtasksUseCase(final IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Issue execute(String key, List<Integer> subTaskOrder) {

        final var issue = issueRepository.getIssue(key);
        final var subTasks = issue.getSubTasks();
        final var reordered = new ArrayList<SubTask>();
        for (var order : subTaskOrder) {
            reordered.add(subTasks.get(order));
        }
        issue.setSubTasks(reordered);
        issueRepository.saveIssue(issue);
        return issue;
    }
}
