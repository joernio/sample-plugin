package io.shiftleft.gitextension

import java.nio.file.{Path, Paths}

import better.files._
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}
import io.shiftleft.semanticcpg.language._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.util.io.DisabledOutputStream

import scala.jdk.CollectionConverters._

object Gitextension {

  /**
    * This is the extensions official name as will be shown in the table
    * one obtains when running `run` on the Ocular shell.
    * */
  val overlayName = "Sample Git Extension"

  /**
    * A short description to be shown in the table obtained when
    * running `run` on the Ocular shell.
    * */
  val description =
    "A sample extension that tags all files that have recently been modified."

  /**
    * Option object initialize to defaults. This object will be made
    * accessible to the user via `opts.myextension`.
    * */
  def defaultOpts = GitExtensionOpts(".")
}

/**
  * Options can be passed to the extension via a custom options
  * class that derives from `LayerCreatorOptions`. In our example,
  * we use the option class below to hand the path to the git
  * repository from the user to the extension.
  * */
case class GitExtensionOpts(var pathToRepo: String)
    extends LayerCreatorOptions {}

class Gitextension(options: GitExtensionOpts) extends LayerCreator {
  override val overlayName: String = Gitextension.overlayName
  override val description: String = Gitextension.description

  /**
    * This method is executed when the user issues the command
    * `run.myextension`.
    * */
  override def create(context: LayerCreatorContext,
                      serializeInverse: Boolean): Unit = {
    val cpg = context.cpg
    val serializedCpg = initSerializedCpg(context.outputDir, "gitextension", 0)
    new DetermineChangedFilesPass(cpg)
      .createApplySerializeAndStore(serializedCpg, serializeInverse)
    serializedCpg.close()
  }

  private class DetermineChangedFilesPass(cpg: Cpg) extends CpgPass(cpg) {
    override def run(): Iterator[DiffGraph] = {
      implicit val diffGraph: DiffGraph.Builder = DiffGraph.newBuilder
      val changedFiles = recentlyChangedFiles().map(_.toAbsolutePath.toString)
      cpg.file.name(changedFiles: _*).newTagNode("RECENTLY_CHANGED").store
      Iterator(diffGraph.build)
    }

    private def recentlyChangedFiles(commitsToGoBack: Int = 10): List[Path] = {
      val builder = new FileRepositoryBuilder()
      val repository = builder
        .setGitDir(File(options.pathToRepo).toJava)
        .setMustExist(true)
        .build()
      val git = new Git(repository)
      val reader = repository.newObjectReader()

      def createIterFor(commit: String) = {
        val iter = new CanonicalTreeParser()
        iter.reset(reader, repository.resolve(commit))
        iter
      }

      val nCommitsAvailable =
        git.log().setMaxCount(commitsToGoBack).call().asScala.toList.size
      if (nCommitsAvailable == 0) {
        List()
      } else {
        val oldTreeIter = createIterFor(s"HEAD~${nCommitsAvailable - 1}^{tree}")
        val newTreeIter = createIterFor(s"HEAD^{tree}")
        val diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)
        diffFormatter.setRepository(repository)
        diffFormatter.scan(oldTreeIter, newTreeIter).asScala.toList.map {
          entry =>
            Paths.get(repository.getDirectory.getParent, entry.getNewPath)
        }
      }
    }

  }

  /**
    * This method may be implemented to check whether the extension has
    * already been run by inspecting the graph. This method is only
    * relevant for legacy CPGs. You can set it to false on new extensions.
    * */
  override def probe(cpg: Cpg): Boolean = false
}
