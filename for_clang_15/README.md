This folder contains the modifications needed to build this project with Clang 15 (the original code are designed for Clang 9).

To achieve this, you need to follow these steps:

1. Apply the modifications in this folder by copying the files to the corresponding locations.
2. Follow the instructions below to build the project.

```bash
# Environment setup
sudo apt-get install -y libclang-15-dev llvm-15 llvm-15-dev clang-15
sudo ln -s /usr/bin/clang-15 /usr/bin/clang
sudo ln -s /usr/bin/clang++-15 /usr/bin/clang++
sudo ln -s /usr/bin/llvm-config-15 /usr/bin/llvm-config

# Build the project
cd debaug
mkdir build
cd build && cmake .. && make

cd ../debdce
mkdir build
cd build && cmake .. && make
cd ../..
mv debdce/build/bin/debdce build/bin/
rm -fr debdce/build

./compile_java
```
