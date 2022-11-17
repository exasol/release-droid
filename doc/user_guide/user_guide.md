# User Guide

## Supported Programming Languages

Release Droid provides advanced support for the following languages:

* Java
* Scala

A repository with any other language should be marked as `Generic`, see
* CLI parameter `--language generic` or
* entry `language: Generic` in configuration file [`release_config.yml`](#file-release_configyml)

## Supported Release Platforms

The following list contains the platforms on which the Release Droid can perform a release:

* [GitHub](https://github.com)
* [Maven Central Repository](https://search.maven.org/) (Java only)
* [Exasol Jira](https://www.exasol.com/support)

## Pre-requirements and Release Rules

Your project must fulfil the criteria listed in this section &mdash; depending on a programming language you use and platforms you want to release to.

### Common Rules for All Repositories

* Currently, Release Droid only supports the GitHub-based projects. So the first essential requirement: the project must be uploaded to the GitHub.
* The project must contain a `/.github/workflows/release_droid_prepare_original_checksum.yml` file to run project tests and prepare a checksum. Please check [templates](templates/prepare_original_checksum_template.md).
* The project must contain a `/.github/workflows/release_droid_print_quick_checksum.yml` file to run project tests and prepare a checksum. Please check [templates](templates/print_quick_checksum_template.md).
* The project must have a valid version number consisting of three parts: `[v]<major version>.<minor version>.<bug fix version>`. The version can contain a leading `v`.
* You need a GitHub account.
* You need to create a GitHub OAuth token. For that:
  * Go to `Settings` &rarr; `Developer Settings` &rarr; `Personal access tokens` &rarr; `Generate new token`
  * Select scope `repo:status` and `public_repo`
  * Then create a token

#### Changes Log

* The project must contain files `changelog.md` and `changes_<version>.md` in the following directory:

```
project root/
 `- doc/
    `- changes/
       |- changelog.md
       `- changes_<version>.md
```

* The user must create a new file [`changes_<version>.md`](templates/changes_file_template.md) for each new release. The `changes_<version>.md` must contain:
    1. Header in the following format: `# <Project name> <version>, released yyyy-mm-dd`
    2. Description of the release changes.
* The `changelog.md` must contain a link to the `changes_<version>.md` file.

### Rules for Java Repositories

* The project must be a valid [Maven](https://maven.apache.org/) project.

* The `pom.xml` file must contain:
    1. a `<version></version>` tag with a valid version as a constant
    1. `<artifactId></artifactId>` tag with a project name

### Rules for Scala Repositories

* The project must be built with [sbt](https://www.scala-sbt.org/).
* The project must contain file `plugins.sbt` specifying with `moduleName` and `version`. You can create the file starting with a copy of RD's [template](templates/sbt_file_template.md).
* The project must include plugin [`sbt-reproducible-builds`](https://github.com/raboof/sbt-reproducible-builds) version 0.25 or later. Please add the plugin to the `plugins.sbt` file:

```
addSbtPlugin("net.bzzt" % "sbt-reproducible-builds" % "0.25")
```

Also, you need to enable the plugin in file [`build.sbt`](templates/sbt_file_template.md).

### Rules for Release on GitHub

* If the GitHub repository's releases page is not empty, the new release version must follow the versioning rules. Release Droid does not allow to
  * skip a version
  * to release the same version twice or
  * to release a version lower than the latest release.
* Files `changes_<version>.md` must contain
  * a line starting with `Code name:`
    * followed by a GitHub release header.
    * This line should appear between the file's header and the first section describing the changes.
  * one or more GitHub ticket numbers in the following format: `#1:<comment>`.
    * All the mentioned on the file tickets must have a closed status on the GitHub.
* (Optional) For uploading assets to the release, the project must contain a file `/.github/workflows/release_droid_upload_github_release_assets.yml`. Please check [templates](templates/upload_github_release_assets_template.md).

### Rules for Release on Maven Central (Java repositories)

Pre-requisites:

1. The repository must be on the while-list of the organizational credential for Maven Central on the GitHub.

Rules:

* The project must contain a `/.github/workflows/release_droid_release_on_maven_central.yml` file in the root directory. Please check a [template](templates/release_on_maven_central_template.md).
* The Maven file must contain all necessary plugins and settings. Please check a [template](templates/maven_central_release_pom_file_template.md).

### Rules for Release on Exasol Jira

* The release on the GitHub is a pre-requisite for the Jira release.
Please, be aware that the GitHub and Jira releases must be made on the same machine because RD will search for the release state stored on the machine.
* You need to provide Jira credentials either by manual input or in configuration file [`~/.release-droid/credentials`](#file-release-droidcredentials).

## Configuration Files

If you want to reduce the number of keystrokes required to run release droid you can provide credentials and some of the command line options in some configuration files.

### File `~/.release-droid/credentials`

If you create this file in your home directory then release droid will read credentials for GitHub and Jira from this file. This way you no longer need to manually enter the credentials into the terminal each time you want to run release droid.

On Windows RD expects the file at `C:\Users\<username>\.release-droid\credentials`.

Sample content:

```properties
github_username=<your github username>
github_oauth_access_token=<github access token>
jira_username=<your jira user name>
jira_password=<jira password>
```

The lines with prefix "jira" are only relevant if releasing to platform Jira.

If you want RD to generate a [Release Guide](#release-guide) then you might want to add additional entries.

If RD cannot find this file during runtime, it asks the user to input the credentials directly through terminal.

We recommend restricting access to this file for security purposes:

```bash
chmod u-x,g-rwx,o-rwx "$HOME"/.release-droid/credentials
```

In case RD reports access "forbidden" when accessing Jira you might need to change your Jira password:
* Try to avoid special characters
* Use short password, i.e. not longer than 12 characters

### File `release_config.yml`

If you create this file in the directory of your project then release droid
will read platforms and programming language from it.

```yaml
release-platforms:
  - GitHub
  - Maven
  - Jira
language: Java
```

Section [Release Guide](#release-guide) describes optional additional entries.

## How to Use Release Droid

### Run from Terminal

#### Requirements

* JRE 11 or higher

#### Run Steps

1. Download the latest available [release](https://github.com/exasol/release-droid/releases) of Release Droid.

1. Optionally create the [configuration files](#configuration-files) described above.

   In case RD reports access "forbidden" when accessing Jira you might need to change your Jira password:
   * Try to avoid special characters
   * Use short password, i.e. not longer than 12 characters

1. Run Release Droid from a terminal:

   `java -jar release-droid-1.3.2.jar -name <project name> -goal <goal> -platforms <comma-separated list of platforms>`

   For example:

   `java -jar release-droid-1.3.2.jar -name virtual-schema-common-java -goal validate -platforms github`

   (Optional) Windows: You can simplify this by creating a `release-droid.bat` file containing the following contents `java -jar C:\tools\release-droid-1.3.2.jar %*`.
   Make sure you use the full path for the .jar file and don't forget to include the location of your new batch file in your PATH so you can always access it from your CLI.
   You can just use the name of the .bat file you created from then on e.g.:
   `release-droid -name virtual-schema-common-java -goal validate ...`

#### Command Line Arguments

| Long Option     | Short Option | Mandatory | Description                                             | Possible values                         |
|-----------------|--------------|-----------|---------------------------------------------------------|-----------------------------------------|
| -branch         | -b           | No        | Git branch to work with (only for `validate` goal)      | A valid git branch name                 |
| -goal           | -g           | No        | Goal to execute. `validate` is a default goal.          | `validate`, `release`                   |
| -help           | -h           | No        | Prints help                                             |                                         |
| -language       | -lg          | No        | Specify repository language if not auto-detected        | `java`, `scala`, `generic`              |
| -local          | -l           | No        | Path to the repository root directory                   | A valid repository root directory path  |
| -name           | -n           | Yes       | GitHub project name                                     | A valid GitHub project name             |
| -platforms      | -p           | No        | Comma-separated list of release platforms. (*)          | `github`, `maven`, `jira`               |
| -skipvalidation |              | No        | Only valid with `release` goal. Use in emergency cases. |                                         |
| -release-guide  | -guide       | No        | Path to write the [Release Guide](#release-guide) to    | Valid path to a file, may exist already |

(*) There are two ways to specify multiple platforms via CLI:

1. `-p github -p maven`
1. `-p github,maven` (This is a deprecated way, which is going to be removed in future)

Please also note the option to specify the platforms in the configuration file [`release_config.yml`](#file-release_configyml)

#### Release Goals

* `validate` - check if the repository is ready to be released. Runs on default branch if `--branch` is not provided.
* `release` - validate and immediately start the release process. Only runs on the default branch.

### Release Guide

Making releases still requires time, effort, and often stereotype tasks. Depending on the project to release the user needs to aggregate data from various sources and type it into different documents and publications. Release Droid therefore provides a document guiding the user through the release process to make releasing as convenient as possible.

Upon [command line option](#command-line-arguments) `-guide` or `--release-guide` RD generates an HTML page containing the release guide.

The user can open the document in a  web browser, follow the described steps, and copy the data for release checklist, team planning, and announcement to chat channels.

#### Data Sources For Release Guide

RD collects the data from various sources:

| Source                                  | Location              | Retrieved information            | Key                                                                             |
|-----------------------------------------|-----------------------|----------------------------------|---------------------------------------------------------------------------------|
| `project-overview/projects.yaml`        | GitHub                | Target audience                  |                                                                                 |
| file `release_config.yml`               | project's repository  | Maven URLs                       | `maven-artifacts`                                                               |
| file `error_code_config.yml`            | project's repository  | Short tag for team planning page | See [error-code-crawler-maven-plugin](https://github.com/exasol/error-code-crawler-maven-plugin) for details |
| file `doc/changes/changes_<version>.md` | project's repository  | Release summary                  | `## Summary`                                                                    |
| file `~/.release-droid/credentials`     | user's home directory | URLs for announcing the release  | See section [URLs for Announcing the Release](#urls-for-announcing-the-release) |

#### Link to Release on Maven Central

If your project is published to Maven Central (i.e. file [`release_config.yml`](#file-release_configyml) mentions platform `Maven`) then RD also adds an appropriate link to the release guide.

By default RD generates the link based on the name of the project's repository. In case your project generates multiple Maven artifacts you need to specify their names in file [`release_config.yml`](#file-release_configyml):

```
maven-artifacts:
  - exasol/project-keeper-cli
  - exasol/project-keeper-maven-plugin
```

#### URLs for Announcing the Release

In file [`~/.release-droid/credentials`](#file-release-droidcredentials) you can add the following keys and assign a URL to each of them

| Key                  | Value: URL of                                                         |
|----------------------|-----------------------------------------------------------------------|
| `release_checklists` | Release checklists page                                               |
| `team_planning`      | Team planning page current quarter in company wiki                    |
| `team_channel`       | Channel for announcing new releases to team in company chat tool      |
| `customer_channel`   | Channel for announcing new releases to customers in company chat tool |

When generating the release guide RD will use these URLs in the generated HTML file.

Additional sample content in file `~/.release-droid/credentials`:

```properties
release_checklists=https://intranet....
team_planning=https://intranet....
team_channel=https://channel....
customer_channel=https://channel...
```

## Debugging

If you need to debug RD, you can adjust the Java log level in file `src/main/resources/logging.properties`. The default level used by release droid is `INFO`.


## Troubleshooting

### Redoing a Release

While making a new release instead of redoing a release, sometimes you can be forced to. E.g. in cases where people depend on a certain version that later turns out to be incomplete. In such cases:

1. Remove all deployed artifacts from the platforms by hand (e.g. GH release and tag)
2. Remove the local status file of the release from `~/.release-droid/state/`
3. Restart the release
