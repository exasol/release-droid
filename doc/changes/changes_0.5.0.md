# Exasol Release Droid 0.5.0, released 2021-05-12

Code name: Community Portal Support

## Features

* #111: Added support for Exasol Community Portal release article. 
* #141: Added validation for Exasol Community Portal release.
* #151: Added log output for created community post URL.
* #154: Added support for specifying platforms via config file.

## Bug fixes

* #146: Fixed NullPointerException caused by validation a branch on an empty repository.
* #150: Fixed community portal validation: we don't check the repository structure with this platform.
* #155: Refactored the credentials reading: we don't need the credentials to the portal during validations.

## Refactoring

* #131: Refactored pom.xml plugins validations.
* #138: Organized packages structure.
* #142: Updated GitHub API library and used recently added features from it.
* #144: Changed the project tag from RR to RD.
* #126: Updated changes file template.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.core:jackson-databind:2.12.2` to `2.12.3`
* Updated `org.apache.maven:maven-model:3.6.3` to `3.8.1`
* Added `org.commonmark:commonmark:0.17.1`
* Updated `org.kohsuke:github-api:1.123` to `1.128`
* Added `org.yaml:snakeyaml:1.28`

### Test Dependency Updates

* Updated `org.mockito:mockito-core:3.8.0` to `3.9.0`
* Updated `org.mockito:mockito-junit-jupiter:3.8.0` to `3.9.0`

### Plugin Dependency Updates

* Added `com.exasol:error-code-crawler-maven-plugin:0.3.0`
* Added `com.exasol:project-keeper-maven-plugin:0.7.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:2.5` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:2.7` to `2.8.2`
* Updated `org.apache.maven.plugins:maven-install-plugin:2.4` to `2.5.2`
* Updated `org.apache.maven.plugins:maven-jar-plugin:2.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-resources-plugin:2.6` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.3` to `3.9.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.6`
