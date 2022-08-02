#!/bin/bash

PROGNAME=$1
inputset=$2
MAXINPUTNUM=$3   #Select at most MAXINPUTNUM inputs for fuzzing
forwhat=augment



if [ -z $PROGNAME ] || [ -z ${inputset} ]; then
    echo "Missing arguments."
    exit 1
fi

if [ -z $MAXINPUTNUM ]; then
    MAXINPUTNUM=50
fi


if [ ! -d rdsfuzz/augment ]; then
    mkdir -p rdsfuzz/augment
fi
cd rdsfuzz/augment



#Must use absolute paths
CURRDIR=$(pwd)
base_testscript_dir=$CURRDIR/basetestscript/${inputset}
origin_testscript_dir=$CURRDIR/../../testscript/${inputset}


#Directory setup
if [ ! -d ${origin_testscript_dir} ]; then
    echo "Test script directory does not exist: ${origin_testscript_dir}."
    exit 1
fi

if [ -d ${base_testscript_dir} ]; then
    rm -fr ${base_testscript_dir}/*
else
    mkdir -p ${base_testscript_dir}
fi


#Select a number of tests to fuzz
> ${base_testscript_dir}/all_inputs.txt
for testf in ${origin_testscript_dir}/*; do

    testfname=$(basename ${testf})

    if grep -q "'/'" ${testf}; then
	#It contains '/' as argument.
	#This can lead to all content from '/' copied as inputs. Skip it!
	continue
    fi

    echo ${testfname} >>${base_testscript_dir}/all_inputs.txt
    
done
shuf ${base_testscript_dir}/all_inputs.txt | head -n ${MAXINPUTNUM} >${base_testscript_dir}/selected_inputs.txt


#Copy the selected tests
while IFS= read -r line; do

    inputid=${line}
    cp ${origin_testscript_dir}/${inputid} ${base_testscript_dir}
    
done < ${base_testscript_dir}/selected_inputs.txt

rm ${base_testscript_dir}/*.txt


#Remove the leading "timeout"s.
#It's fine it's removed. We will use wrapper to execute the script, which always terminates.
sed -i "s|timeout -k 9 \${TIMEOUT}s ||g" ${base_testscript_dir}/*
