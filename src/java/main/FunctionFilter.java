package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class FunctionFilter
{
    public static void main(String[] args) {
	File old_rslt_f = new File(args[0]);
	File merged_cov_f = new File(args[1]);

	List<String> merged_cov_flines = null;
	try { merged_cov_flines = FileUtils.readLines(merged_cov_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (merged_cov_flines == null) { return; }

	Set<String> inconsistent_funcs = new HashSet<String>();

	//Obtain all inconsistent funcs (to filter)
	for (String merged_cov_fline : merged_cov_flines) {
	    merged_cov_fline = merged_cov_fline.trim();
	    if (merged_cov_fline.startsWith("function:") &&
		merged_cov_fline.split(",").length > 3 &&
		merged_cov_fline.split(",")[3].equals("inconsistent")) {

		String func = merged_cov_fline.split(",")[2];
		if (func.contains(":")) {
		    func = func.substring(func.indexOf(":")+1);
		}

		inconsistent_funcs.add(func);
	    }
	}

	List<String> old_rslt_flines = null;
        try { old_rslt_flines = FileUtils.readLines(old_rslt_f, (String) null); }
        catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
        if (old_rslt_flines == null) { return; }

	StringBuilder new_rslt_sb = null;
	
	for (String old_rslt_fline : old_rslt_flines) {
	    if (inconsistent_funcs.contains(old_rslt_fline.trim().split(",")[0])) {
		//Do not add this line
	    }
	    else {
		if (new_rslt_sb == null) { new_rslt_sb = new StringBuilder(); }
		else { new_rslt_sb.append("\n"); }
		new_rslt_sb.append(old_rslt_fline);
	    }
	}

	if (new_rslt_sb != null) {
	    System.out.println(new_rslt_sb.toString());
	}
    }
}
