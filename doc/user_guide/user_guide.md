# User Guide

## Supported Programming Languages

Here you can find a list of supported programming languages. 
Note that 'a programming language' here means 'the main programming language which determines the project's structure'. 

* Java

## Supported Release Platforms

The following list contains the platforms on which the Release Droid can perform a release:

* [GitHub](https://github.com)
* [Maven Central Repository](https://mvnrepository.com/repos/central)

## Pre-requirements and Release Rules

Your project must fulfil the criteria listed in this section &mdash; depending on a programming language you use and platforms you want to release on to make a release with the Release Droid.

### Common Rules for All Repositories

* Currently, Release Droid only supports the GitHub-based projects. 
  So the first essential requirement: the project must be uploaded to the GitHub.
  
* The main programming language of the project must be in the list of [supported programming languages](#supported-programming-languages).

* The project must have a valid version number consisting of three parts: `<major version>.<minor version>.<bug fix version>`. 
  The version must contain only digits and dots.
    
* You need a GitHub account.

* You need to create a GitHub OAuth token. 
  For that, go to `Settings` &rarr; `Developer Settings` &rarr; `Personal access tokens` &rarr; `Generate new token`. 
  Select scope `repo:status` and `public_repo`, then create a token.

#### Changes Log

* The project must contain `changelog.md` and `changes_<version>.md` files in the following directory:

```
project root/
  '-- doc/
       '-- changes/
            |-- changelog.md
            '-- changes_<version>.md
```

* The user must create a new [`changes_<version>.md` file](changes_file_template.md) for each new release. The `changes_<version>.md` must contain:

    1. Header in the following format: `# <Project name> <version>, released yyyy-mm-dd`
    
    2. Description of the release changes. 
     
* The `changelog.md` must contain a link to the `changes_<version>.md` file.
        
### Rules for Java Repositories

* The project must be a valid [Maven](https://maven.apache.org/) project.

* The main `pom.xml` file must contain:
    1. a `<version></version>` tag with a valid version as a constant;
    1. `<artifactId></artifactId>` tag with a project name;

### Rules for Release on GitHub

* If the GitHub repository's releases page is not empty, the new release version must follow the versioning rules.
It is not allowed to skip a version, to release the same version twice or to release a version that comes before the latest release.

* The project must contain a `/.github/workflows/github_release.yml` file in the root directory to upload release assets. 
  Please check [templates](github_release_template.md).

* A `changes_<version>.md` file must contain a line starting with `Code name:` followed by a GitHub release header. 
  This line should appear between the file's header and the first section describing the changes.

* A `changes_<version>.md` file must contain one or more GitHub ticket numbers in the following format: '#1:<comment>'.
  All the mentioned on the file tickets must have a closed status on the GitHub.
 
### Rules for Release on Maven Central

Prerequisites:

1. The repository must be on the while-list of the organizational credential for Maven Central on the GitHub.

Rules: 

* The project must contain a `/.github/workflows/maven_central_release.yml` file in the root directory.
  Please check a [template](maven_central_release_template.md).

* The Maven file must contain all necessary plugins and settings.
  Please check a [template](maven_central_release_pom_file_template.md).
  
## How to Use Release Droid

### Run from Terminal

#### Requirements

* JRE 11 or higher

#### Run Steps

1. Download the latest available [release](https://github.com/exasol/release-droid/releases) of Release Droid.

1. (Optional) Place a file with credentials  in your home directory: `~/.release-droid/credentials`.
    We recommend restricting access to this file for security purposes:
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

| Long Option | Short Option | Mandatory | Description                                        | Possible values               |
|-------------|--------------|-----------|----------------------------------------------------|-------------------------------|
| --branch    | -b           | No        | Git branch to work with (only for `validate` goal) | Any valid git branch name     |
| --goal      | -g           | Yes       | Goal to execute                                    | `validate`, `release`         |
| --name      | -n           | Yes       | GitHub project name                                | Any valid GitHub project name |
| --platforms | -p           | Yes       | Comma-separated list of release platforms          | `github`   `maven`            |

Notice:

* Please, specify the platforms list **without spaces**. This is a valid input:  `github,maven`. This is invalid input: `github, maven`

#### Release Goals

* `validate` - use it when you are not ready to release yet, but want to check whether your branch is ready to be released. 
This goal works with the git `master` branch by default, but you can also choose another branch available on GitHub. 

* `release` - use it to validate and immediately start the release process. This goal always works with the git `master` branch.
Note that you don't need to run `validate` goal before the release, as the `release` goal itself also runs validations.

## Debugging

If you need to debug RR, you can adjust Java's log level. The default level is `INFO`. 

To do that you need to change a default logging level in JDK/JRE. Find a `logging.properties` file in the JDK/JRE directory and change the default logging level. 

For `openjdk-11` you can find this file here: `jdk-11/conf/logging.properties`.
