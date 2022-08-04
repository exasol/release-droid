# Introduction

## Acknowledgments

This document's section structure is derived from the "[arc42](https://arc42.org/)" architectural template by Dr. Gernot Starke, Dr. Peter Hruschka.

## Terms and Abbreviations

<dl>
    <dt>RD</dt><dd>Release Droid</dd>
    <dt>GR</dt><dd>Git repository</dd>
</dl>

## Requirement Overview

Please refer to the [System Requirement Specification](system_requirements.md) for user-level requirements.

# Building Blocks

This section introduces the building blocks of the software. Together those building blocks make up the big picture of the software structure.

## `Repository`

`dsn~repository~1`

The `Repository` is a representation of a project on which RD performs validations and releases.

## `PlatformValidator`

`dsn~platform-validator~1`

The `PlatformValidator` performs a validation on a platform.

## `ReleaseMaker`

`dsn~release-maker~1`

The `ReleaseMaker` performs a release on a platform.

# Runtime

This section describes the runtime behavior of the software.

## Release Droid Run Prerequisites

### Users Set Project

`dsn~users-set-project~1`

Users select a GitHub-based project by providing its name to RD.

Covers:

* `req~users-provide-rd-parameters~1`

Needs: impl

### Users Set Run Goal

`dsn~users-set-run-goal~1`

Users select whether they want to `validate` or `release` the project.

Covers:

* `req~users-provide-rd-parameters~1`

Needs: impl

### Users Set Release Platforms

`dsn~users-set-release-platforms~1`

Users select a list of platforms they want to perform a validation/release on.

Covers:

* `req~users-provide-rd-parameters~1`

Needs: impl

### Users Can Set Git Branch for Validation

`dsn~users-can-set-git-branch-for-validation~1`

Users can provide a name of a Git branch.

Covers:

* `req~users-can-set-git-branch-for-validation~1`

Needs: impl

### User add Upload Definition Files for Their Deliverables

`dsn~users-add-upload-definition-files-for-their-deliverables~1`

