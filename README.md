# Exasol Release Droid

[![Build Status](https://travis-ci.com/exasol/release-droid.svg?branch=master)](https://travis-ci.org/exasol/release-droid)

SonarCloud results:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Arelease-droid&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Arelease-droid)

Release Droid automates release process steps on widely used platforms like GitHub and Jira for Exasol projects.

## Information for Users

* [User Guide](doc/user_guide/user_guide.md)
* [Changelog](doc/changes/changelog.md)

## Dependencies

### Run Time Dependencies

Running the Release Droid requires a Java Runtime version 11 or later.

| Dependency                                                         | Purpose                                             | License                    |
|--------------------------------------------------------------------|-----------------------------------------------------|----------------------------|
| [GitHub API for Java][github-api-for-java]                         | An object oriented representation of the GitHub API | MIT                        |
| [JSON In Java][json-in-java]                                       | JSON Processing                                     | JSON in Java License       |


### Test Dependencies

| Dependency                                                         | Purpose                                            | License                    |
|--------------------------------------------------------------------|----------------------------------------------------|----------------------------|
| [Apache Maven][apache-maven]                                       | Build tool                                         | Apache License 2.0         |
| [Java Hamcrest][java-hamcrest]                                     | Checking for conditions in code via matchers       | BSD License                |
| [JUnit][junit]                                                     | Unit testing framework                             | Eclipse Public License 1.0 |
| [Mockito][mockito]                                                 | Mocking framework                                  | MIT License                |

### Maven Plug-ins

| Plug-in                                                            | Purpose                                            | License                     |
|--------------------------------------------------------------------|----------------------------------------------------|-----------------------------|
| [Maven Assembly Plugin][maven-assembly-plugin]                     | Creating JAR                                       | Apache License 2.0          |
| [Maven Compiler Plugin][maven-compiler-plugin]                     | Setting required Java version                      | Apache License 2.0          |
| [Maven Enforcer Plugin][maven-enforcer-plugin]                     | Controlling environment constants                  | Apache License 2.0          |
| [Maven Jacoco Plugin][maven-jacoco-plugin]                         | Code coverage metering                             | Eclipse Public License 2.0  |
| [Maven Surefire Plugin][maven-surefire-plugin]                     | Unit testing                                       | Apache License 2.0          |
| [OpenFastTrace Maven Plugin][open-fast-trace-maven-plugin]         | Requirement Tracing                                | GPL v3                      |
| [Sonatype OSS Index Maven Plugin][sonatype-oss-index-maven-plugin] | Checking Dependencies Vulnerability                | ASL2                        |
| [Versions Maven Plugin][versions-maven-plugin]                     | Checking if dependencies updates are available     | Apache License 2.0          |

[apache-maven]: https://maven.apache.org/
[github-api-for-java]: https://github-api.kohsuke.org/
[java-hamcrest]: http://hamcrest.org/JavaHamcrest/
[json-in-java]: https://github.com/stleary/JSON-java
[junit]: https://junit.org/junit5
[mockito]: http://site.mockito.org/

[maven-assembly-plugin]: https://maven.apache.org/plugins/maven-assembly-plugin/
[maven-compiler-plugin]: https://maven.apache.org/plugins/maven-compiler-plugin/
[maven-enforcer-plugin]: http://maven.apache.org/enforcer/maven-enforcer-plugin/
[maven-jacoco-plugin]: https://www.eclemma.org/jacoco/trunk/doc/maven.html
[maven-surefire-plugin]: https://maven.apache.org/surefire/maven-surefire-plugin/
[open-fast-trace-maven-plugin]: https://github.com/itsallcode/openfasttrace-maven-plugin
[sonatype-oss-index-maven-plugin]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[versions-maven-plugin]: https://www.mojohaus.org/versions-maven-plugin/
