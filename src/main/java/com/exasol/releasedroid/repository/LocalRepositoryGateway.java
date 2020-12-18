package com.exasol.releasedroid.repository;

import com.exasol.releasedroid.usecases.*;

public class LocalRepositoryGateway implements RepositoryGateway {
    @Override
    public Repository getRepository(final UserInput userInput) {
        return new LocalRepository(userInput.getLocalPath(), userInput.getRepositoryName());
    }
}