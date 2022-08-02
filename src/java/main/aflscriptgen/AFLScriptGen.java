package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class AFLScriptGen
{
    public static void main(String[] args) {
	File testf = new File(args[0]);
	String progname = args[1];
	String output_fuzzdpath = args[2];
	String input_origin_dpath = "/home/qxin6/debaug_expt/debaug/benchmark/"+progname+"_template/input.origin/all";

	File run_afl_f = new File(output_fuzzdpath + "/run_afl");
	String afl_script_ctnt = null;
	

	//By-command fuzzing
	if (progname.equals("tcas")) {
	    //Produce AFL script and write it to file named "run_afl" later	    
	    afl_script_ctnt = AFLScriptGenByCmdUtil.getAFLScript(testf, progname, input_origin_dpath);
	    if (afl_script_ctnt == null) {
		System.err.println("Failed to produce AFL script: " + testf.getPath());
		return;
	    }
	    
	    
	    //Get the test argument string and write it to file
	    File target_dir = new File(output_fuzzdpath+"/afl-in");
	    if (!target_dir.exists()) { target_dir.mkdir(); }

	    String test_arg_str = AFLScriptGenByCmdUtil.getArgString(testf);

	    if (test_arg_str != null) {
		File input_f = new File(output_fuzzdpath+"/afl-in/argfile");
		try { FileUtils.writeStringToFile(input_f, test_arg_str); }
		catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    }
	    else {
		System.err.println("Failed to obtain the input arguments: " + testf.getPath());
	    }
	}
	

	//By-file fuzzing
	else {
	    //Produce AFL script and write it to file named "run_afl" later
	    afl_script_ctnt = AFLScriptGenByFileUtil.getAFLScript(testf, progname, input_origin_dpath);
	    if (afl_script_ctnt == null) {
		System.err.println("Failed to produce AFL script: " + testf.getPath());
		return;
	    }
	    

	    //Copy input file to afl-in
	    String input_fname = AFLScriptGenByFileUtil.getInputFileName(testf);
	    if (input_fname != null && !input_fname.trim().equals("")) { //Otherwise, there may be no input associated
		//Handle special cases
		if ("grep-2.4.2".equals(progname) && "grep*.dat".equals(input_fname)) {
		    input_fname = "grep0.dat"; //Wildcarded name concretized
		}
		else if ("space".equals(progname)) {
		    input_fname = input_fname + ".adl";
		}
		
		//==========
		//System.err.println("Check this: " + input_fname);
		//==========	    
		
		File input_f = new File(input_origin_dpath+"/"+input_fname);
		if (input_f.exists()) {
		    File target_dir = new File(output_fuzzdpath+"/afl-in");
		    if (!target_dir.exists()) { target_dir.mkdir(); }
		    
		    //Used true to preserve date
		    try { FileUtils.copyFileToDirectory(input_f, target_dir, true); }
		    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
		}
		else {
		    System.err.println("File does not exist: " + input_f.getPath());
		    System.err.println("Check test file: " + testf.getPath());
		}
	    }
	}
	

	try { FileUtils.writeStringToFile(run_afl_f, afl_script_ctnt); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
    }
}
