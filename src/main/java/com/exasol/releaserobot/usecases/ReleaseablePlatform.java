package com.exasol.releaserobot.usecases;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.usecases.release.ReleaseMaker;
import com.exasol.releaserobot.usecases.validate.RepositoryValidator;

public class ReleaseablePlatform implements RepositoryValidator, ReleaseMaker {

    private final RepositoryValidator repositoryValidator;
    private final ReleaseMaker releaseMaker;

    public ReleaseablePlatform(final RepositoryValidator repositoryValidator, final ReleaseMaker releaseMaker) {
        super();
        this.repositoryValidator = repositoryValidator;
        this.releaseMaker = releaseMaker;
    }

    @Override
    public void makeRelease(final Repository repository) throws GitHubException {
        this.releaseMaker.makeRelease(repository);
    }

    @Override
    public Report validate(final Repository repository) {
        return this.repositoryValidator.validate(repository);
    }

}
