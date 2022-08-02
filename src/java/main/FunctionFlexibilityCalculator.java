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

public class FunctionFlexibilityCalculator
{
    public static void main(String[] args) {
	File linef = new File(args[0]);
	File gcovd = new File(args[1]);

	Map<String, Float> func_flex_map = new HashMap<String, Float>();
	Map<String, String> func_seline_map = LineFileParser.getFunctionNameStartEndLineMap(linef);
	Set<String> funcs = func_seline_map.keySet();
	File[] gcovfs = gcovd.listFiles();

	for (String func : funcs) {
	    Set<String> uniq_covers = new HashSet<String>(); //Size as nominator
	    int total_cover_num = 0; //Denominator

	    for (File gcovf : gcovfs) {
		List<String> gcovf_lines = null;
		try { gcovf_lines = FileUtils.readLines(gcovf, (String) null); }
		catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
		if (gcovf_lines == null) { continue; }

		//See if function is covered
		boolean cover = false;
		for (String gcovf_line : gcovf_lines) {
		    if (gcovf_line.startsWith("function:") &&
			gcovf_line.split(",")[2].endsWith(func)) {
			int covn = Integer.parseInt(gcovf_line.split(",")[1]);
			if (covn > 0) { cover = true; }
			break;
		    }
		}

		if (!cover) { continue; }

		//If covered, get its line coverage
		total_cover_num += 1;
		int sln = Integer.parseInt(func_seline_map.get(func).split(",")[0]);
		int eln = Integer.parseInt(func_seline_map.get(func).split(",")[1]);
		StringBuilder cover_sb = null;
		for (String gcovf_line : gcovf_lines) {
		    if (gcovf_line.startsWith("lcount:")) {
			int ln = Integer.parseInt(gcovf_line.substring("lcount:".length(), gcovf_line.indexOf(",")));
			if (sln <= ln && ln <= eln) {
			    //Add cover line
			    if (cover_sb == null) { cover_sb = new StringBuilder(); }
			    else { cover_sb.append("\n"); }

			    String cover_line = gcovf_line;
			    if (gcovf_line.endsWith(",0") || gcovf_line.endsWith(",1")) {
				cover_sb.append(cover_line);
			    }
			    else {
				//Map numeric count to 1
				cover_sb.append(cover_line.split(",")[0]+",1");
			    }
			}

			if (ln > eln) {
			    break;
			}
		    }
		}

		//Add to uniq_covers
		if (cover_sb == null) { uniq_covers.add(""); } //Shouldn't happen
		else { uniq_covers.add(cover_sb.toString().trim()); }
	    }

	    //Compute absolute flexibility
	    if (total_cover_num == 0) {
		func_flex_map.put(func, 0f); //No flexibility for non-covered funcs
	    }
	    else {
		float aflex = (float) uniq_covers.size() / (float) total_cover_num;
		func_flex_map.put(func, aflex);
	    }

	}

	//Print relative flexiblity
	float aflex_sum = 0f;
	for (String func : funcs) {
	    aflex_sum += func_flex_map.get(func).floatValue();
	}

	StringBuilder rslt_sb = null;
	for (String func : funcs) {
	    float rflex = func_flex_map.get(func).floatValue() / aflex_sum;
	    if (rslt_sb == null) { rslt_sb = new StringBuilder(); }
	    else { rslt_sb.append("\n"); }
	    rslt_sb.append(func + "," + rflex);
	}

	if (rslt_sb != null) {
	    System.out.println(rslt_sb.toString());
	}
    }
}
