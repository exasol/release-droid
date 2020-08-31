# Exasol Release Robot

Release Robot automates release process steps on widely used platforms like GitHub and Jira for Exasol projects.

## Information for Users

* [User Guide](doc/user_guide/user_guide.md)
* [Changelog](doc/changes/changelog.md)

## Dependencies

### Run Time Dependencies

Running the Release Robot requires a Java Runtime version 11 or later.

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
| [Maven Jacoco Plugin][maven-jacoco-plugin                    ]     | Code coverage metering                             | Eclipse Public License 2.0  |
| [Maven Surefire Plugin][maven-surefire-plugin]                     | Unit testing                                       | Apache License 2.0          |
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
[sonatype-oss-index-maven-plugin]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[versions-maven-plugin]: https://www.mojohaus.org/versions-maven-plugin/
