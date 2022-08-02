package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class RandomAugmenter
{
    public static void main(String[] args) {
	File target_codef = new File(args[0]);
	File origin_codef = new File(args[1]);
	File linef = new File(args[2]);
	String check_type = args[3]; //By stmt number or binary size
	int check_size = Integer.parseInt(args[4]); //The objective size to be augmented
	String compile_scriptfpath = args[5]; //In absolute path. Use $BIN $SRC as parameters
	String getsize_scriptfpath = args[6]; //In absolute path. Use $INPUT $OUTPUT
	String output_fpath = args[7];
	float devrate = Float.parseFloat(args[8]);


	int MAX_ITER = 10000;
	
	//*** Read code lines ***
	List<String> target_codeflines = null;
	try { target_codeflines = FileUtils.readLines(target_codef, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (target_codeflines == null) { return; }

	List<String> origin_codeflines = null;
	try { origin_codeflines = FileUtils.readLines(origin_codef, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (origin_codeflines == null) { return; }


	//*** Parse linef and get target stmt lines for augmentation ***
	List<String> linef_lines = null;
	try { linef_lines = FileUtils.readLines(linef, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (linef_lines == null) { return; }

	List<String> candidate_stmt_lines = new LinkedList<String>();
	for (String linef_line : linef_lines) {
	    linef_line = linef_line.trim();
	    if (!linef_line.startsWith("statement:")) { continue; }
	    if (linef_line.endsWith(",fbcompound") || linef_line.endsWith(",decl")) { continue; } //decl is ignored, as we will add all decl stmts in (and further rely on dead-code eliminator to eliminate unused ones).
	    
	    String stmt_line = linef_line.substring("statement:".length());
	    int sln = Integer.parseInt(stmt_line.split(",")[0]);
	    int eln = Integer.parseInt(stmt_line.split(",")[1]);

	    //Check if target code is equal to origin code (within sln & eln)
	    //Otherwise, add as a candidate
	    boolean is_candidate = false;
	    for (int i=sln-1; i<=eln-1; i++) {
		if (!target_codeflines.get(i).trim().equals(origin_codeflines.get(i).trim())) {
		    is_candidate = true;
		    break;
		}
	    }
	    if (is_candidate) {
		candidate_stmt_lines.add(stmt_line);
	    }
	}


	//*** Augmentation Process ***
	List<String> rslt_codeflines = new ArrayList<String>();
	for (String target_codefline : target_codeflines) {
	    rslt_codeflines.add(target_codefline);
	}

	//Augment with all decl stmts (rely on dead-code eliminator to further remove extra ones)
	for (String linef_line : linef_lines) {
	    linef_line = linef_line.trim();
	    if (linef_line.startsWith("statement:") && linef_line.endsWith(",decl")) {
		String stmt_line = linef_line.substring("statement:".length());
		int sln = Integer.parseInt(stmt_line.split(",")[0]);
		int eln = Integer.parseInt(stmt_line.split(",")[1]);
		for (int i=sln-1; i<=eln-1; i++) {
		    rslt_codeflines.set(i, origin_codeflines.get(i));
		}
	    }
	}
	
	//Compute current size
	int curr_size = eval(rslt_codeflines, check_type, compile_scriptfpath, getsize_scriptfpath);
	if (curr_size == -1) {
	    System.out.println("Compiling error for target code.");
	    return;
	}
	else if (curr_size == -2) {
	    System.out.println("Getsize error for target code.");
            return;
	}

	float check_size_lb = check_size - check_size * devrate; //lower-bound
	float check_size_ub = check_size + check_size * devrate; //upper-bound
	if (check_size_lb <= curr_size && curr_size <= check_size_ub) {
	    System.out.println("Target size (deviated) already met. There is no need for augmentation. The input code is also the output.");
	}

	else {
	    //Augmentation loop
	    for (int iter=0; iter<MAX_ITER; iter++) {
		int select_idx = (int) (candidate_stmt_lines.size() * Math.random());
		int sln = Integer.parseInt(candidate_stmt_lines.get(select_idx).split(",")[0]);
		int eln = Integer.parseInt(candidate_stmt_lines.get(select_idx).split(",")[1]);
		
		//Augment by replacing
		List<String> rslt_backup_codeflines = new ArrayList<String>();
		for (int i=sln-1; i<=eln-1; i++) {
		    rslt_backup_codeflines.add(rslt_codeflines.get(i)); //Backup
		    rslt_codeflines.set(i, origin_codeflines.get(i));
		}
		
		//Compute size
		int curr_size_tmp = eval(rslt_codeflines, check_type, compile_scriptfpath, getsize_scriptfpath);
		if (curr_size_tmp < 0 || curr_size_tmp > check_size_ub) {
		    if (curr_size_tmp == -1) {
			System.err.println("Generated a program that does not compile. Try another.");
		    }
		    else if (curr_size_tmp == -2) {
			System.err.println("Cannot get program's size. Try another.");
		    }
		    else if (curr_size_tmp > check_size_ub) {
			System.err.println("Generated a program that exceeds the size upper-bound. Try another.");
		    }
		    
		    //Revert the changed lines
		    for (int i=sln-1; i<=eln-1; i++) {
			rslt_codeflines.set(i, rslt_backup_codeflines.get(i-(sln-1)));
		    }
		}
		else {
		    //Valid augmentation
		    //=============
		    System.out.println("Valid augmentation.");
		    //=============		    
		    curr_size = curr_size_tmp;
		    candidate_stmt_lines.remove(select_idx);
		}

		//==========
		System.out.println("Current size: " + curr_size);
		System.out.println("Check size lower-bound: " + check_size_lb);
		System.out.println("Check size upper-bound: " + check_size_ub);
		//==========		
		
		if (check_size_lb <= curr_size && curr_size <= check_size_ub) {
		    break;
		}
	    }
	}
	
	StringBuilder rslt_sb = null;
	for (String rslt_codefline : rslt_codeflines) {
	    if (rslt_sb == null) { rslt_sb = new StringBuilder(); }
	    else { rslt_sb.append("\n"); }
	    rslt_sb.append(rslt_codefline);
	}

	if (rslt_sb != null) {
	    File output_f = new File(output_fpath);
	    try { FileUtils.writeStringToFile(output_f, rslt_sb.toString(), (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
    }

    private static int eval(List<String> rslt_codeflines, String check_type, String compile_scriptfpath, String getsize_scriptfpath) {

	//Prepare a working dir
	File work_dir = new File("randomaugtmp");
	if (work_dir.exists()) { //Clean its content
	    File[] work_files = work_dir.listFiles();
	    for (File work_file : work_files) {
		try { FileUtils.forceDelete(work_file); }
		catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    }
	}
	else {
	    try { FileUtils.forceMkdir(work_dir); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}

	//Write rslt code to file
	File rslt_codef = new File("randomaugtmp/rslt.nodce.c");
	try { FileUtils.writeLines(rslt_codef, (String) null, rslt_codeflines); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	

	//Compile
	String[] compile_cmds = new String[3];
	compile_cmds[0] =
	    (compile_scriptfpath.contains("/")) ? compile_scriptfpath : ("./"+compile_scriptfpath);
	compile_cmds[1] = "rslt.nodce.c";
	compile_cmds[2] = "rslt.nodce";

	int compile_exit = CommandExecutor.execute(compile_cmds, work_dir);
	if (compile_exit != 0) {
	    return -1;
	}

	
	//Remove dead code
	String[] dce_cmds = new String[3];
	dce_cmds[0] = "/home/qxin6/debdce/bin/debdce";
	dce_cmds[1] = "rslt.nodce.c";
	dce_cmds[2] = "rslt.c";

	CommandExecutor.execute(dce_cmds, work_dir);
	
	
	//Get size
	String[] getsize_cmds = new String[3];
	getsize_cmds[0] =
	    (getsize_scriptfpath.contains("/")) ? getsize_scriptfpath : ("./"+getsize_scriptfpath);

	if ("statement".equals(check_type)) {
	    getsize_cmds[1] = "rslt.c";
	}
	else if ("binary".equals(check_type)) {
	    //First the get the binary for dce-ed code
	    String[] compile_cmds1 = new String[3];
	    compile_cmds1[0] = compile_cmds[0];
	    compile_cmds1[1] = "rslt.c";
	    compile_cmds1[2] = "rslt";
	    int compile_exit1 = CommandExecutor.execute(compile_cmds1, work_dir);
	    if (compile_exit1 != 0) { return -1; }
	    
	    getsize_cmds[1] = "rslt";
	}
	getsize_cmds[2] = "size.txt";
	
	int getsize_exit = CommandExecutor.execute(getsize_cmds, work_dir);
	if (getsize_exit != 0) {
	    return -2;
	}


	//Return size
	File size_f = new File("randomaugtmp/size.txt");
	String size_fctnt = null;
	try { size_fctnt = FileUtils.readFileToString(size_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (size_fctnt == null) { return -2; }

	int size = -2;
	try { size = Integer.parseInt(size_fctnt.trim()); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	return size;
    }
}
