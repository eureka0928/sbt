/*
 * sbt
 * Copyright 2023, Scala center
 * Copyright 2011 - 2022, Lightbend, Inc.
 * Copyright 2008 - 2010, Mark Harrah
 * Licensed under Apache License 2.0 (see LICENSE)
 */

package sbt
package plugins

import Keys.*
import sbt.internal.SysProp
import sbt.librarymanagement.{ ScalaArtifacts, SemanticSelector, VersionNumber }
import sbt.librarymanagement.Configurations.{ Compile, Test }
import ProjectExtra.inConfig

/**
 * Enables Scala 3 best-effort compilation, which produces .betasty files
 * even when compilation fails. This is used by IDEs (e.g., Metals) to
 * provide code navigation and completion in errored programs.
 *
 * The feature requires Scala 3.5.0 or later and is gated on:
 *   - `bestEffortEnabled` setting (default `false`)
 *   - system property `sbt.bestEffort=true` or env `SBT_BESTEFFORT=true`
 *
 * @see https://dotty.epfl.ch/docs/internals/best-effort-compilation.html
 * @see https://github.com/sbt/sbt/issues/7900
 */
object BestEffortPlugin extends AutoPlugin:
  override def requires = JvmPlugin
  override def trigger = allRequirements

  override lazy val globalSettings: Seq[Def.Setting[?]] = Seq(
    bestEffortEnabled := SysProp.bestEffort,
    bestEffortOptions := List(),
  )

  override lazy val projectSettings: Seq[Def.Setting[?]] =
    inConfig(Compile)(configurationSettings) ++
      inConfig(Test)(configurationSettings)

  lazy val configurationSettings: Seq[Def.Setting[?]] = List(
    bestEffortOptions := {
      val sv = scalaVersion.value
      val enabled = bestEffortEnabled.value
      if enabled && isScala35Plus(sv) then Seq("-Ybest-effort")
      else Nil
    },
    scalacOptions := {
      val orig = scalacOptions.value
      val opts = bestEffortOptions.value
      if opts.nonEmpty then orig ++ opts
      else orig
    },
  )

  private def isScala35Plus(sv: String): Boolean =
    ScalaArtifacts.isScala3(sv) &&
      VersionNumber(sv).matchesSemVer(SemanticSelector(">=3.5.0"))
end BestEffortPlugin
