package com.exasol.releasedroid.adapter.github;

import java.util.Collections;
import java.util.Map;

import com.exasol.releasedroid.progress.Progress;

/**
 * Instances of this class may contain map of dispatches and {@link Progress} for worflow execution.
 */
public class WorkflowOptions {

    private Map<String, Object> dispatches = Collections.emptyMap();
    private Progress progress = Progress.SILENT;

    /**
     * @param value dispatches to be used when executing the workflow
     * @return this for fluent programming
     */
    public WorkflowOptions withDispatches(final Map<String, Object> value) {
        this.dispatches = value;
        return this;
    }

    /**
     * @param value optional {@link Progress} to track and report progress of workflow execution
     * @return this for fluent programming
     */
    public WorkflowOptions withProgress(final Progress value) {
        this.progress = value;
        return this;
    }

    /**
     * @return optional dispatches for workflow execution
     */
    public Map<String, Object> dispatches() {
        return this.dispatches;
    }

    /**
     * @return optional {@link Progress} to track and report progress of workflow execution
     */
    public Progress progress() {
        return this.progress;
    }
}
