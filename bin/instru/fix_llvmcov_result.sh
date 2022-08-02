#!/bin/bash

PROGNAME=$1
cov_dir=$2       #Directory containing llvm-cov coverage files (transformed into gcc format)
gcc_cov_dir=$3   #Directory containing gcc coverage files


if [ -z $PROGNAME ] || [ -z ${cov_dir} ] || [ -z ${gcc_cov_dir} ]; then
    echo "Missing arguments!"
    exit 1
fi


FIXER_BIN=${DEBAUG_DIR}/bin/llvmgcovfixer

cd gcov

if [ ! -d ${gcc_cov_dir} ]; then
    echo "No gcc cov results available."
    exit 1
fi

for covf in ${cov_dir}/*; do
    covfname=$(basename ${covf})
    
    if [ -f ${gcc_cov_dir}/${covfname} ]; then
	#==============
	#echo "${FIXER_BIN} ${covf} ${gcc_cov_dir}/${covfname}"
	#==============	
	${FIXER_BIN} ${covf} ${gcc_cov_dir}/${covfname}
    fi
done



