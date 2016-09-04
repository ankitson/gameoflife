import sbt.Keys._

name := "Game of Life"

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
  )

lazy val lifeJVM = life.jvm.settings(
    sourceDirectory in Jmh := (sourceDirectory in Test).value,
    classDirectory in Jmh := (classDirectory in Test).value,
    dependencyClasspath in Jmh := (dependencyClasspath in Test).value,
    compile in Jmh <<= (compile in Jmh) dependsOn (compile in Test),
    run in Jmh <<= (run in Jmh) dependsOn (Keys.compile in Jmh)
).enablePlugins(JmhPlugin)

lazy val lifeJS = life.js.settings(
  resolvers += Resolver.sonatypeRepo("releases"),

  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1",
    "eu.unicredit" %%% "akkajsactor" % "0.2.0"
  ),
  jsDependencies ++= Seq(
    "org.webjars.bower" % "react" % "15.2.1"
      /        "react-with-addons.js"
      minified "react-with-addons.min.js"
      commonJSName "React",

    "org.webjars.bower" % "react" % "15.2.1"
      /         "react-dom.js"
      minified  "react-dom.min.js"
      dependsOn "react-with-addons.js"
      commonJSName "ReactDOM",

    "org.webjars.bower" % "react" % "15.2.1"
      /         "react-dom-server.js"
      minified  "react-dom-server.min.js"
      dependsOn "react-dom.js"
      commonJSName "ReactDOMServer"
  )
).enablePlugins(ScalaJSPlugin)
