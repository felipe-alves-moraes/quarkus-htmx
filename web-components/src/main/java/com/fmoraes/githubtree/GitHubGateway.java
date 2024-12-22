package com.fmoraes.githubtree;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.List;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

@ApplicationScoped
public class GitHubGateway {

    public List<String> getRepositories(String username) {
        try {
            final var gitHub = GitHub.connectAnonymously();
            final var gitHubUser = gitHub.getUser(username);
            final var ghRepositories = gitHubUser.listRepositories();
            return ghRepositories.toList().stream()
                .map(GHRepository::getName)
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RepositoryRelease> getRepositoryReleases(String username, String repositoryName) {
        try {
            final var gitHub = GitHub.connectAnonymously();
            final var gitHubUser = gitHub.getUser(username);
            final var ghRepositories = gitHubUser.getRepository(repositoryName);
            return ghRepositories.listReleases().toList()
                .stream()
                .map(ghRelease -> new RepositoryRelease(ghRelease.getId(), ghRelease.getName()))
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRepositoryReleases(final String username, final String name, final long releaseId) {
        try {
            final var github = GitHub.connectAnonymously();
            final var wimdeblauwe = github.getUser(username);
            final var repository = wimdeblauwe.getRepository(name);
            return repository.getRelease(releaseId).getBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
