# Exasol Release Droid 2.0.1, released 2026-07-27

Code name: Dependency Update on top of 2.0.0

## Summary

This release fixes CWE-915 by updating the transitive dependency `com.fasterxml.jackson.core:jackson-databind` to 2.22.1.

The Jira client does not have an update yet, so we had to pin two transitive dependencies.

We also updated the GitHub API to 2.0-RC7 and adapted some code to match that. While we were at it, we fixed some Sonar findings.

This release fixes the following 10 vulnerabilities:

### CVE-2026-8484 (CWE-122) in dependency `org.fusesource.jansi:jansi:jar:2.4.3:compile`
A heap buffer overflow vulnerability exists in the Jansi JNI "ioctl()" wrapper due to a lack of size verification for the argument array before the system call. This can lead to heap corruption and application crashes (DoS).
All versions are believed to be vulnerable.Â This project is unmaintained at the time of CVE assignment.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-8484?component-type=maven&component-name=org.fusesource.jansi%2Fjansi&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-8484
* https://cert.pl/en/posts/2026/06/CVE-2026-8484

### CVE-2025-48924 (CWE-674) in dependency `org.apache.commons:commons-lang3:jar:3.17.0:compile`
Uncontrolled Recursion vulnerability in Apache Commons Lang.

This issue affects Apache Commons Lang: Starting withÂ commons-lang:commons-langÂ 2.0 to 2.6, and, from org.apache.commons:commons-lang3 3.0 beforeÂ 3.18.0.

The methods ClassUtils.getClass(...) can throwÂ StackOverflowError on very long inputs. Because an Error is usually not handled by applications and libraries, a
StackOverflowError couldÂ cause an application to stop.

Users are recommended to upgrade to version 3.18.0, which fixes the issue.

Sonatype's research suggests that this CVE's details differ from those defined at NVD. See https://guide.sonatype.com/vulnerability/CVE-2025-48924 for details
#### References
* https://guide.sonatype.com/vulnerability/CVE-2025-48924?component-type=maven&component-name=org.apache.commons%2Fcommons-lang3&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-48924
* https://github.com/advisories/GHSA-j288-q9x7-2f5v

### CVE-2025-41242 (CWE-22) in dependency `org.springframework:spring-core:jar:6.2.8:compile`
Spring Framework MVC applications can be vulnerable to a âPath Traversal Vulnerabilityâ when deployed on a non-compliant Servlet container.

An application can be vulnerable when all the following are true:

*  the application is deployed as a WAR or with an embedded Servlet container
*  the Servlet container  does not reject suspicious sequences https://jakarta.ee/specifications/servlet/6.1/jakarta-servlet-spec-6.1.html#uri-path-canonicalization
*  the application  serves static resources https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/static-resources.html#page-title Â with Spring resource handling

We have verified that applications deployed on Apache Tomcat or Eclipse Jetty are not vulnerable, as long as default security features are not disabled in the configuration. Because we cannot check exploits against all Servlet containers and configuration variants, we strongly recommend upgrading your application.

Sonatype's research suggests that this CVE's details differ from those defined at NVD. See https://guide.sonatype.com/vulnerability/CVE-2025-41242 for details
#### References
* https://guide.sonatype.com/vulnerability/CVE-2025-41242?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-41242
* https://spring.io/security/cve-2025-41242

### CVE-2025-41249 (CWE-285) in dependency `org.springframework:spring-core:jar:6.2.8:compile`
The Spring Framework annotation detection mechanism may not correctly resolve annotations on methods within type hierarchies with a parameterized super type with unbounded generics. This can be an issue if such annotations are used for authorization decisions.

Your application may be affected by this if you are using Spring Security's @EnableMethodSecurityÂ feature.

You are not affected by this if you are not using @EnableMethodSecurityÂ or if you do not use security annotations on methods in generic superclasses or generic interfaces.

This CVE is published in conjunction with  CVE-2025-41248 https://spring.io/security/cve-2025-41248 .

Sonatype's research suggests that this CVE's details differ from those defined at NVD. See https://guide.sonatype.com/vulnerability/CVE-2025-41249 for details
#### References
* https://guide.sonatype.com/vulnerability/CVE-2025-41249?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-41249
* https://spring.io/security/cve-2025-41249

### CVE-2026-22745 (CWE-400) in dependency `org.springframework:spring-core:jar:6.2.8:compile`
Spring MVC and WebFlux applications are vulnerable to Denial of Service attacks when resolving static resources.

More precisely, an application can be vulnerable when all the following are true:

*  the application is using Spring MVC or Spring WebFlux
*  the application is serving static resources from the file system
*  the application is running on a Windows platform

When all the conditions above are met, the attacker can send malicious requests that are slow to resolve and that can keep HTTP connections in use. This can cause a Denial of Service on the application.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-22745?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-22745
* https://spring.io/security/cve-2026-22745

### CVE-2026-41842 (CWE-400) in dependency `org.springframework:spring-core:jar:6.2.8:compile`
Spring MVC and WebFlux applications are vulnerable to Denial of Service (DoS) attacks when resolving static resources.

