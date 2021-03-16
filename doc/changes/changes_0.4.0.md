# Exasol Release Droid 0.4.0, released 2021-??-??

Code name: Changed release strategy and added Scala support

## Summary

## Bug fixes

* #114: Fixed validation of a code name for the GitHub release.
* #124: Fixed wrong error message when retrieving the latest tag from local repository.

## Features

* #110: Added support for Scala projects release on GitHub.
* #116: Changed release strategy for Java repositories.
* #128: Changed release strategy for Scala repositories.

## Documentation

## Refactoring

* #119: Removed maven plugin validation and added a check that `project-keeper-maven-plugin` exists instead.

## Dependency updates

## Runtime Dependencies

* Updated `org.kohsuke:github-api:1.117` to `1.123`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.12.0` to `2.12.2`
* Updated `org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r` to `5.11.0.202103091610-r`
* Updated `org.json:json:20201115` to `20210307`
* Updated `com.exasol:error-reporting-java:0.2.2` to `0.4.0`

## Test Dependencies

* Updated `org.mockito:mockito-core:3.6.28` to `3.7.0`
* Updated `org.mockito:mockito-junit-jupiter:3.6.28` to `3.7.0`
* Updated `org.junit.jupiter:junit-jupiter:5.7.0` to `5.7.1`

## Plugin Dependencies

* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`