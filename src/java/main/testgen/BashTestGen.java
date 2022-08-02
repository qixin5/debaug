package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class BashTestGen
{
    public static void main(String[] args) {
	File torun_scripts_file = new File(args[0]);
	String output_dpath = args[1];

	List<String> scriptnames = null;
	try { scriptnames = FileUtils.readLines(torun_scripts_file); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (scriptnames == null) { return; }

	int counter = 0;
	for (String scriptname : scriptnames) {
	    String script_ctnt = getScriptCtnt(scriptname, counter);
	    try { FileUtils.writeStringToFile(new File(output_dpath+"/"+(counter++)), script_ctnt); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
    }

    //Generate a perl script
    private static String getScriptCtnt(String scriptname, int newtestid) {
	String testing_dpath = "/home/qxin6/debaug_expt/debaug/working/bash/testplans.alt/testplans.fine";
	
	StringBuilder sb = new StringBuilder();
	sb.append("#!/usr/bin/perl -w\n\n");

	sb.append("use File::Basename;\n\n");
	
	sb.append("$scriptName = \"" + scriptname + "\";\n");
	sb.append("$TEST_DIR = \"" + testing_dpath + "\";\n\n");

	sb.append("$BIN = $ARGV[0];\n");
	sb.append("$OUT_DIR = $ARGV[1];\n");
	sb.append("$TIMEOUT = $ARGV[2];\n");

	sb.append("$SCRIPT_FILE = \"testfile\";\n");

	sb.append("$ENV{BASH_SRC_DIR} = dirname($BIN);\n");
	sb.append("$ENV{TESTS_SRC} = $TEST_DIR;\n");
	sb.append("$ENV{SHLVL} = 4;\n\n");

	sb.append("close(STDERR);\n\n");
	
	sb.append("@fileParts = split(/\\");
	sb.append("./, $scriptName);\n");
	sb.append("`cd $TEST_DIR && cat setup$fileParts[1] $scriptName cleanup$fileParts[2] > $TEST_DIR/$SCRIPT_FILE && chmod 700 $TEST_DIR/$SCRIPT_FILE`;\n\n");

	sb.append("$outfile = \"$OUT_DIR/o"+newtestid+"\";\n\n");

	sb.append("`cd $TEST_DIR`;\n");	
	sb.append("`timeout -k 9 ${TIMEOUT}s $BIN $TEST_DIR/$SCRIPT_FILE > $outfile`;\n");

	return sb.toString();
    }
}
