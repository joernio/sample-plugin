import sbt._

/* Declare dependency versions in one place */
object Versions {
  val cpg = IO.read(Path.userHome / "bin/joern/joern-cli/schema-extender/cpg-version")
  val overflowdb = "1.29"
}
