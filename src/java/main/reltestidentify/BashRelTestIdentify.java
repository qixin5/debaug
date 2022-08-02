package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class BashRelTestIdentify
{
    public static boolean isRelated(File train_f, File test_f) {
	String func_str0 = getFunctionalityString(train_f);
	String func_str1 = getFunctionalityString(test_f);
	if (func_str0 == null) {
	    System.err.println("Failed to get the functionality string: " + train_f.getPath());
	    return false;
	}
	if (func_str1 == null) {
	    System.err.println("Failed to get the functionality string: " + test_f.getPath());
	    return false;
	}
	return func_str0.equals(func_str1);
    }

    private static String getFunctionalityString(File target_f) {
	List<String> target_flines = null;
	try { target_flines = FileUtils.readLines(target_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (target_flines == null) { return null; }

	for (String target_fline : target_flines) {
	    target_fline = target_fline.trim();
	    if (target_fline.startsWith("$scriptName =")) {
		String tmp_str = target_fline.substring(target_fline.indexOf("=")+1, target_fline.indexOf(";")).trim();
		int sidx = tmp_str.startsWith("\"") ? 1 : 0;
		int eidx = tmp_str.indexOf("#");
		if (eidx == -1) { return null; }
		return tmp_str.substring(sidx, eidx);
	    }
	}
	return null;
    }
}
