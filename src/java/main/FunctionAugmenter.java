package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class FunctionAugmenter
{
    public static void main(String[] args) {
	File target_funcf = new File(args[0]);
	File target_codef = new File(args[1]);
	File linef = new File(args[2]);
	File origin_codef = new File(args[3]);

	List<String> target_funcs = null;
	try { target_funcs = FileUtils.readLines(target_funcf, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (target_funcs == null) { return; }

	System.out.println(getAugmentedCode(target_codef, origin_codef, linef, target_funcs));
    }

    //function_names is a list of functions to be augmented
    public static String getAugmentedCode(File target_codef, File origin_codef, File linef, List<String> function_names) {
	Map<String, String> lstrs = LineFileParser.getFunctionNameStartEndLineMap(linef);
	Map<Integer, Integer> lmap = new HashMap<Integer,Integer>();
	for (String function_name : function_names) {
	    String lstr = lstrs.get(function_name);
	    if (lstr == null) {
		System.out.println("Function name not found: " + function_name);
		continue;
	    }
	    int start_line = Integer.parseInt(lstr.split(",")[0]);
	    int end_line = Integer.parseInt(lstr.split(",")[1]);
	    lmap.put(start_line, end_line);
	}

	return getAugmentedCode(target_codef, origin_codef, lmap);
    }
    
    //lmap marks the starting and ending lines of functions to be augmented.
    public static String getAugmentedCode(File target_codef, File origin_codef, Map<Integer, Integer> lmap) {
	List<String> target_cflines = null;
	try { target_cflines = FileUtils.readLines(target_codef, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (target_cflines == null) { return null; }

	List<String> origin_cflines = null;
	try { origin_cflines = FileUtils.readLines(origin_codef, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (origin_cflines == null) { return null; }
	
	StringBuilder sb = null;
	int target_cflines_size = target_cflines.size();
	for (int i=0; i<target_cflines_size; i++) {
	    if (lmap.get(i+1) == null) {
		if (sb == null) { sb = new StringBuilder(); }
		else { sb.append("\n"); }
		sb.append(target_cflines.get(i));
	    }
	    else {
		//Starting line of a function to be augmented
		int j = lmap.get(i+1).intValue() - 1;
		for (int k=i; k<=j; k++) {
		    if (sb == null) { sb = new StringBuilder(); }
		    else { sb.append("\n"); }
		    sb.append(origin_cflines.get(k));
		}
		i = j;
	    }
	}

	return (sb == null) ? null : sb.toString();
	
    }
}
