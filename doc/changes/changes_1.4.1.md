# Exasol Release Droid 1.4.1, released 2022-??-??

Code name: Dependency Upgrade

## Summary

Updated dependencies and excluded vulnerability sonatype-2022-6438 reported for dependency `com.fasterxml.jackson.core:jackson-core:jar:2.14.1` as there is no update available, yet.

## Bugfixes

* #282: Excluded vulnerabilities

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1` to `2.14.1`
* Updated `org.codehaus.jettison:jettison:1.5.1` to `1.5.3`
* Updated `org.commonmark:commonmark:0.19.0` to `0.21.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r` to `6.4.0.202211300538-r`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.23` to `6.0.3`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.10.1` to `3.12.3`
* Updated `org.mockito:mockito-core:4.8.0` to `4.10.0`
* Updated `org.mockito:mockito-junit-jupiter:4.8.0` to `4.10.0`
