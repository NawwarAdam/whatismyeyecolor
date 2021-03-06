inThisBuild(
  Seq(
    organization := "com.whatismyeyecolor",
    scalaVersion := "2.12.4",
    scalacOptions ++= Seq("-deprecation", "-feature", "-language:_"),
    javaOptions ++= Seq("-Djava.library.path=" + sys.env("OPENCV_JAVA_PATH")),
    fork := true
  )
)

lazy val root = (project in file("."))
  .settings(
    onLoad in Global := {
      sys.env.get("OPENCV_JAVA_PATH") match {
        case Some(_) => (onLoad in Global).value
        case None => sys.error("Please set environment variable OPENCV_JAVA_PATH before continuing")
      }
    }
  )
  .aggregate(library, cli, gui, training)

val library = (project in file("library"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )

val cli = (project in file("cli"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.rogach" %% "scallop" % "3.1.1",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    ),
    unmanagedBase := (unmanagedBase in library).value,
    mainClass in (Compile, run) := Some("com.whatismyeyecolor.cli.Client")
  )
  .dependsOn(library)

val gui = (project in file("gui"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.rogach" %% "scallop" % "3.1.1",
      "com.twitter" %% "finagle-http" % "17.12.0",
      "com.lihaoyi" %% "scalatags" % "0.6.7",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    ),
    unmanagedBase := (unmanagedBase in library).value,
    mainClass in (Compile, run) := Some("com.whatismyeyecolor.gui.Server")
  )
  .dependsOn(library)

val training = (project in file("training"))
  .settings(
    libraryDependencies ++= Seq(
      "org.rogach" %% "scallop" % "3.1.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    unmanagedBase := (unmanagedBase in library).value,
    mainClass in (Compile, run) := Some("com.whatismyeyecolor.training.Client")
  )
  .dependsOn(library)
