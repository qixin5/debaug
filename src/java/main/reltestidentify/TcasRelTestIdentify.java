package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class TcasRelTestIdentify
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
	    if (target_fline.contains("$BIN")) {
		String core_part = RelTestIdentifyUtil.getCorePart(target_fline);

		//Remove strings that are quoted (by '' or by "")
		core_part = RelTestIdentifyUtil.filterQuotedParts(core_part, '\'');
		core_part = RelTestIdentifyUtil.filterQuotedParts(core_part, '\"');
		
		for (String elem : core_part.split("\\s+")) {
		    if (elem.startsWith("-") && !elem.startsWith("-o${CURRDIR}")) {
			option_flags.add(elem);
		    }
		}
		
		break;
	    }
	}

	//-frq => -f, -r, & -q
        option_flags = RelTestIdentifyUtil.splitMultiFlags(option_flags);
	List<String> option_flags_list = new ArrayList<String>(option_flags);
	option_flags_list = RelTestIdentifyUtil.filterNumberFlags(option_flags_list); //Do no use negative numbers as option flags
	Collections.sort(option_flags_list); //Sort the option flags

	String rslt = "";
	for (String option_flag : option_flags_list) {
	    if ("".equals(rslt)) { rslt = option_flag; }
	    else { rslt += " " + option_flag; }
	}
	return rslt;
    }
}
