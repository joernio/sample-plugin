name := "joern-sample-extension"
ThisBuild/organization := "io.joern"
ThisBuild/scalaVersion := "2.13.0"
val cpgVersion = "1.2.17+2-344ab09b"

enablePlugins(JavaAppPackaging)

lazy val schema = project.in(file("schema"))
dependsOn(schema)
libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.7.0.202003110725-r",
  "io.shiftleft" %% "semanticcpg" % cpgVersion,
  "io.shiftleft" %% "semanticcpg-tests" % cpgVersion % Test classifier "tests",
  "io.shiftleft" %% "fuzzyc2cpg" % cpgVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test
)
excludeDependencies += ExclusionRule("io.shiftleft", "codepropertygraph-domain-classes_2.13")

ThisBuild/Compile/scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-feature",
  "-deprecation",
  "-language:implicitConversions",
)

ThisBuild/licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

Global/onChangedBuildSource := ReloadOnSourceChanges

ThisBuild/resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.bintrayRepo("shiftleft", "maven"),
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public")
