import com.typesafe.sbt.SbtGit.git
import com.typesafe.sbt.SbtScalariform._
import sbt.TestFrameworks.Specs2
import sbt.Tests.Argument
import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys.{format => scalariformFormat}

import scalariform.formatter.preferences._
import scoverage.ScoverageKeys._
import scoverage.ScoverageSbtPlugin
import org.scalastyle.sbt.ScalastylePlugin._
import wartremover._

object Settings extends Dependencies {

  val FunctionalTest: Configuration = config("fun") extend Test describedAs "Runs only functional tests"

  private val commonSettings = Seq(
    organization    := "$organization$",
    git.baseVersion := "0.1.0",

    scalaVersion := scalaVersionUsed
  )

  private val rootSettings = commonSettings

  private val modulesSettings = scalariformSettings ++ commonSettings ++ Seq(
    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-explaintypes",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-numeric-widen",
      "-Ywarn-nullary-override",
      "-Ywarn-unused-import",
      "-Xfatal-warnings",
      "-Xlint",
      "-Xlint:unsound-match"
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

    scalastyleFailOnError := true,

    wartremoverWarnings in (Compile, compile) ++= Warts.allBut(
      Wart.Any,                   // - see puffnfresh/wartremover#263
      Wart.ExplicitImplicitTypes, // - see puffnfresh/wartremover#226
      Wart.ImplicitConversion,    // - see mpilquist/simulacrum#35
      Wart.ImplicitParameter,
      Wart.PublicInference,
      Wart.NonUnitStatements,
      Wart.Nothing                // - see puffnfresh/wartremover#263
    )
  )

  abstract class TestConfigurator(project: Project, config: Configuration) {

    protected def configure: Project = project
      .configs(config)
      .settings(inConfig(config)(Defaults.testSettings): _*)
      .settings(inConfig(config)(configScalariformSettings))
      .settings(compileInputs in (config, compile) :=
        ((compileInputs in (config, compile)) dependsOn (scalariformFormat in config)).value
      )
      .settings(fork in config := true)
      .settings(testFrameworks := Seq(Specs2))
      .settings(libraryDependencies ++= testDeps map (_ % config.name))
      .enablePlugins(ScoverageSbtPlugin)

    protected def configureSequential: Project = configure
      .settings(testOptions in config += Argument(Specs2, "sequential"))
      .settings(parallelExecution in config := false)
  }

  implicit class DataConfigurator(project: Project) {

    def setName(newName: String): Project = project.settings(name := newName)

    def setDescription(newDescription: String): Project = project.settings(description := newDescription)

    def setInitialCommand(newInitialCommand: String): Project =
      project.settings(initialCommands := s"$package$.\$newInitialCommand")
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
