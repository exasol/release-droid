package com.exasol.releasedroid.usecases.release;

import java.util.List;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Interface for the Release use case.
 */
public interface ReleaseUseCase {
    /**
     * Make a new release.
     *
     * @param repository repository to validate
     * @param platforms  list of platforms to validate
     * @return list of reports
     */
    List<Report> release(Repository repository, List<PlatformName> platforms);
}