# Exasol Release Droid 2.0.2, released 2026-10-27

Code name: Dependency Security Updates

## Summary

This release addresses 12 OSS Index vulnerability findings in Spring Framework, Jackson, Guava, and Objenesis dependencies.

## Security

* #329: Fixed CVE-2026-59282 (unbounded allocation from crafted property paths) in dependency `org.springframework:spring-beans:jar:7.0.8:compile`
* #330: Fixed CVE-2026-68498 (unbounded resource allocation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:compile`
* #335: Fixed CVE-2026-89407 (regex backtracking denial of service) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:compile`
* #336: Fixed CVE-2026-89425 (unbounded error token accumulation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:compile`
* Fixed CVE-2026-116486 (type confusion from class name cache collisions) in dependency `org.objenesis:objenesis:jar:3.5:test`
* #340: Fixed CVE-2026-102554 (memory exhaustion during deserialization) in dependency `com.google.guava:guava:jar:33.2.1-jre:compile`
* #342: Fixed CVE-2026-110879 (polymorphic type validation bypass) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #327: Fixed CVE-2026-19032 (unrestricted URI schemes in Path deserialization) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #328: Fixed CVE-2026-68497 (CPU exhaustion from unbounded numeric parsing) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #331: Fixed CVE-2026-91776 (unbounded type ID cache growth) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #332: Fixed CVE-2026-91777 (algorithmic complexity denial of service) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #333: Fixed CVE-2026-83557 (unsafe polymorphic object instantiation) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.6.2` to `3.1.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
