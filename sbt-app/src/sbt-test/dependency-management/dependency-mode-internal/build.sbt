// Reproducer for https://github.com/sbt/sbt/issues/9009
// LibB.dependsOn(LibA), LibA.dependsOn(Core). Under Direct/PlusOne, dependencyMode
// must be enforced on the internal (project) classpath, not just external libs.

ThisBuild / scalaVersion := "3.3.4"

lazy val checkDirect = taskKey[Unit]("check Direct mode on internal classpath")
lazy val checkPlusOne = taskKey[Unit]("check PlusOne mode on internal classpath")
lazy val checkTransitive = taskKey[Unit]("check Transitive mode on internal classpath")

lazy val Core = (project in file("Core"))

lazy val LibA = (project in file("LibA")).dependsOn(Core)

lazy val LibB = (project in file("LibB"))
  .dependsOn(LibA)
  .settings(
    checkTransitive := {
      val cp = (Compile / filteredDependencyClasspath).value.map(_.data.id)
      assert(cp.exists(_.contains("core")), s"expected Core in transitive mode, got: $cp")
      assert(cp.exists(_.contains("liba")), s"expected LibA in transitive mode, got: $cp")
    },
    checkDirect := {
      val cp = (Compile / filteredDependencyClasspath).value.map(_.data.id)
      assert(cp.exists(_.contains("liba")),
        s"expected LibA in Direct mode (direct .dependsOn), got: $cp")
      assert(!cp.exists(_.contains("core")),
        s"expected no Core in Direct mode (transitive via LibA), got: $cp")
    },
    checkPlusOne := {
      val cp = (Compile / filteredDependencyClasspath).value.map(_.data.id)
      assert(cp.exists(_.contains("liba")),
        s"expected LibA in PlusOne mode (direct .dependsOn), got: $cp")
      assert(cp.exists(_.contains("core")),
        s"expected Core in PlusOne mode (one hop via LibA), got: $cp")
    },
  )
