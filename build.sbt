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

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra",
    scalacOptions ++= options
  )
  .aggregate(core, effects, test)
  .dependsOn(core, effects, test)

lazy val core = (project in file("modules/core"))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra-core",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      Libraries.catsCore,
      Libraries.catsEffect,
      Libraries.datastaxCore,
      Libraries.scalaLogging,
      Libraries.logbackClassic,
      Libraries.Testing.scalaTest % Test,
      Libraries.Testing.scalaCheck % Test,
      Libraries.Testing.scalaTestPlus % Test,
      Libraries.Testing.scalaCheckToolboxMagic % Test,
      Libraries.Testing.scalaCheckToolboxDatetime % Test,
      Libraries.Testing.scalaCheckToolboxCombinators % Test
    )
  ).dependsOn(test)

lazy val effects = (project in file("modules/effects"))
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings))
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
  .configs(Test)
  .settings(inConfig(Test)(Defaults.testSettings))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra-effects",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      Libraries.catsCore,
      Libraries.catsKernel,
      Libraries.catsMacros,
      Libraries.catsEffect,
      Libraries.fs2Core,
      Libraries.datastaxCore,
      Libraries.scalaLogging,
      Libraries.logbackClassic,
      Libraries.log4CatsCore,
      Libraries.log4CatsSlf4j,
      Libraries.Testing.scalaTest % Test,
      Libraries.Testing.scalaCheck % Test,
      Libraries.Testing.scalaTestPlus % Test,
      Libraries.Testing.scalaCheckToolboxMagic % Test,
      Libraries.Testing.scalaCheckToolboxDatetime % Test,
      Libraries.Testing.scalaCheckToolboxCombinators % Test,
      Libraries.Testing.testContainersCassandra % IntegrationTest
    )
  )
  .dependsOn(core, test)

lazy val test = (project in file("modules/test"))
  .settings(commonSettings: _*)
  .settings(
    name := "catssandra-test",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      Libraries.catsCore,
      Libraries.catsEffect,
      Libraries.datastaxCore,
      Libraries.Testing.scalaTest,
      Libraries.Testing.scalaCheck,
      Libraries.Testing.scalaTestPlus,
      Libraries.Testing.scalaCheckToolboxMagic,
      Libraries.Testing.scalaCheckToolboxDatetime,
      Libraries.Testing.scalaCheckToolboxCombinators,
      Libraries.Testing.testContainersCore,
      Libraries.Testing.testContainersCassandra
    )
  )