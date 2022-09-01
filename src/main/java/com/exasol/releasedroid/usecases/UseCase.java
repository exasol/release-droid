package com.exasol.releasedroid.usecases;

import java.util.List;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.ReleasePlatforms;

/**
 * Use case can be either validate or release
 */
public interface UseCase {
    /**
     * @param repository repository to consider
     * @param platforms  platforms to include in the current use case
     * @return findings when applying the use case
     */
    List<Report> apply(Repository repository, ReleasePlatforms platforms);
}
