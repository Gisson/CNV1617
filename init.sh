#!/bin/bash

BASEDIR="$HOME/CNV1617"
BITURL="http://grupos.tecnico.ulisboa.pt/~meic-cnv.daemon/labs/labs-bit/BIT.zip"

cd $BASEDIR



if ! [[ -d  "$HOME/BIT" ]];then
  pushd .
  cd "$HOME"
  wget $BITURL
  unzip BIT.zip
  popd
fi

cd "server/"

make run
