package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class LLVMGCovFixer
{
    /* 
       llvm_gcov_binary_f is a binary-count version of the .gcov file (yielded by llvm-cov as .lcov file first and further transformed into .gcov file).
       gcov_count_f is the .gcov file generated through using gcov -i SOURCE.
       This fixer compares the two files, and fixes lines whose counts are 0s in the first file but are non-0s in the second.
       It also fixes count and adds mark ("inconsistent") for any function with inconsistent counts (0s in the first file but non-0s in the second). These functions will be restored to their original versions later for debloating.
    */
    public static void main(String[] args) {
	File llvm_gcov_binary_f = new File(args[0]);
	File gcov_count_f = new File(args[1]);
	
	List<String> llvm_gcov_binary_flines = null;
	try { llvm_gcov_binary_flines = FileUtils.readLines(llvm_gcov_binary_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (llvm_gcov_binary_flines == null) { return; }

	List<String> gcov_count_flines = null;
	try { gcov_count_flines = FileUtils.readLines(gcov_count_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (gcov_count_flines == null) { return; }
	
	//Parse gcov_count_f into maps
	Map<Integer, Long> gcov_lcmap = GCovUtils.getLineCountMap(gcov_count_flines);
	Map<Integer, Long> gcov_flcmap = GCovUtils.getFunctionLineCountMap(gcov_count_flines);
	Map<String, List<Integer>> gcov_fnlsmap = GCovUtils.getFunctionNameLinesMap(gcov_count_flines);

	//Process each line in llvm_gcov_binary_f
	int size = llvm_gcov_binary_flines.size();
	for (int i=0; i<size; i++) {
	    String line = llvm_gcov_binary_flines.get(i).trim();

	    if (line.startsWith("function:")) {
		String[] elems = line.substring(line.indexOf(":")+1).split(",");
		if (elems[1].equals("1")) { //Skip covered lines
		    continue;
		}

		String funcname = elems[2];
		if (funcname.contains(":")) { //Eliminate the leading file name
		    funcname = funcname.substring(funcname.indexOf(":")+1);
		}
		
		int l = Integer.parseInt(elems[0]);
		List<Integer> funclines = gcov_fnlsmap.get(funcname);
		if (funclines == null) {
		    System.err.println("Missing function: " + funcname + " in .gcov file generated by GCov.");
		}
		else {
		    //There might be multiple functions with the same name
		    //Pick the one whose starting line has the smallest distance with l
		    int l1 = findLineWithSmallestDistance(l, funclines);
		    if (gcov_flcmap.get(l1).longValue() > 0l) {
			//This is a line marked with 0 and non-0 counts by llvm-cov and gcov
			llvm_gcov_binary_flines.set(i,
		        ("function:" + elems[0] + ",1," + elems[2] + ",inconsistent"));
		    }
		}
	    }

	    else if (line.startsWith("lcount:")) {
		String[] elems = line.substring(line.indexOf(":")+1).split(",");
		if (elems[1].equals("1")) { //Skip covered lines
                    continue;
                }

		int l = Integer.parseInt(elems[0]);
		if (gcov_lcmap.get(l) != null && gcov_lcmap.get(l).longValue() > 0l) {
		    llvm_gcov_binary_flines.set(i, "lcount:"+elems[0]+",1");
		}
	    }
	}

	//Write updated info back to file
	try { FileUtils.writeLines(llvm_gcov_binary_f, llvm_gcov_binary_flines); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
    }


    private static int findLineWithSmallestDistance(int l, List<Integer> funclines) {
	int min_i = 0;
	int min_dist = Math.abs(funclines.get(0).intValue() - l);

	for (int i=1; i<funclines.size(); i++) {
	    if (Math.abs(funclines.get(i).intValue() - l) < min_dist) {
		min_i = i;
		min_dist = Math.abs(funclines.get(i).intValue() - l);
	    }
	}
	return funclines.get(min_i).intValue();
    }
}
