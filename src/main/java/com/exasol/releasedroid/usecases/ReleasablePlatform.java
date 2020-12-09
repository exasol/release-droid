package com.exasol.releasedroid.usecases;

import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Provides a high-level interaction with a repository.
 */
public class ReleasablePlatform implements RepositoryValidator, ReleaseMaker {
    private final RepositoryValidator repositoryValidator;
    private final ReleaseMaker releaseMaker;

    /**
     * Create a new instance of {@link ReleasablePlatform}
     *
     * @param repositoryValidator instance of {@link RepositoryValidator}
     * @param releaseMaker        instance of {@link ReleaseMaker}
     */
    public ReleasablePlatform(final RepositoryValidator repositoryValidator, final ReleaseMaker releaseMaker) {
        this.repositoryValidator = repositoryValidator;
        this.releaseMaker = releaseMaker;
    }

    @Override
    public void makeRelease(final Repository repository) throws ReleaseException {
        this.releaseMaker.makeRelease(repository);
    }

    @Override
    public Report validate(final Repository repository) {
        return this.repositoryValidator.validate(repository);
    }
}