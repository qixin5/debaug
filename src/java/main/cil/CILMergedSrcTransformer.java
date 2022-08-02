package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class CILMergedSrcTransformer
{
    public static void main(String[] args) {
	File srcf = new File(args[0]); //Original merged code
	File linef = new File(args[1]); //Line file

	Map<Integer,Integer> semap = LineFileParser.getStartEndLineMap(linef, "statement");

	List<String> srcflines = null;
	try { srcflines = FileUtils.readLines(srcf, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (srcflines == null) { return; }

	for (int i=0; i<srcflines.size(); i++) {
	    if (srcflines.get(i).trim().endsWith("else")) {
		int el = -1;
		if (semap.get(i+2) != null) {
		    el = semap.get(i+2).intValue() - 1;
		}		
		if (el != -1) {
		    srcflines.set(i, srcflines.get(i)+" { //Added block"); //Add starting "{"
		    srcflines.set(el, srcflines.get(el)+"}"); //Add ending "}"
		}
		else {
		    System.err.println("Cannot process line#"+(i+1)+": " + srcflines.get(i));
		}
	    }
	}

	StringBuilder sb = null;
	for (String srcfline : srcflines) {
	    if (sb == null) { sb = new StringBuilder(); }
	    else { sb.append("\n"); }

	    //Split multiple "}"s. Use one "}" for one line.
	    if (srcfline.trim().matches("\\}\\}+")) {
		int char_num = srcfline.trim().length();
		String strline_to_add = srcfline.substring(0, srcfline.indexOf("}")) + "}";
		for (int j=0; j<char_num; j++) {
		    if (j != 0) { sb.append("\n"); }
		    sb.append(strline_to_add);
		}
	    }
	    else {
		sb.append(srcfline);
	    }
	}
	if (sb != null) {
	    System.out.println(sb.toString());
	}
    }
}
