package com.exasol.releasedroid.usecases;

import com.exasol.releasedroid.github.GitHubException;

public interface ReleaseManager {
    void prepareForRelease(Repository repository) throws GitHubException;

    void cleanUpAfterRelease(Repository repository) throws GitHubException;
}