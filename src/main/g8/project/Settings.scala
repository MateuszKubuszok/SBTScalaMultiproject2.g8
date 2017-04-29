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
      "-Yno-predef",
      "-Ywarn-dead-code",
      "-Ywarn-extra-implicit",
      "-Ywarn-inaccessible",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Xfatal-warnings",
      "-Xlint",
      "-Xlint:adapted-args",
      "-Xlint:by-name-right-associative",
      "-Xlint:constant",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-override",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unsound-match"
    ),
    scalacOptions in (Compile, console) --= Seq(
      "-Ywarn-unused:imports",
      "-Xfatal-warnings"
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
      Wart.Any,
      Wart.ExplicitImplicitTypes,
      Wart.ImplicitConversion,
      Wart.ImplicitParameter,
      Wart.PublicInference,
      Wart.NonUnitStatements,
      Wart.Nothing
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
      project.settings(initialCommands := s"import $package$.$newInitialCommand")
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