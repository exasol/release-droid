# User Guide


## How to Use It

### Run from Terminal

Requirements:

* JVM 11 or higher

Download the latest available release and start Java from a terminal:

`java -jar target/release-robot-0.1.0.jar -name <project name> -goal <goal> -platforms <comma-separated list of platforms>`

For example:

`java -jar target/release-robot-0.1.0.jar -name virtual-schema-common-java -goal validate -platforms github`

## Supported Programming Languages

A release with the Release Robot is only possible if you use on of the programming languages listed here 
and follow the set of rules applying to the project depending on the language.

Supported project languages:

* Java (Maven projects)

## Release Platforms

### GitHub

## Release Rules

You must fulfill all the rules listed here depending on a Programming Language you use, to make a release with the Release Robot.

### Common Rules for All Projects

#### Changes Log

1. The project must contain `changelog.md` and `changes-<version>.md` files in the following directory:

project root
  |
  |__ doc
       |
       |___changes
            |
            |__ changelog.md
            |
            |__ changes-<version>.md
            
### Rules for Java Repositories

1. The Project must be a valid Maven project.
1. The pom file of the project must contain a`<version></version>` tag with a valid version without variables.