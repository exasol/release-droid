# Exasol Release Droid 0.7.0, released 2021-08-24

Code name: Updated Jira release process, allowed skipping workflows

## Summary

* Uploading assets for a GitHub release now an optional step and only works if your repository has a workflow file `release_droid_upload_github_release_assets.yml`.
* Checksum workflows are also an optional step now, they only work when the workflows are present in the repository.
* You can add a human-readable project name to the `release_config.yml` file with `human-readable-project-name` param (this is useful for Jira tickets for example).

## Features

* #30: Adjusted `release_droid_upload_github_release_assets.yml` template to release with sha256sum.
* #173: Change GitHub uploading assets to an optional step.
* #182: Allowed release without checksum generation workflows.
* #187: Allow configuring Jira project name via config file.

## Refactoring

* #185: Changed Jira tickets project to EXACOMM.

## Dependency Updates
