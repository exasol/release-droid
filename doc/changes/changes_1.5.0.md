# Exasol Release Droid 1.5.0, released 2023-??-??

Code name: Updated Release Guide

## Summary

This release improves the Release Guide added in version 1.4.0.

## Features

* #281: For projects with multiple error codes display the shortest common prefix.

## Bugfixes

Marked vulnerability CVE-2022-45688 in [org.json:json:jar:20220924](https://ossindex.sonatype.org/component/pkg:maven/org.json/json@20220924?u) in compile to be ignored as affected releases to community portal are currently disabled anyway.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1` to `2.14.2`
* Updated `org.apache.maven:maven-model:3.8.6` to `3.8.7`
* Updated `org.codehaus.jettison:jettison:1.5.1` to `1.5.3`
* Updated `org.commonmark:commonmark:0.19.0` to `0.21.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r` to `6.4.0.202211300538-r`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.23` to `5.3.24`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.10.1` to `3.12.4`
* Updated `org.junit.jupiter:junit-jupiter:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-core:4.8.0` to `5.1.1`
* Updated `org.mockito:mockito-junit-jupiter:4.8.0` to `5.1.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `2.9.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.13.0` to `2.14.2`
