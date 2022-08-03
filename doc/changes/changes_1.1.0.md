# Exasol Release Droid 1.1.0, released 2022-??-??

Code name: Support Multi-module Maven Projects with Additional Minor Improvements

## Summary

Support releases of project-keeper again, after PK has been turned into a multi-module maven project.

## Features

* #228: Enabled to release multi-maven projects.
Actually only the check for project-keeper-maven-plugin in the pom file has been removed.
* #238: Added a warning in case file `~/.release-droid/credentials` has not been found.
Saving credentials in this file is expected to be the preferred use case.
* #231: Enabled to configure project language in file `release_config.yml`.

## Documentation

* #230: Updated [user_guide.md](../user_guide/user_guide.md) regarding command line syntax and Jira credentials

## Dependency Updates

### Compile Dependency Updates

* Updated `org.apache.maven:maven-model:3.8.5` to `3.8.6`
* Updated `org.commonmark:commonmark:0.18.2` to `0.19.0`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.21` to `5.3.22`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter:5.8.2` to `5.9.0`
* Updated `org.mockito:mockito-core:4.5.1` to `4.6.1`
* Updated `org.mockito:mockito-junit-jupiter:4.5.1` to `4.6.1`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.4.6` to `2.5.0`
