package com.exasol.releasedroid.main;

import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

/**
 * The interface consumes the response from Release Droid.
 */
public interface ReleaseDroidResponseConsumer {
    /**
     * Consume response.
     *
     * @param response response
     */
    void consumeResponse(ReleaseDroidResponse response);
}