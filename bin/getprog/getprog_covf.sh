#!/bin/bash

PROGNAME=$1
inputset=$2
fuzznum=$3
maxinputtofuzz=$4
force_radamsa_to_run=$5
ftype=ch


if [ -z $PROGNAME ] || [ -z ${inputset} ]; then
    echo "Missing arguments."
    exit 1
fi

if [ -z ${fuzznum} ]; then
    fuzznum=10
fi

if [ -z ${maxinputtofuzz} ]; then
    maxinputtofuzz=100
fi

if [ -z ${force_radamsa_to_run} ]; then
    force_radamsa_to_run=false
fi


fuzzid=rdsfuzz${ftype}n${fuzznum}

argsdir=rdsfuzz/augment/args/${inputset}

if [ ${force_radamsa_to_run} == "true" ]; then
    rm -fr ${argsdir}
fi


#Generate fuzzed testscripts
if [ ! -d ${argsdir} ]; then
    echo "Generating fuzzed inputs"

    rdsfuzz_reliability_test_sirprog/prepare_basescript.sh $PROGNAME ${inputset}
    rdsfuzz_reliability_test_sirprog/create_fuzzed_testscript.sh $PROGNAME ${inputset} ${fuzznum}
fi

#Remove any old augment_cov_ch.txt, which keeps track of inputs leading to crash/hang
if [ -f rdsfuzz/augment/augment_cov_ch.txt ]; then
    rm rdsfuzz/augment/augment_cov_ch.txt
fi

#Run robust testing for program made by cov; obtain the specific inputs leading to crash/hang
if [ ${ftype} == "ch" ]; then

    ${DEBAUG_DIR}/bin/getprog/rdsfuzz_run.sh $PROGNAME ${inputset}_cov ${inputset}
    CURRDIR=$(pwd)
    cd rdsfuzz/augment/output/reduced/${inputset}_cov/${inputset}
    grep -r -l "Segmentation fault\|^124" &>../../../../augment_cov_ch.txt
    cd ${CURRDIR}
fi


#Run instru
echo "Instru run for llvm-cov"
${DEBAUG_DIR}/bin/instru/run_instru_fuzz_rds.sh $PROGNAME ${inputset}

echo "Instru run for gcov"
${DEBAUG_DIR}/bin/instru/run_instru_gccfuzz_rds.sh $PROGNAME ${inputset}

echo "Instru fix"
${DEBAUG_DIR}/bin/instru/fix_llvmcov_result.sh $PROGNAME ${inputset}_${fuzzid} ${inputset}_gcc${fuzzid}

#Get reduced prog
echo "Produce reduced program"
${DEBAUG_DIR}/bin/getprog/getprog_covfuzz_rds.sh $PROGNAME ${inputset}
