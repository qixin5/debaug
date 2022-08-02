package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class AFLScriptGenUtil
{
    private static Pattern numflagpat = Pattern.compile("-\\d+");
    private static Pattern redoutputpat = Pattern.compile("\\&?\\>\\s*\\$OUTDIR");


    public static String getCorePart(String bin_line) {
	int end_idx = -1;
	Matcher matcher = redoutputpat.matcher(bin_line);
	if (matcher.find()) { end_idx = matcher.start(); }
	if (end_idx == -1) {
	    return bin_line.substring(bin_line.indexOf("$BIN")+4).trim();
	}
	else {
	    return bin_line.substring(bin_line.indexOf("$BIN")+4, end_idx).trim();	    
	}
    }
}
