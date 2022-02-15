scalaVersion := "2.13.8"

name := "kuzminki-zio-2"

version := "0.8.0-test"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

lazy val root = (project in file("."))
  .settings(
    name := "kuzminki-zio",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.13.8",
      "org.postgresql" % "postgresql" % "42.2.24",
      "dev.zio" %% "zio" % "2.0.0-RC2"
    )
  )

