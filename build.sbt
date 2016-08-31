import sbt.Keys._

name := "Game of Life root project"

scalaVersion in ThisBuild := "2.11.8"

lazy val root = project.in(file(".")).
  aggregate(lifeJS, lifeJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val life = crossProject.in(file(".")).
  settings(
    name := "life",
    version := "0.1",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.4.3" % "test"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalaJSUseRhino in Global := false,
    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-Xfuture",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ywarn-unused"
    )
  ).jvmSettings(
    // bl
  ).jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.0"
    )
    // Add JS-specific settings here
  ).enablePlugins(JmhPlugin)

lazy val lifeJVM = life.jvm.settings(
    sourceDirectory in Jmh := (sourceDirectory in Test).value,
    classDirectory in Jmh := (classDirectory in Test).value,
    dependencyClasspath in Jmh := (dependencyClasspath in Test).value,
    // rewire tasks, so that 'jmh:run' automatically invokes 'jmh:compile' (otherwise a clean 'jmh:run' would fail)
    compile in Jmh <<= (compile in Jmh) dependsOn (compile in Test),
    run in Jmh <<= (run in Jmh) dependsOn (Keys.compile in Jmh)
).enablePlugins(JmhPlugin)
lazy val lifeJS = life.js
