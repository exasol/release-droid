package com.exasol.releasedroid.usecases.validate;

import java.util.List;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Interface for the Validate use case.
 */
public interface ValidateUseCase {
    /**
     * Validate project.
     *
     * @param repository repository to validate
     * @param platforms  list of platforms to validate
     * @return new {@link Report}
     */
    Report validate(Repository repository, List<PlatformName> platforms);
}