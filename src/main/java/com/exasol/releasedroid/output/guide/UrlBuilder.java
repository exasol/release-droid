package com.exasol.releasedroid.output.guide;

import java.nio.file.Path;
import java.nio.file.Paths;

class UrlBuilder {
    private String prefix = "";
    private String infix = "/";
    private String suffix = "/";

    UrlBuilder prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    UrlBuilder infix(final String infix) {
        this.infix = infix;
        return this;
    }

    UrlBuilder suffix(final String suffix) {
        this.suffix = suffix;
        return this;
    }

    String build(final String repo, final String version) {
        return this.prefix + repo + this.infix + version + this.suffix;
    }

    Path path(final String repo, final String version) {
        return Paths.get(build(repo, version));
    }
}