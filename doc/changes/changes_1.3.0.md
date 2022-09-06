# Exasol Release Droid 1.3.0, released 2022-09-06

Code name: Git Tags For Golang and Minor Improvements

## Summary

With this release RD creates appropriate git tags for different types of go sources, see [system design](../design.md#git-tags).

## Features

* #257: Create appropriate git tags for golang projects, too.
* #260: Accept groupId in file `pom.xml` from parent, too.
* #259: Deprecated release platform Jira to stop creating EXACOM tickets.

## Dependency Updates

### Compile Dependency Updates

* Updated `org.yaml:snakeyaml:1.30` to `1.31`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.6.2` to `2.7.0`
