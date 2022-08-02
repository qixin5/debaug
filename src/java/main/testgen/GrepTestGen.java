package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

//This class is tailored to the syntax of file whose path is origin_test_fpath.
public class GrepTestGen
{
    public static void main(String[] args) {
	String origin_test_fpath = "/home/qxin6/debaug_expt/debaug/benchmark/grep-2.4.2_template/testscript/test_all.universe.sh";
	
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
	String old_bin_path = "/home/qxin6/debaug_expt/debaug/benchmark/grep-2.4.2_template/grep-2.4.2";
	String old_input_path0 = "../inputs";
	String old_input_path1 = "/home/qxin6/debaug_expt/debaug/working/grep/inputs";
	String old_input_path2 = "/home/qxin6/debaug_expt/debaug/working/grep/inputs/../inputs";
	String old_output_path = "/home/qxin6/debaug_expt/debaug/working/grep/outputs";


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
	sb.append("timeout -k 9 ${TIMEOUT}s ");

	String cmd0 = origin_test_flines.get(sl+1).trim();
	cmd0 = cmd0.replace(old_bin_path, "$BIN");
	cmd0 = cmd0.replace(old_input_path2+"/", "$INDIR/");  //Note the ordering
	cmd0 = cmd0.replace(old_input_path1+"/", "$INDIR/");
	cmd0 = cmd0.replace(old_input_path0+"/", "$INDIR/");	
	cmd0 = cmd0.replace(old_output_path+"/t"+testid, "$OUTDIR/o"+newtestid);

	sb.append(cmd0+"\n\n");
	
	if (!"".equals(origin_test_flines.get(sl+2).trim())) {
	    System.err.println("Unknown line: " + origin_test_flines.get(sl+2));
	}
	
	return sb.toString();
    }
}
