#!/usr/bin/env bash

#IP=localhost
PORT=11111

if [[ -n "$1" ]] ; then
    IFS=':'
    VAR='IP'
    for x in $1 ; do
        eval $VAR=$x
        VAR='PORT'
    done
else
    IP=`curl ipinfo.io/ip`
fi

echo "My IP address is $IP:$PORT"
OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=$PORT -Djava.rmi.server.hostname=$IP -Dcom.sun.management.jmxremote.rmi.port=$PORT"

java $OPTS -jar preprocess.jar