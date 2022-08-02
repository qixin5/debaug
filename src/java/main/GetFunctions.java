package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class GetFunctions
{
    public static void main(String[] args) {
	File trace_f = new File(args[0]);
	System.out.println(getFunctions(trace_f));
    }

    public static String getFunctions(File trace_f) {
	List<String> trace_flines = null;
        try { trace_flines = FileUtils.readLines(trace_f); }
        catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
        if (trace_flines == null) { return null; }

	List<String> func_names = new ArrayList<String>();
	for (String trace_fline : trace_flines) {
	    if (!trace_fline.startsWith("<STMTID>")) { continue; }
	    String[] elems = trace_fline.split(":");
	    String func_name = elems[1].substring(0, elems[1].indexOf("("));
	    if (!func_names.contains(func_name)) {
		func_names.add(func_name);
	    }
	}

	StringBuilder sb = null;
	for (String func_name : func_names) {
            if (sb == null) { sb = new StringBuilder(); }
            else { sb.append(" "); }
            sb.append(func_name);
	}

        return (sb == null) ? null : sb.toString();	
    }
}
