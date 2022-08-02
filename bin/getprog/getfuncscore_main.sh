#!/bin/bash

PROGNAME=$1
inputset=$2
scoretype=rutilfreqflex
SCORECALBIN=${DEBAUG_DIR}/bin/functionscorecalculator
SCOREFILTERBIN=${DEBAUG_DIR}/bin/functionfilter


if [ -z $PROGNAME ] || [ -z ${inputset} ]; then
    echo "Missing arguments."
    exit 1
fi

statictype=rutil


scf=func/info/${statictype}/rslt.txt          #Static score file
flexf=func/info/flex/${inputset}_cov/rslt.txt
freqf=func/info/freq/${inputset}_cov/rslt.txt
gcovmergedf=gcov/${inputset}_cov_merged       #Used to identify inconsistent funcs

if [ ! -f ${gcovmergedf} ]; then
    echo "No merged cov file: ${gcovmergedf}."
    exit 1
fi


${DEBAUG_DIR}/bin/getprog/getfuncstaticscore_main.sh $PROGNAME ${statictype}

${DEBAUG_DIR}/bin/getprog/getfuncflex_main.sh $PROGNAME ${inputset} || exit 1

${DEBAUG_DIR}/bin/getprog/getfuncfreq_main.sh $PROGNAME ${inputset} || exit 1

rsltdir=func/rank/${scoretype}/${inputset}_cov
if [ ! -d ${rsltdir} ]; then
    mkdir -p ${rsltdir}
else
    rm -fr ${rsltdir}/*
fi


$SCORECALBIN ${scoretype} ${scf} ${freqf} ${flexf} >${rsltdir}/rslt.nofilter.txt
$SCOREFILTERBIN ${rsltdir}/rslt.nofilter.txt ${gcovmergedf} >${rsltdir}/rslt.txt
