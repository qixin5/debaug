package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

//This class is tailored to the syntax of file whose path is origin_test_fpath.
public class TcasTestGen
{
    public static void main(String[] args) {
	String origin_test_fpath = "/home/qxin6/debaug_expt/debaug/benchmark/tcas_template/testscript/universe";
	
	File torun_scripts_file = new File(args[0]);
	String output_dpath = args[1];


	//Read original test script
	List<String> origin_test_flines = null;
	try { origin_test_flines = FileUtils.readLines(new File(origin_test_fpath)); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (origin_test_flines == null) { return; }


	//Read test ids selected to produce tests
	List<String> testids = null;
	try { testids = FileUtils.readLines(torun_scripts_file); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (testids == null) { return; }

	
	//Get script ctnt and write to file
	int counter = 0;
	for (String testid : testids) {
	    String script_ctnt = getScriptCtnt(origin_test_flines, testid, counter);
	    try { FileUtils.writeStringToFile(new File(output_dpath+"/"+(counter++)), script_ctnt); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
    }


    //Note: testid should represent a number that starts from 1
    private static String getScriptCtnt(List<String> origin_test_flines, String testid, int newtestid) {
	StringBuilder sb = new StringBuilder();
	sb.append("#!/bin/bash\n\n");
	sb.append("BIN=$1\nOUTDIR=$2\nTIMEOUT=$3\nINDIR=$4\n\n");
	sb.append("timeout -k 9 ${TIMEOUT}s $BIN");

	String cmd0 = origin_test_flines.get(Integer.parseInt(testid)-1).trim();
	sb.append(" " + cmd0);
	sb.append(" &> $OUTDIR/o" + newtestid);
	sb.append("\n\n");
	
	return sb.toString();
    }
    
}
