package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class FunctionSelecter
{
    public static void main(String[] args) {
	File scoref = new File(args[0]);
	String select_type = args[1];
	int select_value = Integer.parseInt(args[2]);

	List<String> scoreflines = null;
	try { scoreflines = FileUtils.readLines(scoref); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (scoreflines == null) { return; }
	int scoreflines_size = scoreflines.size();

	int topk = 0;
	if ("percent".equals(select_type)) {
	    topk = scoreflines_size * select_value / 100;
	}
	else if ("score".equals(select_type)) {
	    for (String scorefline : scoreflines) {
		float score = Float.parseFloat(scorefline.split(",")[1]);
		if (score * 100 > select_value) {
		    topk += 1;
		}
		else {
		    break;
		}
	    }
	}
	
	StringBuilder rslt_sb = null;
	for (int i=0; i<topk; i++) {
	    if (rslt_sb == null) { rslt_sb = new StringBuilder(); }
	    else { rslt_sb.append("\n"); }
	    rslt_sb.append(scoreflines.get(i).split(",")[0]);
	}

	if (rslt_sb != null) {
	    System.out.println(rslt_sb.toString());
	}
    }
}
