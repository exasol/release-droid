package com.exasol.releasedroid.usecases;

/**
 * Responsible for reading user-specified properties.
 */
public interface PropertyReader {
    /**
     * Reads property by key.
     * 
     * @param key key
     * @return value
     */
    String readProperty(String key);
}