#!/bin/bash

binarylink="https://drive.google.com/uc?id=1oo5zTRL6PokZEHvmkDPHZbZkVlcWsCy7"

gdown ${binarylink}
unzip debaug_binary.zip
./compile_java
rm debaug_binary.zip
