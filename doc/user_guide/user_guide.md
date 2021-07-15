# User Guide

## Supported Programming Languages

We provide an advanced support for the following languages:

* Java
* Scala
  
A repository with any other language should be marked as `Generic` (`--language generic` CLI parameter is mandatory in this case).

## Supported Release Platforms

The following list contains the platforms on which the Release Droid can perform a release:

* [GitHub](https://github.com)
* [Maven Central Repository](https://mvnrepository.com) (Java only)
* [Exasol Community Portal](https://community.exasol.com/)
* [Exasol Jira](https://www.exasol.com/support)

## Pre-requirements and Release Rules

Your project must fulfil the criteria listed in this section &mdash; depending on a programming language you use and platforms you want to release on to make a release with the Release Droid.

### Common Rules for All Repositories

* Currently, Release Droid only supports the GitHub-based projects. So the first essential requirement: the project must be uploaded to the GitHub.

* The project must contain a `/.github/workflows/release_droid_prepare_original_checksum.yml` file to run project tests and prepare a checksum. Please check [templates](templates/prepare_original_checksum_template.md).

* The project must contain a `/.github/workflows/release_droid_print_quick_checksum.yml` file to run project tests and prepare a checksum. Please check [templates](templates/print_quick_checksum_template.md).

* The project must have a valid version number consisting of three parts: `<major version>.<minor version>.<bug fix version>`. The version must contain only digits and dots.

* You need a GitHub account.

* You need to create a GitHub OAuth token. For that, go to `Settings` &rarr; `Developer Settings` &rarr; `Personal access tokens` &rarr; `Generate new token`. Select scope `repo:status` and `public_repo`, then create a token.

#### Changes Log

* The project must contain `changelog.md` and `changes_<version>.md` files in the following directory:

```
project root/
  '-- doc/
       '-- changes/
            |-- changelog.md
            '-- changes_<version>.md
```

* The user must create a new [`changes_<version>.md` file](templates/changes_file_template.md) for each new release. The `changes_<version>.md` must contain:
    
    1. Header in the following format: `# <Project name> <version>, released yyyy-mm-dd`
    
    2. Description of the release changes.

* The `changelog.md` must contain a link to the `changes_<version>.md` file.

### Rules for Java Repositories

* The project must be a valid [Maven](https://maven.apache.org/) project.

* The `pom.xml` file must contain:
    1. a `<version></version>` tag with a valid version as a constant;
    1. `<artifactId></artifactId>` tag with a project name;
    1. `project-keeper-maven-plugin` plugin version `0.6.0` or higher.

### Rules for Scala Repositories

* The project must be built with [sbt](https://www.scala-sbt.org/).

* The project must contain `plugins.sbt` file with `moduleName` and `version` specified. Check a [template](templates/sbt_file_template.md) for example.

* The project must include [`sbt-reproducible-builds` plugin](https://github.com/raboof/sbt-reproducible-builds) version 0.25 or later. Please add the plugin to the `plugins.sbt` file:

```
addSbtPlugin("net.bzzt" % "sbt-reproducible-builds" % "0.25")
```

Also, you need to enable the plugin in the [`build.sbt` file](templates/sbt_file_template.md).

### Rules for Release on GitHub

* If the GitHub repository's releases page is not empty, the new release version must follow the versioning rules. It is not allowed to skip a version, to release the same version twice or to release a version that comes before the latest release.

* The project must contain a `/.github/workflows/release_droid_upload_github_release_assets.yml` file in the root directory to upload release assets. Please check [templates](templates/upload_github_release_assets_template.md).

* A `changes_<version>.md` file must contain a line starting with `Code name:` followed by a GitHub release header. This line should appear between the file's header and the first section describing the changes.

* A `changes_<version>.md` file must contain one or more GitHub ticket numbers in the following format: '#1:<comment>'. All the mentioned on the file tickets must have a closed status on the GitHub.

### Rules for Release on Maven Central (Java repositories)

Pre-requisites:

1. The repository must be on the while-list of the organizational credential for Maven Central on the GitHub.

Rules:

* The project must contain a `/.github/workflows/release_droid_release_on_maven_central.yml` file in the root directory. Please check a [template](templates/release_on_maven_central_template.md).

* The Maven file must contain all necessary plugins and settings. Please check a [template](templates/maven_central_release_pom_file_template.md).

### Rules for Release on Exasol Community Portal

* The project must contain a `release_config.yml` file in the root directory. This file has a few required objects:

1. Array of strings `community-tags`: one or more tag of the project to be added to the community post;
1. String `community-project-name`: this is how the project name will be displayed in the post header;
1. String `community-project-description`: a short description of the project. Please, use markdown in the description;

An example of the file:

```yaml
community-tags:
  - Release Droid Testing
  - Java Tools
  - Open Source
  - GitHub
community-project-name: Testing Release Droid
community-project-description: |
  This is an `open-source tool` that helps integration us testing
  the [Release Droid](https://github.com/exasol/release-droid).
```

* A `changes_<version>.md` file must contain a `## Summary` section. Please describe the changes nicely as we copy the section directly into the releaese announcement. 

* (Optional) In the`~/.release-droid/credentials` file you can add your Exasol Community Portal credentials:

    ```properties
    community_username=<username>
    community_password=<password>
    ```
If you miss this step Release Droid will ask you to input the credentials directly through terminal.

### Rules for Release on Exasol Jira

* The release on the Community portal is a pre-requisite for the Jira release. 
Please, be aware that the Community and Jira releases must be made on the same machine because RD will search for the release state stored on the machine.

## How to Use Release Droid

### Run from Terminal

#### Requirements

* JRE 11 or higher

#### Run Steps

1. Download the latest available [release](https://github.com/exasol/release-droid/releases) of Release Droid.

1. (Optional) Place a file with credentials in your home directory: `~/.release-droid/credentials`. We recommend restricting access to this file for security purposes:
    ```bash
    chmod u-wx,g-rwx,o-rwx "$HOME"/.release-droid/credentials
    ````
   The file must contain the following two properties:

    ```properties
    github_username=<your username>
    github_oauth_access_token=<github access token>
    ```
   If Release Droid cannot find this file during an execution, it asks the user to input the credentials directly through terminal.

1. Run Release Droid from a terminal:

   `java -jar release-droid-<version>.jar -name <project name> -goal <goal> -platforms <comma-separated list of platforms>`

   For example:

   `java -jar release-droid-0.1.0.jar -name virtual-schema-common-java -goal validate -platforms github`

#### Command Line Arguments

| Long Option      | Short Option | Mandatory | Description                                             | Possible values                        |
|------------------|--------------|-----------|---------------------------------------------------------|----------------------------------------|
| --branch         | -b           | No        | Git branch to work with (only for `validate` goal)      | A valid git branch name                |
| --goal           | -g           | No        | Goal to execute. `validate` is a default goal.          | `validate`, `release`                  |
| --help           | -h           | No        | Prints help                                             |                                        |
| --language       | -lg          | No        | Specify repository language if not auto-detected        | `java`, `scala`, `generic`             |
| --local          | -l           | No        | Path to the repository root directory                   | A valid repository root directory path |
| --name           | -n           | Yes       | GitHub project name                                     | A valid GitHub project name            |
| --platforms      | -p           | No        | Comma-separated list of release platforms.*             | `github`, `maven`, `community`, `jira` |
| --skipvalidation |              | No        | Only valid with `release` goal. Use in emergency cases. |                                        |

Notice:

* You can specify the release platforms on the project level once instead of providing them each time via CLI.
  To specify the release platforms, add `release-platforms` list to the `release_config.yml` file.

Example:

```yaml
release-platforms:
  - GitHub
  - Maven
  - Community
  - Jira  
```

* There are two ways to specify multiple platforms via CLI:

1. `-p github -p maven`
1. `-p github,maven` (This is a deprecated way, which is going to be removed in future)

#### Release Goals

* `validate` - check if the repository is ready to be released. Runs on default branch if `--branch` is not provided.

* `release` - validate and immediately start the release process. Only runs on the default branch.

## Debugging

If you need to debug RD, you can adjust Java's log level. The default level for this project is `INFO`. To do that you need to change a logging level in a `logging.properties` file in the `src/main/resources/logging.properties` directory. 
