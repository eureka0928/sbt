lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "scripted-snapshot-launcher",
    scriptedSbt := "2.0.0-RC9-bin-SNAPSHOT",
    TaskKey[Unit]("check") := {
      val lv = scriptedLauncherVersion.value
      assert(lv == "2.0.0-RC9", s"Expected scriptedLauncherVersion 2.0.0-RC9, got $lv")
      val sv = scriptedSbt.value
      assert(sv == "2.0.0-RC9-bin-SNAPSHOT", s"Expected scriptedSbt 2.0.0-RC9-bin-SNAPSHOT, got $sv")
    },
  )
