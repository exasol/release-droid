# User Guide

## Supported Programming Languages

Here you can find a list of supported programming languages. 
Note that 'a programming language' here means 'the main programming language which determines the project's structure'. 

* Java

## Supported Release Platforms

The following list contains the platforms on which the Release Robot can perform a release:

* [GitHub](https://github.com)

## Pre-requirements and Release Rules

Your project must fulfil the criteria listed in this section &mdash; depending on a programming language you use and platforms you want to release on to make a release with the Release Robot.

### Common Rules for All Projects

* Currently, Release Robot only supports the GitHub-based projects. 
  So the first essential requirement: the project must be uploaded to the GitHub.
  
* The main programming language of the project must be in the list of [supported programming languages](#supported-programming-languages).

* The project must have a valid release version consisting of three parts: `<major version>.<minor version>.<bug fix version>`

#### Changes Log

* The project must contain `changelog.md` and `changes_<version>.md` files in the following directory:

```
project root/
  '-- doc/
       '-- changes/
            |-- changelog.md
            '-- changes_<version>.md
```

* The user must create a new `changes_<version>.md` file for each new release. The `changes_<version>.md` must contain:

    1. Header in the following format: `# <Project name> <version>, released yyyy-mm-dd`
    
    2. Description of the release changes. 
     
* The `changelog.md` must contain a link to the `changes_<version>.md` file.
        
### Rules for Java Repositories

* The project must be a valid [Maven](https://maven.apache.org/) project.

* The main file `pom.xml` of the project must contain a `<version></version>` tag containing a valid version as a constant.

### Rules for Release on GitHub

* If the GitHub repository's releases page is not empty, the new release version must follow the versioning rules.
It is not allowed to skip a version, to release the same version twice or to release a version that comes before the latest release.

* The project must contain a `/.github/workflows/upload_release_asset.yml` file in the root directory. 
You can find examples [here](upload_release_asset_example.md).

## How to Use Release Robot

### Run from Terminal

#### Requirements

* JRE 11 or higher

#### Run Steps

1. Download the latest available [release](https://github.com/exasol/release-robot/releases) of Release Robot.

1. (Optional) Place a file with credentials  in your home directory: `~/.release-robot/credentials`.
    The file must contain the following two properties:

    ```properties
    github_username=<your username>
    github_oauth_access_token=<github access token>
    ```
    If Release Robot cannot find this file during an execution, it asks the user to input the credentials directly through terminal.

1. Run Release Robot from a terminal:
    
    `java -jar release-robot-<version>.jar -name <project name> -goal <goal> -platforms <comma-separated list of platforms>`
    
    For example:

    `java -jar release-robot-0.1.0.jar -name virtual-schema-common-java -goal validate -platforms github`

##### Command Line Arguments

| Long Option | Short Option | Mandatory | Description                               | Possible values               |
|-------------|--------------|-----------|-------------------------------------------|-------------------------------|
| --goal      | -g           | Yes       | Goal to execute                           | `validate`, `release`         |
| --name      | -n           | Yes       | GitHub project name                       | Any valid GitHub project name |
| --platforms | -p           | Yes       | Comma-separated list of release platforms | `github`                      |
