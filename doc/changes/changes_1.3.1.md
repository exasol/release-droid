# Exasol Release Droid 1.3.1, released 2022-09-27

Code name: Fix vulnerabilities in dependencies

## Summary

This release fixes CVE-2022-38751 and CVE-2022-38752 in snakeyaml.

## Features

* #264: Added hint to enhance file `release_config.yml` for warning about unspecified language.

## Bug Fixes

* #267: Fixed vulnerabilities in dependencies.

## Dependency Updates

### Compile Dependency Updates

* Added `org.codehaus.jettison:jettison:1.5.1`
* Updated `org.yaml:snakeyaml:1.31` to `1.33`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.7.0` to `2.8.0`
