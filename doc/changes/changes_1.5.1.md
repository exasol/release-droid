# Exasol Release Droid 1.5.1, released 2023-??-??

Code name: Upgrade dependencies

## Summary

This release updates dependencies to fix the following vulnerabilities:

* CVE-2022-1471
* CVE-2022-45688

## Features

* #288: Upgraded dependencies to fix vulnerabilities

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1` to `2.14.2`
* Added `jakarta.json:jakarta.json-api:2.1.1`
* Updated `org.apache.maven:maven-model:3.8.6` to `3.9.0`
* Updated `org.codehaus.jettison:jettison:1.5.1` to `1.5.3`
* Updated `org.commonmark:commonmark:0.19.0` to `0.21.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r` to `6.5.0.202303070854-r`
* Added `org.eclipse:yasson:3.0.2`
* Removed `org.json:json:20220924`
* Updated `org.kohsuke:github-api:1.313` to `1.314`
* Updated `org.yaml:snakeyaml:1.33` to `2.0`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.23` to `5.3.25`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.10.1` to `3.14`
* Updated `org.junit.jupiter:junit-jupiter:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-core:4.8.0` to `5.2.0`
* Updated `org.mockito:mockito-junit-jupiter:4.8.0` to `5.2.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `2.9.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.13.0` to `2.14.2`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.5.0` to `1.6.1`
