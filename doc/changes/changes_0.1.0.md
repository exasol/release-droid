# Exasol Release Droid 0.1.0, released 2020-09-21

Code name: GitHub validation and release support

## Features

* #3: Added initial implementation on GitHub Release.
* #10: Added support for validation on a user-specified git branch.
* #11: Added validation for GitHub issues.
* #15: Improved GitHub release letter parsing.
* #16: Improved validation and release process for GitHub.
* #21: Improved error handling according to our new standards.
* #26: Added automatic detection for asset's name of jar file.

## Documentation

* #9: Added requirements and design.
* #17: Documented GitHub authentication details.

## Refactoring

* #27: Removed use of deprecated method getContent.
* #33: Renamed 'Upload_release_assets.yml' to 'github_release.yml'

## Dependency updates

<details>
  <summary>Click to expand</summary>
  
* Added `org.kohsuke:github-api:1.116`
* Added `commons-cli:commons-cli:1.4`
* Added `org.json:json:20200518`
* Added `org.mockito:mockito-core:3.5.10`
* Added `org.junit.jupiter:junit-jupiter:5.7.0`
* Added `org.hamcrest:hamcrest:2.2`
* Added `org.apache.maven.plugins:maven-compiler-plugin:3.8.1`
* Added `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M4`
* Added `org.apache.maven.plugins:maven-assembly-plugin:3.3.0`
* Added `org.codehaus.mojo:versions-maven-plugin:2.7`
* Added `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0`
* Added `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3`
* Added `org.jacoco:jacoco-maven-plugin:0.8.5`
* Added `org.itsallcode:openfasttrace-maven-plugin:1.0.0`

</details>