Affected versions:
Spring Framework 7.0.0 through 7.0.7; 6.2.0 through 6.2.18; 6.1.0 through 6.1.27; 5.3.0 through 5.3.48.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-41842?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-41842
* https://spring.io/security/cve-2026-41842

### CVE-2026-41848 (CWE-1333) in dependency `org.springframework:spring-core:jar:6.2.8:compile`
Applications may be vulnerable to a Regular Expression Denial of Service (ReDoS) attack if an attacker is able to provide a pattern which is then directly or indirectly supplied to one of the following methods in AntPathMatcher: match(String pattern, String path), matchStart(String pattern, String path), extractUriTemplateVariables(String pattern, String path).

Affected versions:
Spring Framework 7.0.0 through 7.0.7; 6.2.0 through 6.2.18; 6.1.0 through 6.1.27; 5.3.0 through 5.3.48.

Sonatype's research suggests that this CVE's details differ from those defined at NVD. See https://guide.sonatype.com/vulnerability/CVE-2026-41848 for details
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-41848?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-41848
* https://spring.io/security/cve-2026-41848

### CVE-2026-41853 (CWE-444) in dependency `org.springframework:spring-core:jar:6.2.8:compile`
Spring MVC and WebFlux applications are vulnerable to Multipart request smuggling attacks.

Affected versions:
Spring Framework 7.0.0 through 7.0.7; 6.2.0 through 6.2.18; 6.1.0 through 6.1.27; 5.3.0 through 5.3.48.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-41853?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-41853
* https://spring.io/security/cve-2026-41853

### CVE-2026-54515 (CWE-915) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.0:compile`
jackson-databind contains the general-purpose data-binding functionality and tree-model for Jackson Data Processor. From 2.8.0 until 2.18.9, 2.21.5, and 3.1.4, in BeanDeserializerBase.createContextual(), per-property @JsonIgnoreProperties exclusions are applied by _handleByNameInclusion(), producing a contextual deserializer whose BeanPropertyMap has the ignored properties removed. The subsequent per-property case-insensitivity block (triggered by @JsonFormat(ACCEPT_CASE_INSENSITIVE_PROPERTIES)) rebuilds from this._beanProperties (the original, unfiltered map) instead of contextual._beanProperties, then overwrites the filtered map â restoring every property _handleByNameInclusion had just removed. The ignored property becomes writable again. This vulnerability is fixed in 2.18.9, 2.21.5, and 3.1.4.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-54515?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-54515
* https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-5jmj-h7xm-6q6v

### CVE-2026-59889 (CWE-863) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.0:compile`
Jackson Databind -  Authorization bypass on JsonView Setter/Field
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-59889?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-59889
* https://github.com/FasterXML/jackson-databind/issues/6060
* https://github.com/FasterXML/jackson-databind/pull/6056

## Security

* #314: Fixed vulnerability CVE-2026-8484 in dependency `org.fusesource.jansi:jansi:jar:2.4.3:compile`
* #315: Fixed vulnerability CVE-2025-48924 in dependency `org.apache.commons:commons-lang3:jar:3.17.0:compile`
* #316: Fixed vulnerability CVE-2025-41242 in dependency `org.springframework:spring-core:jar:6.2.8:compile`
* #317: Fixed vulnerability CVE-2025-41249 in dependency `org.springframework:spring-core:jar:6.2.8:compile`
* #318: Fixed vulnerability CVE-2026-22745 in dependency `org.springframework:spring-core:jar:6.2.8:compile`
* #319: Fixed vulnerability CVE-2026-41842 in dependency `org.springframework:spring-core:jar:6.2.8:compile`
* #320: Fixed vulnerability CVE-2026-41848 in dependency `org.springframework:spring-core:jar:6.2.8:compile`
* #321: Fixed vulnerability CVE-2026-41853 in dependency `org.springframework:spring-core:jar:6.2.8:compile`
* #322: Fixed vulnerability CVE-2026-54515 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.0:compile`
* #323: Fixed vulnerability CVE-2026-59889 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.0:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:7.0.1` to `7.0.2`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.0` to `2.22.1`
* Removed `com.infradna.tool:bridge-method-annotation:1.31`
* Removed `jakarta.json:jakarta.json-api:2.1.3`
* Added `org.apache.commons:commons-lang3:3.20.0`
* Updated `org.commonmark:commonmark:0.28.0` to `0.29.0`
* Removed `org.fusesource.jansi:jansi:2.4.3`
* Added `org.jline:jansi:4.3.1`
* Updated `org.kohsuke:github-api:1.330` to `2.0-rc.7`
* Added `org.springframework:spring-beans:7.0.8`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter:6.1.0` to `6.1.2`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.4` to `1.0.1`
* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.7` to `2.1.0`
* Updated `com.exasol:project-keeper-maven-plugin:5.6.2` to `5.7.4`
* Removed `com.exasol:quality-summarizer-maven-plugin:0.2.1`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.10.0` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.6.2` to `3.6.3`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.5` to `3.5.6`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.5` to `3.5.6`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.14` to `0.8.15`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356` to `5.7.0.6970`
* Added `org.spdx:spdx-maven-plugin:1.0.4`
