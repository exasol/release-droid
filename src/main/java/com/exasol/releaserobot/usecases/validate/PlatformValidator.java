package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.usecases.Report;

/**
 * A common interface for classes performing validation depending on a platform.
 */
public interface PlatformValidator {
    /**
     * Validate a project.
     *
     * @param repository instance of {@link Repository}
     *
     * @return validation report
     */
    public Report validate(final Repository repository);
}