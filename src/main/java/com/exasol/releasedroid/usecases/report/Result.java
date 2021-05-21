package com.exasol.releasedroid.usecases.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * A result of Release Droid action.
 */
public class Result {
    private final boolean successful;
    private final String actionDescription;
    private final String message;
    private final List<PlatformName> platformNames = new ArrayList<>();

    protected Result(final boolean successful, final String actionDescription, final String message) {
        this.successful = successful;
        this.actionDescription = actionDescription;
        this.message = message;
    }

    /**
     * Check is a validation is successful.
     *
     * @return true if a validation is successful
     */
    public boolean isFailed() {
        return !this.successful;
    }

    /**
     * Get the action description.
     *
     * @return action description
     */
    public String getActionDescription() {
        return this.actionDescription;
    }

    /**
     * Get the message.
     *
     * @return message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Get the platform names.
     *
     * @return platform names
     */
    public List<PlatformName> getPlatformNames() {
        return this.platformNames;
    }

    protected void addPlatforms(final List<PlatformName> platformNames) {
        for (final PlatformName platformName : platformNames) {
            if (!this.platformNames.contains(platformName)) {
                this.platformNames.add(platformName);
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Result result = (Result) o;
        return this.successful == result.successful && this.actionDescription.equals(result.actionDescription)
                && this.message.equals(result.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.successful, this.actionDescription, this.message);
    }

    @Override
    public String toString() {
        return "Result{" + "successful=" + this.successful + ", actionDescription='" + this.actionDescription + '\''
                + ", message='" + this.message + '\'' + ", platformNames=" + this.platformNames + '}';
    }
}