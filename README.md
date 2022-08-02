# Coverage-based and Augmentation-based Debloating Techniques

This repo contains source code and running scripts for three debloating techniques: Cov, CovF, and CovA. Cov is a coverage-based debloating technique. CovF and CovA work essentially by using Cov to produce a reduced program first and then performing different augmentation strategies for program generality enhancement. CovF performs fuzz-based augmentation, and CovA performs analysis-based augmentation. 

## Installation

### Requirements

Before installing the debloating tools, please first make sure the following software is installed.

- GCC (version >= 7.5)
- Clang (version >= 9.0)
- CMake (version >= 3.10.2)
- Java (version >= 1.8)
- gdown (version >= 4.5.1)
- [Radamsa](https://gitlab.com/akihe/radamsa.git) (version >= 0.6)


### Build

```
git clone https://github.com/qixin5/debaug
cd debaug
./setup.sh
```

Make sure that DEBAUG_DIR has the correct value.
```
export DEBAUG_DIR=[debaug directory]
```

## Quick Example

Here is a quick example.

To run it, first make sure you are in `debaug`, and then do the following:
```
cd example/toy
./run_cov.sh
```

After this, you should see a file `toy.reduced.c` whose content is the same as what's shown below.
```
int main(int argc , char **argv)
{
  int x = atoi(*(argv + 1));
  if (x >= 0) {
    printf("x >= 0\n");
  }
  else {

  }
}
```

## Usage

### Preparation of a work directory

Please first create a directory containing the program's source code, the compiling code, and the testing scripts serving as the debloating inputs. To facilitate this, we provide [workdir](resource/workdir). You can copy it to wherever you want. Get into the directory. Copy the original program's source code to `src/origin/`. Copy a directory of testing scripts (i.e., the inputs) to `testscript/`. And modify `compile.sh` if needed.

### Debloating

First make sure you're in the work directory (which could be adapted from workdir for example). 

Next make sure DEBAUG_DIR is properly set. Run `echo ${DEBAUG_DIR}` and check the output. If you need to set DEBAUG_DIR, run `export DEBAUG_DIR=[debaug directory]`.

To use Cov, run 
```
${DEBAUG_DIR}/bin/getprog/getprog_cov.sh PROGNAME INPUTSET
```
where PROGNAME is the name of the program and INPUTSET is the name of the directory holding the input testing scripts.

To use CovF, run
```
${DEBAUG_DIR}/bin/getprog/getprog_covf.sh PROGNAME INPUTSET
```

To use CovA, run
```
${DEBAUG_DIR}/bin/getprog/getprog_cova.sh PROGNAME INPUTSET THRESHOLD
```
where THRESHOLD, which denotes the augmentation threshold, is an integer between 1 and 100 (inclusive).

When it's done, check out `src/reduced` in the work directory for the debloated code.


