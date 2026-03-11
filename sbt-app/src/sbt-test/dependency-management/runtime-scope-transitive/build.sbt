ThisBuild / scalaVersion := "3.7.4"

// kafka-clients has transitive deps with <scope>runtime</scope> in its POM:
// zstd-jni, lz4-java, snappy-java, slf4j-api
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.6.1"

val checkCompileClasspath = taskKey[Unit]("Verify runtime-scoped transitives are NOT on compile classpath")
checkCompileClasspath := {
  val cp = (Compile / managedClasspath).value.map(_.data.getName)
  assert(cp.exists(_.contains("kafka-clients")), s"kafka-clients missing from compile classpath: $cp")
  // All transitives of kafka-clients are runtime-scoped — must NOT be on compile classpath
  assert(!cp.exists(_.contains("zstd-jni")), s"zstd-jni should not be on compile classpath: $cp")
  assert(!cp.exists(_.contains("lz4-java")), s"lz4-java should not be on compile classpath: $cp")
  assert(!cp.exists(_.contains("snappy-java")), s"snappy-java should not be on compile classpath: $cp")
  assert(!cp.exists(_.contains("slf4j-api")), s"slf4j-api should not be on compile classpath: $cp")
}

val checkRuntimeClasspath = taskKey[Unit]("Verify runtime-scoped transitives ARE on runtime classpath")
checkRuntimeClasspath := {
  val cp = (Runtime / managedClasspath).value.map(_.data.getName)
  // All deps must be on runtime classpath
  assert(cp.exists(_.contains("kafka-clients")), s"kafka-clients missing from runtime classpath: $cp")
  assert(cp.exists(_.contains("zstd-jni")), s"zstd-jni missing from runtime classpath: $cp")
  assert(cp.exists(_.contains("lz4-java")), s"lz4-java missing from runtime classpath: $cp")
  assert(cp.exists(_.contains("snappy-java")), s"snappy-java missing from runtime classpath: $cp")
  assert(cp.exists(_.contains("slf4j-api")), s"slf4j-api missing from runtime classpath: $cp")
}
