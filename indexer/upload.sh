#!/usr/bin/env bash

KEY=$HOME/.ssh/lappsgrid-shared-key.pem
HOST=ubuntu@149.165.169.127
DIR=/home/ubuntu

CLOUD=root@129.114.16.34

if [[ "$1" == "cloud" ]] ; then
    KEY=$HOME/.ssh/tacc-shared-key.pem
    HOST=$CLOUD
    DIR=/root
fi

#scp -i $HOME/.ssh/lappsgrid-shared-key.pem target/indexer.jar ubuntu@149.165.169.127:/home/ubuntu
#scp -i $HOME/.ssh/tacc-shared-key.pem target/indexer.jar root@149.165.169.127:/home/ubuntu
scp -i $KEY target/indexer.jar $HOST:$DIR

