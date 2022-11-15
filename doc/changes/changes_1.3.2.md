# Exasol Release Droid 1.3.2, released 2022-??-??

Code name: Improved warning message for unspecified release platforms

## Summary

Added option to generate a release guide guding the user through the release process. See details in [User Guide](../user_guide/user_guide.md#release-guide).

Making releases still requires some time, effort, and stereotype tasks. Depending on the project to release the user needs to enter data in various places. Release Droid therefore provides a guide aggregating all the data to make the current release as convenient as possible.

Improved warning message for unspecified release platforms.

If user misspells the key `release-platforms` then RD complained about missing specification of release platforms but was not able to identify a misspelled key, e.g. `platforms` in the reported issue for repository [small-json-files-test-fixture](https://github.com/exasol/small-json-files-test-fixture).

The improved warning message is now
> E-RD-20: No release platform specified. Please specify at least one release platform either on command line or with key 'release-platforms' in file 'release_config.yml' and re-run the Release Droid.

## Features

* #275: Generate a release guide.

## Bug Fixes

* #266: Improved warning message for unspecified release platforms.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Added `com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.2.0.202206071550-r` to `6.3.0.202209071007-r`
* Updated `org.json:json:20220320` to `20220924`
* Updated `org.kohsuke:github-api:1.307` to `1.313`
* Updated `org.slf4j:slf4j-simple:1.7.36` to `2.0.3`

### Runtime Dependency Updates

* Updated `org.springframework:spring-beans:5.3.22` to `5.3.23`

### Test Dependency Updates

* Updated `com.exasol:maven-project-version-getter:1.1.0` to `1.2.0`
* Updated `org.junit.jupiter:junit-jupiter:5.9.0` to `5.9.1`
* Updated `org.mockito:mockito-core:4.6.1` to `4.8.0`
* Updated `org.mockito:mockito-junit-jupiter:4.6.1` to `4.8.0`
