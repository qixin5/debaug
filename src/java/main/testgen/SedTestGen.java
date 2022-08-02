package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

//This class is tailored to the syntax of file whose path is origin_test_fpath.
public class SedTestGen
{
    public static void main(String[] args) {
	String origin_test_fpath = "/home/qxin6/debaug_expt/debaug/benchmark/sed-4.1.5_template/testscript/test_all.universe.sh";
	
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
	String old_bin_path = "/home/qxin6/debaug_expt/debaug/benchmark/sed-4.1.5_template/sed-4.1.5";
	String old_input_path0 = "../inputs";
	String old_output_path0 = "/home/qxin6/debaug_expt/debaug/working/sed/outputs";


	int flines_size = origin_test_flines.size();
	
	int sl = -1;
	for (int i=0; i<flines_size; i++) {
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
	//sb.append("CURRDIR=$(pwd)\n\n");


	for (int j=sl+1; j<flines_size; j++) {
	    String origin_test_fline = origin_test_flines.get(j).trim();
	    if ("".equals(origin_test_fline)) {
		if (j+1<flines_size &&
		    origin_test_flines.get(j+1).startsWith("echo \">>>>>>>>")) { break;	}
		else { continue; }
	    }

	    if (origin_test_fline.startsWith("#")) { continue; } //Omit comments

	    //Bin executing line
	    if (origin_test_fline.startsWith(old_bin_path)) {
		String cmd0 = origin_test_fline;
		cmd0 = cmd0.replace(old_bin_path, "$BIN");
		cmd0 = cmd0.replace(old_input_path0+"/", "$INDIR/");
		cmd0 = cmd0.replace(old_output_path0+"/t"+testid, "$OUTDIR/o"+newtestid);

		//Fix quotes by changing single into double for input path
		int sidx = cmd0.indexOf("\'$INDIR/");
		if (sidx != -1) {
		    int eidx = cmd0.indexOf("\'", sidx+1);
		    if (eidx == -1) {
			System.err.println("Why is ending quote missing? " + cmd0);
		    }
		    else {
			StringBuilder sb_tmp = new StringBuilder(cmd0);
			sb_tmp.setCharAt(sidx, '\"');
			sb_tmp.setCharAt(eidx, '\"');
			cmd0 = sb_tmp.toString();
		    }
		}
		
		sb.append("timeout -k 9 ${TIMEOUT}s ");
		sb.append(cmd0+"\n\n");
	    }

	    //Result moving line
	    else if (origin_test_fline.startsWith("mv ")) {
		String[] cmds = origin_test_fline.split("\\s+");
		sb.append("cat ");
		sb.append(cmds[1]);
		sb.append(" >> ");
		sb.append("$OUTDIR/o"+newtestid);
		sb.append("\n");
	    }

	    else {
		System.err.println("Unknown line: " + origin_test_fline);
	    }
	}
	
	return sb.toString();
    }
    
}
