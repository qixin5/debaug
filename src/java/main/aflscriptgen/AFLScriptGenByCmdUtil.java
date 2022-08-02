package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class AFLScriptGenByCmdUtil
{    
    public static String getAFLScript(File test_f, String progname, String input_origin_dpath) {
	StringBuilder rslt_sb = new StringBuilder();

        rslt_sb.append("#!/bin/bash\n\n");
        rslt_sb.append("PROGNAME=" + progname + "\n");
        rslt_sb.append("AFLFUZZ=/home/qxin6/afl-2.52b/afl-fuzz\n");
        rslt_sb.append("CURRDIR=$(pwd)\n\n");
	rslt_sb.append("AFL_SKIP_CPUFREQ=1 $AFLFUZZ -i $CURRDIR/afl-in -o $CURRDIR/afl-out $CURRDIR/$PROGNAME @@");

	return rslt_sb.toString();
    }

    public static String getArgString(File test_f) {
	List<String> test_flines = null;
	try { test_flines = FileUtils.readLines(test_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (test_flines == null) { return null; }

	for (String test_fline : test_flines) {
            test_fline = test_fline.trim();
            if (test_fline.contains("$BIN")) { //Find the bin line
		return AFLScriptGenUtil.getCorePart(test_fline);
	    }
	}

	return null;
    }    
}


