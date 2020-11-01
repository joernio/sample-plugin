name := "joern-sample-extension"
ThisBuild/organization := "io.joern"
ThisBuild/scalaVersion := "2.13.1"
val cpgVersion = "1.2.16"

enablePlugins(JavaAppPackaging)

import better.files._
import scala.sys.process._

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
  val cpgDirName = "codepropertygraph"
  val cpgDirExists = File(cpgDirName).exists

  val correctCpgIsPresent = if (cpgDirExists){
    println("CPG directory already exist")
    val versionAtHead =
      sys.process.Process(Seq("git","describe", "--tags"), new java.io.File(cpgDirName)).!!.stripLineEnd
    val versionsMatch = s"v$cpgVersion" == versionAtHead
    println("Required version: " + s"v$cpgVersion")
    println("Version at head: " + versionAtHead)
    versionsMatch
  } else {
    false
  }

  if(!correctCpgIsPresent) {
    if (cpgDirExists) {
      File(cpgDirName).delete()
    }
    println(s"Cloning CPG version ${cpgVersion}...")
    s"git clone --depth 1 --branch v${cpgVersion} https://github.com/ShiftLeftSecurity/codepropertygraph/" !!
  }

  Seq()
}.taskValue


ThisBuild/licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))


