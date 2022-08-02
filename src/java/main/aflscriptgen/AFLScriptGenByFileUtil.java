package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class AFLScriptGenByFileUtil
{
    public static String getAFLScript(File test_f, String progname, String input_origin_dpath) {
	StringBuilder rslt_sb = new StringBuilder();
	rslt_sb.append("#!/bin/bash\n\n");
	rslt_sb.append("PROGNAME=" + progname + "\n");
	rslt_sb.append("AFLFUZZ=/home/qxin6/afl-2.52b/afl-fuzz\n");
	rslt_sb.append("CURRDIR=$(pwd)\n\n");

	if (progname.equals("make-3.79")) { //Add its specific code for setup
	    rslt_sb.append(getSetupScriptForMake(test_f, input_origin_dpath));
	}

	rslt_sb.append("AFL_SKIP_CPUFREQ=1 $AFLFUZZ -i $CURRDIR/afl-in -o $CURRDIR/afl-out $CURRDIR/$PROGNAME " + getArgStringForAFLScript(test_f));

	return rslt_sb.toString();
    }

    public static String getSetupScriptForMake(File test_f, String input_origin_dpath) {
	StringBuilder rslt_sb = new StringBuilder();

	List<String> test_flines = null;
	try { test_flines = FileUtils.readLines(test_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (test_flines == null) { return rslt_sb.toString(); }
	
	int set_sidx = -1, bin_idx = -1;
	for (int i=0; i<test_flines.size(); i++) {
	    if (test_flines.get(i).trim().equals("CURRDIR=$(pwd)")) {
		set_sidx = i+1;
	    }
	    else if (test_flines.get(i).contains("$BIN")) {
		bin_idx = i;
	    }
	}
	if (set_sidx != -1 && bin_idx != -1) {
	    for (int i=set_sidx; i<bin_idx; i++) {
		rslt_sb.append(test_flines.get(i).replace("$INDIR/", input_origin_dpath+"/")+"\n");
	    }
	}
	return rslt_sb.toString();
    }

    public static String getArgStringForAFLScript(File test_f) {
	List<String> test_flines = null;
        try { test_flines = FileUtils.readLines(test_f, (String) null); }
        catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
        if (test_flines == null) { return null; }

        for (String test_fline : test_flines) {
            test_fline = test_fline.trim();
            if (test_fline.contains("$BIN")) { //Find the bin line
		return getArgStringForAFLScript(test_fline);
	    }
	}

	return null;
    }

    public static String getInputFileName(File test_f) {
	List<String> test_flines = null;
        try { test_flines = FileUtils.readLines(test_f, (String) null); }
        catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
        if (test_flines == null) { return null; }

        for (String test_fline : test_flines) {
            test_fline = test_fline.trim();
            if (test_fline.contains("$BIN")) { //Find the bin line
		return getInputFileName(test_fline);
	    }
	}

	return null;
    }
        

    //For stdin-input, replace "< $INDIR/XXX" with empty.
    //For others, replace the first input-file argument with "@@" and the remaining with EMPTY
    public static String getArgStringForAFLScript(String bin_line) {
	String arg_str = AFLScriptGenUtil.getCorePart(bin_line);
	StringBuilder rslt_sb = new StringBuilder();

	boolean first_input = true;
	for (int i=0; i<arg_str.length(); i++) {
	    if (arg_str.charAt(i) == '$' &&
		(i+6 < arg_str.length()) &&
		arg_str.charAt(i+1) == 'I' &&
		arg_str.charAt(i+2) == 'N' &&
		arg_str.charAt(i+3) == 'D' &&
		arg_str.charAt(i+4) == 'I' &&
		arg_str.charAt(i+5) == 'R' &&
		arg_str.charAt(i+6) == '/') {

		//Check if the input file is provided as stdin
		boolean is_stdin = false;
		for (int k=i-1; k>=0; k--) {
		    if (Character.isWhitespace(arg_str.charAt(k))) {
			continue;
		    }
		    else {
			if (arg_str.charAt(k) == '<') {
			    is_stdin = true;
			    rslt_sb = new StringBuilder(rslt_sb.substring(0, rslt_sb.length()-(i-k))); //Remove characters added from "<"
			}
			break;
		    }
		}
		
		//Remove the preceding " or '
		if (i-1 > 0 && (arg_str.charAt(i-1) == '"' || arg_str.charAt(i-1) == '\'')) {
		    rslt_sb = new StringBuilder(rslt_sb.substring(0, rslt_sb.length()-1));
		}
		

		//Find the ending
		int j;
		for (j=i+1; j<arg_str.length(); j++) {
		    if (Character.isWhitespace(arg_str.charAt(j))) {
			//Find the ending index for this input argument
			break;
		    }
		}
		i = j; //Move the cursor


		//Process the input
		if (first_input) {
		    if (!is_stdin) { rslt_sb.append("@@ "); }
		    else { rslt_sb.append(" "); }
		    first_input = false;
		}
		else {
		    rslt_sb.append(" ");
		}
	    }
	    else {
		rslt_sb.append(arg_str.charAt(i));
	    }
	}

	return rslt_sb.toString();
    }


    public static String getInputFileName(String bin_line) {
	String arg_str = AFLScriptGenUtil.getCorePart(bin_line);
	StringBuilder rslt_sb = new StringBuilder();

	for (int i=0; i<arg_str.length(); i++) {
	    if (arg_str.charAt(i) == '$' &&
		(i+6 < arg_str.length()) &&
		arg_str.charAt(i+1) == 'I' &&
		arg_str.charAt(i+2) == 'N' &&
		arg_str.charAt(i+3) == 'D' &&
		arg_str.charAt(i+4) == 'I' &&
		arg_str.charAt(i+5) == 'R' &&
		arg_str.charAt(i+6) == '/') {

		for (int j=i+7; j<arg_str.length(); j++) {
		    if (Character.isWhitespace(arg_str.charAt(j))) {
			//Find the ending index for this input argument
			break;
		    }
		    rslt_sb.append(arg_str.charAt(j));
		}

		break; //Consider only ONE input file
	    }
	}

	String rslt = rslt_sb.toString();
	if (rslt.endsWith("\"") || rslt.endsWith("'")) {
	    rslt = rslt.substring(0, rslt.length()-1);
	}
	return rslt;
    }
}
