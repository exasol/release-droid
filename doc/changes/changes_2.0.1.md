# Exasol Release Droid 2.0.1, released 2026-07-27

Code name: Dependency Update on top of 2.0.0

## Summary

This release fixes CWE-915 by updating the transitive dependency `com.fasterxml.jackson.core:jackson-databind` to 2.22.1.

The Jira client does not have an update yet, so we had to pin two transitive dependencies.

We also updated the GitHub API to 2.0-RC7 and adapted some code to match that. While we were at it, we fixed some Sonar findings.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:7.0.1` to `7.0.2`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.0` to `2.22.1`
* Removed `com.infradna.tool:bridge-method-annotation:1.31`
* Removed `jakarta.json:jakarta.json-api:2.1.3`
* Added `org.apache.commons:commons-lang3:3.20.0`
* Updated `org.commonmark:commonmark:0.28.0` to `0.29.0`
* Removed `org.fusesource.jansi:jansi:2.4.3`
* Added `org.jline:jansi:4.3.1`
* Updated `org.kohsuke:github-api:1.330` to `2.0-rc.7`
* Added `org.springframework:spring-beans:7.0.8`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter:6.1.0` to `6.1.2`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.4` to `1.0.1`
* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.7` to `2.1.0`
* Updated `com.exasol:project-keeper-maven-plugin:5.6.2` to `5.7.4`
* Removed `com.exasol:quality-summarizer-maven-plugin:0.2.1`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.10.0` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.6.2` to `3.6.3`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.5` to `3.5.6`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.5` to `3.5.6`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.14` to `0.8.15`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356` to `5.7.0.6970`
* Added `org.spdx:spdx-maven-plugin:1.0.4`
