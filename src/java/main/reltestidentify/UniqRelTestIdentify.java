package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class UniqRelTestIdentify
{
    public static boolean isRelated(File train_f, File test_f) {
	String op_str0 = getOptionString(train_f);
	String op_str1 = getOptionString(test_f);
	if (op_str0 == null) {
	    System.err.println("Failed to get option string: " + train_f.getPath());
	    return false;
	}
	if (op_str1 == null) {
	    System.err.println("Failed to get option string: " + test_f.getPath());
	    return false;
	}
	return op_str0.equals(op_str1);
    }

    private static String getOptionString(File target_f) {
	List<String> target_flines = null;
	try { target_flines = FileUtils.readLines(target_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (target_flines == null) { return null; }

	Set<String> option_flags = new HashSet<String>();
	
	for (String target_fline : target_flines) {
	    target_fline = target_fline.trim();
	    if (target_fline.contains("$BIN")) { //No "timeout -k" because there are cases where the utility is invoked from a script (run.sh)
		String core_part = RelTestIdentifyUtil.getCorePart(target_fline);

		//Remove strings that are quoted (by '' or by "")
		core_part = RelTestIdentifyUtil.filterQuotedParts(core_part, '\'');
		core_part = RelTestIdentifyUtil.filterQuotedParts(core_part, '\"');
		
		for (String elem : core_part.split("\\s+")) {
		    if (elem.startsWith("--")) { //Below are all long options used
			if (elem.startsWith("--group")) {
			    option_flags.add("--group");
			}
			else if (elem.startsWith("--all-repeated")) {
			    option_flags.add("--all-repeated");
			}
		    }
		    else if (elem.startsWith("-") && !elem.startsWith("-o${CURRDIR}")) {
			//Filter non-characters (as they are used as option values)        
			//E.g., -f1, -s2
			String elem_filtered = "-";
			for (int i=1; i<elem.length(); i++) {
			    if (('a' <= elem.charAt(i) && elem.charAt(i) <= 'z') ||
				('A' <= elem.charAt(i) && elem.charAt(i) <= 'Z')) {
				elem_filtered += elem.charAt(i);
			    }
			}
			option_flags.add(elem_filtered);
		    }
		}
		break;
	    }
	}

	//-frq => -f, -r, & -q
	option_flags = RelTestIdentifyUtil.splitMultiFlags(option_flags);
        List<String> option_flags_list = new ArrayList<String>(option_flags);
        Collections.sort(option_flags_list); //Sort the option flags

	String rslt = "";
	for (String option_flag : option_flags_list) {
            if ("".equals(rslt)) { rslt = option_flag; }
            else { rslt += " " + option_flag; }
	}
        return rslt;
    }
}
