# Exasol Release Droid 1.2.1, released 2022-08-18

Code name: Fixed and Improved Output

## Summary

Fixed display of HTML URL for github releases and supported colored messages even on windows console by using [fusesource/jansi](https://github.com/fusesource/jansi).

## Features

* #253: RD supports colored messages in windows console, too.

## Bug Fixes

* #252: RD was printing URL of previous workflow, RD prints URL of current workflow, now.

## Dependency Updates

### Compile Dependency Updates

* Added `org.fusesource.jansi:jansi:2.4.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.6.1` to `2.6.2`
