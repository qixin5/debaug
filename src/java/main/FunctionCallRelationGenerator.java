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

public class FunctionCallRelationGenerator
{
    public static void main(String[] args) {
	File cgf = new File(args[0]);
	File linef = new File(args[1]);

	Map<String,Set<String>> call_rel_map = getCallRelationMap(cgf, linef);
	StringBuilder sb = null;
	for (String from_funcname : call_rel_map.keySet()) {
	    if (sb == null) { sb = new StringBuilder(); }
	    else { sb.append("\n"); }

	    sb.append(from_funcname);
	    Set<String> to_funcnames = call_rel_map.get(from_funcname);
	    for (String to_funcname : to_funcnames) {
		sb.append(" " + to_funcname);
	    }
	}
	if (sb != null) {
	    System.out.println(sb.toString());
	}
    }

    public static Map<String,Set<String>> getCallRelationMap(File cgf, File linef) {
	Map<String,Set<String>> rslt_map = new HashMap<String,Set<String>>();
	
	Map<String,String> nid_funcname_map = getNodeIdFuncNameMap(cgf);
	Map<String,String> funcname_sdl_map = LineFileParser.getFunctionNameStartEndLineMap(linef);
	Map<String,List<String>> nid_tonidlist_map = getNodeIdInvokedNodeIdListMap(cgf);

	for (String nid : nid_tonidlist_map.keySet()) {
	    String from_funcname = nid_funcname_map.get(nid);
	    if (funcname_sdl_map.get(from_funcname) == null) {
		//Non-tracked function (probabily undefined in source and thus not interesting)
		continue;
	    }

	    List<String> to_nid_list = nid_tonidlist_map.get(nid);
	    Set<String> to_funcname_set = new HashSet<String>();
	    for (String to_nid : to_nid_list) {
		String to_funcname = nid_funcname_map.get(to_nid);
                if (funcname_sdl_map.get(to_funcname) != null) { //Filter non-tracked functions
		    to_funcname_set.add(to_funcname);
		}
	    }

	    rslt_map.put(from_funcname, to_funcname_set);
	}

	//It's possible that functions with no callees are not included in nid_tonidlist_map.
	//So we need to bring them in.
	for (String funcname : nid_funcname_map.values()) {
	    //Non-tracked function
	    if (funcname_sdl_map.get(funcname) == null) { continue; }
	    //Already-covered function
	    if (rslt_map.get(funcname) != null) { continue; }

	    //This is a function with no callee
	    rslt_map.put(funcname, new HashSet<String>());
	}

	return rslt_map;
    }
    
    public static Map<String,String> getNodeIdFuncNameMap(File cgf) {
	List<String> cgflines = null;
	try { cgflines = FileUtils.readLines(cgf, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (cgflines == null) { return null; }

	Map<String,String> rslt_map = new HashMap<String,String>();
	for (String cgfline : cgflines) {
	    cgfline = cgfline.trim();
	    if (cgfline.contains("[shape=") && cgfline.contains(",label=")) {
		int sidx = cgfline.indexOf("\"{");
		int eidx = cgfline.indexOf("}\"");
		String funcname = cgfline.substring(sidx+2, eidx).trim();
		String nodeid = cgfline.split(" ")[0];
		rslt_map.put(nodeid, funcname);
	    }
	}

	return rslt_map;
    }

    public static Map<String,List<String>> getNodeIdInvokedNodeIdListMap(File cgf) {
	List<String> cgflines = null;
	try { cgflines = FileUtils.readLines(cgf, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (cgflines == null) { return null; }

	Map<String,List<String>> rslt_map = new HashMap<String,List<String>>();
	for (String cgfline : cgflines) {
	    cgfline = cgfline.trim();
	    String[] elems = cgfline.split(" ");
	    if (elems.length == 3 && "->".equals(elems[1])) {
		String from_nodeid = elems[0].trim();
		String to_nodeid = elems[2].trim();
		if (to_nodeid.endsWith(";")) {
		    to_nodeid = to_nodeid.substring(0, to_nodeid.length()-1).trim();
		}

		List<String> to_nodeid_list = rslt_map.get(from_nodeid);
		if (to_nodeid_list == null) {
		    to_nodeid_list = new ArrayList<String>();
		    rslt_map.put(from_nodeid, to_nodeid_list);
		}
		to_nodeid_list.add(to_nodeid);
	    }
	}
	return rslt_map;
    }
}
