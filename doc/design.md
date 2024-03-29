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
* [`req~users-provide-rd-parameters~1`](system_requirements.md#users-provide-rd-parameters)

Needs: impl

### Users Set Run Goal

`dsn~users-set-run-goal~1`

Users select whether they want to `validate` or `release` the project.

Covers:
* [`req~users-provide-rd-parameters~1`](system_requirements.md#users-provide-rd-parameters)

Needs: impl

### Users Set Release Platforms

`dsn~users-set-release-platforms~1`

Users select a list of platforms they want to perform a validation/release on.

Covers:
* [`req~users-provide-rd-parameters~1`](system_requirements.md#users-provide-rd-parameters)

Needs: impl

### Users Can Set Git Branch for Validation

`dsn~users-can-set-git-branch-for-validation~1`

Users can provide a name of a Git branch.

Covers:
* [`req~users-can-set-git-branch-for-validation~1`](system_requirements.md#users-can-set-git-branch-for-validation)

Needs: impl

### User add Upload Definition Files for Their Deliverables
`dsn~users-add-upload-definition-files-for-their-deliverables~1`

Users add upload [definitions for deliverables](user_guide/templates/upload_github_release_assets_template.md) in form of a [`.yml` file](https://yaml.org/) to their project.

Covers:
* [`req~detect-deliverables-in-a-maven-project~1`](system_requirements.md#detect-deliverables-in-a-maven-project)

Needs: impl

### RD Runs Validate Goal

`dsn~rd-runs-validate-goal~1`

RD performs only validation if it gets a `validate` goal.

Covers:
* [`req~validate-project~1`](system_requirements.md#validate-project)

Needs: impl

### RD Runs Release Goal

`dsn~rd-runs-release-goal~1`

RD performs validation and then release if it gets a `release` goal.

Covers:
* [`req~release-project~1`](system_requirements.md#release-project)

Needs: impl

## Repository

### Local Repository

`dsn~local-repository~1`

The `LocalRepository` provides access to a local repository

Covers:
* [`req~access-project-files~1`](system_requirements.md#access-project-files)

Needs: impl

### GitHub Repository

`dsn~github-repository~1`

The `GitHubRepository` provides access to a GitHub-based repository.

Covers:
* [`req~access-project-files~1`](system_requirements.md#access-project-files)

Needs: impl

### Repository Provides Current Version

`dsn~repository-provides-current-version~1`

The `Repository` detects a current project's version depending on a project's programming language and project's structure.

Covers:
* [`req~detect-current-version-from-maven-pom~1`](system_requirements.md#detect-current-version-from-maven-pom)

Needs: impl, utest

### Repository Retrieves Branch Content

`dsn~repository-retrieves-branch-content~1`

The `Repository` retrieves content of specified branch.

Covers:
* [`req~access-project-files~1`](system_requirements.md#access-project-files)

Needs: impl

## Release Droid Runtime

### RD Starts Release Only If All Validation Succeed

`dsn~rd-starts-release-only-if-all-validations-succeed~1`

RD starts release only if all validation for the platforms users specified succeed.

Covers:
* [`req~stopping-the-release-on-failed-step-validation~1`](system_requirements.md#stopping-the-release-on-failed-step-validation)

Needs: impl

### RD Creates Validation Report

`dsn~rd-creates-validation-report~1`

RD creates a validation report containing a summary of all validations and their results.

Covers:
* [`req~validation-report~1`](system_requirements.md#validation-report)

Needs: impl

### RD Creates Release Report

`dsn~rd-creates-release-report~1`

RD creates a release report containing a summary of all releases and their results.

Covers:
* [`req~release-report~1`](system_requirements.md#release-report)

Needs: impl, utest

### RD Writes Report to a File

`dsn~rd-writes-report-to-file~1`

RD writes a full report to a `home/.release-droid/last_report.txt` file.

Covers:
* [`req~validation-report~1`](system_requirements.md#validation-report)
* [`req~release-report~1`](system_requirements.md#release-report)

Needs: impl, utest

## Validation

### Git Repository Validation

Validations listed here are platform-independent.

#### Validate Release Version Format

`dsn~validate-release-version-format~1`

RD validates that a version consists of three parts containing only digits: <major><feature><bug>.

Covers:
* [`req~detect-version-conflicts-in-the-project-sources~1`](system_requirements.md#stopping-the-release-in-case-of-version-conflicts-in-the-project-sources)

Needs: impl, utest

#### Validate Release Version Increased Correctly

`dsn~validate-release-version-increased-correctly~1`

RD validates that a new version differs from a previous version in one incrementing digit.

Rationale:

Let us assume that a previous version was 1.2.3. That means that a valid version for the next release is 1.2.4 or 1.3.0 or 2.0.0.

Covers:
* [`req~detect-version-conflicts-in-the-project-sources~1`](system_requirements.md#stopping-the-release-in-case-of-version-conflicts-in-the-project-sources)

Needs: impl, utest

#### Validate Changelog

`dsn~validate-changelog~1`

RD validates that `changelog.md` file contains a link to `changes_<version>.md` file.

Covers:
* [`req~validate-mandatory-directory-tree-elements~1`](system_requirements.md#validate-mandatory-directory-tree-elements)

Needs: impl, utest

#### Validate Changes File Contains Release Version

`dsn~validate-changes-file-contains-release-version~1`

RD validates that `changes_<version>.md` file contains a version to be released.

Covers:
* [`req~validate-mandatory-directory-tree-elements~1`](system_requirements.md#validate-mandatory-directory-tree-elements)

Needs: impl, utest

#### Validate Changes File Contains Release Letter Body

`dsn~validate-changes-file-contains-release-letter-body~1`

RD validates that `changes_<version>.md` file contains a release letter body.

Covers:
* [`req~validate-mandatory-directory-tree-elements~1`](system_requirements.md#validate-mandatory-directory-tree-elements)

Needs: impl, utest

### GitHub Platform Validation

Validations listed here are necessary for a release on the GitHub.

#### Validate Release Letter Contains Release Header

`dsn~validate-release-letter~1`

RD validates that a release letter for a GitHub release is present and contains all necessary information.

Covers:
* [`req~gitub-release-description-from-release-letter~1`](system_requirements.md#github-release-description-from-release-letter)

Needs: impl, utest

#### Validate GitHub Issues Exist

`dsn~validate-github-issues-exists~1`

RD validates that all the issues mentioned in the release letter exist.

Covers:
* [`req~validation-github-issue-ticket-numbers~1`](system_requirements.md#validating-github-issue-ticket-numbers)

Needs: impl, utest

#### Validate GitHub Issues Are Closed

`dsn~validate-github-issues-are-closed~1`

RD validates that all the issues mentioned in the release letter are closed on the GitHub.

Covers:
* [`req~validating-that-github-issues-are-closed~1`](system_requirements.md#validating-that-github-issues-are-closed)

Needs: impl, utest

### Maven Platform Validation

Validations listed here are necessary for a release on the Maven CEntral.

#### Validate `release_droid_release_on_maven_central.yml` Workflow Exists

`dsn~validate-maven-release-workflow-exists~1`

RD validates that a workflow that helps us to release on Maven Central Repository exists.

Covers:
* [`req~releasing-on-maven~1`](system_requirements.md#releasing-on-maven-central-repository)

Needs: impl, utest

## Release

### Release Duration Estimation
`dsn~estimate-duration~1`

If the current release is not the first one then RD uses the duration of the preceeding release as estimation for the current release.

Covers:
* [`req~estimation-and-progress-display~1`](system_requirements.md#release-estimation-and-progress-display)

Needs: impl

#### Estimation Scope
`dsn~estimation-scope~1`

Design decision: The overall estimation only considers the durations of the execution of all involved GitHub workflows. For other release platforms it is sufficient to use a constant estimation, usually only a few seconds.

Covers:
* [`req~estimation-and-progress-display~1`](system_requirements.md#release-estimation-and-progress-display)

Rationale:
Most of the overall runtime is spent for execution of GitHub workflows (aka. action, workflow run). Additionally other platforms do not provide an easy way to retrieve the duration of the preceding release.

Estimation based on workflow execution includes
* Preparation and checksum phase
* Upload of artifacts to GitHub
* Release to maven central


#### Missing Estimation
`dsn~missing-estimation~1`

If the current release is the first one or retrieval of duration of the preceding release fails then RD continues without an estimation.

Covers:
* [`req~estimation-and-progress-display~1`](system_requirements.md#release-estimation-and-progress-display)

Needs: impl

#### Progress Display
`dsn~progress-display~1`

During release process RD displays and continuously updates a progress display with the following information:
* when the release has begun
* how long it is already running
* when it is expected to be finished

Covers:
* [`req~estimation-and-progress-display~1`](system_requirements.md#release-estimation-and-progress-display)

Needs: impl

### Release Guide

#### Target audience
`dsn~target-audience~1`

RD detects the defined target audience for releases of the current project from central project overview.

Covers:
* `req~target-audience-for-annnouncing-the-release~1`

Needs: impl

#### Aggregate data
`dsn~aggregate-data~1`

RD aggregates data from appropriate sources in order to fill the content of the release guide.

Covers:
* `req~channels-for-announcing-the-release~1`

Needs: impl

#### Release Guide Channels
`dsn~release-guide-channels~1`

For each channel the release guide contains all required data.

Covers:
* `req~channels-for-announcing-the-release~1`

Needs: impl

#### Configure Actual URLs
`dsn~configure-actual-urls~1`

User can configure actual URLs for channels and publication pages in separate configuration files.

Rationale:
* Hide confidential data such as URLs inside the company's intranet.
* Adapt to changes of processes or tools in the future without code changes.

Needs: impl

### Release Preparations

#### Prepare Checksum
`dsn~prepare-checksum~1`

RD prepares a checksum for each deliverable and store it in the GitHub artifactory.
RD uses `prepare_original_checksum.yml` workflow for it.

Rationale:

RD verifies that the build is green and stores a checksum file to avoid running tests again in case the release was not finished.

Covers:
* [`req~run-tests-only-once~1`](system_requirements.md#run-tests-only-once)

Needs: impl

#### Compare Checksum

`dsn~compare-checksum~1`

RD compares a stored checksum if one exists with a fresh checksum.
RD uses `print_quick_checksum.yml` workflow to create a fresh checksum.
RD uses `prepare_original_checksum.yml` artifactory to obtain a stored checksum.

Covers:
* [`req~run-tests-only-once~1`](system_requirements.md#run-tests-only-once)

Needs: impl

#### Validating the Release Date

`dsn~validating-release-date~1`

During validation RD checks if the release date is up-to-date. If it is not equal to today's date, RD prints a warning message and fails the validation.

Covers:
* [`req~validating-release-date~1`](system_requirements.md#validating-the-release-date)

Needs: impl, utest

### Release on GitHub

#### Create new GitHub Release

`dsn~create-new-github-release~1`

RD creates a new GitHub release.

Covers:
* [`req~releasing-on-github~1`](system_requirements.md#releasing-on-github)

Needs: impl

#### Retrieve GitHub Release Header from Release Letter

`dsn~retrieve-github-release-header-from-release-letter~2`

RD builds the GitHub release's title by concatenating the version and the header of the release letter.

Covers:
* [`req~github-release-title-from-release-letter~1`](system_requirements.md#github-release-title-from-release-letter)

Needs: impl

#### Retrieve GitHub Release Body from Release Letter

`dsn~retrieve-github-release-body-from-release-letter~1`

RD extracts the GitHub release's body from the release letter.

Covers:
* [`req~gitub-release-description-from-release-letter~1`](system_requirements.md#github-release-description-from-release-letter)

Needs: impl

#### Upload GitHub Release Assets
`dsn~upload-github-release-assets~1`

RD uploads and attaches GitHub Release assets to the new release.

Covers:
* [`req~attaching-deliverables-to-a-github-release~1`](system_requirements.md#attaching-deliverables-to-a-github-release)

Needs: impl

#### Git Tags
`dsn~creating-git-tags~1`

For regular releases RD benefits from GitHub API already creating an appropriate tag. Projects with modules using programming language `go` in contrast may require different or even additional tags.

RD identifies go modules by entries in file `.project-keeper.yml`:

* If file `.project-keeper.yml` contains a go module in root folder (`path: go.mod`)
  * RD creates a git tag with prefix `v`, e.g. `v1.2.3`
  * RD ignores other source modules
* Otherwise RD simply falls back to regular git tags, e.g. `1.2.3` as created by GitHub API by default.

Additionally for each go module in a subfolder contained in file `.project-keeper.yml` (e.g. `path: subfolder/go.mod`)
* RD creates an *additional* git tag with prefix containing the name of the subfolder, a slash `/` and the letter `v`, e.g. `subfolder/v1.2.3`

Covers:
* [`req~creating-git-tags~1`](system_requirements.md#creating-additional-tags)

Needs: impl

### Release on Maven Central Repository

#### Create new Maven Release

`dsn~create-new-maven-release~1`

RD creates a new release on the Maven Central Repository.

Covers:
* [`req~releasing-on-maven~1`](system_requirements.md#releasing-on-maven-central-repository)

Needs: impl, utest

### Release on Exasol Community Portal

#### Create new Release Announcement on Exasol Community Portal

`dsn~create-new-release-announcement-on-exasol-community-portal~1`

RD creates a new release announcement draft on the Exasol Community Portal via Khoros API.

Covers:
* [`req~releasing-on-exasol-community-portal~1`](system_requirements.md#releasing-on-exasol-community-portal)

Needs: impl

#### Extract Release Changes Description from Release Letter

`dsn~extract-release-changes-description-from-release-letter~1`

RD extracts a release changes description from the `## Summary` section of the release letter.

Covers:
* [`req~release-changes-description-from-release-letter~1`](system_requirements.md#release-changes-description-from-release-letter)

Needs: impl, utest

#### Extract Project Description from File

`dsn~extract-project-description-from-file~1`

RD extracts information required to create a release announcement from the `release_config.yml` file that is located in the root directory of the project.

Covers:
* [`req~get-project-description-from-repository~1`](system_requirements.md#get-project-description-from-repository)

Needs: impl, utest

### Release Clean Up

#### Remove Checksum

`dsn~remove-checksum~1`

RD removes a stored checksum after the release or when it's outdated.

Covers:
* [`req~run-tests-only-once~1`](system_requirements.md#run-tests-only-once)

Needs: impl

# Cross-cutting Concerns

# Design Decisions

* [Estimation Scope](#estimation-scope)

# Quality Scenarios

# Risks
