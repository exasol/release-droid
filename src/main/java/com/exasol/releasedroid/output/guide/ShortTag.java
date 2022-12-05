package com.exasol.releasedroid.output.guide;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;

class ShortTag {
    static final String FILENAME = "error_code_config.yml";

    private final RepositoryGate gate;

    ShortTag(final RepositoryGate gate) {
        this.gate = gate;
    }

    String retrieve() {
        String content;
        try {
            content = this.gate.getSingleFileContentAsString(FILENAME);
        } catch (final RepositoryException exception) {
            return ReleaseGuideProperties.error("Could not retrieve shorttag: " + exception.getMessage());
        }
        final Map<String, Object> ecc = new Yaml().load(content);
        final Object errorTags = ecc.get("error-tags");
        if (errorTags instanceof Map) {
            return shortestKey((Map<?, ?>) errorTags);
        }
        return "";
    }

    private String shortestKey(final Map<?, ?> map) {
        String tag = "";
        for (final Object o : map.keySet()) {
            if (o instanceof String) {
                tag = shortest(tag, (String) o);
            }
        }
        return tag;
    }

    private String shortest(final String current, final String candidate) {
        return ((current == null) || candidate.startsWith(current)) //
                ? candidate
                : current;
    }
}
