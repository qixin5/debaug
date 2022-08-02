#!/bin/bash

PROGNAME=$1
LINEGEN_BIN=${DEBAUG_DIR}/bin/startandendlineprinter

if [ -z $PROGNAME ]; then
    echo "Missing argument."
    exit 1
fi

if [ ! -f line.txt ]; then
    ${LINEGEN_BIN} src/origin/$PROGNAME.c >line.txt
    if [ -d debop-out ]; then
	rm -fr debop-out
    fi
fi
