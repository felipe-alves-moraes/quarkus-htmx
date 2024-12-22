package com.fmoraes.inlineediting.issue;

import java.util.List;

public class Issue {

    private final String key;
    private String summary;
    private final IssueType type;
    private final IssuePriority priority;
    private final String fixVersion;
    private List<SubTask> subTasks;

    public Issue(final String key, final String summary, final IssueType type, final IssuePriority priority,
        final String fixVersion,
        final List<SubTask> subTasks) {
        this.key = key;
        this.summary = summary;
        this.type = type;
        this.priority = priority;
        this.fixVersion = fixVersion;
        this.subTasks = subTasks;
    }

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public IssueType getType() {
        return type;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public String getFixVersion() {
        return fixVersion;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(final List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
