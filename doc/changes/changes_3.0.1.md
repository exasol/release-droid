# Exasol Release Droid 3.0.1, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-59374, CVE-2026-59418

## Summary

This release fixes the following 2 vulnerabilities:

### CVE-2026-59374 (CWE-180) in dependency `org.springframework:spring-core:jar:7.0.10:compile`
Spring Framework - Data binding bypass on indexed types
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-59374?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-59374
* https://spring.io/security/cve-2026-59374

### CVE-2026-59418 (CWE-401) in dependency `org.springframework:spring-core:jar:7.0.10:compile`
Spring Framework - Denial of service via streaming text decoding in WebFlux
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-59418?component-type=maven&component-name=org.springframework%2Fspring-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-59418
* https://spring.io/security/cve-2026-59418

## Security

* #350: Fixed vulnerability CVE-2026-59374 in dependency `org.springframework:spring-core:jar:7.0.10:compile`
* #351: Fixed vulnerability CVE-2026-59418 in dependency `org.springframework:spring-core:jar:7.0.10:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `io.atlassian.fugue:fugue:7.0.2` to `7.0.3`
* Updated `org.apache.commons:commons-lang3:3.20.0` to `3.22.0`
* Updated `org.apache.maven:maven-model:3.10.0` to `3.10.2`
* Updated `org.commonmark:commonmark:0.29.0` to `0.33.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:7.7.0.202606012155-r` to `7.8.1.202611041320-r`
* Updated `org.eclipse.yasson:yasson:3.0.5` to `3.0.6`
* Updated `org.jline:jansi:4.4.8` to `4.7.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.8.1` to `5.8.2`
