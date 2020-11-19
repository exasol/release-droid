# Introduction

## Acknowledgments

This document's section structure is derived from the "[arc42](https://arc42.org/)" architectural template by Dr. Gernot Starke, Dr. Peter Hruschka.

## Terms and Abbreviations

<dl>
    <dt>RR</dt><dd>Release Droid</dd>
    <dt>GR</dt><dd>Git repository</dd>
</dl>

## Requirement Overview

Please refer to the [System Requirement Specification](system_requirements.md) for user-level requirements.

# Building Blocks

This section introduces the building blocks of the software. Together those building blocks make up the big picture of the software structure.

## `Repository`
`dsn~repository~1`

The `Repository` is a representation of a project on which RR performs validations and releases.   

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

Users select a GitHub-based project by providing its name to RR.

Covers:

* `req~users-provide-rr-parameters~1`

Needs: impl

### Users Set Run Goal
`dsn~users-set-run-goal~1`

Users select whether they want to `validate` or `release` the project. 

Covers:

* `req~users-provide-rr-parameters~1`

Needs: impl

### Users Set Release Platforms
`dsn~users-set-release-platforms~1`

Users select a list of platforms they want to perform a validation/release on.

Covers:

* `req~users-provide-rr-parameters~1`

Needs: impl

### Users Can Set Git Branch for Validation
`dsn~users-can-set-git-branch-for-validation~1`

Users can provide a name of a Git branch. 

Covers:

* `req~users-can-set-git-branch-for-validation~1`

Needs: impl

### User add Upload Definition Files for Their Deliverables
`dsn~users-add-upload-definition-files-for-their-deliverables~1`

