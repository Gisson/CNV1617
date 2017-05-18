#!/bin/bash

#BASEDIR="$HOME/CNV1617"
BITURL="http://grupos.tecnico.ulisboa.pt/~meic-cnv.daemon/labs/labs-bit/BIT.zip"
BITDIR="$HOME/BIT"

AWS_SDK="$HOME/aws-java-sdk-1.11.131"
AWS_SDK_URL="http://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip"

#This is specific for the aws machine
#export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk"

# no need to cd to the repo... this script is already there
#cd $BASEDIR


# also needed for travisCI
if ! [[ -d  "$BITDIR" ]];then
  pushd .
  cd "$HOME"
  wget $BITURL
  unzip BIT.zip
  popd
fi

if ! [[ -d "$AWS_SDK" ]]; then
  (
    cd ~
    wget "$AWS_SDK_URL"
    unzip aws-java-sdk.zip
  )
fi

if test "x$1" = "xrender-node"; then
  cd "server/"
  make run
fi
# vim: expandtab:ts=2:sw=2
