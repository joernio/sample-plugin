#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -eu

readonly JOERN_VERSION=$(<joern-version)

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

# Check required tools are installed.
check_installed() {
  if ! type "$1" > /dev/null; then
    echo "Please ensure you have $1 installed."
    exit 1
  fi
}

readonly JOERN_INSTALL="$SCRIPT_ABS_DIR/joern-inst"

echo "Examining Joern installation..."

if [ ! -d "${JOERN_INSTALL}" ]; then
    echo "Cannot find Joern installation at ${JOERN_INSTALL}"
    echo "Installing..."
    check_installed "curl"

    # Fetch installer
    echo "https://github.com/ShiftLeftSecurity/joern/releases/download/v$JOERN_VERSION/joern-install.sh"
    curl -L "https://github.com/ShiftLeftSecurity/joern/releases/download/v$JOERN_VERSION/joern-install.sh" -o "$SCRIPT_ABS_DIR/joern-install.sh"

    # Install into `joern-inst`
    chmod +x $SCRIPT_ABS_DIR/joern-install.sh
    $SCRIPT_ABS_DIR/joern-install.sh --install-dir="$SCRIPT_ABS_DIR/joern-inst" --version=v$JOERN_VERSION --without-plugins
    rm $SCRIPT_ABS_DIR/joern-install.sh
fi

echo "Building and installing plugin - incl. domain classes for schema extension..."
pushd $SCRIPT_ABS_DIR
sbt createDistribution replaceDomainClassesInJoern
popd

pushd "${JOERN_INSTALL}/joern-cli"
  ./joern --remove-plugin plugin || true
  ./joern --add-plugin $SCRIPT_ABS_DIR/plugin.zip
popd

echo "All done! Joern and this plugin are ready to use in ${JOERN_INSTALL}. Example usage:"
echo "joern> nodes.ExampleNode.PropertyNames.all"
echo "joern> io.shiftleft.gitextension.Gitextension.description"
