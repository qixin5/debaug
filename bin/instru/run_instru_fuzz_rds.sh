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
OUTDIR=$CURRDIR/output.instru/${inputset}_${fuzzid}
INDIR=$CURRDIR/input
GCOVDIR=$CURRDIR/gcov/${inputset}_${fuzzid}
fuzzdir=$CURRDIR/rdsfuzz/augment    #augment is the relevant sub-directory
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
./compile.sh $CURRDIR/src/origin/$PROGNAME.c $BIN "-fprofile-instr-generate -fcoverage-mapping"


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
cd $CURRDIR/tmp


produce_gcov_file () {
    llvm-profdata merge -o $PROGNAME.profdata default.profraw
    llvm-cov export -format=lcov $BIN -instr-profile=$PROGNAME.profdata $CURRDIR/src/origin/$PROGNAME.c >$PROGNAME.lcov
    ${LCOV2GCOV_BIN} $PROGNAME.lcov >$PROGNAME.gcov
    ${GCOV_ANAL_BIN} $PROGNAME.gcov getbcov >$1
}


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
	
	#Produce gcov file
	gcovfname=$GCOVDIR/${inputid}_${fuzzvid}
	if [ -f default.profraw ]; then
	    produce_gcov_file ${gcovfname}
	    
	    #Sometimes, producing a valid gcov would fail.
            #When this happens, try running the test again.
            if [ ! -f ${gcovfname} ] || ! grep -q '[^[:space:]]' ${gcovfname}; then
		${fuzzed_testscript} $BIN $TIMEOUT
                produce_gcov_file ${gcovfname}
            fi
	else
	    echo "No profraw file generated for fuzzed input ${fuzzed_testscript} by llvm-cov."
	fi
	
	if [ ! -f ${gcovfname} ] || ! grep -q '[^[:space:]]' ${gcovfname}; then
            echo "No gcov file generated for fuzzed input ${fuzzed_testscript} by llvm-cov."
        fi
	
	#Clean                                                                          
        chmod 755 -R $CURRDIR/tmp
        rm -fr $CURRDIR/tmp/*
	
    done
    
done
