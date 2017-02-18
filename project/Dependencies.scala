import sbt._

import Dependencies._

object Dependencies {

  // scala version
  val scalaVersion = "2.11.8"

  // resolvers
  val resolvers = Seq(
    Resolver sonatypeRepo "public",
    Resolver typesafeRepo "releases",
    Resolver.bintrayRepo("cakesolutions", "maven")
  )

  val scalaConfig = "com.typesafe" % "config" % "1.3.1"
  val pureConfig  = "com.github.melrief" %% "pureconfig" % "0.5.1"
  val jodaTime    = "joda-time" % "joda-time" % "2.9.4"
  val jodaConvert = "org.joda" % "joda-convert" % "1.8.1"

  val monix = "io.monix" %% "monix" % "2.0.5"

  // functional utils
  val cats = "org.typelevel" %% "cats" % "0.7.2"

  // logging
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.8"

  // testing
  val spec2Core  = "org.specs2" %% "specs2-core"  % "3.8.5.1"
  val spec2JUnit = "org.specs2" %% "specs2-junit" % "3.8.5.1"
  val spec2Mock  = "org.specs2" %% "specs2-mock"  % "3.8.5.1"
}

trait Dependencies {

  val scalaVersionUsed = scalaVersion

  // resolvers
  val commonResolvers = resolvers

  val mainDeps = Seq(cats, scalaConfig, pureConfig, jodaTime, jodaConvert, monix, logback)

  val testDeps = Seq(spec2Core, spec2JUnit, spec2Mock)

  implicit class ProjectRoot(project: Project) {

    def root: Project = project in file(".")
  }

  implicit class ProjectFrom(project: Project) {

    private val commonDir = "modules"

    def from(dir: String): Project = project in file(s"$commonDir/$dir")
  }

  implicit class DependsOnProject(project: Project) {

    private val dependsOnCompileAndTest = "test->test;compile->compile"

    def compileAndTestDependsOn(projects: Project*): Project =
      project dependsOn (projects.map(_ % dependsOnCompileAndTest): _*)
  }
}
