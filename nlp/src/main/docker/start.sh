#!/usr/bin/env bash

IP=129.114.17.83
#IP=localhost
PORT=11111

if [[ -n "$1" ]] ; then
    IP=$1
fi
OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=$PORT -Djava.rmi.server.hostname=$IP -Dcom.sun.management.jmxremote.rmi.port=$PORT"

java $OPTS -jar stanford.jar
