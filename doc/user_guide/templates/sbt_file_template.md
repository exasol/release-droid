# Template of `build.sbt` File of Scala Projects

Check if your `build.sbt` file contains `moduleName`, `version` and `ReproducibleBuildsPlugin` is enabled:

```
lazy val root =
  project
    .in(file("."))
    .settings(moduleName := "<project-name>")
    .settings(version := "<project-version>")
    .settings(orgSettings)
    .settings(buildSettings)
    .settings(Settings.projectSettings(scalaVersion))
    .settings(resolvers ++= Dependencies.Resolvers)
    .enablePlugins(ReproducibleBuildsPlugin, IntegrationTestPlugin, GitVersioning)
```
