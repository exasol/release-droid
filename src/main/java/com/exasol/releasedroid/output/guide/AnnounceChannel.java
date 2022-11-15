package com.exasol.releasedroid.output.guide;

import java.util.HashMap;
import java.util.Map;

// [impl->dsn~release-guide-channels~1]
class AnnounceChannel {

    private static final Map<String, AnnounceChannel> channels = new HashMap<>();

    static {
        channels.put("customer", new AnnounceChannel("#global-product-news", "customer_channel"));
        channels.put("team", new AnnounceChannel("#integration", "team_channel"));
    }

    static final AnnounceChannel find(final String targetAudience) {
        final AnnounceChannel channel = channels.get(targetAudience);
        return channel != null //
                ? channel
                : new AnnounceChannel(null, null);
    }

    String label;
    String property;

    AnnounceChannel(final String label, final String property) {
        this.label = label;
        this.property = property;
    }
}