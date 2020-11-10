# Exasol Release Robot 0.2.0, released 2020-11-??

Code name: Maven Central Release

## Features / Enhancements

* #35: Added validation that checks ".github/workflows/github_release.yml" file exists.
* #43: Added a logger message instead of throwing an exception when validating a release date and a list of closed tickets on the not-default branches. 
* #25: Added validation report.
* #47: Added release report.
* #36: Added Maven release support.
* #54: Added check from an HTTP response from the Github when triggering a workflow.

## Bugs

* #39: Disabled the GitHub issues check if the branch is not the default one.

## Refactoring

* #37: Renamed `throwParsingException` to `createParsingException`.
* #52: Refactored the project's structure.
* #46: Separated pom parsing and validation.
* #69: Removed the repository instance variable from the ValidateInteractor class.
* #70: Made the ReleaseInteractor instance independent of the repository.
* #74: Refactored GitHub API adapter.
* #68: Extracted UserInput parsing.

## Dependencies updates

* Added org.apache.maven:maven-model:3.6.3
* Added org.mockito:mockito-junit-jupiter:3.6.0
* Updated org.mockito:mockito-core:3.5.10 to version 3.6.0