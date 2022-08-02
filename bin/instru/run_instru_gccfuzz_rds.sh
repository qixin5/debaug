#!/bin/bash

PROGNAME=$1
inputset=$2
fuzznum=$3
ftype=ch


if [ -z $PROGNAME ] || [ -z ${inputset} ]; then
    echo "Missing arguments!"
    exit 1
fi


if [ -z ${fuzznum} ]; then
    fuzznum=100
fi


GCOV_ANAL_BIN=${DEBAUG_DIR}/bin/gcovanalyzer
LCOV2GCOV_BIN=${DEBAUG_DIR}/bin/lcov2gcov


CURRDIR=$(pwd) #Program's working dir


BIN=$CURRDIR/$PROGNAME
fuzzid=rdsfuzz${ftype}n${fuzznum}
OUTDIR=$CURRDIR/output.instru/${inputset}_gcc${fuzzid}
INDIR=$CURRDIR/input
GCOVDIR=$CURRDIR/gcov/${inputset}_gcc${fuzzid}
TIMEOUT=30


#Copy input.origin/${inputset} to input
if [ -d $INDIR ]; then
    rm -fr $INDIR
fi

if [ -d ${INDIR}.origin/${inputset} ]; then
    cp -r ${INDIR}.origin/${inputset} $INDIR 
else
    mkdir $INDIR
fi


#Compile with instrumentation
./compile.sh $CURRDIR/src/origin/$PROGNAME.c $BIN "-fprofile-arcs -ftest-coverage" gcc


#Check if .gcno (the file generated by gcc-instru compilation) exists
if [ ! -f ${PROGNAME}.gcno ]; then
    echo "${PROGNAME}.gcno is not found."
    exit 1
fi


#Clean old output files
if [ ! -d $OUTDIR ]; then
    mkdir -p $OUTDIR
else
    rm -fr $OUTDIR/*
fi

#Clean old gcov files
if [ ! -d ${GCOVDIR} ]; then
    mkdir -p ${GCOVDIR}
else
    rm -fr ${GCOVDIR}/*
fi

#Use a tmp directory for running
if [ ! -d $CURRDIR/tmp ]; then
    mkdir $CURRDIR/tmp
else
    chmod 755 -R $CURRDIR/tmp
    rm -fr $CURRDIR/tmp/*
fi

#Change dir
cd $CURRDIR/tmp


#Run test
fuzzed_testscript_dir=$CURRDIR/rdsfuzz/augment/fuzzedtestscript/${inputset}
augment_cov_ch_file=$CURRDIR/rdsfuzz/augment/augment_cov_ch.txt
if [ ${ftype} == "ch" ] && [ ! -f ${augment_cov_ch_file} ]; then
    echo "File is not found: ${augment_cov_ch_file}! Check if rdsfuzz_reliability_test_sirprog/run.sh was executed with no problem."
fi

for inputdir in ${fuzzed_testscript_dir}/*; do
    if [ ! -d ${inputdir} ]; then
        continue
    fi
    inputid=$(basename ${inputdir})
    
    for fuzzed_testscript in ${inputdir}/*; do
        fuzzvid=$(basename ${fuzzed_testscript})
	
	if [ ${ftype} == "ch" ] && [ -f ${augment_cov_ch_file} ]; then
	    if ! grep -q "${inputid}/${fuzzvid}" ${augment_cov_ch_file}; then
                continue
            fi
	fi
	
	cd $CURRDIR/tmp  #Make sure we're always in the right dir to run tests!
	
	${fuzzed_testscript} $BIN $TIMEOUT &> $OUTDIR/${inputid}_${fuzzvid}
	
	cd $CURRDIR/tmp
	
	#Create gcov report
	gcovfname=$GCOVDIR/${inputid}_${fuzzvid}
	if [ -f ${CURRDIR}/${PROGNAME}.gcda ]; then  #.gcda seems to be generated in the directory where $BIN is located
            mv ${CURRDIR}/${PROGNAME}.gcda ./        #Copy .gcda produced for this test run
	    
	    cp ${CURRDIR}/${PROGNAME}.gcno ./        #Copy .gcno
            cp $CURRDIR/src/origin/$PROGNAME.c ./    #Copy original source
            gcov -i $PROGNAME.c
	    
            if [ -f $PROGNAME.c.gcov ]; then
                mv $PROGNAME.c.gcov ${gcovfname}
	    else
                echo "No $PROGNAME.c.gcov is generated."
            fi
        else
            echo "No ${PROGNAME}.gcda file found in ${CURRDIR} when running ${fuzzed_testscript}."
        fi
	
        #Clean
        chmod 755 -R $CURRDIR/tmp
        rm -fr $CURRDIR/tmp/*
    done
done
    
rm ${CURRDIR}/${PROGNAME}.gcno
