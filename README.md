Joern Sample Plugin
===================

The Joern Git Plugin is a minimal example of a plugin. It reads a git
history using a Java library and tags nodes of files that have
recently been modified. The main purpose of this plugin is to provide
an example that shows how Joern plugins can be developed and tested in
the IntelliJ IDE. The plugin is written in Scala and makes use of the
Java library jgit.

To install, make sure that joern and sbt are installed, then simply run `./install.sh`

More details at: https://docs.joern.io/extensions

To upgrade this plugin to the latest joern/ocular: 
1) find the latest joern version in https://github.com/joernio/joern/releases
2) find out which cpg version was used for that release - replace `master` with the joern version in the following url: https://github.com/joernio/joern/blob/master/build.sbt and look for `val cpgVersion = "x.y.z"` in the top section
3) update the files `joern-version` and `cpg-version` in this repository
