# Exasol Release Droid 0.3.0, released 2020-12-??

Code name: Renamed project, monitoring of GitHub release workflows

## Summary

The project was renamed to ReleaseDroid. Please,also rename a credentials' folder to '.release-droid'.
Release Droid monitors now during RELEASE stage that all GitHub workflows are finished successfully before returning a release report. If a release workflow do not succeed, we stop the release process.

In this release we also introduced a new way to specify a platforms list: `-p github -p maven`.
The old way is deprecated amd will be removed in future.

## Features

* #48: Made Release Droid wait for GitHub workflows to end. 
* #86: Added support for ${project.version} property while detecting deliverables.
* #91: Improved console logging output.
* #92: Allowed to specify a 'platforms' arguments multiple times.
* #100: Added release priority: 1. MAVEN, 2.GITHUB.

## Documentation

* #90: Improved the changes file template.

## Refactoring

* #76: Removed GitHubException from usecases.
* #87: Renamed the project to Release Droid.
* #97: Improved logging when the version validation fails.

## Bug fixes
 
* #89: Fixed false positive validation for Github open tickets