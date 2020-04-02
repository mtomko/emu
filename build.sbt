import higherkindness.mu.rpc.srcgen.Model._

inThisBuild(Seq(
  organization := "dev.mtomko",
  scalaVersion := "2.12.10",
  scalacOptions += "-language:higherKinds"
))

def isOldScala(sv: String): Boolean =
  CrossVersion.partialVersion(sv) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }

val macroSettings: Seq[Setting[_]] = {

  def paradiseDependency(sv: String): Seq[ModuleID] =
    if (isOldScala(sv))
      Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch))
    else
      Seq.empty

  def macroAnnotationScalacOption(sv: String): Seq[String] =
    if (isOldScala(sv))
      Seq.empty
    else
      Seq("-Ymacro-annotations")

  Seq(
    libraryDependencies ++= paradiseDependency(scalaVersion.value),
    scalacOptions ++= macroAnnotationScalacOption(scalaVersion.value)
  )
}

val protocol = project
  .settings(
    name := "emu-protocol",

    libraryDependencies ++= Seq(
      // Needed for the generated code to compile
      "io.higherkindness" %% "mu-rpc-channel" % "0.21.3"
    ),

    // Needed to expand the @service macro annotation
    macroSettings,

    // Generate sources from .proto files
    muSrcGenIdlType := IdlType.Proto,
    // Make it easy for 3rd-party clients to communicate with us via gRPC
    muSrcGenIdiomaticEndpoints := true,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )

val server = project
  .settings(
    name := "emu-rpc-server",

    libraryDependencies ++= Seq(
      // Needed to build a gRPC client
      "io.higherkindness" %% "mu-rpc-server" % "0.21.3",

      // Silence all logs in the demo
      "org.slf4j" % "slf4j-nop" % "1.7.30",

      "org.scalatest" %% "scalatest" % "3.1.1" % Test,

      // Needed to build an in-memory server in the test
      "io.higherkindness" %% "mu-rpc-testing" % "0.21.3" % Test
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
  .dependsOn(protocol)

val client = project
  .settings(
    name := "emu-rpc-client",

    libraryDependencies ++= Seq(
      // Needed to build a gRPC client (although you could use mu-rpc-okhttp instead)
      "io.higherkindness" %% "mu-rpc-netty" % "0.21.3",

      // For console I/O in the demo client
      "dev.profunktor" %% "console4cats" % "0.8.1",

      // Silence all logs in the demo
      "org.slf4j" % "slf4j-nop" % "1.7.30"
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
  .dependsOn(protocol)

val root = (project in file("."))
  .settings(
    name := "emu"
  )
  .aggregate(protocol, server, client)
