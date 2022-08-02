package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.io.FileUtils;


public class RelTestIdentify
{
    public static void main(String[] args) {
	String progname = args[0];
	File[] train_fs = new File(args[1]).listFiles();
	File[] test_fs = new File(args[2]).listFiles();

	StringBuilder sb = null;
	for (File test_f : test_fs) {
	    String rslt_line = null;

	    for (File train_f : train_fs) {
		boolean is_related = false;
		if ("bzip2-1.0.5".equals(progname)) {
		    is_related = BzipRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("chown-8.2".equals(progname)) {
		    is_related = ChownRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("date-8.21".equals(progname)) {
		    is_related = DateRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("grep-2.19".equals(progname)) {
		    is_related = Grep219RelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("gzip-1.2.4".equals(progname)) {
		    is_related = Gzip124RelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("mkdir-5.2.1".equals(progname)) {
		    is_related = MkdirRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("rm-8.4".equals(progname)) {
		    is_related = RmRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("sort-8.16".equals(progname)) {
		    is_related = SortRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("tar-1.14".equals(progname)) {
		    is_related = TarRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("uniq-8.16".equals(progname)) {
		    is_related = UniqRelTestIdentify.isRelated(train_f, test_f);
		}
		

		else if ("bash-2.05".equals(progname)) {
		    is_related = BashRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("flex-2.5.4".equals(progname)) {
		    is_related = FlexRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("grep-2.4.2".equals(progname)) {
		    is_related = Grep242RelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("gzip-1.3".equals(progname)) {
		    is_related = Gzip13RelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("make-3.79".equals(progname)) {
		    is_related = MakeRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("printtokens".equals(progname) || "printtokens2".equals(progname)) {
		    is_related = PrintTokensRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("replace".equals(progname)) {
		    is_related = ReplaceRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("schedule".equals(progname) || "schedule2".equals(progname)) {
		    is_related = ScheduleRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("sed-4.1.5".equals(progname)) {
		    is_related = SedRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("space".equals(progname)) {
		    is_related = SpaceRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("tcas".equals(progname)) {
		    is_related = TcasRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("totinfo".equals(progname)) {
		    is_related = TotinfoRelTestIdentify.isRelated(train_f, test_f);
		}
		else if ("vim-5.8".equals(progname)) {
		    is_related = VimRelTestIdentify.isRelated(train_f, test_f);
		}
		//TODO: Add more cases
		
		if (is_related) {
		    if (rslt_line == null) { rslt_line = test_f.getName(); }
		    rslt_line += "," + train_f.getName();
		}
	    }

	    if (rslt_line != null) {
		if (sb == null) { sb = new StringBuilder(); }
		else { sb.append("\n"); }
		sb.append(rslt_line);
	    }
	}

	if (sb != null) {
	    System.out.println(sb.toString());
	}
    }
}
