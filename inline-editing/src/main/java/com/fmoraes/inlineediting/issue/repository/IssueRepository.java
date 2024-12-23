package com.fmoraes.inlineediting.issue.repository;

import com.fmoraes.inlineediting.issue.Issue;
import com.fmoraes.inlineediting.issue.IssuePriority;
import com.fmoraes.inlineediting.issue.IssueType;
import com.fmoraes.inlineediting.issue.Status;
import com.fmoraes.inlineediting.issue.SubTask;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class IssueRepository {

    private final Map<String, Issue> issues = new HashMap<>();

    public IssueRepository() {
        issues.putAll(Stream.of(
                new Issue("XXX-123", "As a web developer, I want to use htmx", IssueType.STORY,
                    IssuePriority.MEDIUM, "1.0",
                    List.of(
                        new SubTask("XXX-124", "Read website htmx.org", Status.IN_PROGRESS),
                        new SubTask("XXX-125", "Subscribe on htmx discord", Status.TODO),
                        new SubTask("XXX-126", "Learn about various hx-trigger options", Status.TODO)
                    )
                ))
            .collect(Collectors.toMap(Issue::getKey,
                Function.identity()))
        );
    }

    public Issue getIssue(String key) {
        return issues.get(key);
    }

    public void saveIssue(Issue issue) {
        issues.put(issue.getKey(), issue);
    }
}
