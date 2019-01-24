lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "zendesk",
    organization := "io.github.synesso",
    scalaVersion := "2.12.8",
    libraryDependencies ++= List(
      "org.specs2" %% "specs2-core" % "4.3.6" % "test,it",
      "org.specs2" %% "specs2-scalacheck" % "4.3.6" % "test"
    )
  )

