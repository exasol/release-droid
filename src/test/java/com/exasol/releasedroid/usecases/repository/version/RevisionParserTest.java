package com.exasol.releasedroid.usecases.repository.version;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.repository.version.RevisionParser.ChangelogException;
import com.exasol.releasedroid.usecases.repository.version.RevisionParser.ConfigurationException;

@ExtendWith(MockitoExtension.class)
class RevisionParserTest {

    private static final String CHANGELOG = "[1.2.3]";

    @Mock
    private RepositoryGate repositoryGate;

    @Test
    void invalidChangelog() {
        assertThrows(ChangelogException.class, () -> RevisionParser.parse("][", ""));
    }

    @ParameterizedTest
    @ValueSource(strings = { "sources: value", "sources:\\n  - type" })
    void invalidConfiguration(final String content) {
        assertThrows(ConfigurationException.class, () -> RevisionParser.parse(CHANGELOG, content));
    }

    @Test
    void emptyConfiguration() throws Exception {
        assertThat(RevisionParser.parse(CHANGELOG, "").modules(), empty());
        assertThat(RevisionParser.parse(CHANGELOG, "abc: value").modules(), empty());
        assertThat(RevisionParser.parse(CHANGELOG, "sources: ").modules(), empty());
    }

    @ParameterizedTest
    @CsvSource(value = { "abc,", ",a/b/c", "golang, a/b/c" })
    void typeAndPath(final String type, final String path) throws Exception {
        String content = "sources:\n  - ";
        if (type != null) {
            content += "type: " + type + "\n    ";
        }
        if (path != null) {
            content += "path: " + path;
        }
        assertThat(RevisionParser.parse(CHANGELOG, content).modules(), containsInAnyOrder(new Module(type, path)));
    }
}
