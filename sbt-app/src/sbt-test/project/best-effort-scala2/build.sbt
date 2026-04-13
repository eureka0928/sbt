ThisBuild / scalaVersion := "2.13.16"

lazy val root = project.in(file(".")).settings(
  bestEffortEnabled := true,

  TaskKey[Unit]("checkNoFlag") := {
    val opts = (Compile / scalacOptions).value
    assert(!opts.contains("-Ybest-effort"), s"Expected no -Ybest-effort for Scala 2, got: $opts")
  },
)
