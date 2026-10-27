# Exasol Release Droid 3.0.0, released 2026-10-27

Code name: Dependency Security Updates

## Summary

This release addresses 14 OSS Index vulnerability findings in Spring Framework, Jackson, Guava, Objenesis, and Apache HttpClient dependencies. BOM imports now manage the Spring Framework, Jackson, and SLF4J modules.

**Breaking Change:** Release Droid now requires Java 25 to build and run.

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
* Fixed CVE-2026-64607 (connection leak on unsupported content encoding) in dependency `org.apache.httpcomponents.client5:httpclient5:jar:5.6.2:compile`
* Fixed CVE-2026-71290 (improper TLS hostname verification) in dependency `org.apache.httpcomponents.client5:httpclient5:jar:5.6.2:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:7.0.2` to `8.0.1`
* Removed `com.fasterxml.jackson.core:jackson-databind:2.22.1`
* Updated `io.atlassian.fugue:fugue:6.1.4` to `7.0.2`
* Updated `org.apache.maven:maven-model:3.9.16` to `3.10.0`
* Removed `org.eclipse.parsson:parsson:1.1.9`
* Added `org.eclipse.yasson:yasson:3.0.5`
* Removed `org.eclipse:yasson:3.0.4`
* Updated `org.jline:jansi:4.3.1` to `4.4.8`
* Updated `org.slf4j:slf4j-simple:1.7.36` to `2.0.20`
* Removed `org.springframework:spring-beans:7.0.8`
* Updated `org.yaml:snakeyaml:2.6` to `2.7`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:4.5` to `4.5.2`
* Updated `org.junit.jupiter:junit-jupiter:6.1.2` to `6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.2.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.0` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.0` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.3.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.1` to `3.22.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.6.2` to `3.1.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
