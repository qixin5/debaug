package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;


public class RelTestIdentifyUtil
{
    private static Pattern numflagpat = Pattern.compile("-\\d+");
    private static Pattern redoutputpat = Pattern.compile("\\&?\\>\\s*\\$OUTDIR");

    public static String filterQuotedParts(String str, char quote_char) {
	String last_str = str;
	while(true) {
	    String new_str = new String(last_str);

	    //Identify the starting and ending quotes
	    int dquote_sidx = -1, dquote_eidx = -1;
	    for (int i=0; i<new_str.length(); i++) {
		if (new_str.charAt(i) == quote_char) {
		    if (i == 0 || (i > 0 && new_str.charAt(i-1) != '\\')) {
			if (dquote_sidx == -1) {  dquote_sidx = i; }
			else if (dquote_eidx == -1) { dquote_eidx = i; }
		    }
		}

		if (dquote_sidx != -1 && dquote_eidx != -1) {
		    break;
		}
	    }

	    //Remove the quoted part
	    if (dquote_sidx != -1 && dquote_eidx != -1) {
		new_str = new_str.substring(0, dquote_sidx) + new_str.substring(dquote_eidx+1);
	    }

	    
	    if (new_str.equals(last_str)) {
		break;
	    }
	    else {
		last_str = new_str; //Need further removal, update last_str as new_str.
	    }
	}
	return last_str;
    }

    //Check if the flag is something like "-5"
    public static boolean isNumberFlag(String flag) {
	if (flag == null) { return false; }
	else { return numflagpat.matcher(flag).matches(); }
    }

    public static List<String> filterNumberFlags(List<String> flag_list) {
	List<String> new_flag_list = new ArrayList<String>();
	for (String flag : flag_list) {
	    if (!isNumberFlag(flag)) {
		new_flag_list.add(flag);
	    }
	}
	return new_flag_list;
    }

    //For example, split "-frv" into "-f", "-r", and "-v"
    public static Set<String> splitMultiFlags(Set<String> flags) {
	Set<String> new_flags = new HashSet<String>();
	for (String flag : flags) {
	    if (flag.startsWith("--")) {
		new_flags.add(flag);
	    }
	    else if (flag.startsWith("-")) {
		for (int i=1; i<flag.length(); i++) {
		    new_flags.add("-" + flag.charAt(i));
		}
	    }
	}
	return new_flags;
    }

    public static String getCorePart(String bin_line) {
	int end_idx = -1;
	Matcher matcher = redoutputpat.matcher(bin_line);
	if (matcher.find()) { end_idx = matcher.start(); }
	if (end_idx == -1) {
	    return bin_line.substring(bin_line.indexOf("$BIN")+4).trim();
	}
	else {
	    return bin_line.substring(bin_line.indexOf("$BIN")+4, end_idx).trim();	    
	}
    }
}
