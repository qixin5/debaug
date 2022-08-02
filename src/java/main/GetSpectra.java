package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class GetSpectra
{
    public static void main(String[] args) {
	File trace_f = new File(args[0]);
	String func_name = args[1];
	System.out.println(getSpectra(trace_f, func_name));
    }

    public static String getSpectra(File trace_f, String func_name) {
	List<String> trace_flines = null;
	try { trace_flines = FileUtils.readLines(trace_f); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (trace_flines == null) { return null; }
	
	List<String> stmt_ids = new ArrayList<String>();
	for (String trace_fline : trace_flines) {
	    if (!trace_fline.startsWith("<STMTID>")) { continue; }
	    String[] elems = trace_fline.split(":");
	    if (elems[1].startsWith(func_name)) {
		String sid = elems[2].substring(4);
		if (!stmt_ids.contains(sid)) { stmt_ids.add(sid); }
	    }
	}

	StringBuilder sb = null;
	for (String stmt_id : stmt_ids) {
	    if (sb == null) { sb = new StringBuilder(); }
	    else { sb.append(","); }
	    sb.append(stmt_id);
	}

	return (sb == null) ? null : sb.toString();
    }
}

