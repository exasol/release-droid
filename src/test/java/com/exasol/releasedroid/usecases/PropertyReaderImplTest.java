package com.exasol.releasedroid.usecases;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.exasol.errorreporting.ExaError;

class PropertyReaderImplTest {
    @Test
    void testReadProperty() throws IOException {
        final Path toFile = createTemporaryFile("key", "some_value");
        final PropertyReader reader = new PropertyReaderImpl(toFile.toString());
        assertThat(reader.readProperty("key", false), equalTo("some_value"));
    }

    private Path createTemporaryFile(final String key, final String value) throws IOException {
        final String credentials = key + "=" + value + "\n";
        final Path tempFile = Files.createTempFile("rd_properties", "temp");
        Files.write(tempFile, credentials.getBytes());
        return tempFile;
    }

    @Test
    void testReadPropertyWithEmptyValue() throws IOException {
        final Path toFile = createTemporaryFile("key", "");
        final PropertyReader reader = new PropertyReaderImpl(toFile.toString(), (key, hide) -> "value_from_console");
        assertThat(reader.readProperty("key", false), equalTo("value_from_console"));
    }

    @Test
    void testReadPropertyWithMissingValue() throws IOException {
        final Path toFile = createTemporaryFile("key", "");
        final PropertyReader reader = new PropertyReaderImpl(toFile.toString(), (key, hide) -> "value_from_console");
        assertThat(reader.readProperty("another_key", false), equalTo("value_from_console"));
    }

    @Test
    void testReadPropertyWithMissingFile() {
        final PropertyReader reader = new PropertyReaderImpl("wrong/path", (key, hide) -> "value_from_console");
        assertThat(reader.readProperty("another_key", false), equalTo("value_from_console"));
    }

    @Test
    void test() {
        final Exception exception = new RuntimeException("something");
        System.out.println(ExaError.messageBuilder("W-RD-GH-29")
                .message("Failed to retrieve duration of latest run of workflow {{workflow}}: {{cause|uq}}.", //
                        "workflowName", exception.getMessage()) //
                .mitigation("Execution workflow with empty estimation.") //
                .toString());
    }
}