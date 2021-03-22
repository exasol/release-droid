package com.exasol.releasedroid.usecases.repository;

/**
 * Write changes to a repository.
 */
public interface RepositoryModifier {
    /**
     * Write today's date to a changes file.
     * 
     * @param repository repository to write to
     */
    public void writeReleaseDate(Repository repository);
}