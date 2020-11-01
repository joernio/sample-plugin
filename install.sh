readonly JOERN_INSTALL=~/bin/joern/

sbt stage
cp target/universal/stage/lib/io.joern.joern-sample-extension-*.jar ${JOERN_INSTALL}/joern-cli/lib/
cp target/universal/stage/lib/org.eclipse.jgit.org.eclipse.jgit* ${JOERN_INSTALL}/joern-cli/lib/
