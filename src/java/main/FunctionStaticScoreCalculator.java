package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.scoring.*;


public class FunctionStaticScoreCalculator
{
    public static void main(String[] args) {
	File cgf = new File(args[0]);
        File linef = new File(args[1]);
	String type = args[2];

	Map<String,Set<String>> call_rel_map = FunctionCallRelationGenerator.getCallRelationMap(cgf, linef);
	Map<String, Double> rslt_map = null;
	if ("pr".equals(type)) {
	    rslt_map = getPageRankResult(call_rel_map);
	}

	else if ("rpr".equals(type)) {
	    Map<String, Double> pr_rslt_map = getPageRankResult(call_rel_map);
	    rslt_map = new HashMap<String, Double>();
	    for (String func : pr_rslt_map.keySet()) {
		rslt_map.put(func, new Double(1 - pr_rslt_map.get(func).doubleValue()));
	    }

	    //Normalization
	    double sum = 0;
	    for (String func : rslt_map.keySet()) {
		sum += rslt_map.get(func).doubleValue();
	    }
	    for (String func : rslt_map.keySet()) {
		rslt_map.put(func, (rslt_map.get(func).doubleValue() / sum));
	    }
	}

	else if ("util".equals(type)) {
	    rslt_map = getUtilityhoodResult(call_rel_map);
	}

	else if ("rutil".equals(type)) {
	    Map<String, Double> util_rslt_map = getUtilityhoodResult(call_rel_map);
	    rslt_map = new HashMap<String, Double>();
	    for (String func : util_rslt_map.keySet()) {
		rslt_map.put(func, new Double(1 - util_rslt_map.get(func).doubleValue()));
	    }
	    
	    //Normalization
	    double sum = 0;
	    for (String func : rslt_map.keySet()) {
		sum += rslt_map.get(func).doubleValue();
	    }
	    for (String func : rslt_map.keySet()) {
		rslt_map.put(func, (rslt_map.get(func).doubleValue() / sum));
	    }
	}

	else {
	    System.err.println("Unrecgonized type: " + type);
	    return;
	}
	
	if (rslt_map != null) {
	    StringBuilder rslt_sb = null;
	    for (String funcname : rslt_map.keySet()) {
		if (rslt_sb == null) { rslt_sb = new StringBuilder(); }
		else { rslt_sb.append("\n"); }
		rslt_sb.append(funcname +","+ rslt_map.get(funcname).doubleValue());
	    }
	    if (rslt_sb != null) {
		System.out.println(rslt_sb.toString());
	    }
	}
    }

    private static Map<String, Double> getUtilityhoodResult(Map<String,Set<String>> caller_callee_map) {
	//Build a map that maps a callee to its caller(s)
	Map<String,Set<String>> callee_caller_map = new HashMap<String,Set<String>>();
	for (String caller : caller_callee_map.keySet()) {
	    Set<String> callees = caller_callee_map.get(caller);
	    if (callees == null) { continue; }
	    for (String callee : callees) {
		Set<String> callers = callee_caller_map.get(callee);
		if (callers == null) {
		    callers = new HashSet<String>();
		    callee_caller_map.put(callee, callers);
		}
		callers.add(caller);
	    }
	}

	//Because there are functions that have no callers, we need to add them in
	for (String func : caller_callee_map.keySet()) {
	    if (callee_caller_map.get(func) == null) { //func has no caller
		callee_caller_map.put(func, new HashSet<String>());
	    }
	}

	//Compute utilityhood score (refer to the paper "summarizing the content of large traces to facilitate ...")
	double N = caller_callee_map.keySet().size();
	Map<String, Double> rslt_map = new HashMap<String, Double>();
	for (String func : caller_callee_map.keySet()) {
	    double fanout = (caller_callee_map.get(func) == null) ? 0 : caller_callee_map.get(func).size();
	    double fanin = (callee_caller_map.get(func) == null) ? 0 : callee_caller_map.get(func).size();
	    double score = (fanin/N) * Math.log(N/(fanout+1)) / Math.log(N);
	    rslt_map.put(func, score);
	}

	//Normalization
	double sum = 0;
	for (String func : rslt_map.keySet()) {
	    sum += rslt_map.get(func).doubleValue();
	}
	for (String func : rslt_map.keySet()) {
	    rslt_map.put(func, (rslt_map.get(func).doubleValue() / sum));
	}

	return rslt_map;
    }
    
    private static Map<String, Double> getPageRankResult(Map<String,Set<String>> call_rel_map) {
	DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);
	for (String from_funcname : call_rel_map.keySet()) {
	    g.addVertex(from_funcname);
	}

	for (String from_funcname : call_rel_map.keySet()) {
	    Set<String> to_funcnames = call_rel_map.get(from_funcname);
	    for (String to_funcname : to_funcnames) {
		g.addEdge(from_funcname, to_funcname);
	    }
	}

	//Run page-rank
	VertexScoringAlgorithm<String, Double> pr = new PageRank<>(g);
	return pr.getScores();
    }
}
