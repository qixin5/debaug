package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class MakeRelTestIdentify
{
    public static boolean isRelated(File train_f, File test_f) {
	String op_str0 = getOptionString(train_f);
	String op_str1 = getOptionString(test_f);
	if (op_str0 == null) {
	    System.err.println("Failed to get option string: " + train_f.getPath());
	    return false;
	}
	if (op_str1 == null) {
	    System.err.println("Failed to get option string: " + test_f.getPath());
	    return false;
	}
	return op_str0.equals(op_str1);
    }

    private static String getOptionString(File target_f) {
	List<String> target_flines = null;
	try { target_flines = FileUtils.readLines(target_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (target_flines == null) { return null; }

	Set<String> option_flags = new HashSet<String>();
	
	for (String target_fline : target_flines) {
	    target_fline = target_fline.trim();
	    if (target_fline.contains("$BIN")) {
		String core_part = RelTestIdentifyUtil.getCorePart(target_fline);;

		//Remove strings that are quoted (by '' or by "")
		core_part = RelTestIdentifyUtil.filterQuotedParts(core_part, '\'');
		core_part = RelTestIdentifyUtil.filterQuotedParts(core_part, '\"');
		
		for (String elem : core_part.split("\\s+")) {
		    //Map long options to short ones
		    if (elem.startsWith("--")) {

			//Made sure the no long option is a prefix of another
			if (elem.startsWith("--always-make")) {
			    option_flags.add("-B");
			}
			else if (elem.startsWith("--directory")) {
			    option_flags.add("-C");
			}
			else if (elem.startsWith("--debug")) {
			    option_flags.add("-d");
			}
			else if (elem.startsWith("--environment-overrides")) {
			    option_flags.add("-e");
			}
			else if (elem.startsWith("--eval")) {
			    option_flags.add("-E");
			}
			else if (elem.startsWith("--file") || elem.startsWith("--makefile")) {
			    option_flags.add("-f");
			}
			else if (elem.startsWith("--help")) {
			    option_flags.add("-h");
			}
			else if (elem.startsWith("--ignore-errors")) {
			    option_flags.add("-i");
			}
			else if (elem.startsWith("--include-dir")) {
			    option_flags.add("-I");
			}
			else if (elem.startsWith("--jobs")) {
			    option_flags.add("-j");
			}
			else if (elem.startsWith("--keep-going")) {
			    option_flags.add("-k");
			}
			else if (elem.startsWith("--load-average") || elem.startsWith("--max-load")) {
			    option_flags.add("-l");
			}
			else if (elem.startsWith("--check-symlink-times")) {
			    option_flags.add("-L");
			}
			else if (elem.startsWith("--just-print") ||
				 elem.startsWith("--dry-run") ||
				 elem.startsWith("--recon")) {
			    option_flags.add("-n");
			}
			else if (elem.startsWith("--old-file") || elem.startsWith("--assume-old")) {
			    option_flags.add("-o");
			}
			else if (elem.startsWith("--output-sync")) {
			    option_flags.add("-O");
			}
			else if (elem.startsWith("--print-data-base")) {
			    option_flags.add("-p");
			}
			else if (elem.startsWith("--question")) {
			    option_flags.add("-q");
			}
			else if (elem.startsWith("--no-builtin-rules")) {
			    option_flags.add("-r");
			}
			else if (elem.startsWith("--no-builtin-variables")) {
			    option_flags.add("-R");
			}
			else if (elem.startsWith("--silent") || elem.startsWith("--quiet")) {
			    option_flags.add("-s");
			}
			else if (elem.startsWith("--no-keep-going") || elem.startsWith("--stop")) {
			    option_flags.add("-S");
			}
			else if (elem.startsWith("--touch")) {
			    option_flags.add("-t");
			}
			else if (elem.startsWith("--trace")) {
			    option_flags.add("--trace");
			}
			else if (elem.startsWith("--version")) {
			    option_flags.add("-v");
			}
			else if (elem.startsWith("--print-directory")) {
			    option_flags.add("-w");
			}
			else if (elem.startsWith("--no-print-directory")) {
			    option_flags.add("--no-print-directory");
			}
			else if (elem.startsWith("--what-if") ||
				 elem.startsWith("--new-file") ||
				 elem.startsWith("--assume-new")) {
			    option_flags.add("-W");
			}
			else if (elem.startsWith("--warn-undefined-variables")) {
			    option_flags.add("--warn-undefined-variables");
			}
		    }

		    else if (elem.startsWith("-") && !elem.startsWith("-o${CURRDIR}")) {
			option_flags.add(elem);
		    }
		}
		
		break;
	    }
	}

	//-frq => -f, -r, & -q
	option_flags = RelTestIdentifyUtil.splitMultiFlags(option_flags);
	List<String> option_flags_list = new ArrayList<String>(option_flags);
	Collections.sort(option_flags_list); //Sort the option flags

	String rslt = "";
	for (String option_flag : option_flags_list) {
	    if ("".equals(rslt)) { rslt = option_flag; }
	    else { rslt += " " + option_flag; }
	}
	return rslt;
    }
}
