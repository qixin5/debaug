#!/bin/bash

PROGNAME=$1
scoretype=$2
SCORECALBIN=${DEBAUG_DIR}/bin/functionstaticscorecalculator

if [ -z $PROGNAME ] || [ -z ${scoretype} ]; then
    echo "Missing arguments."
    exit 1
fi

#Make callgraph.dot if it doesn't exist
${DEBAUG_DIR}/bin/getprog/getcallgraph.sh $PROGNAME

#Make line.txt if it doesn't exist
${DEBAUG_DIR}/bin/getprog/getlinefile.sh $PROGNAME 


rsltdir=func/info/${scoretype}
if [ ! -d ${rsltdir} ]; then
    mkdir -p ${rsltdir}
else
    rm -fr ${rsltdir}/*
fi

${SCORECALBIN} func/info/callgraph.dot line.txt ${scoretype} >${rsltdir}/rslt.txt

