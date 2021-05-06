package com.exasol.releasedroid.adapter.github;

import java.io.IOException;

import org.kohsuke.github.GitHub;

/**
 * This interface abstracts the connection to the real GitHub server to allow API mock testing.
 */
public interface GitHubConnector {
    /**
     * Connect to GitHub.
     * 
     * @return instance of {@link GitHub}
     */
    GitHub connectToGitHub() throws IOException;
}