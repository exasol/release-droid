# User Guide

## Supported Programming Languages

Here you can find a list of supported programming languages. 
Note that 'a programming language' here means 'the main programming language which determines the project's structure'. 

* Java

## Supported Release Platforms

The following list contains the platforms on which the Release Robot can perform a release:

* [GitHub](https://github.com)

## Pre-requirements and Release Rules

You must fulfill the rules listed here depending on a programming language you use and platforms you want to release on to make a release with the Release Robot.

### Common Rules for All Projects

* Currently, the Release Robot only supports the GitHub-based projects. 
  So the first essential requirement: the project must be uploaded to the GitHub.
  
* The main programming language of the project must be in the list of [supported programming languages](#supported-programming-languages).

* The project must have a valid release version consisting of three parts: `<major version>.<minor version>.<bug fix version>`

#### Changes Log

* The project must contain `changelog.md` and `changes-<version>.md` files in the following directory:

```
project root
  |
  |__ doc
       |
       |___changes
            |
            |__ changelog.md
            |
            |__ changes-<version>.md
```

* The user must create a new `changes-<version>.md` file for each new release. The `changes-<version>.md` must contain:

    1. Header in the following format: `# <Project name> <version>, released yyyy-mm-dd`
    
    2. Description of the release changes. 
     
* The `changelog.md` must contain a link to the `changes-<version>.md` file.
        
### Rules for Java Repositories

* The Project must be a valid Maven project.

* The pom file of the project must contain a`<version></version>` tag with a valid version without variables.

### Rules for Release on GitHub

* If the GitHub repository's releases page is not empty, the new release version must follow the versioning rules.
It is not allowed to skip a version, to release the same version twice or to release the version that comes before the latest release.

* The project must contain a `/.github/workflows/upload_release_asset.yml` file in the root directory. 
You can find examples [here](upload-release-asset-example.md).

## How to Use Release Robot

### Run from Terminal

#### Requirements

* JRE 11 or higher

#### Run Steps

1. Download the latest available [release](https://github.com/exasol/release-robot/releases) of the Release Robot.

1. (Optional) Place in your home directory a file with credentials: `/<home>/.release-robot/credentials`.
    The file must contain the following two properties:

    ```properties
    github_username=<your username>
    github_oauth_access_token=<github access token>
    ```
    If the Release Robot cannot find this file during an execution, it asks the user to input the credentials directly through terminal.

2. Run the Release Robot with terminal:
    
    `java -jar release-robot-<version>.jar -name <project name> -goal <goal> -platforms <comma-separated list of platforms>`
    
    For example:

    `java -jar release-robot-0.1.0.jar -name virtual-schema-common-java -goal validate -platforms github`

##### Input Arguments

| Long Option | Short Option | Mandatory | Description                               | Possible values               |
|-------------|--------------|-----------|-------------------------------------------|-------------------------------|
| --goal      | -g           | Yes       | Goal to execute                           | `validate`, `release`         |
| --name      | -n           | Yes       | GitHub project name                       | Any valid GitHub project name |
| --platforms | -p           | Yes       | Comma-separated list of release platforms | `github`                      |