package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class FunctionScoreCalculator
{
    public static void main(String[] args) {
	String type = args[0];

	if ("prflex".equals(type) || "rprflex".equals(type) ||
	    "prfreq".equals(type) || "rprfreq".equals(type) ||
	    "utilflex".equals(type) || "rutilflex".equals(type) ||
	    "rutilfreq".equals(type) || "rutilfreq".equals(type)) {
	    Map<String, Float> static_score_map = getFuncScoreMap(new File(args[1]));
	    Map<String, Float> dynamic_score_map = getFuncScoreMap(new File(args[2]));

	    List<FuncScore> score_list = new ArrayList<FuncScore>();
	    for (String funcname : static_score_map.keySet()) {
		float static_score = static_score_map.get(funcname).floatValue();
		float dynamic_score = dynamic_score_map.get(funcname).floatValue();
		score_list.add(new FuncScore(funcname, (0.5f*static_score + 0.5f*dynamic_score)));
	    }

	    Collections.sort(score_list);
	    StringBuilder sb = null;
	    for (FuncScore score : score_list) {
		if (sb == null) { sb = new StringBuilder(); }
		else { sb.append("\n"); }
		sb.append(score.getFuncName()+","+score.getScore());
	    }
	    if (sb != null) {
		System.out.println(sb.toString());
	    }
	}

	else if ("prfreqflex".equals(type) || "rprfreqflex".equals(type) ||
		 "utilfreqflex".equals(type) || "rutilfreqflex".equals(type)) {
	    Map<String, Float> static_score_map = getFuncScoreMap(new File(args[1]));
	    Map<String, Float> freq_map = getFuncScoreMap(new File(args[2]));
	    Map<String, Float> flex_map = getFuncScoreMap(new File(args[3]));

	    List<FuncScore> score_list = new ArrayList<FuncScore>();
	    for (String funcname : static_score_map.keySet()) {
		float static_score = static_score_map.get(funcname).floatValue();
		float freq = freq_map.get(funcname).floatValue();
		float flex = flex_map.get(funcname).floatValue();
		score_list.add(new FuncScore(funcname, (0.33f*static_score + 0.33f*freq + 0.33f*flex)));
	    }

	    Collections.sort(score_list);
	    StringBuilder sb = null;
	    for (FuncScore score : score_list) {
		if (sb == null) { sb = new StringBuilder(); }
		else { sb.append("\n"); }
		sb.append(score.getFuncName()+","+score.getScore());
	    }
	    if (sb != null) {
		System.out.println(sb.toString());
	    }
	}
	
	else if ("randombyfunc".equals(type)) {
	    Map<String, Float> static_score_map = getFuncScoreMap(new File(args[1]));

	    List<FuncScore> score_list = new ArrayList<FuncScore>();
            for (String funcname : static_score_map.keySet()) {
                score_list.add(new FuncScore(funcname, 0f)); //Score doesn't matter
            }

            Collections.shuffle(score_list);
            StringBuilder sb = null;
            for (FuncScore score : score_list) {
                if (sb == null) { sb = new StringBuilder(); }
                else { sb.append("\n"); }
                sb.append(score.getFuncName()+","+score.getScore());
            }
            if (sb != null) {
                System.out.println(sb.toString());
            }	    
	}

	else {
	    System.out.println("Unknown type: " + type);
	}
    }

    private static Map<String, Float> getFuncScoreMap(File f) {
	List<String> flines = null;
	try { flines = FileUtils.readLines(f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (flines == null) { return null; }

	Map<String, Float> fsmap = new HashMap<String, Float>();
	for (String fline : flines) {
	    fsmap.put(fline.split(",")[0], Float.parseFloat(fline.split(",")[1]));
	}
	return fsmap;
    }
    
    private static class FuncScore implements Comparable<FuncScore> {
	String funcname;
	float score;

	public FuncScore(String fn, float s) {
	    funcname = fn;
	    score = s;
	}

	public String getFuncName() { return funcname; }

	public float getScore() { return score; }
	
	@Override public int compareTo(FuncScore fs) {
	    if (score == fs.getScore()) { return 0; }
	    else if (score > fs.getScore()) { return -1; }
	    else { return 1; }
	}
    }
}
