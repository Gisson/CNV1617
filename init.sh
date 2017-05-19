#!/bin/bash

#BASEDIR="$HOME/CNV1617"
BITURL="http://grupos.tecnico.ulisboa.pt/~meic-cnv.daemon/labs/labs-bit/BIT.zip"
BITDIR="$HOME/BIT"

AWS_SDK="$HOME/aws-java-sdk-1.11.132"
AWS_SDK_URL="http://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip"

#This is specific for the aws machine
#export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk"

# no need to cd to the repo... this script is already there
#cd $BASEDIR

if test "x$1" = "xload-balancer"; then
  loadbalancer=true
else
  loadbalancer=false
fi

# also needed for travisCI
if (! [[ -d  "$BITDIR" ]]) && (! $loadbalancer) ;then
  pushd .
  cd "$HOME"
  wget $BITURL
  unzip BIT.zip
  popd
fi

# for render-nodes and the load-balancer
if ! [[ -d "$AWS_SDK" ]]; then
  (
    cd ~
    wget "$AWS_SDK_URL"
    unzip -q aws-java-sdk.zip
    rm aws-java-sdk.zip*
  )
fi

if test "x$1" = "xrender-node"; then
  cd "server/"
  make run
fi

if $loadbalancer; then
  cd "load-balancer"
  make run
fi
# vim: expandtab:ts=2:sw=2
