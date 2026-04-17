# Exasol Release Droid 2.0.0, released 2026-??-??

Code name:

## Summary

* This release updates dependencies and you need Java 21 to run release droid.

## Features

* ISSUE_NUMBER: description

## Security

* #295: Fixed dependency check vulnerability findings
* #299: Fixed dependency check vulnerability findings
* #302: Fixed vulnerability CVE-2023-4759 in `org.eclipse.jgit:org.eclipse.jgit`
* #304: Fixed vulnerability CVE-2023-4043 by updating transitive dependency `org.eclipse.parsson:parsson`
* #307: Fixed vulnerability CVE-2024-47554 in `commons-io:commons-io`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:5.2.4` to `7.0.1`
* Updated `com.exasol:error-reporting-java:1.0.1` to `1.0.2`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.2` to `2.21.2`
* Added `com.infradna.tool:bridge-method-annotation:1.31`
* Updated `commons-cli:commons-cli:1.5.0` to `1.11.0`
* Updated `io.atlassian.fugue:fugue:5.0.0` to `6.1.2`
* Updated `jakarta.json:jakarta.json-api:2.1.1` to `2.1.3`
* Updated `org.apache.maven:maven-model:3.9.1` to `3.9.15`
* Removed `org.codehaus.jettison:jettison:1.5.4`
* Updated `org.commonmark:commonmark:0.21.0` to `0.28.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r` to `7.6.0.202603022253-r`
* Added `org.eclipse.parsson:parsson:1.1.7`
* Updated `org.eclipse:yasson:3.0.2` to `3.0.4`
* Updated `org.fusesource.jansi:jansi:2.4.0` to `2.4.3`
* Updated `org.kohsuke:github-api:1.314` to `1.330`
* Updated `org.yaml:snakeyaml:2.0` to `2.6`

### Runtime Dependency Updates

* Removed `org.springframework:spring-beans:5.3.25`

### Test Dependency Updates

* Updated `com.exasol:maven-project-version-getter:1.2.0` to `1.2.2`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.14.1` to `4.5`
* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Updated `org.junit.jupiter:junit-jupiter:5.9.2` to `6.0.3`
* Updated `org.mockito:mockito-core:5.2.0` to `5.23.0`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.23.0`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.2` to `0.4.4`
* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `2.0.6`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.6` to `5.4.6`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.1`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.2`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.1`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.3.0` to `3.8.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.15.0`
* Added `org.apache.maven.plugins:maven-dependency-plugin:3.10.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:2.8.2` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.2.1` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M6` to `3.5.4`
* Updated `org.apache.maven.plugins:maven-install-plugin:2.5.2` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.3.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.2.0` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.0` to `3.12.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M6` to `3.5.4`
* Added `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:2.0.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.7.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.14.2` to `2.21.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.6.1` to `1.6.2`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.14`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `5.5.0.6356`
