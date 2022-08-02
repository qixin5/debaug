#!/bin/bash

target_dir=$1 #Pass as a copy

radamsa_dir(){
    tar=$1
    if [ -f ${tar} ]; then
	size=`ls -l ${tar} | cut -d' ' -f5`
	#Generate a random char, and save it in the file
	if [ ${size} -eq 0 ]; then
	    rchar=`cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 1 | head -n 1`
	    echo ${rchar} >${tar}
	fi
	#Fuzz the file
	echo `radamsa ${tar}` > ${tar}
    elif [ -d ${tar} ]; then
	for subtar in ${tar}/*
	do
	    radamsa_dir ${subtar}
	done
    fi
}


radamsa_dir ${target_dir}
