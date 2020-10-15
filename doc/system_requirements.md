<head><link href="oft_spec.css" rel="stylesheet"></head>

# System Requirement Specification Exasol Release Robot

## Introduction

Release Robot (short "RR") is a combination of validators and automation that make a software release safer, faster and more convenient.

## About This Document

### Target Audience

The target audience are software developers, software integrators and quality assurance. See section ["Stakeholders"](#stakeholders) for more details.

### Goal

Release Robot's main goals are:

* speed up software releases
* eliminate human error
* make releases uniform
* make releases reproducible

### Quality Goals

TODO.

## Stakeholders

When reading this section please remember that the listed stakeholders are roles, not people! It is not uncommon in software projects that the same person fulfills multiple roles.

### Software Developers

Software Developers use RR to validate whether their contributions affecting the next release of software project are complete and correct.

### Software Integrators

Software Integrators are responsible for integrating the various parts of a software project into one or more deliverables. They also prepare and execute releases.

### Terms and Abbreviations

The following list gives you an overview of terms and abbreviations commonly used in RR documents.

<dl>
<dt>Deliverables</dt><dd>Files that are published in a release.</dd>
<dt>Release</dt><dd>The act or result of publishing a project version.</dd>
<dt>Version</dt><dd>A set of changes on a projects sources bundled into a version.</dd>
</dl>

## Features

Features are the highest level requirements in this document that describe the main functionality of RR.

### Release Validation
`feat~release-validation~1`

RR lets Software Developers and Integrators check whether all conditions for a release are fulfilled.

Needs: req

### Release Automation
`feat~release-automation~1`

RR generates releases without human interaction.

Needs: req

### Maven Support
`feat~maven-support~1`

RR support releases based on Apache Maven projects.

Needs: req

### GitHub Support
`feat~github-support~1`

RR supports releasing on GitHub.

Needs: req

### Report
`feat~report~1`

RR writes a report that summarizes all steps and their results.

Needs: req

## Functional Requirements

### Access Project Files
`req~access-project-files~1`

RR accesses the project's files it needs for validations and releases.

Covers:

* [feat~release-automation~1](#release-automation)

Needs: dsn

### Users Provide RR Parameters
`req~users-provide-rr-parameters~1`

Users provide parameters RR needs for releases and validations.

Covers:

* [feat~release-automation~1](#release-automation)

Needs: dsn

### Validate Project
`req~validate-project~1`

RR runs validations on a user-specified project.

Covers:

* [feat~release-validation~1](#release-validation)

Needs: dsn

### Validation Report
`req~validation-report~1`

RR creates a validation report containing all successful and failed validations.

Covers:

* [feat~release-validation~1](#report)

Needs: dsn

### Release Project
`req~release-project~1req~release-project~1`

RR releases a user-specified project.

Covers:

* [feat~release-automation~1](#release-automation)

Needs: dsn

### Users Can Set Git Branch for Validation
`req~users-can-set-git-branch-for-validation~1`

Users can set a branch to perform a validation on.

Rationale:

This allows users to run RR on a specified git branch and fix problems it detects before merging the branch into master.

Needs: dsn

### Release Conditions Validation

There are two kinds of validation in a release process, the one before you start releasing and the ones that assert that a step in the release was successful.

Let's call them "pre-condition validation" and "release step validation".

#### Pre-condition validation

Teams usually agree on rules that each member follows in order to have complete and uniformly looking releases. This helps teammates navigate each other's projects and provides a professional appearance.

Typical examples of these rules are directory layout, naming conventions, versioning rules and so forth.

Often parts of these rules are enforced by the build system. This is especially true if the rules are centered around standards set by either the language in which the project is realized or the used build framework itself. 
The [Apache Maven](https://maven.apache.org/) framework for example is well known for its convention over configuration approach, intentionally chosen so that anyone familiar with the framework will be able to navigate and understand foreign projects built with the same tool chain.

Other parts however are more team-specific or company-specific like the decision about contents and layout of a release letter or versioning schemes.

RR must be able to cover both ways of pre-condition validation, one where it uses the validations built into the build framework and defining additional validation rules for the parts the build framework does not cover.

##### Validate Mandatory Directory Tree Elements
`req~validate-mandatory-directory-tree-elements~1`

RR validates mandatory directory tree elements.

Comment:

Only parts of the tree can be standardized, since for example a Python project has different conventions than a Java or C++ project and it is recommended to follow the established conventions in order to help experienced developers getting to know a project quickly.

Rationale:

This helps ensure a uniform layout and easier navigation since team members are instantly familiar with a projects's directory structure.

Covers:

* [feat~release-validation~1](#release-validation)

Needs: dsn

##### Stopping the Release on Failed Step Validation
`req~stopping-the-release-on-failed-step-validation~1`

RR stops a release if a step's validation fails.

Rationale:

This typically happens if an unforeseen problem occurred, like a network outage or exceeded storage space. In this case stopping the release and fixing the underlying issue is required.

Covers:

* [feat~release-validation~1](#release-validation)

Needs: dsn

##### Stopping the Release in Case of Version Conflicts in the Project Sources
`req~detect-version-conflicts-in-the-project-sources~1`

RR stops a release if there is detects one of the version conflicts listed below:

* Version number of the current release was already used in a previous release.
* Version numbering has a gap (e.g it jumped from 1.1.0 to 3.0.0)

Rationale:

Such a conflict usually indicates that the current version number was not updated or a merge operation went wrong. Fixing the issue before the release is required.

Covers:

* [feat~release-validation~1](#release-validation)

Needs: dsn

#### Release Step Validation

Release step validation serves two purposes. The first one is obvious: you want to validate that a step in your release process was executed successfully and that all the expected results exist and are valid.

Additionally, step validation helps to recover from a release that was interrupted midway. 
Imagine a situation where you built and signed your delivery packages but somewhere along the way a server that you wanted to deploy the delivery on was not reachable because it underwent maintenance at that time. 
Depending on how many steps your release process has and how complicated they are, starting over can be terribly annoying and costly.

What you really want is to find out, how far you got and restart from that point.

One strategy would be to remember which steps were successful and repeat the first one that failed. That strategy is unstable though, since it can for example be that the step is not fully repeatable.

A safer option is to use the step validation for each step to find out which parts of the release are there and which are missing.

Since the step validation rules depend on the platform the step is executed on, they are listed on a per-platform basis in later sections of this document.

### Platform and Tool Support

#### Maven Project Support

##### Detect Current Version From Maven POM
`req~detect-current-version-from-maven-pom~1`

RR can derive the current version from the project's POM file.

Rationale:

Integrators should maintain the current version in one single place, the project sources.

Covers:

* [feat~maven-support~1](#maven-support)

Needs: dsn

##### Detect Deliverables in a Maven Project
`req~detect-deliverables-in-a-maven-project~1`

RR detects which files in a Maven Project are deliverables.

Rationale:

Maven projects have strict conventions. If the developers follow them, detecting the deliverables is possible using a fixed ruleset. This eliminates the chance for human error when assembling the list of deliverables.

Covers:

* [feat~maven-support~1](#maven-support)

Needs: dsn

#### GitHub Support

##### Releasing on GitHub
`req~releasing-on-github~1`

RR can create a GitHub release.

Rationale:

GitHub is as of June 2020 the most prominent Source Code platform. If you use GitHub, creating releases there is a must.

Covers:

* [feat~github-support~1](#github-support)

Needs: dsn

##### GitHub Release Description From Release Letter
`req~gitub-release-description-from-release-letter~1`

RR copies the release letter into the release description of a GitHub release.

Rationale:

Integrators should have to maintain only one single source for a release letter.

Covers:

* [feat~github-support~1](#github-support)

Needs: dsn

##### GitHub Release Title From Release Letter
`req~github-release-title-from-release-letter~1`

RR extracts the GitHub release title from the release letter.

Rationale:

Integrators should have to maintain only one single source for the release title.

Covers:

* [feat~github-support~1](#github-support)

Needs: dsn

##### Validating GitHub Issue Ticket Numbers
`req~validation-github-issue-ticket-numbers~1`

RR validates that the issue ticket numbers mentioned in the release letter exist.

Rationale:

Typos in ticket IDs happen an RR should find them before users do.

Covers:

* [feat~github-support~1](#github-support)

Needs: dsn

##### Validating That GitHub Issues are Closed
`req~validating-that-github-issues-are-closed~1`

RR validates that the issue ticket numbers mentioned in the release letter are all closed.

Rationale:

If an issue is not close, the version is not complete and therefore not ready to be released.

Covers:

* [feat~github-support~1](#github-support)

Needs: dsn

##### Attaching Deliverables to a GitHub Release
`req~attaching-deliverables-to-a-github-release~1`

RR attaches the deliverables to a GitHub release.

Rationale:

After it found out, what the deliverables are in a separate step (see e.g. ["Detect Deliverables in a Maven Project"](#detect-deliverables-in-a-maven-project)), RR is capable of uploading them to GitHub without user intervention.

Covers:

* [feat~github-support~1](#github-support)

Needs: dsn

## Quality Requirements

### Quality Tree

    Utility
      |
      |-- Performance
      |-- Modifiability
      '-- Security

### Quality Scenarios
