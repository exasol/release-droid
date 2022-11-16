package com.exasol.releasedroid.output.guide;

import java.util.Collection;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.github.GitHubRepositoryGate;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;

// [impl->dsn~target-audience~1]
class TargetAudience {

    static final String PROJECT_OVERVIEW_REPO = "exasol/project-overview";
    static final String INVENTORY = "projects.yaml";
    private static final String AUDIENCE_PROPERTY = "target_audience";
    private static final String DEFAULT_AUDIENCE = "team";

//    static TargetAudience retrieve(final PropertyReader propertyReader, final String repoName) {
//        final GitHubGateway githubGateway = new GitHubAPIAdapter(new GitHubConnectorImpl(propertyReader));
//        final GitHubRepositoryGate gate = new GitHubRepositoryGate(githubGateway, "main", PROJECT_OVERVIEW_REPO);
//        return retrieve(gate, repoName);
//    }

    static TargetAudience retrieve(final GitHubGateway githubGateway, final String repoName) {
        final GitHubRepositoryGate gate = new GitHubRepositoryGate(githubGateway, "main", PROJECT_OVERVIEW_REPO);
        return retrieve(gate, repoName);
    }

    static TargetAudience retrieve(final RepositoryGate gate, final String repoName) {
        final String inventory;
        try {
            inventory = gate.getSingleFileContentAsString(INVENTORY);
        } catch (final RepositoryException exception) {
            return failed(exception);
        }
        final Map<String, Object> projectOverview = new Yaml().load(inventory);
        for (final Object o : (Collection<?>) projectOverview.get("projects")) {
            if (o instanceof Map<?, ?>) {
                final Map<?, ?> project = (Map<?, ?>) o;
                final String id = (String) project.get("id");
                if (repoName.equals(id)) {
                    return fromValueOrDefault((String) project.get(AUDIENCE_PROPERTY));
                }
            }
        }
        return failed(null);
    }

    static TargetAudience fromValueOrDefault(final String name) {
        return new TargetAudience(null, name != null ? name : DEFAULT_AUDIENCE);
    }

    static TargetAudience failed(final RepositoryException exception) {
        return new TargetAudience(exception, null);
    }

    private final RepositoryException exception;
    private final String name;

    private TargetAudience(final RepositoryException exception, final String name) {
        this.exception = exception;
        this.name = name;
    }

    String display() {
        return (this.name != null) //
                ? this.name
                : ReleaseGuideProperties.error("Failed to retrieve target audience: " + suffix());
    }

    private String suffix() {
        return this.exception == null ? "" : " " + this.exception.getMessage();
    }

    boolean available() {
        return (this.exception == null) & (this.name != null);
    }
}
