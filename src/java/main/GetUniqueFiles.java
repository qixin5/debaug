package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class GetUniqueFiles
{
    public static void main(String[] args) {
	File dir = new File(args[0]);
	File[] files = dir.listFiles();
	Set<String> file_ctnts = new HashSet<String>();
	StringBuilder sb = null;
	
	for (File file : files) {
	    String file_ctnt = null;
	    try { file_ctnt = FileUtils.readFileToString(file, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    if (file_ctnt == null) { continue; }
	    else { file_ctnt = file_ctnt.trim(); }
	    
	    boolean new_file = file_ctnts.add(file_ctnt);
	    if (new_file) {
		if (sb == null) { sb = new StringBuilder(); }
		else { sb.append(","); }
		sb.append(file.getName());
	    }
	}

	if (sb != null) { System.out.println(sb.toString()); }
    }
}
