import Dependencies._

lazy val commonSettings = Seq(
  version := "0.0.1",
  organizationName := "Cassandra client for Cats Effect",
  organization := "com.leysoft",
  scalaVersion := "2.13.0",
  scalafmtOnCompile in ThisBuild := true,
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)

lazy val options = Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds"
)

lazy val dependencies = Seq(
  Libraries.catsCore,
  Libraries.catsKernel,
  Libraries.catsMacros,
  Libraries.catsEffect,
  Libraries.datastaxCore,
  Libraries.scalaLogging,
  Libraries.logbackClassic,
  Libraries.log4CatsCore,
  Libraries.log4CatsSlf4j,
  Libraries.Testing.scalaTest,
  Libraries.Testing.scalaCheck,
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra",
    scalacOptions ++= options,
    libraryDependencies ++= dependencies
  )
  .aggregate(core, effects)
  .dependsOn(core, effects)

lazy val core = (project in file("modules/core"))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra-core",
    scalacOptions ++= options,
    libraryDependencies ++= dependencies
  )

lazy val effects = (project in file("modules/effects"))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra-effects",
    scalacOptions ++= options,
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)