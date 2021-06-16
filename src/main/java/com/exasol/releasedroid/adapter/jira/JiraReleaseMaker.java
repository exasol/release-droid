package com.exasol.releasedroid.adapter.jira;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_STATE_DIRECTORY;

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
        final String linkToReleaseAnnouncement = getLinkToReleaseAnnouncement(repository);
        final var projectName = "MARCOMMS";
        final var issueTypeName = "New Content";
        final var summary = repository.getName() + " " + repository.getVersion() + " released";
        final var description = "The integration team has prepared a release announcement: " //
                + linkToReleaseAnnouncement + LINE_SEPARATOR //
                + "Please, review and publish the announcement.";
        try {
            final String linkToTicket = this.jiraGateway.createTicket(projectName, issueTypeName, summary, description);
            LOGGER.info(() -> "A Jira ticket was created at: " + linkToTicket);
            return linkToTicket;
        } catch (final JiraException exception) {
            throw new ReleaseException(exception);
        }
    }

    private String getLinkToReleaseAnnouncement(final Repository repository) {
        final Map<PlatformName, String> progress = this.releaseState.getProgress(repository.getName(),
                repository.getVersion());
        return progress.get(PlatformName.COMMUNITY);
    }
}