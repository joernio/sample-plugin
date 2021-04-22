name := "joern-sample-plugin-schema"

libraryDependencies += "io.shiftleft" %% "codepropertygraph-schema" % Versions.cpg

val generateDomainClasses = taskKey[Seq[File]]("generate overflowdb domain classes for our schema")

generateDomainClasses := Def.taskDyn {
  val outputRoot = target.value / "odb-codegen"
  val currentMd5 = FileUtils.md5(sourceDirectory.value)

  if (!outputRoot.exists || CodeGenGlobalState.lastMd5 != currentMd5) {
    Def.task {
      FileUtils.deleteRecursively(outputRoot)
      val invoked = (Compile/runMain).toTask(s" CpgExtCodegen schema/target/odb-codegen").value
      CodeGenGlobalState.lastMd5 = currentMd5
      FileUtils.listFilesRecursively(outputRoot)
    }
  } else {
    Def.task {
      CodeGenGlobalState.lastMd5 = currentMd5
      FileUtils.listFilesRecursively(outputRoot)
    }
  }
}.value
