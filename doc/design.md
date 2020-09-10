# Introduction

## Acknowledgments

This document's section structure is derived from the "[arc42](https://arc42.org/)" architectural template by Dr. Gernot Starke, Dr. Peter Hruschka.

## Terms and Abbreviations

<dl>
    <dt>RR</dt><dd>Release Robot</dd>
    <dt>GR</dt><dd>Git repository</dd>
</dl>

## Requirement Overview

Please refer to the [System Requirement Specification](system_requirements.md) for user-level requirements.

# Building Blocks

This section introduces the building blocks of the software. Together those building blocks make up the big picture of the software structure.

## `GitRepository`
`dsn~git-repository~1`

The `GitRepository` is a representation of a git-based project on which RR performs validations and releases.   

## `PlatformValidator`
`dsn~platform-validator~1`

The `PlatformValidator` performs a validation on a platform.

## `ReleaseMaker`
`dsn~release-maker~1`

The `ReleaseMaker` performs a release on a platform.

# Runtime

This section describes the runtime behavior of the software.

## Release Robot Run Prerequisites

### Users Set Project 
`dsn~users-set-project~1`

Users select a GitHub-based project and provide its name to RR.

Covers:

* `req~users-provide-rr-parameters~1`

Needs: impl

### Users Set Run Goal
`dsn~users-set-run-goal~1`

Users select whether they want to `validate` or `release` the project. 

- RR performs only validation if it gets a `validate` goal.
- RR performs validation and then release if it gets a `release` goal.

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

### Users Add to Their Project Yml Files That Helps Upload Deliverables 
`dsn~users-add-to-their-project-yml-files-that-helps-upload-deliverables~1`

Users add to their project a `yml` files that helps RR uploading deliverables.

Covers:

* `req~detect-deliverables-in-a-maven-project~1`

Needs: impl

## Git Repository

### GR Provides Current Version
`dsn~gr-provides-current-version~1`

The `GitRepository` detects and provides a current project's version.

Covers:

* `req~detect-current-version-from-maven-pom~1`

Needs: impl, utest

### GR Retrieves Branch Content
`dsn~gr-retrieves-branch-content~1`

The `GitRepository` retrieves content of specified branch.

Covers:

* `req~access-project-files~1`

Needs: impl, utest

## Release Robot Runtime

### RR Starts Release Only If All Validation Succeed
`dsn~rr-starts-release-only-if-all-validation-succeed~1`

RR starts release if and only if all validation for the platforms users specified succeed.

Covers:

* `req~stopping-the-release-on-failed-step-validation~1`

Needs: impl

## Validation

### Git Repository Validation

Validations listed here are platform-independent.

#### Validate Release Version
`dsn~validate-release-version~1`

RR validates if a new version is suitable for a new release based on the information about previous releases.

Covers:

* `req~detect-version-conflicts-in-the-project-sources~1`

Needs: impl, utest

#### Validate Changelog
`dsn~validate-changelog~1`

RR validates `changelog.md` file.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

#### Validate Changes File
`dsn~validate-changes-file~1`

RR validates `changes_<version>.md` file.

Covers:

* `req~validate-mandatory-directory-tree-elements~1`

Needs: impl, utest

### GitHub Platform Validation

Validations listed here are necessary for a release on the GitHub.

#### Validate Release Letter
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

## Release

### Release on GitHub

#### Create new GitHub Release
`dsn~create-new-github-release~1`

RR creates a new GitHub release.

Covers:

* `req~releasing-on-github~1`

Needs: impl, utest

#### Retrieve GitHub Release Header from Release Letter
`dsn~retrieve-github-release-header-from-release-letter~1`

RR parses a release letter and retrieves a header for a GitHub release.

Covers:

* `req~github-release-title-from-release-letter~1`

Needs: impl, utest

#### Retrieve GitHub Release Body from Release Letter
`dsn~retrieve-github-release-body-from-release-letter~1`

RR parses a release letter and retrieves a body for a GitHub release.

Covers:

* `req~gitub-release-description-from-release-letter~1`

Needs: impl, utest

#### Upload GitHub Release Assets
`dsn~upload-github-release-assets~1`

RR uploads and attaches GitHub Release Assets to the new release.

Covers:

* `req~attaching-deliverables-to-a-github-release~1`

Needs: impl

# Cross-cutting Concerns

# Design Decisions

# Quality Scenarios

# Risks