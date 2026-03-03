scalaVersion := "3.7.4"

// Verify that Java 9+ platform module classes (e.g. javax.sql.DataSource)
// are loadable from the console classloader. See sbt#8664.
TaskKey[Unit]("checkPlatformModule") := {
  val si = (Compile / console / scalaInstance).value
  val cp = (Compile / fullClasspath).value
  val converter = fileConverter.value
  val cpFiles = Attributed.data(cp).map(converter.toPath).map(_.toFile)
  val fullcp = (cpFiles ++ si.allJars).distinct
  val tempDir = IO.createUniqueDirectory((Compile / console / taskTemporaryDirectory).value).toPath
  val loader = sbt.internal.inc.classpath.ClasspathUtil.makeLoader(
    fullcp.map(_.toPath), si.loaderLibraryOnly, si, tempDir
  )
  loader.loadClass("javax.sql.DataSource")
  streams.value.log.info("Successfully loaded javax.sql.DataSource from console classloader")
}
