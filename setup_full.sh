#!/bin/bash

CURRDIR=$(pwd)

if [ ! -d llvm-project ]; then

    echo "Build LLVM. This can take a few hours!"
    
    git clone https://github.com/llvm/llvm-project.git
    cd llvm-project
    git checkout d8b01111
    mkdir build
    cd build
    cmake -G "Unix Makefiles" -DLLVM_ENABLE_PROJECTS='clang;clang-tools-extra;llvm' ../llvm
    make -j 4
    
fi


export PATH="${CURRDIR}/llvm-project/build/bin:${PATH}"

echo "Build instrumenter"
cd $CURRDIR
if [ -d build ]; then
   rm -fr build
fi
mkdir build

cd build && cmake .. && make


echo "Build debdce"
cd $CURRDIR/debdce
if [ -d build ]; then
   rm -fr build
fi
mkdir build

cd build && cmake .. && make


echo "Move debdce binary"
cd $CURRDIR
mv debdce/build/bin/debdce build/bin/
rm -fr debdce/build


echo "Compile Java code"
./compile_java
