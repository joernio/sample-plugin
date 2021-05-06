import sbt._

object Versions {
  val cpg = IO.read(file("cpg-version"))
}

object Projects {
  lazy val schema = project.in(file("schema"))
  lazy val domainClasses = project.in(file("domain-classes"))
  lazy val schemaExtender = project.in(file("schema-extender"))
}
