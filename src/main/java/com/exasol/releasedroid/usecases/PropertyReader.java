package com.exasol.releasedroid.usecases;

/**
 * Responsible for reading user-specified properties.
 */
public interface PropertyReader {
    /**
     * Reads property by key.
     * 
     * @param key  key
     * @param hide specify if you want to hide the credentials
     * @return value
     */
    String readProperty(String key, boolean hide);
}