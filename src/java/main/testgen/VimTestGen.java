package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class VimTestGen
{
    public static void main(String[] args) {
	File torun_scripts_file = new File(args[0]);
	String output_dpath = args[1];
	String script_template_f = "/home/qxin6/debaug/resource/vim.test.template";

	List<String> scriptnames = null;
	try { scriptnames = FileUtils.readLines(torun_scripts_file); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (scriptnames == null) { return; }


	//Load the script template into a string
	String script_template_str = null;
	try { script_template_str = FileUtils.readFileToString(new File(script_template_f), (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (script_template_str == null) { return; }


	//Produce script content and write to file
	int counter = 0;
	for (String scriptname : scriptnames) {
	    String script_ctnt = "#!/usr/bin/perl\n\nuse File::Basename;\n\n$scriptName = \""+scriptname+"\";\n" + script_template_str.replace("XXX", ""+counter);
	    try { FileUtils.writeStringToFile(new File(output_dpath+"/"+(counter++)), script_ctnt); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
    }
}

