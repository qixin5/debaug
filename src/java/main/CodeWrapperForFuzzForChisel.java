package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.io.FileUtils;

//This class is used to create a new main() and rename the original main with main_core.
//This class is different from CodeWrapperForFuzz in that, for Chisel-generated code, a different set of target_lines is used. This is because Chisel moved code lines in debloating.

//NOTE: This is highly specific to the Chisel code used!!!
//NOTE: See utilprog/create_aflfuzzscript_for_reliability_testing.sh for what code is targeted.

public class CodeWrapperForFuzzForChisel
{
    public static void main(String[] args) {
	String progname = args[0];
	File codef = new File(args[1]);
	File rsltf = new File(args[2]);

	String target_lines = null;

	if ("bzip2-1.0.5".equals(progname)) {
	    target_lines = "10234"; //I9
	}
	else if ("chown-8.2".equals(progname)) {
	    target_lines = "7078";  //I5
	}
	else if ("date-8.21".equals(progname)) {
	    target_lines = "9554,9595";	 //I6    
	}
	else if ("grep-2.19".equals(progname)) {
	    target_lines = "23872"; //I4
	}
	else if ("gzip-1.2.4".equals(progname)) {
	    target_lines = "6680,6803"; //I7
	}
	else if ("mkdir-5.2.1".equals(progname)) {
	    target_lines = "5121"; //I8
	}
	else if ("rm-8.4".equals(progname)) {
	    target_lines = "7221"; //I0
	}
	else if ("sort-8.16".equals(progname)) {
	    target_lines = "13907,13917";   //I6
	}
	else if ("tar-1.14".equals(progname)) {
	    target_lines = "25257";  //I4
	}
	else if ("uniq-8.16".equals(progname)) {
	    target_lines = "7207";   //I5
	}
	else if ("tcas".equals(progname)) {
	    target_lines = "375";  //n10train
	}
	else {
	    System.err.println("Unknown progname: " + progname);
	    return;
	}

	List<String> codeflines = null;
	try { codeflines = FileUtils.readLines(codef, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (codeflines == null) { return; }

	for (String target_line : target_lines.split(",")) {
	    int ln = Integer.parseInt(target_line);
	    codeflines.set(ln-1, codeflines.get(ln-1).replace("main(", "main_core("));
	}

	//Add the wrapper main
	codeflines.add("int main(int argc, char **argv) {");
	codeflines.add("int new_argc = 1;");
	codeflines.add("char arg_line[100];");
	codeflines.add("FILE* arg_f = fopen(argv[1], \"r\");");
	codeflines.add("fgets(arg_line, 100, arg_f);");
	codeflines.add("fclose(arg_f);");
	codeflines.add("");
	codeflines.add("char str0[100];");
	codeflines.add("char str1[100];");
	codeflines.add("strcpy(str0, arg_line);");
	codeflines.add("strcpy(str1, arg_line);");
	codeflines.add("");
	codeflines.add("char* ptr = strtok(str0, \" \");");
	codeflines.add("while (ptr != 0) {");
	codeflines.add("new_argc += 1;");
	codeflines.add("ptr = strtok(0, \" \");");
	codeflines.add("}");
	codeflines.add("");
	codeflines.add("char *new_argv[new_argc];");
	codeflines.add("new_argv[0] = argv[0];");
	codeflines.add("int curr = 1;");
	codeflines.add("ptr = strtok(str1, \" \");");
	codeflines.add("while (ptr != 0) {");
	codeflines.add("new_argv[curr] = ptr;");
	codeflines.add("curr += 1;");
	codeflines.add("ptr = strtok(0, \" \");");
	codeflines.add("}");
	codeflines.add("");
	codeflines.add("main_core(new_argc, new_argv);");
	codeflines.add("}");

	//Write code to file
	try { FileUtils.writeLines(rsltf, codeflines); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
    }
}
