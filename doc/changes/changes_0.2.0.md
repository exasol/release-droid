# Exasol Release Robot 0.2.0, released 2020-??-??

Code name: 

## Features / Enhancements

* #35: Added validation that checks ".github/workflows/github_release.yml" file exists.
* #43: Added a logger message instead of throwing an exception when validating a release date and a list of closed tickets on the not-default branches. 
* #25: Added validation report.

## Bugs

* #39: Disabled the GitHub issues check if the branch is not the default one.

## Refactoring

* #37: Renamed throwParsingException to createParsingException