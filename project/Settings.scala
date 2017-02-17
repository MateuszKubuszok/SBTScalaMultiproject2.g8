import com.typesafe.sbt.SbtScalariform._
import sbt.Defaults.testTasks
import sbt.TestFrameworks.Specs2
import sbt.Tests.Argument
import sbt._
import sbt.Keys._

import scalariform.formatter.preferences._
import scoverage.ScoverageKeys._
import scoverage.ScoverageSbtPlugin
import org.scalastyle.sbt.ScalastylePlugin._

object Settings extends Dependencies {

  val FunctionalTest = config("fun") extend Test describedAs "Runs only functional tests"

  private val commonSettings = Seq(
    organization := "pl.combosolutions",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scalaVersionUsed
  )

  private val rootSettings = commonSettings

  private val modulesSettings = scalariformSettings ++ commonSettings ++ Seq(
    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-unused-import",
      "-Xfatal-warnings",
      "-Xlint"
    ),

    resolvers ++= commonResolvers,

    libraryDependencies ++= mainDeps,
    libraryDependencies ++= testDeps map (_ % Test),

    coverageEnabled := false,

    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignArguments, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(IndentLocalDefs, false)
      .setPreference(PreserveSpaceBeforeArguments, true),

    scalastyleFailOnError := true
  )

  abstract class TestConfigurator(project: Project, config: Configuration) {

    protected def configure: Project = project.
      configs(config).
      settings(inConfig(config)(Defaults.testSettings): _*).
      settings(testFrameworks := Seq(Specs2)).
      settings(libraryDependencies ++= testDeps map (_ % config.name)).
      enablePlugins(ScoverageSbtPlugin)

    protected def configureSequential: Project = configure.
      settings(testOptions in config += Argument(Specs2, "sequential")).
      settings(parallelExecution in config := false)
  }

  implicit class DataConfigurator(project: Project) {

    def setName(newName: String): Project = project.settings(name := newName)

    def setDescription(newDescription: String): Project = project.settings(description := newDescription)

    def setInitialCommand(newInitialCommand: String): Project =
      project.settings(initialCommands := s"pl.combosolutions.$newInitialCommand")
  }

  implicit class RootConfigurator(project: Project) {

    def configureRoot: Project = project.settings(rootSettings: _*)
  }

  implicit class ModuleConfigurator(project: Project) {

    def configureModule: Project = project.settings(modulesSettings: _*)
  }

  implicit class UnitTestConfigurator(project: Project) extends TestConfigurator(project, Test) {

    def configureTests: Project = configure

    def configureTestsSequential: Project = configureSequential
  }

  implicit class FunctionalTestConfigurator(project: Project) extends TestConfigurator(project, FunctionalTest) {

    def configureFunctionalTests: Project = configure

    def configureFunctionalTestsSequential: Project = configureSequential
  }

  implicit class IntegrationTestConfigurator(project: Project) extends TestConfigurator(project, IntegrationTest) {

    def configureIntegrationTests: Project = configure

    def configureIntegrationTestsSequential: Project = configureSequential
  }
}
