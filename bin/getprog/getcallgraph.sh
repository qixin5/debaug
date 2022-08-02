#!/bin/bash

PROGNAME=$1

if [ -z $PROGNAME ]; then
    echo "Missing arguments."
    exit 1
fi

if [ ! -d func/info ]; then
    mkdir -p func/info
fi

if [ ! -f func/info/callgraph.dot ]; then
    cd func/info
    clang -S -emit-llvm ../../src/origin/$PROGNAME.c -w -o - | opt -analyze -dot-callgraph
fi

