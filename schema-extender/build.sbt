name := "joern-sample-plugin-schema-extender"

val joernInstallPath = settingKey[File]("path to joern installation")
joernInstallPath := file("joern-inst/joern-cli") // as defined in `install.sh`

val replaceDomainClassesInJoern = taskKey[Unit]("generates new domain classes based on the given schema, and installs them in the joern distribution")

replaceDomainClassesInJoern := {
  import java.nio.file._
  val newDomainClassesJar = (Projects.domainClasses/Compile/packageBin).value

  val targetFile = joernInstallPath.value / "lib" / s"io.shiftleft.codepropertygraph-domain-classes_2.13-${Versions.cpg}.jar"
  assert(targetFile.exists, s"target jar assumed to be $targetFile, but that file doesn't exist...")

  println(s"copying $newDomainClassesJar to $targetFile")
  Files.copy(newDomainClassesJar.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)
}
