#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -eu

readonly JOERN_DISTRIBUTION="$HOME/bin/joern/joern-cli"

if [ "$(uname)" = 'Darwin' ]; then
  # get script location
  # https://unix.stackexchange.com/a/96238
  if [ "${BASH_SOURCE:-x}" != 'x' ]; then
    this_script=$BASH_SOURCE
  elif [ "${ZSH_VERSION:-x}" != 'x' ]; then
    setopt function_argzero
    this_script=$0
  elif eval '[[ -n ${.sh.file} ]]' 2>/dev/null; then
    eval 'this_script=${.sh.file}'
  else
    echo 1>&2 "Unsupported shell. Please use bash, ksh93 or zsh."
    exit 2
  fi
  relative_directory=$(dirname "$this_script")
  SCRIPT_ABS_DIR=$(cd "$relative_directory" && pwd)
else
  SCRIPT_ABS_PATH=$(readlink -f "$0")
  SCRIPT_ABS_DIR=$(dirname "$SCRIPT_ABS_PATH")
fi

echo "Examining Joern installation..."

if [ ! -d "${JOERN_DISTRIBUTION}" ]; then
    echo "Cannot find Joern installation at ${JOERN_DISTRIBUTION} - please install Joern first"
    exit
fi

echo "Building and installing plugin - incl. domain classes for schema extension..."
pushd $SCRIPT_ABS_DIR
sbt createDistribution replaceDomainClassesInJoern
popd

pushd "${JOERN_DISTRIBUTION}"
  ./joern --remove-plugin plugin || true
  ./joern --add-plugin $SCRIPT_ABS_DIR/plugin.zip
popd

echo "all done. you can now use this plugin in joern. Examples:"
echo "joern> nodes.ExampleNode.PropertyNames.all"
echo "joern> io.shiftleft.gitextension.Gitextension.description"
