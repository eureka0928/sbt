/*
 * sbt
 * Copyright 2023, Scala center
 * Copyright 2011 - 2022, Lightbend, Inc.
 * Copyright 2008 - 2010, Mark Harrah
 * Licensed under Apache License 2.0 (see LICENSE)
 */

package sbt

object ScriptedPluginSpec extends verify.BasicTestSuite:
  test("stableLauncherVersion should strip SNAPSHOT from version with RC tag") {
    assert(
      ScriptedPlugin.stableLauncherVersion("2.0.0-RC9-bin-SNAPSHOT") == "2.0.0-RC9",
      "2.0.0-RC9-bin-SNAPSHOT should become 2.0.0-RC9"
    )
  }

  test("stableLauncherVersion should strip SNAPSHOT from plain version") {
    assert(
      ScriptedPlugin.stableLauncherVersion("2.0.0-SNAPSHOT") == "2.0.0",
      "2.0.0-SNAPSHOT should become 2.0.0"
    )
  }

  test("stableLauncherVersion should strip nightly timestamp") {
    assert(
      ScriptedPlugin.stableLauncherVersion("1.5.0-bin-20210302T081602") == "1.5.0",
      "1.5.0-bin-20210302T081602 should become 1.5.0"
    )
  }

  test("stableLauncherVersion should keep stable RC version unchanged") {
    assert(
      ScriptedPlugin.stableLauncherVersion("2.0.0-RC9") == "2.0.0-RC9",
      "2.0.0-RC9 should remain 2.0.0-RC9"
    )
  }

  test("stableLauncherVersion should keep stable release version unchanged") {
    assert(
      ScriptedPlugin.stableLauncherVersion("1.9.0") == "1.9.0",
      "1.9.0 should remain 1.9.0"
    )
  }
end ScriptedPluginSpec
