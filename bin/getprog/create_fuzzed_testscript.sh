#!/bin/bash

PROGNAME=$1
inputset=$2
fuzznum=$3    #For every input
forwhat=augment

compilescriptgenbin=${DEBAUG_DIR}/bin/compilescriptgeneratorforfuzzedtestscriptgenerator
fuzztestscriptgensrcf=${DEBAUG_DIR}/src/c/FuzzedTestScriptGenerator.c


if [ -z $PROGNAME ] || [ -z ${inputset} ]; then
    echo "Missing arguments."
    exit 1
fi

if [ -z ${fuzznum} ]; then
    fuzznum=10
fi

if [ ! -d augment ]; then
    mkdir augment
fi
cd augment
    
CURRDIR=$(pwd)


#Prepare basetestscript dir
if [ ! -d basetestscript ]; then
    if [ -d ../basetestscript ]; then
	cp -r ../basetestscript ./
    else
	echo "Missing ${CURRDIR}/../basetestscript"
	exit 1
    fi
fi



#Must use absolute paths
argsdir=$CURRDIR/args/${inputset}
argsbackupdir=$CURRDIR/args.backup/${inputset}
fuzzedtestscriptdir=$CURRDIR/fuzzedtestscript/${inputset}
basetestscriptdir=$CURRDIR/basetestscript/${inputset}
inorigindir=$CURRDIR/../../input.origin
indir=$CURRDIR/input


if [ ! -d ${basetestscriptdir} ]; then
    echo "Missing ${basetestscriptdir}"
    exit 1
fi



#Directory setup
if [ ! -d ${argsdir} ]; then
    mkdir -p ${argsdir}
else
    rm -fr ${argsdir}/*
fi

if [ ! -d args.backup ]; then
    mkdir args.backup
fi

if [ ! -d ${fuzzedtestscriptdir} ]; then
    mkdir -p ${fuzzedtestscriptdir}
else
    rm -fr ${fuzzedtestscriptdir}/*
fi

if [ -d ${indir} ]; then
    rm -fr ${indir}
fi

if [ -d ${inorigindir} ]; then
    cp -r ${inorigindir}/${inputset} ${indir}
else
    mkdir ${indir}
fi


if [ ! -d $CURRDIR/tmp ]; then
    mkdir $CURRDIR/tmp
else
    chmod 755 -R $CURRDIR/tmp
    rm -fr $CURRDIR/tmp/*
fi



#Execute every base test script to generate fuzzed test script
for testf in ${basetestscriptdir}/*; do

    cd ${CURRDIR}                                 #i.e., rdsfuzz/augment
    testid=$(basename ${testf})
    mkdir ${argsdir}/${testid}                    #Fuzzed args are saved here
    mkdir ${fuzzedtestscriptdir}/${testid}        #Fuzzed test scripts are saved here
    

    ### Generate the fuzz wrapper
    #The fuzz wrapper, upon execution, parses arguments and generates fuzzed test script.
    cd ${CURRDIR}
    if [ -f $PROGNAME ]; then
	rm $PROGNAME #Remove any previous binary
    fi
    if [ -f compilescript.sh ]; then
	rm compilescript.sh #Remove any previous script
    fi

    ${compilescriptgenbin} ${argsdir}/${testid} ${fuzzedtestscriptdir}/${testid} ${fuzznum} $PROGNAME ${fuzztestscriptgensrcf} >compilescript.sh
    chmod 700 compilescript.sh
    #============
    #cat compilescript.sh
    #============    
    ./compilescript.sh   #This generates the wrapper program named $PROGNAME
    rm compilescript.sh


    ### Execute the wrapper against test inputs（to generate fuzzed testscripts)
    BIN=${CURRDIR}/$PROGNAME
    cd $CURRDIR/tmp
    echo "${testf} $BIN $CURRDIR/tmp 10 ${indir}"
    ${testf} $BIN $CURRDIR/tmp 10 ${indir}
    chmod 755 -R $CURRDIR/tmp
    rm -fr $CURRDIR/tmp/*

done


cd ${CURRDIR}
#Backup newly generated args
if [ -d ${argsbackupdir} ]; then
    rm -fr ${argsbackupdir}
fi
cp -r ${argsdir} ${argsbackupdir}

#Chmod for newly generated test scripts
chmod -R 700 ${fuzzedtestscriptdir}
