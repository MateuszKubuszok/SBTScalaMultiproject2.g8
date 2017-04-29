import sbt._
import Settings._

scalaVersion in ThisBuild := scalaVersionUsed

lazy val root = project.root
  .setName("$name$")
  .setDescription("$about$")
  .configureRoot
  .aggregate(common, first, second)

lazy val common = project.from("common")
  .setName("common")
  .setDescription("Common utilities")
  .configureModule
  .configureTests
  .configureFunctionalTests
  .configureIntegrationTests
  .settings(resourceGenerators in Compile += task[Seq[File]] {
    val file = (resourceManaged in Compile).value / "$name;format="normalize"$-version.conf"
    IO.write(file, s"version=\${version.value}")
    Seq(file)
  })

lazy val first = project.from("first")
  .setName("first")
  .setDescription("First project")
  .configureModule
  .configureTests
  .compileAndTestDependsOn(common)
  .settings(mainClass in (Compile, run) := Some("$package$.first.First"))

lazy val second = project.from("second")
  .setName("second")
  .setDescription("Second project")
  .configureModule
  .configureTests
  .compileAndTestDependsOn(common)
  .settings(mainClass in (Compile, run) := Some("$package$.first.Second"))
