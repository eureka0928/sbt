ThisBuild / scalaVersion := "3.7.4"

lazy val root = project.in(file(".")).settings(
  bestEffortEnabled := true,

  TaskKey[Unit]("checkBestEffortFlags") := {
    val opts = (Compile / scalacOptions).value
    assert(opts.contains("-Ybest-effort"), s"Expected -Ybest-effort in scalacOptions, got: $opts")
  },

  TaskKey[Unit]("checkDisabled") := {
    val opts = (Compile / scalacOptions).value
    assert(!opts.contains("-Ybest-effort"), s"Expected no -Ybest-effort when disabled, got: $opts")
  },
)
