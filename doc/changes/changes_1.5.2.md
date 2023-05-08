# Exasol Release Droid 1.5.2, released t.b.d.

Code name: t.b.d.

## Summary

This release exludes a vulnerability reported in transitive dependency `com.google.guava:guava:jar:30.1.1` of `jira-rest-java-client-core` as currently there is no update available for Jira Client.

## Features

* #295: Excluded vulnerabilities reported in dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:5.2.4` to `5.2.5`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.2` to `2.15.0`
* Updated `commons-cli:commons-cli:1.5.0` to `20040117.000000`
* Updated `org.eclipse:yasson:3.0.2` to `3.0.3`
* Updated `org.slf4j:slf4j-simple:1.7.36` to `2.0.7`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.25` to `6.0.8`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter:5.9.2` to `5.9.3`
* Updated `org.mockito:mockito-core:5.2.0` to `5.3.1`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.3.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `1.2.3`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.6` to `2.9.7`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.2.1` to `3.3.0`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.4.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.14.2` to `2.15.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.9`
