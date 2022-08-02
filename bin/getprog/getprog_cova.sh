#!/bin/bash

PROGNAME=$1
inputset=$2
selecthreshold=$3 #Range: [1,100]

buildupon=cov
ranktype=rutilfreqflex
topselectby=percent


AUGBIN=${DEBAUG_DIR}/bin/functionaugmenter
SELECTBIN=${DEBAUG_DIR}/bin/functionselecter
DEBDCE_BIN=${DEBAUG_DIR}/bin/debdce


if [ -z $PROGNAME ] || [ -z ${inputset} ] || [ -z ${selecthreshold} ]; then
    echo "Missing arguments."
    exit 1
fi

if [ ${selecthreshold} -gt 100 ] || [ ${selecthreshold} -lt 1 ]; then
    echo "Invalid threshold. Must be between 1 and 100."
    exit 1
fi


#Make line.txt
${DEBAUG_DIR}/bin/getprog/getlinefile.sh $PROGNAME

scriptdir=$(pwd)


builduponsrc=./src/reduced/${inputset}_${buildupon}/$PROGNAME.nodce.c
if [ ! -f ${builduponsrc} ]; then
    ${DEBAUG_DIR}/bin/getprog/getprog_cov.sh $PROGNAME ${inputset}
    if [ ! -f ${builduponsrc} ]; then
	echo "Missing ${builduponsrc}"
	exit 1
    fi
fi


funcfile=./func/rank/${ranktype}/${inputset}_cov/rslt.txt
if [ ! -f ${funcfile} ]; then
    ${DEBAUG_DIR}/bin/getprog/getfuncscore_main.sh $PROGNAME ${inputset}
fi

rsltdir=./src/reduced/${inputset}_cova${selecthreshold}

if [ ! -d ${rsltdir} ]; then
    mkdir -p ${rsltdir}
else
    rm -fr ${rsltdir}/*
fi

if [ ! -d tmp ]; then
    mkdir tmp
fi

#Produce augmented program
$SELECTBIN ${funcfile} ${topselectby} ${selecthreshold} >tmp/funcs.txt
$AUGBIN tmp/funcs.txt ${builduponsrc} line.txt ./src/origin/$PROGNAME.c >${rsltdir}/$PROGNAME.nodce.c

#Remove dead code
${DEBDCE_BIN} ${rsltdir}/$PROGNAME.nodce.c ${rsltdir}/$PROGNAME.c
