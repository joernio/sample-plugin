name := "joern-sample-extension"
ThisBuild/organization := "io.joern"
ThisBuild/scalaVersion := "2.13.1"
val cpgVersion = "1.2.16"

enablePlugins(JavaAppPackaging)

ThisBuild/resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.bintrayRepo("shiftleft", "maven"),
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public")


libraryDependencies ++= Seq(

  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.7.0.202003110725-r",
  "io.shiftleft" %% "semanticcpg" % cpgVersion,
  "io.shiftleft" %% "semanticcpg-tests" % cpgVersion % Test classifier "tests",
  "io.shiftleft" %% "fuzzyc2cpg" % cpgVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test
)

ThisBuild/Compile/scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-feature",
  "-deprecation",
  "-language:implicitConversions",
)

Compile / sourceGenerators += Def.task {
  println("REACHED")
  Seq()
}.taskValue


ThisBuild/licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))


