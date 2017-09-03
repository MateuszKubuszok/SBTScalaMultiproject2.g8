addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("com.github.tkawachi" % "sbt-lock" % "0.3.0")

addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.10")

addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.10")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC3")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.1.1")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.9.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"
