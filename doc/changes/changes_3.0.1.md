# Exasol Release Droid 3.0.1, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-119078, CVE-2026-124077

## Summary

This release fixes the following 2 vulnerabilities:

### CVE-2026-119078 (CWE-1068) in dependency `commons-codec:commons-codec:jar:1.22.1:compile`
Apache Commons Codec's Blake3 class documented the output of its doFinalize methods as depending on any previously finalized bytes, so that consecutive calls would return different output. In fact, doFinalize does not change the hash state: calling it again without supplying more input returns the same bytes. The key-derivation example in the class Javadoc, shipped since 1.16.0, derives two keys with two consecutive doFinalize calls. Code that follows it gets identical keys, so the separation between the derived keys is lost. The same applies to any application that calls doFinalize more than once on one instance and expects new output each time.

This issue affects Apache Commons Codec from 1.16.0 before 1.23.0.

Version 1.23.0 corrects the documentation, but the behavior of doFinalize is unchanged. Upgrading alone does not fix affected code. Users should check their applications for repeated doFinalize calls on the same Blake3 instance and derive multiple keys by requesting the combined length in one call and splitting the output, as the updated Javadoc shows.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-119078?component-type=maven&component-name=commons-codec%2Fcommons-codec&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-119078
* https://lists.apache.org/thread.html/ol71kdgopfxwlxgqmkd0jrfcw05wdq5z
* http://www.openwall.com/lists/oss-security/2026/11/09/29

### CVE-2026-124077 (CWE-158) in dependency `commons-codec:commons-codec:jar:1.22.1:compile`
The GitIdentifiers class in Apache Commons Codec did not reject NUL bytes in tree entry names. Because Git uses a NUL byte to mark the end of each entry name in a tree object, a crafted name could make one tree serialize to the same bytes as a different tree. Two different directory trees could then receive the same Git tree identifier or SWHID.

The impact is limited: common file systems do not allow NUL bytes in file names, so trees built by walking a file system are not affected. Only applications that build trees from names supplied by other sources, such as entries of archive formats that permit NUL bytes in names (e.g. ZIP) or names passed directly to the API, are exposed.

This issue affects Apache Commons Codec: from 1.22.0 before 1.23.0.

Users are recommended to upgrade to version 1.23.0, which fixes the issue.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-124077?component-type=maven&component-name=commons-codec%2Fcommons-codec&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-124077
* https://lists.apache.org/thread.html/zns7tbnkq685prps9d85q7ct6oobvzsm
* http://www.openwall.com/lists/oss-security/2026/11/09/30

## Security

* #347: Fixed vulnerability CVE-2026-119078 in dependency `commons-codec:commons-codec:jar:1.22.1:compile`
* #348: Fixed vulnerability CVE-2026-124077 in dependency `commons-codec:commons-codec:jar:1.22.1:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `io.atlassian.fugue:fugue:7.0.2` to `7.0.3`
* Updated `org.apache.commons:commons-lang3:3.20.0` to `3.22.0`
* Updated `org.apache.maven:maven-model:3.10.0` to `3.10.1`
* Updated `org.commonmark:commonmark:0.29.0` to `0.32.0`
* Updated `org.eclipse.jgit:org.eclipse.jgit:7.7.0.202606012155-r` to `7.8.1.202611041320-r`
* Updated `org.jline:jansi:4.4.8` to `4.7.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.8.1` to `5.8.2`
