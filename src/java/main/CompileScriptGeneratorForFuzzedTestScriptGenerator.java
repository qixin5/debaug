package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.io.FileUtils;
    
/* It prints the linux command used for compiling the fuzzed testscript generator
   (whose src is given as fuzzedtestscriptgenerator_srcfpath), which generates 
   the fuzzed test script (fuzzed input) used later for obtaining fuzzing coverage.
*/

public class CompileScriptGeneratorForFuzzedTestScriptGenerator
{
    public static void main(String[] args) {
	String fuzzidpath = args[0]; //Fuzzed files saved
	String fuzzsdpath = args[1]; //Test script for fuzzing saved
	String fuzznum = args[2];    
	String progname = args[3];
	String fuzzedtestscriptgenerator_srcfpath = args[4];

	String s = "#!/bin/bash\n\n";
	s += "clang -DFUZZIDIR=\'\"" + fuzzidpath + "\"\'";
	s += " -DFUZZSDIR=\'\"" + fuzzsdpath + "\"\'";
	s += " -DFUZZNUM=" + fuzznum;
	if ("0".equals(fuzznum)) { s += " -DNOFUZZ"; }
	s += " -w -o " + progname;
	s += " " + fuzzedtestscriptgenerator_srcfpath;

	System.out.println(s);
    }
    
}
