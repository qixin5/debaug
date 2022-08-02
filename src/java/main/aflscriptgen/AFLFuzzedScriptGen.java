package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class AFLFuzzedScriptGen
{
    public static void main(String[] args) {
	File testf = new File(args[0]);
        String progname = args[1];
	String afl_fuzzed_inputfpath = args[2];  //Fuzzed file in afl-in
	File afl_fuzzed_inputf = new File(afl_fuzzed_inputfpath);
	String input_fname = args[3];            //Provide the original name for a copy of the fuzzed input file
	String outid = args[4];
	String rslt_fpath = args[5];             //File saving the result
       
	
        String input_origin_dpath = "/home/qxin6/debaug_expt/debaug/benchmark/"+progname+"_template/input.origin/all";

	String script_ctnt  = "#!/bin/bash\n\nBIN=$1\nOUTDIR=$2\nTIMEOUT=$3\nCURRDIR=$(pwd)\n\n";
	
	//By-command script
	if (progname.equals("tcas")) {
	    script_ctnt += "timeout -k 9 ${TIMEOUT}s $BIN ";

	    List<String> afl_fuzzed_inputflines = null;
	    try { afl_fuzzed_inputflines = FileUtils.readLines(afl_fuzzed_inputf, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    if (afl_fuzzed_inputflines == null) { return; }

	    //Omit the fuzzed input if it contains more than 1 line
	    if (afl_fuzzed_inputflines.size() > 1) { return; }

	    script_ctnt += afl_fuzzed_inputflines.get(0) + " &> $OUTDIR/o" + outid;
	}

	//By-file script
	else {	    
	    if (progname.equals("make-3.79")) {
		script_ctnt += AFLScriptGenByFileUtil.getSetupScriptForMake(testf, input_origin_dpath) + "\n";
	    }

	    //Copy the fuzzed input file	    
	    script_ctnt += "cp " + afl_fuzzed_inputfpath + " $CURRDIR/" + input_fname + "\n";

	    
	    //Produce input_fname_toshow
	    String input_fname_toshow = input_fname;
	    if (progname.equals("space") && input_fname_toshow.endsWith(".adl")) {
		input_fname_toshow = input_fname_toshow.substring(0, input_fname_toshow.length()-4);
	    }

	    //Produce a string of arguments using the afl-fuzzed input file
	    String argstr = AFLScriptGenByFileUtil.getArgStringForAFLScript(testf);
	    if (argstr.contains("@@")) {
		argstr = argstr.replace("@@", "$CURRDIR/" + input_fname_toshow);
	    }
	    else {
		argstr += " < $CURRDIR/" + input_fname_toshow; //Use input as stdin
	    }

	    script_ctnt += "timeout -k 9 ${TIMEOUT}s $BIN " + argstr + " &> $OUTDIR/o" + outid;
	}

	try { FileUtils.writeStringToFile(new File(rslt_fpath), script_ctnt); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
    }
}
