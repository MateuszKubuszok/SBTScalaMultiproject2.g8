import sbt._
import Settings._

scalaVersion in ThisBuild := scalaVersionUsed
scalafmtVersion in ThisBuild := scalaFmtVersionUsed

lazy val root = project.root
  .setName("$name$")
  .setDescription("$about$")
  .configureRoot
  .aggregate(common, first, second)

lazy val common = project.from("common")
  .setName("common")
  .setDescription("Common utilities")
  .setInitialCommand("_")
  .configureModule
  .configureTests()
  .configureFunctionalTests()
  .configureIntegrationTests()
  .settings(resourceGenerators in Compile += task[Seq[File]] {
    val file = (resourceManaged in Compile).value / "$name;format="normalize"$-version.conf"
    IO.write(file, s"version=\${version.value}")
    Seq(file)
  })

lazy val first = project.from("first")
  .setName("first")
  .setDescription("First project")
  .setInitialCommand("first._")
  .configureModule
  .configureTests()
  .compileAndTestDependsOn(common)
  .settings(mainClass in (Compile, run) := Some("$package$.first.First"))
  .settings(mainClass in assembly := Some("$package$.first.First"))

lazy val second = project.from("second")
  .setName("second")
  .setDescription("Second project")
  .setInitialCommand("second._")
  .configureModule
  .configureTests()
  .compileAndTestDependsOn(common)
  .settings(mainClass in (Compile, run) := Some("$package$.second.Second"))
  .settings(mainClass in assembly := Some("$package$.second.Second"))


addCommandAlias("fullTest", ";test;it:test;scalastyle")
