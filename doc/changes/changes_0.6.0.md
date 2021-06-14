# Exasol Release Droid 0.6.0, released 2021-??-??

Code name: Save release state

## Summary

Now you can specify the release platforms on the project level once instead of providing them each time via CLI. To
specify the release platforms, add `release-platforms` list to the `release_config.yml` file.

Example:

```yaml
release-platforms:
  - GitHub
  - Maven
  - Community
```

If a release on some platform fails, next time you re-start the RD, it will continue from the failed place.

You still can specify the platforms via CLI. If you do so, the `release_config.yml` file will be ignored, and the CLI
arguments will be used.

Be aware that the release state is saved on the machine the RD running on. It means if you re-run the release from a
different machine, the release will be started from a scratch.

## Features

* # 159: Saved the release state.
* # 161: Made VALIDATE a default goal.

## Refactoring

* # 162: Refactored reports.

## Dependency Updates

### Compile Dependency Updates

* Added `com.atlassian.fugue:fugue:2.6.1`
* Added `com.atlassian.jira:jira-rest-java-client-core:4.0.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:0.7.0` to `0.7.2`