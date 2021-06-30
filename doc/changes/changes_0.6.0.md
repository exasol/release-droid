# Exasol Release Droid 0.6.0, released 2021-??-??

Code name: Save release state, Jira support

## Summary

Now you can specify the release platforms on the project level once instead of providing them each time via CLI. To
specify the release platforms, add `release-platforms` list to the `release_config.yml` file.

Example:

```yaml
release-platforms:
  - GitHub
  - Maven
  - Community
  - Jira  
```

If a release on some platform fails, next time you re-start the RD, it will continue from the failed place.

You still can specify the platforms via CLI. If you do so, the `release_config.yml` file will be ignored, and the CLI
arguments will be used.

Be aware that the release state is saved on the machine the RD running on. It means if you re-run the release from a
different machine, the release will be started from a scratch.

## Features

* # 158: Added Jira support.
* # 159: Saved the release state.
* # 161: Made VALIDATE a default goal.

## Refactoring

* # 162: Refactored reports.

## Dependency Updates

### Compile Dependency Updates

* Added `com.atlassian.jira:jira-rest-java-client-core:5.2.2`
* Removed `com.fasterxml.jackson.core:jackson-databind:2.12.3`
* Added `io.atlassian.fugue:fugue:4.7.2`
* Updated `org.commonmark:commonmark:0.17.1` to `0.17.2`
* Updated `org.kohsuke:github-api:1.128` to `1.130`
* Updated `org.yaml:snakeyaml:1.28` to `1.29`

### Test Dependency Updates

* Updated `org.mockito:mockito-core:3.9.0` to `3.11.1`
* Updated `org.mockito:mockito-junit-jupiter:3.9.0` to `3.11.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.3.0` to `0.4.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.7.0` to `0.7.3`
