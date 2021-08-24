package com.exasol.releasedroid.adapter.jira;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_STATE_DIRECTORY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.release.ReleaseState;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Release maker for Jira platform.
 */
public class JiraReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(JiraReleaseMaker.class.getName());
    private static final String JIRA_CONFIG_PATH = "release_config.yml";
    private final ReleaseState releaseState = new ReleaseState(RELEASE_DROID_STATE_DIRECTORY);
    private final JiraGateway jiraGateway;

    /**
     * Create a new instance of {@link JiraReleaseMaker}.
     *
     * @param jiraGateway jira gateway
     */
    public JiraReleaseMaker(final JiraGateway jiraGateway) {
        this.jiraGateway = jiraGateway;
    }

    @Override
    public String makeRelease(final Repository repository) throws ReleaseException {
        LOGGER.fine("Creating a Jira ticket.");
        try {
            final String linkToTicket = createTicketRequest(repository);
            LOGGER.info(() -> "A Jira ticket was created at: " + linkToTicket);
            return linkToTicket;
        } catch (final JiraException exception) {
            throw new ReleaseException(exception);
        }
    }

    private String createTicketRequest(final Repository repository) throws JiraException {
        final String linkToGitHubRelease = getLinkToGitHubRelease(repository);
        final var projectName = "EXACOMM";
        final var issueTypeName = "Task";
        final var summary = getHumanReadableName(repository) + " " + repository.getVersion() + " released";
        final var description = "Link to the GitHub release: " //
                + linkToGitHubRelease + LINE_SEPARATOR;
        return this.jiraGateway.createTicket(projectName, issueTypeName, summary, description);
    }

    private String getHumanReadableName(final Repository repository) {
        final String projectNameFromConfig = getProjectNameFromConfig(repository);
        if (projectNameFromConfig != null) {
            return projectNameFromConfig;
        } else {
            return getDefaultProjectName(repository.getName());
        }
    }

    private String getProjectNameFromConfig(final Repository repository) {
        return JiraConfigParser.parse(repository.getSingleFileContentAsString(JIRA_CONFIG_PATH));
    }

    protected String getDefaultProjectName(final String repositoryName) {
        final String[] fullName = repositoryName.split("/");
        final String repositoryNameWithoutOwner = fullName.length == 2 ? fullName[1] : repositoryName;
        final String[] words = repositoryNameWithoutOwner.split("-");
        final List<String> capitalizedWords = new ArrayList<>();
        for (final String word : words) {
            capitalizedWords.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
        }
        return String.join(" ", capitalizedWords);
    }

    private String getLinkToGitHubRelease(final Repository repository) {
        final Map<PlatformName, String> progress = this.releaseState.getProgress(repository.getName(),
                repository.getVersion());
        return progress.get(PlatformName.GITHUB);
    }
}