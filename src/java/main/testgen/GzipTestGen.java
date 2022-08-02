package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

//This class is tailored to the syntax of file whose path is origin_test_fpath.
public class GzipTestGen
{
    public static void main(String[] args) {
	String origin_test_fpath = "/home/qxin6/debaug_expt/debaug/benchmark/gzip-1.3_template/testscript/test_all.universe.sh";
	
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


    
    private static String getScriptCtnt(List<String> origin_test_flines, String testid, int newtestid) {
	String old_bin_path = "/home/qxin6/debaug_expt/debaug/benchmark/gzip-1.3_template/gzip-1.3";
	String old_input_path0 = "../inputs";
	String old_input_path1 = "/home/qxin6/debaug_expt/debaug/working/gzip/inputs";
	String old_output_path = "/home/qxin6/debaug_expt/debaug/working/gzip/outputs";
	String old_utilscript_path = "/home/qxin6/debaug_expt/debaug/working/gzip/testplans.alt/testscripts";
	String new_utilscript_path = "/home/qxin6/debaug_expt/debaug/benchmark/gzip-1.3_template/testscript/util";


	int sl = -1;
	for (int i=0; i<origin_test_flines.size(); i++) {
	    String origin_test_fline = origin_test_flines.get(i).trim();
	    if (("echo \">>>>>>>>running test "+testid+"\"").equals(origin_test_fline)) {
		sl = i;
		break;
	    }
	}

	if (sl == -1) {
	    System.err.println("Didn't find test id: " + testid + " in the original test script.");
	    return null;
	}
	
	StringBuilder sb = new StringBuilder();
	sb.append("#!/bin/bash\n\n");
	sb.append("BIN=$1\nOUTDIR=$2\nTIMEOUT=$3\nINDIR=$4\n\n");
	
	if ("55".equals(testid)) {
	    String origin_test_fline = origin_test_flines.get(sl+1).trim();
	    sb.append(origin_test_fline.replace(old_utilscript_path, new_utilscript_path)+" $INDIR $OUTDIR/o"+newtestid+"\n\n");
	}

	sb.append("timeout -k 9 ${TIMEOUT}s ");

	String cmd0 = null;
	if ("55".equals(testid)) { cmd0 = origin_test_flines.get(sl+2).trim(); }
	else { cmd0 = origin_test_flines.get(sl+1).trim(); }
	cmd0 = cmd0.replace(old_bin_path, "$BIN");
	cmd0 = cmd0.replace(old_input_path0+"/", "$INDIR/");
	cmd0 = cmd0.replace(old_input_path1+"/", "$INDIR/");
	cmd0 = cmd0.replace(old_output_path+"/test"+testid, "$OUTDIR/o"+newtestid);
	sb.append(cmd0+"\n\n");

	String cmd1 = null;
	if ("55".equals(testid)) { cmd1 = origin_test_flines.get(sl+3).trim(); }
	else { cmd1 = origin_test_flines.get(sl+2).trim(); }

	if (cmd1.endsWith(".sh")) {
	    sb.append(cmd1.replace(old_utilscript_path, new_utilscript_path)+" $INDIR $OUTDIR/o"+newtestid+"\n");
	}
	else if (!"".equals(cmd1)) {
	    System.err.println("Unknown line: " + cmd1);
	}
	
	return sb.toString();
    }
}
