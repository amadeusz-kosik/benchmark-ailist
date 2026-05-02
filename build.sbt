ThisBuild / scalaVersion := "2.13.18"

val defaultJavacOptions  = Seq("-source", "17", "-target", "17")
val defaultScalacOptions = Seq("-deprecation", "-unchecked", "-Xlint", "-Xdisable-assertions")


lazy val algorithms = (project in file("algorithms"))
  .settings(
    name := "benchmark-ailist-algorithms",
    javacOptions  := defaultJavacOptions,
    scalacOptions := defaultScalacOptions
  )

lazy val benchmarks = (project in file("benchmarks"))
  .settings(
    name := "benchmark-ailist-benchmarks",
    javacOptions  := defaultJavacOptions,
    scalacOptions := defaultScalacOptions
  )
  .dependsOn(algorithms)
  .enablePlugins(JmhPlugin)


lazy val root = (project in file("."))
  .settings(
    name := "benchmark-ailist"
  )
  .aggregate(algorithms, benchmarks)