Users add upload [definitions for deliverables](user_guide/github_release_template.md) in form of a [`.yml` file](https://yaml.org/) to their project.

Covers:

* `req~detect-deliverables-in-a-maven-project~1`

Needs: impl

### RR Runs Validate Goal
`dsn~rr-runs-validate-goal~1`

RR performs only validation if it gets a `validate` goal.

Covers:

* `req~validate-project~1`

Needs: impl

### RR Runs Release Goal
`dsn~rr-runs-release-goal~1`

RR performs validation and then release if it gets a `release` goal.

Covers:

* `req~release-project~1`

Needs: impl

## Repository

### Repository Provides Current Version
`dsn~repository-provides-current-version~1`

The `Repository` detects a current project's version depending on a project's programming language and project's structure.

Covers:

* `req~detect-current-version-from-maven-pom~1`

Needs: impl, utest

### Repository Provides Deliverables Information
`dsn~repository-provides-deliverables-information~1`

Repository provides a list of key-value pairs containing information in format `deliverable name - deliverable path` where a path is a path from project's root to the deliverable file.

Covers:

* `req~detect-deliverables-in-a-maven-project~1`

Needs: impl, utest

### Repository Retrieves Branch Content
`dsn~repository-retrieves-branch-content~1`

The `Repository` retrieves content of specified branch.

Covers:

* `req~access-project-files~1`

Needs: impl, utest

## Release Droid Runtime

### RR Starts Release Only If All Validation Succeed
`dsn~rr-starts-release-only-if-all-validation-succeed~1`

RR starts release only if all validation for the platforms users specified succeed.

Covers:

* `req~stopping-the-release-on-failed-step-validation~1`

Needs: impl

### RR Creates Validation Report
`dsn~rr-creates-validation-report~1`

RR creates a validation report containing a summary of all validations and their results.

Covers:

* `req~validation-report~1`

Needs: impl

### RR Creates Release Report
`dsn~rr-creates-release-report~1`

RR creates a release report containing a summary of all releases and their results.

Covers:

* `req~release-report~1`

Needs: impl, utest

### RR Writes Report to a File
`dsn~rr-writes-report-to-file~1`

RR writes a full report to a `home/.release-droid/last_report.txt` file.

Covers:

* `req~validation-report~1`
* `req~release-report~1`

Needs: impl, utest

## Validation

### Git Repository Validation

Validations listed here are platform-independent.

#### Validate Release Version Format
`dsn~validate-release-version-format~1`

RR validates that a version consists of three parts containing only digits: <major><feature><bug>. 

Covers:

* `req~detect-version-conflicts-in-the-project-sources~1`

Needs: impl, utest

#### Validate Release Version Increased Correctly
`dsn~validate-release-version-increased-correctly~1`

RR validates that a new version differs from a previous version in one incrementing digit.

Rationale:

Let us assume that a previous version was 1.2.3. That means that a valid version for the next release is 1.2.4 or 1.3.0 or 2.0.0. 

Covers:

* `req~detect-version-conflicts-in-the-project-sources~1`

Needs: impl, utest

#### Validate Changelog
`dsn~validate-changelog~1`

RR validates that `changelog.md` file contains a link to `changes_<version>.md` file.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

#### Validate Changes File Contains Release Version
`dsn~validate-changes-file-contains-release-version~1`

RR validates that `changes_<version>.md` file contains a version to be released.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

#### Validate Changes File Contains Correct Release Date
`dsn~validate-changes-file-contains-release-date~1`

RR validates that `changes_<version>.md` file contains today's date.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

#### Validate Changes File Contains Release Letter Body
`dsn~validate-changes-file-contains-release-letter-body~1`

RR validates that `changes_<version>.md` file contains a release letter body.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

### GitHub Platform Validation

Validations listed here are necessary for a release on the GitHub.

#### Validate Release Letter Contains Release Header
`dsn~validate-release-letter~1`

RR validates that a release letter for a GitHub release is present and contains all necessary information.

Covers:

* `req~gitub-release-description-from-release-letter~1`

Needs: impl, utest

#### Validate GitHub Issues Exist
`dsn~validate-github-issues-exists~1`

RR validates that all the issues mentioned in the release letter exist.

Covers:

* `req~validation-github-issue-ticket-numbers~1`

Needs: impl, utest

#### Validate GitHub Issues Are Closed
`dsn~validate-github-issues-are-closed~1`

RR validates that all the issues mentioned in the release letter are closed on the GitHub.

Covers:

* `req~validating-that-github-issues-are-closed~1`

Needs: impl, utest

#### Validate `github_release.yml` Workflow Exists
`dsn~validate-github-workflow-exists~1`

RR validates that a workflow that helps us to release on GitHub exists.

Covers:

* `req~attaching-deliverables-to-a-github-release~1`

Needs: impl, utest

### Maven Platform Validation

Validations listed here are necessary for a release on the Maven CEntral.

#### Validate `maven_central_release.yml` Workflow Exists
`dsn~validate-maven-release-workflow-exists~1`

RR validates that a workflow that helps us to release on Maven Central Repository exists.

Covers:

* `req~releasing-on-maven~1`

Needs: impl, utest

#### Validate Pom File Contains Required Plugins for Maven Release
`dsn~validate-pom-contains-required-plugins-for-maven-release~1`

RR validates that all plugins required for a Maven release exist.

Covers:

* `req~releasing-on-maven~1`

Needs: impl, utest

## Release

### Release on GitHub

#### Create new GitHub Release
`dsn~create-new-github-release~1`

RR creates a new GitHub release.

Covers:

* `req~releasing-on-github~1`

Needs: impl

#### Retrieve GitHub Release Header from Release Letter
`dsn~retrieve-github-release-header-from-release-letter~1`

RR extracts the GitHub release's title from the release letter.

Covers:

* `req~github-release-title-from-release-letter~1`

Needs: impl

#### Retrieve GitHub Release Body from Release Letter
`dsn~retrieve-github-release-body-from-release-letter~1`

RR extracts the GitHub release's body from the release letter.

Covers:

* `req~gitub-release-description-from-release-letter~1`

Needs: impl

#### Upload GitHub Release Assets
`dsn~upload-github-release-assets~1`

RR uploads and attaches GitHub Release Assets to the new release.

Covers:

* `req~attaching-deliverables-to-a-github-release~1`

Needs: impl

### Release on Maven Central Repository

#### Create new Maven Release
`dsn~create-new-maven-release~1`

RR creates a new release on the Maven Central Repository.

Covers:

* `req~releasing-on-maven~1`

Needs: impl, utest

# Cross-cutting Concerns

# Design Decisions

# Quality Scenarios

# Risks
