organization in ThisBuild := "com.dannyrobert"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `lagom-demo` = (project in file("."))
  .aggregate(`lagom-demo-api`, `lagom-demo-impl`, `lagom-demo-stream-api`, `lagom-demo-stream-impl`)

lazy val `lagom-demo-api` = (project in file("lagom-demo-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-demo-impl` = (project in file("lagom-demo-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-demo-api`)

lazy val `lagom-demo-stream-api` = (project in file("lagom-demo-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-demo-stream-impl` = (project in file("lagom-demo-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-demo-stream-api`, `lagom-demo-api`)

