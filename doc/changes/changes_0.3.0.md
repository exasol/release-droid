# Exasol Release Droid 0.3.0, released 2020-12-??

Code name: Renamed project, monitoring of GitHub release workflows

## Summary

- The project was renamed to ReleaseDroid. Please,also rename a credentials' folder to '.release-droid'.
- Release Droid monitors now during RELEASE stage that all GitHub workflows are finished successfully before returning a release report. If a release workflow does not succeed, we stop the release process.

- In this release we also introduced a new way to specify a platforms list: `-p github -p maven`. The old way is deprecated and will be removed in the next major version release.

- You don't need to change the release date manually. From this version the Release Droid makes an automatic commit if the release date is outdated.

## Features

* #48: Made Release Droid wait for GitHub workflows to end.
* #86: Added support for ${project.version} property while detecting deliverables.
* #91: Improved console logging output.
* #92: Allowed to specify a 'platforms' arguments multiple times.
* #100: Added release priority: 1. MAVEN, 2.GITHUB.
* #107: Modified the release date if it's outdated.

## Documentation

* #90: Improved the changes file template.

## Refactoring

* #76: Removed GitHubException from usecases.
* #87: Renamed the project to Release Droid.
* #97: Improved logging when the version validation fails.
* #62: Decouple report formatting logic.
* #101: Added error builder.
* #105: Organize logging and formatting packages.

## Bug fixes

* #89: Fixed false positive validation for Github open tickets

## Dependency updates

* Added `com.fasterxml.jackson.core:jackson-databind:jar:2.12.0` to avoid CVE-2020-25649.
* Added `com.exasol:error-reporting-java:0.2.1`