package com.exasol.validation;

import com.exasol.ReleasePlatform;

/**
 * this is a common interface for release platforms validators.
 */
public interface ProjectValidator {
    /**
     * Validate common release requirements.
     */
    public void validatePlatformIndependent();

    /**
     * Validate release requirements depending on the platform.
     * 
     * @param releasePlatform release platform
     */
    public void validatePlatform(ReleasePlatform releasePlatform);
}