package com.exasol.releaserobot.usecases.release;

import java.util.List;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.UserInput;

/**
 * Interface for the Release use case.
 */
public interface ReleaseUseCase {
    /**
     * Make a new release.
     *
     * @param userInput user input
     * @return list of reports
     * @throws GitHubException
     */
    public List<Report> release(final UserInput userInput) throws GitHubException;
}