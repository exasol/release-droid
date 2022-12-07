# Exasol Release Droid 1.4.0, released 2022-12-07

Code name: Release Guide

## Summary

Added option to generate a release guide guiding the user through the release process, see details in the User Guide.

Improved warning message for unspecified release platforms.

If user misspells the key `release-platforms` then RD complained about missing specification of release platforms but was not able to identify a misspelled key, e.g. `platforms` in the reported issue for repository [small-json-files-test-fixture](https://github.com/exasol/small-json-files-test-fixture).

The improved warning message is now
> E-RD-20: No release platform specified. Please specify at least one release platform either on command line or with key 'release-platforms' in file 'release_config.yml' and re-run the Release Droid.

Bug #278 was caused by dependency `jira-rest-java-client-core` using an older version of `slf4j-api:1.7.30` while there was a direct dependency to the implementation `slf4j-simple:2.0.3` and could be fixed by downgrading dependency to slf4j implementation to version `slf4j-simple:1.7.36`.

## Features

* #275: Generate a release guide.

## Bug Fixes

* #277: Made RD ignore Git tags with unsupported version pattern.
* #266: Improved warning message for unspecified release platforms.
* #278: Fixed slf4j error "failed to load class StaticLoggerBinder"

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Added `com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.2.0.202206071550-r` to `6.3.0.202209071007-r`
* Updated `org.json:json:20220320` to `20220924`
* Updated `org.kohsuke:github-api:1.307` to `1.313`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.22` to `5.3.23`

### Test Dependency Updates

* Updated `com.exasol:maven-project-version-getter:1.1.0` to `1.2.0`
* Updated `org.junit.jupiter:junit-jupiter:5.9.0` to `5.9.1`
* Updated `org.mockito:mockito-core:4.6.1` to `4.8.0`
* Updated `org.mockito:mockito-junit-jupiter:4.6.1` to `4.8.0`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.0` to `0.4.2`
* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.2` to `1.2.1`
* Updated `com.exasol:project-keeper-maven-plugin:2.8.0` to `2.9.1`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.2` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.13.0`
