package com.exasol.releasedroid.usecases;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class retrieves a user property.
 */
public final class PropertyReaderImpl implements PropertyReader {
    private static final Logger LOGGER = Logger.getLogger(PropertyReaderImpl.class.getName());
    private final String pathToPropertyFile;

    /**
     * Create a new instance of {@link PropertyReaderImpl}.
     *
     * @param pathToPropertyFile path to a property file
     */
    public PropertyReaderImpl(final String pathToPropertyFile) {
        this.pathToPropertyFile = pathToPropertyFile;
    }

    @Override
    public String readProperty(final String key, final boolean hide) {
        final Optional<String> property = readFromFile(key);
        if (property.isPresent()) {
            LOGGER.fine(() -> "Using property '" + key + "' from the file '" + this.pathToPropertyFile + "'.");
            return property.get();
        } else {
            LOGGER.fine(() -> "Property '" + key + "' is not found in the file '" + this.pathToPropertyFile + "'.");
            return getCredentialsFromConsole(key, hide);
        }
    }

    private Optional<String> readFromFile(final String key) {
        try (final InputStream stream = new FileInputStream(this.pathToPropertyFile)) {
            final var properties = new Properties();
            properties.load(stream);
            final String value = properties.getProperty(key);
            if (value == null) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        } catch (final IOException exception) {
            return Optional.empty();
        }
    }

    private String getCredentialsFromConsole(final String key, final boolean hide) {
        final String description = "Enter " + key.replace("_", " ") + ": ";
        if (hide) {
            return String.valueOf(System.console().readPassword(description));
        } else {
            return System.console().readLine(description);
        }
    }
}