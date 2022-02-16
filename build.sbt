scalaVersion := "2.12.15"

name := "kuzminki-zio-2"

version := "0.9.2"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

lazy val root = (project in file("."))
  .settings(
    name := "kuzminki-zio",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.12.15",
      "org.postgresql" % "postgresql" % "42.2.24",
      "dev.zio" %% "zio" % "2.0.0-RC2"
    )
  )

