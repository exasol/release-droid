# Exasol Release Droid 1.5.0, released 2023-03-21

Code name: Minor Improvements

## Summary

RD now only considers successful runs for estimating the duration of GitHub workflows such as creating a release.

On option `-g release` RD now additionally first validates the project and continues release only if validation didn't show any failures.

Additionally this release fixes and improves the Release Guide added in version 1.4.0.
* Moved "edit draft release" to the bottom of the release guide, as this action usually comes last because it needs to wait until RD has created the draft release on github.
* Add code name to proposed release summary in release guide
* Enable users to configure the prefix for announcements in file `.release-droid/credentials`, e.g. "I just released"

Last not least the dependency to `org.json:json` has been replaced by `jakarta.json:jakarta.json-api` and `org.eclipse.yasson` and this release updates dependencies to fix the following vulnerabilities:

* CVE-2022-1471
* CVE-2022-45688

## Features

* #272: Changed duration estimate to consider only successful workflow runs
* #240: Changed RD to always validate the current project before attempting to release
* #280: Optimized Release Guide
* #288: Upgraded dependencies to fix vulnerabilities
* #290: Ensured RD reports validation success only once

## Bugfixes

* #281: Fixed display of shortest common prefix for projects with multiple error codes.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1` to `2.14.2`
* Added `jakarta.json:jakarta.json-api:2.1.1`
* Updated `org.apache.maven:maven-model:3.8.6` to `3.8.7`
* Updated `org.codehaus.jettison:jettison:1.5.1` to `1.5.3`
* Updated `org.commonmark:commonmark:0.19.0` to `0.21.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r` to `6.4.0.202211300538-r`
* Added `org.eclipse:yasson:3.0.2`
* Removed `org.json:json:20220924`

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
