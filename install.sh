#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o nounset

readonly JOERN_INSTALL=~/bin/joern/
readonly JAR_INSTALL_DIR=${JOERN_INSTALL}/joern-cli/lib/
readonly SCHEMA_DIR=src/main/schema/

echo "Examining Joern installation..."

if [ ! -d "${JOERN_INSTALL}" ]; then
    echo "Cannot find Joern installation at ${JOERN_INSTALL}"
    echo "Please install Joern first"
    exit
fi

echo "Compiling (sbt stage)..."
sbt stage

echo "Installing jars into: ${JAR_INSTALL_DIR}"
cp target/universal/stage/lib/io.joern.joern-sample-extension-*.jar ${JAR_INSTALL_DIR}
cp target/universal/stage/lib/org.eclipse.jgit.org.eclipse.jgit* ${JAR_INSTALL_DIR}
