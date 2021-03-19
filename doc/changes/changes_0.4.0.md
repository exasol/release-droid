# Exasol Release Droid 0.4.0, released 2021-03-19

Code name: Changed release strategy and added Scala support

## Summary

In this release we have changed the release strategy:

Before we start releasing, we run all tests, build a checksum for each packaged file (jar) and store the checksums. After that, we release on each platform skipping the tests during the build to speed up the process.

If the release was interrupted, we prepare a quick checksum and compare it against a stored one. If the checksums are equal, we are skipping the tests. If not, we re-run the tests and store a new checksum.

When the release is successful, we remove the checksum.

To use the new version of the Release Robot you need to add two new workflows to your projects:

1. `release_droid_prepare_original_checksum.yml`
2. `release_droid_print_quick_checksum.yml`

You also need to rename and update the existing templates:

3. `github_release.yml` renamed to `release_droid_upload_github_release_assets.yml`
4. `maven_central_release.yml` rename to `release_droid_release_on_maven_central.yml`

## Bug fixes

* #114: Fixed validation of a code name for the GitHub release.
* #124: Fixed wrong error message when retrieving the latest tag from a local repository.
* #134: Fixed parsing of the plugins in the pom file.
* #136: Excluded `project-keeper-maven-plugin` from the version validation.

## Features

* #110: Added support for Scala projects release on GitHub.
* #116: Changed release strategy for Java repositories.
* #128: Changed release strategy for Scala repositories.

## Refactoring

* #119: Removed maven plugin validation and added a check that `project-keeper-maven-plugin` exists instead.
* #132: Renamed the workflow scripts.

## Dependency updates

### Runtime Dependencies

* Updated `org.kohsuke:github-api:1.117` to `1.123`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.12.0` to `2.12.2`
* Updated `org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r` to `5.11.0.202103091610-r`
* Updated `org.json:json:20201115` to `20210307`
* Updated `com.exasol:error-reporting-java:0.2.2` to `0.4.0`

### Test Dependencies

* Updated `org.mockito:mockito-core:3.6.28` to `3.7.0`
* Updated `org.mockito:mockito-junit-jupiter:3.6.28` to `3.7.0`
* Updated `org.junit.jupiter:junit-jupiter:5.7.0` to `5.7.1`

### Plugin Dependencies

* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`