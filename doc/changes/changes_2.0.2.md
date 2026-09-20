# Exasol Release Droid 2.0.2, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-68498, CVE-2026-91776, CVE-2026-91777, CVE-2026-83557

## Summary

This release fixes the following 4 vulnerabilities:

### CVE-2026-68498 (CWE-770) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:compile`
jackson-core - Allocation of Resources Without Limits or Throttling

Sonatype's research suggests that this CVE's details differ from those defined at NVD. See https://guide.sonatype.com/vulnerability/CVE-2026-68498 for details
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-68498?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-core&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-68498
* https://github.com/FasterXML/jackson-core/security/advisories/GHSA-649p-m576-vr99

### CVE-2026-91776 (CWE-770) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
Jackson-databind - Unbounded growth of type id cache in TypeDeserializer
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-91776?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-91776
* https://github.com/FasterXML/jackson-databind/pull/6203

### CVE-2026-91777 (CWE-407) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
Jackson-databind - Denial of Service (DoS)
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-91777?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-91777
* https://github.com/FasterXML/jackson-databind/pull/6204

### CVE-2026-83557 (CWE-502) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
jackson-databind - Deserialization of untrusted data [CVE-2026-83557]
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-83557?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-83557
* https://github.com/FasterXML/jackson-databind/issues/6156
* https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-gx83-3vf8-gh7j

## Security

* #330: Fixed vulnerability CVE-2026-68498 in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:compile`
* #331: Fixed vulnerability CVE-2026-91776 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #332: Fixed vulnerability CVE-2026-91777 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #333: Fixed vulnerability CVE-2026-83557 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:7.0.2` to `8.0.0-894d8cbg`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.1` to `2.22.2`
* Updated `io.atlassian.fugue:fugue:6.1.4` to `7.0.1`
* Updated `org.commonmark:commonmark:0.29.0` to `0.30.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:7.7.0.202606012155-r` to `7.8.0.202609011348-r`
* Updated `org.eclipse:yasson:3.0.4` to `3.0.5`
* Updated `org.jline:jansi:4.3.1` to `4.4.5`
* Updated `org.slf4j:slf4j-simple:1.7.36` to `2.0.19`
* Updated `org.springframework:spring-beans:7.0.8` to `7.0.9`
* Updated `org.yaml:snakeyaml:2.6` to `2.7`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:4.5` to `4.5.2`
* Updated `org.junit.jupiter:junit-jupiter:6.1.2` to `6.1.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.7.6`
