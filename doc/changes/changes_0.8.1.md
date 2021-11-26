# Exasol Release Droid 0.8.1, released 2021-11-25

Code name: Print release URLs

## Summary

This release prints the URL of the GitHub release and the new version's Maven Central repository location and validates the release date.

## Features

* #201: Added validation for credentials from a file.
* #208: Print URLs of all released artifacts
* #200: Added validation for release date
* #208: Added feature to print URLs of all released artifacts
* #208: Added feature to print URLs of all released artifacts and validate that groupId is available in pom.xml

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.0` to `0.4.1`
* Updated `commons-cli:commons-cli:1.4` to `1.5.0`
* Updated `org.apache.maven:maven-model:3.8.3` to `3.8.4`
* Updated `org.kohsuke:github-api:1.133` to `1.301`

### Test Dependency Updates

* Updated `org.mockito:mockito-core:4.0.0` to `4.1.0`
* Updated `org.mockito:mockito-junit-jupiter:4.0.0` to `4.1.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:1.3.0` to `1.3.4`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.14`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.0.0` to `1.2.0`
