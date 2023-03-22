# Exasol Release Droid 1.5.1, released 2023-03-22

Code name: Fixed Report

## Summary

This release fixes RD's output to report successful validation only once. The changes requested by issue #290 failed to remove the duplicate message.

## Features

* #293: Ensured validation success is reported only once

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.9.4` to `2.9.6`
