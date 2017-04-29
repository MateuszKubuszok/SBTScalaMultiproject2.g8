import sbt._
import Settings._

scalaVersion in ThisBuild := scalaVersionUsed

lazy val root = project.root
  .setName("SBT template")
  .setDescription("Backup DSL")
  .setInitialCommand("_")
  .configureRoot
  .aggregate(common, first, second)

lazy val common = project.from("common")
  .setName("common")
  .setDescription("Common utilities")
  .setInitialCommand("_")
  .configureModule
  .configureTests
  .configureFunctionalTests
  .configureIntegrationTests
  .settings(resourceGenerators in Compile += task[Seq[File]] {
    val file = (resourceManaged in Compile).value / "project-version.conf"
    IO.write(file, s"version=${version.value}")
    Seq(file)
  })

lazy val first = project.from("first")
  .setName("first")
  .setDescription("First project")
  .setInitialCommand("first._")
  .configureModule
  .configureTests
  .compileAndTestDependsOn(common)
  .settings(mainClass in (Compile, run) := Some("pl.combosolutions.first.First"))

lazy val second = project.from("second")
  .setName("second")
  .setDescription("Second project")
  .setInitialCommand("second._")
  .configureModule
  .configureTests
  .compileAndTestDependsOn(common)
  .settings(mainClass in (Compile, run) := Some("pl.combosolutions.first.Second"))
