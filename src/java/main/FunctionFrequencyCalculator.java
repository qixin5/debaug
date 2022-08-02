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

public class FunctionFrequencyCalculator
{
    public static void main(String[] args) {
	File linef = new File(args[0]);
	File gcovd = new File(args[1]);

	Map<String, Float> func_freq_map = new HashMap<String, Float>();
	Map<String, String> func_seline_map = LineFileParser.getFunctionNameStartEndLineMap(linef);
	Set<String> funcs = func_seline_map.keySet();
	File[] gcovfs = gcovd.listFiles();
	int total_test_num = gcovfs.length; //Denominator

	for (String func : funcs) {
	    int total_cover_num = 0; //Nominator

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

		//If covered, increase the counter
		if (cover) {
		    total_cover_num += 1;
		}
	    }

	    //Compute absolute frequency
	    float afreq = (float) total_cover_num / (float) total_test_num;
	    func_freq_map.put(func, afreq);
	}

	//Print relative frequency
	float afreq_sum = 0f;
	for (String func : funcs) {
	    afreq_sum += func_freq_map.get(func).floatValue();
	}

	StringBuilder rslt_sb = null;
	for (String func : funcs) {
	    float rfreq = func_freq_map.get(func).floatValue() / afreq_sum;
	    if (rslt_sb == null) { rslt_sb = new StringBuilder(); }
	    else { rslt_sb.append("\n"); }
	    rslt_sb.append(func + "," + rfreq);
	}

	if (rslt_sb != null) {
	    System.out.println(rslt_sb.toString());
	}
    }
}
