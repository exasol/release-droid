# Exasol Release Droid 1.0.0, released 2022-06-27

Code name: Support for multi maven module projects

## Summary

This release adds support for multi maven module projects, prepends version (tag) to Github release title, fixes some bugs.

## Features

* #218: Added support for multi maven module projects
* #229: Prepended version (tag) to Github release title

## Bug Fixes

* #223: Removed Spring Beans dependency with CVE-2022-22965
* #222: Fixed executing the JAR by adding a manifest
* #226: Fixed error when running Jira release
* #233: Updated dependencies to fix vulnerabilities reported by ossindex: CVE-2022-22970, sonatype-2020-0926, sonatype-2020-0926.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:5.2.2` to `5.2.4`
* Updated `commons-cli:commons-cli:1.5.0` to `20040117.000000`
* Updated `io.atlassian.fugue:fugue:4.7.2` to `5.0.0`
* Updated `org.apache.maven:maven-model:3.8.4` to `3.8.6`
* Updated `org.commonmark:commonmark:0.18.0` to `0.19.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r` to `6.2.0.202206071550-r`
* Updated `org.json:json:20210307` to `20220320`
* Updated `org.kohsuke:github-api:1.301` to `1.307`
* Updated `org.slf4j:slf4j-simple:1.7.32` to `1.7.36`
* Updated `org.yaml:snakeyaml:1.29` to `1.30`

### Runtime Dependency Updates

* Added `org.springframework:spring-beans:5.3.21`

### Test Dependency Updates

* Added `com.exasol:maven-project-version-getter:1.1.0`
* Updated `org.junit.jupiter:junit-jupiter:5.8.1` to `5.8.2`
* Updated `org.mockito:mockito-core:4.1.0` to `4.6.1`
* Updated `org.mockito:mockito-junit-jupiter:4.1.0` to `4.6.1`

### Plugin Dependency Updates

* Added `com.exasol:artifact-reference-checker-maven-plugin:0.4.0`
* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.1` to `1.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.4.6`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.14` to `0.15`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.1.0` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.1`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M6`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.0` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.12.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M4` to `3.0.0-M6`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.10.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.2.0` to `1.5.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.7` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
