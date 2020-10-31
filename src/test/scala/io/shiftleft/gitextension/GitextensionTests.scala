package io.shiftleft.gitextension

import java.nio.file.Paths

import io.shiftleft.semanticcpg.layers.LayerCreatorContext
import io.shiftleft.semanticcpg.testfixtures.CodeDirToCpgFixture
import io.shiftleft.semanticcpg.language._
import java.io.{File => JFile}

class GitextensionTests extends CodeDirToCpgFixture {

  val pathToRepo = "src/test/resources/testrepo"
  override val dir = new java.io.File(pathToRepo)

  override def beforeAll(): Unit = {
    super.beforeAll()
    val context = new LayerCreatorContext(cpg)
    val options = GitExtensionOpts(Paths.get(pathToRepo, "dotgit").toString)
    new Gitextension(options).create(context)
  }

  "find a valid test setup" in {
    new JFile(pathToRepo).exists() shouldBe true
    new JFile(Paths.get(pathToRepo, "dotgit").toString).exists() shouldBe true
    cpg.method.name("main").size shouldBe 1
  }

  "mark only function `foo` as recently changed" in {
    val changedFiles = new Tag(cpg.tag.name("RECENTLY_CHANGED")).file.name.l
    changedFiles.size shouldBe 1
    changedFiles.head.endsWith("justadded.c") shouldBe true
  }

}