Users add upload [definitions for deliverables](user_guide/templates/upload_github_release_assets_template.md) in form of a [`.yml` file](https://yaml.org/) to their project.

Covers:

* `req~detect-deliverables-in-a-maven-project~1`

Needs: impl

### RD Runs Validate Goal

`dsn~rd-runs-validate-goal~1`

RD performs only validation if it gets a `validate` goal.

Covers:

* `req~validate-project~1`

Needs: impl

### RD Runs Release Goal

`dsn~rd-runs-release-goal~1`

RD performs validation and then release if it gets a `release` goal.

Covers:

* `req~release-project~1`

Needs: impl

## Repository

### Local Repository

`dsn~local-repository~1`

The `LocalRepository` provides access to a local repository

Covers:

* `req~access-project-files~1`

Needs: impl

### GitHub Repository

`dsn~github-repository~1`

The `GitHubRepository` provides access to a GitHub-based repository.

Covers:

* `req~access-project-files~1`

Needs: impl

### Repository Provides Current Version

`dsn~repository-provides-current-version~1`

The `Repository` detects a current project's version depending on a project's programming language and project's structure.

Covers:

* `req~detect-current-version-from-maven-pom~1`

Needs: impl, utest

### Repository Retrieves Branch Content

`dsn~repository-retrieves-branch-content~1`

The `Repository` retrieves content of specified branch.

Covers:

* `req~access-project-files~1`

Needs: impl

## Release Droid Runtime

### RD Starts Release Only If All Validation Succeed

`dsn~rd-starts-release-only-if-all-validation-succeed~1`

RD starts release only if all validation for the platforms users specified succeed.

Covers:

* `req~stopping-the-release-on-failed-step-validation~1`

Needs: impl

### RD Creates Validation Report

`dsn~rd-creates-validation-report~1`

RD creates a validation report containing a summary of all validations and their results.

Covers:

* `req~validation-report~1`

Needs: impl

### RD Creates Release Report

`dsn~rd-creates-release-report~1`

RD creates a release report containing a summary of all releases and their results.

Covers:

* `req~release-report~1`

Needs: impl, utest

### RD Writes Report to a File

`dsn~rd-writes-report-to-file~1`

RD writes a full report to a `home/.release-droid/last_report.txt` file.

Covers:

* `req~validation-report~1`
* `req~release-report~1`

Needs: impl, utest

## Validation

### Git Repository Validation

Validations listed here are platform-independent.

#### Validate Release Version Format

`dsn~validate-release-version-format~1`

RD validates that a version consists of three parts containing only digits: <major><feature><bug>.

Covers:

* `req~detect-version-conflicts-in-the-project-sources~1`

Needs: impl, utest

#### Validate Release Version Increased Correctly

`dsn~validate-release-version-increased-correctly~1`

RD validates that a new version differs from a previous version in one incrementing digit.

Rationale:

Let us assume that a previous version was 1.2.3. That means that a valid version for the next release is 1.2.4 or 1.3.0 or 2.0.0.

Covers:

* `req~detect-version-conflicts-in-the-project-sources~1`

Needs: impl, utest

#### Validate Changelog

`dsn~validate-changelog~1`

RD validates that `changelog.md` file contains a link to `changes_<version>.md` file.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

#### Validate Changes File Contains Release Version

`dsn~validate-changes-file-contains-release-version~1`

RD validates that `changes_<version>.md` file contains a version to be released.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

#### Validate Changes File Contains Release Letter Body

`dsn~validate-changes-file-contains-release-letter-body~1`

RD validates that `changes_<version>.md` file contains a release letter body.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

### GitHub Platform Validation

Validations listed here are necessary for a release on the GitHub.

#### Validate Release Letter Contains Release Header

`dsn~validate-release-letter~1`

RD validates that a release letter for a GitHub release is present and contains all necessary information.

Covers:

* `req~gitub-release-description-from-release-letter~1`

Needs: impl, utest

#### Validate GitHub Issues Exist

`dsn~validate-github-issues-exists~1`

RD validates that all the issues mentioned in the release letter exist.

Covers:

* `req~validation-github-issue-ticket-numbers~1`

Needs: impl, utest

#### Validate GitHub Issues Are Closed

`dsn~validate-github-issues-are-closed~1`

RD validates that all the issues mentioned in the release letter are closed on the GitHub.

Covers:

* `req~validating-that-github-issues-are-closed~1`

Needs: impl, utest

### Maven Platform Validation

Validations listed here are necessary for a release on the Maven CEntral.

#### Validate `release_droid_release_on_maven_central.yml` Workflow Exists

`dsn~validate-maven-release-workflow-exists~1`

RD validates that a workflow that helps us to release on Maven Central Repository exists.

Covers:

* `req~releasing-on-maven~1`

Needs: impl, utest

## Release

### Release Preparations

#### Prepare Checksum

`dsn~prepare-checksum~1`

RD prepares a checksum for each deliverable and store it in the GitHub artifactory.
RD uses `prepare_original_checksum.yml` workflow for it.

Rationale:

RD verifies that the build is green and stores a checksum file to avoid running tests again in case the release was not finished.

Covers:

* `req~run-tests-only-once~1`

Needs: impl

#### Compare Checksum

`dsn~compare-checksum~1`

RD compares a stored checksum if one exists with a fresh checksum.
RD uses `print_quick_checksum.yml` workflow to create a fresh checksum.
RD uses `prepare_original_checksum.yml` artifactory to obtain a stored checksum.

Covers:

* `req~run-tests-only-once~1`

Needs: impl

#### Validating the Release Date

`dsn~validating-release-date~1`

During validation RD checks if the release date is up-to-date. If it is not equal to today's date, RD prints a warning message and fails the validation.

Covers:

* `req~validating-release-date~1`

Needs: impl, utest

### Release on GitHub

#### Create new GitHub Release

`dsn~create-new-github-release~1`

RD creates a new GitHub release.

Covers:

* `req~releasing-on-github~1`

Needs: impl

#### Retrieve GitHub Release Header from Release Letter

`dsn~retrieve-github-release-header-from-release-letter~2`

RD builds the GitHub release's title by concatenating the version and the header of the release letter.

Covers:

* `req~github-release-title-from-release-letter~1`

Needs: impl

#### Retrieve GitHub Release Body from Release Letter

`dsn~retrieve-github-release-body-from-release-letter~1`

RD extracts the GitHub release's body from the release letter.

Covers:

* `req~gitub-release-description-from-release-letter~1`

Needs: impl

#### Upload GitHub Release Assets

`dsn~upload-github-release-assets~1`

RD uploads and attaches GitHub Release Assets to the new release.

Covers:

* `req~attaching-deliverables-to-a-github-release~1`

Needs: impl

### Release on Maven Central Repository

#### Create new Maven Release

`dsn~create-new-maven-release~1`

RD creates a new release on the Maven Central Repository.

Covers:

* `req~releasing-on-maven~1`

Needs: impl, utest

### Release on Exasol Community Portal

#### Create new Release Announcement on Exasol Community Portal

`dsn~create-new-release-announcement-on-exasol-community-portal~1`

RD creates a new release announcement draft on the Exasol Community Portal via Khoros API.

Covers:

* `req~releasing-on-exasol-community-portal~1`

Needs: impl

#### Extract Release Changes Description from Release Letter

`dsn~extract-release-changes-description-from-release-letter~1`

RD extracts a release changes description from the `## Summary` section of the release letter.

Covers:

* `req~release-changes-description-from-release-letter~1`

Needs: impl, utest

#### Extract Project Description from File

`dsn~extract-project-description-from-file~1`

RD extracts information required to create a release announcement from the `release_config.yml` file that is located in the root directory of the project.

Covers:

* `req~get-project-description-from-repository~1`

Needs: impl, utest

### Release Clean Up

#### Remove Checksum

`dsn~remove-checksum~1`

RD removes a stored checksum after the release or when it's outdated.

Covers:

* `req~run-tests-only-once~1`

Needs: impl

# Cross-cutting Concerns

# Design Decisions

# Quality Scenarios

# Risks
