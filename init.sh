#!/bin/bash

BASEDIR="$HOME/CNV1617"
BITURL="http://grupos.tecnico.ulisboa.pt/~meic-cnv.daemon/labs/labs-bit/BIT.zip"
BITDIR="$HOME/BIT"

#This is specific for the aws machine
export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk"

cd $BASEDIR


# also needed for travisCI
if ! [[ -d  "$HOME/BIT" ]];then
  pushd .
  cd "$HOME"
  wget $BITURL
  unzip BIT.zip
  source "$BITDIR/java-config.sh"
  popd
fi


if test $1 = "render-node"; then
  cd "server/"
  make run
fi
# vim: expandtab:ts=2:sw=2
