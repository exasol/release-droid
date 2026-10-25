# Exasol Release Droid 2.0.2, released 2026-??-??

Code name: Fixed vulnerability CVE-2026-110879 in com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile

## Summary

This release fixes the following vulnerability:

### CVE-2026-110879 (CWE-502) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
jackson-databind - Deserialization of Untrusted Data
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110879?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110879
* https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18.12
* https://github.com/FasterXML/jackson-databind/pull/6287

## Security

* #342: Fixed vulnerability CVE-2026-110879 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.atlassian.jira:jira-rest-java-client-core:7.0.2` to `8.0.1`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.1` to `2.22.4`
* Updated `io.atlassian.fugue:fugue:6.1.4` to `7.0.2`
* Updated `org.apache.commons:commons-lang3:3.20.0` to `3.21.0`
* Updated `org.apache.maven:maven-model:3.9.16` to `3.10.0`
* Updated `org.commonmark:commonmark:0.29.0` to `0.31.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:7.7.0.202606012155-r` to `7.8.0.202609011348-r`
* Updated `org.eclipse:yasson:3.0.4` to `3.0.5`
* Updated `org.jline:jansi:4.3.1` to `4.4.8`
* Updated `org.slf4j:slf4j-simple:1.7.36` to `2.0.20`
* Updated `org.springframework:spring-beans:7.0.8` to `7.0.10`
* Updated `org.yaml:snakeyaml:2.6` to `2.7`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:4.5` to `4.5.2`
* Updated `org.junit.jupiter:junit-jupiter:6.1.2` to `6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.1`
