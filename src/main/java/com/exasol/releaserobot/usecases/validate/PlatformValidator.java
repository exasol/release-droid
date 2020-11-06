package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.Repository;

/**
 * A common interface for classes performing validation depending on a platform.
 */
public interface PlatformValidator {
    /**
     * Validate a project.
     *
     * @param repository instance of {@link Repository}
     * @return validation report
     */
    public Report validate(final Repository repository);
}