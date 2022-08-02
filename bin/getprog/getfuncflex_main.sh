#!/bin/bash

PROGNAME=$1
inputset=$2
FLEXCALBIN=${DEBAUG_DIR}/bin/functionflexibilitycalculator

if [ -z $PROGNAME ] || [ -z ${inputset} ]; then
    echo "Missing arguments."
    exit 1
fi

if [ ! -f line.txt ]; then
    ${DEBAUG_DIR}/bin/getprog/getlinefile.sh $PROGNAME
fi

if [ ! -d gcov/${inputset}_cov ]; then
    echo "Missing coverage files for gcov/${inputset}_cov."
    exit 1
fi

rsltdir=func/info/flex/${inputset}_cov
if [ ! -d ${rsltdir} ]; then
    mkdir -p ${rsltdir}
else
    rm -fr ${rsltdir}/*
fi

$FLEXCALBIN line.txt gcov/${inputset}_cov >${rsltdir}/rslt.txt
