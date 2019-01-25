lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "zd-test",
    organization := "io.github.synesso",
    scalaVersion := "2.12.8",
    Defaults.itSettings,
    fork := true,
    libraryDependencies ++= List(
      "com.lihaoyi" %% "upickle" % "0.7.1",
      "org.specs2" %% "specs2-core" % "4.3.6" % "test,it",
      "org.specs2" %% "specs2-scalacheck" % "4.3.6" % "test"
    )
  )

