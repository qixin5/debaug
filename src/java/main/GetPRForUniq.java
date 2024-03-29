package edu.gatech.cc.debaug;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.scoring.*;


public class GetPRForUniq
{
    public static void main(String[] args) {
	DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        g.addVertex("close_stdout");
        g.addVertex("close_stream");
        g.addVertex("c_strcasecmp");
        g.addVertex("c_tolower");
        g.addVertex("last_component");
        g.addVertex("argmatch");
        g.addVertex("argmatch_invalid");
        g.addVertex("argmatch_valid");
        g.addVertex("__xargmatch_internal");
        g.addVertex("xstrtoul");
        g.addVertex("xmemcoll");
        g.addVertex("xmalloc");
        g.addVertex("xrealloc");
        g.addVertex("x2realloc");
        g.addVertex("xalloc_die");
        g.addVertex("version_etc_arn");
        g.addVertex("version_etc_va");
        g.addVertex("version_etc");
        g.addVertex("set_char_quoting");
        g.addVertex("quotearg_n_style");
        g.addVertex("quotearg_n_style_mem");
        g.addVertex("quotearg_char_mem");
        g.addVertex("quotearg_char");
        g.addVertex("quotearg_colon");
        g.addVertex("quote_n");
        g.addVertex("quote");
        g.addVertex("set_program_name");
        g.addVertex("posix2_version");
        g.addVertex("memcoll");
        g.addVertex("memcasecmp");
        g.addVertex("locale_charset");
        g.addVertex("initbuffer");
        g.addVertex("readlinebuffer_delim");
        g.addVertex("hard_locale");
        g.addVertex("rpl_fseeko");
        g.addVertex("freopen_safer");
        g.addVertex("rpl_fflush");
        g.addVertex("rpl_fclose");
        g.addVertex("fdadvise");
        g.addVertex("fadvise");
        g.addVertex("usage");
        g.addVertex("main");
        g.addVertex("__argmatch_die");
        g.addVertex("bkm_scale___0");
        g.addVertex("bkm_scale_by_power___0");
	g.addVertex("collate_error");
        g.addVertex("x2nrealloc");
	g.addVertex("quoting_options_from_style");
        g.addVertex("quotearg_n_options");
	g.addVertex("quotearg_buffer_restyled");
        g.addVertex("gettext_quote");
	g.addVertex("xcharalloc");
        g.addVertex("strcoll_loop");
	g.addVertex("get_charset_aliases");
        g.addVertex("protect_fd");
	g.addVertex("clear_ungetc_buffer_preserving_position");
        g.addVertex("emit_try_help");
	g.addVertex("emit_ancillary_info");
        g.addVertex("size_opt");
	g.addVertex("check_file");
        g.addVertex("find_field");
	g.addVertex("to_uchar");
        g.addVertex("different");
	g.addVertex("writeline");
        g.addVertex("xnmalloc");

        g.addEdge("close_stdout", "close_stream");
        g.addEdge("close_stdout", "quotearg_colon");
        g.addEdge("rpl_fflush", "clear_ungetc_buffer_preserving_position");
        g.addEdge("clear_ungetc_buffer_preserving_position", "rpl_fseeko");

	g.addEdge("quotearg_colon", "quotearg_char");
	g.addEdge("find_field", "to_uchar");
	g.addEdge("freopen_safer", "protect_fd"); 

	g.addEdge("close_stream", "rpl_fclose"); 
        g.addEdge("rpl_fclose", "rpl_fflush");
        g.addEdge("get_charset_aliases", "rpl_fclose");
        g.addEdge("readlinebuffer_delim", "x2realloc");

        g.addEdge("collate_error", "quotearg_n_style_mem"); 
        g.addEdge("quotearg_char", "quotearg_char_mem"); 
        g.addEdge("gettext_quote", "locale_charset"); 
        g.addEdge("gettext_quote", "c_strcasecmp"); 
        g.addEdge("check_file", "find_field");
        g.addEdge("check_file", "initbuffer"); 
        g.addEdge("check_file", "freopen_safer"); 
        g.addEdge("check_file", "readlinebuffer_delim"); 
        g.addEdge("check_file", "rpl_fclose"); 
        g.addEdge("check_file", "different"); 
        g.addEdge("check_file", "writeline");
        g.addEdge("check_file", "fadvise");

        g.addEdge("different", "xmemcoll"); 
        g.addEdge("different", "memcasecmp");
        g.addEdge("locale_charset", "get_charset_aliases"); 
        g.addEdge("c_strcasecmp", "c_tolower"); 
        g.addEdge("xmemcoll", "memcoll");
        g.addEdge("xmemcoll", "collate_error"); 
        g.addEdge("memcoll", "strcoll_loop"); 
        g.addEdge("quotearg_n_style_mem", "quoting_options_from_style");
        g.addEdge("quotearg_n_style_mem", "quotearg_n_options");
        g.addEdge("quotearg_char_mem", "quotearg_n_options"); 
        g.addEdge("quotearg_char_mem", "set_char_quoting"); 
        g.addEdge("quotearg_buffer_restyled", "quotearg_buffer_restyled"); 
        g.addEdge("quotearg_buffer_restyled", "gettext_quote");
        g.addEdge("__argmatch_die", "usage"); 
        g.addEdge("xstrtoul", "bkm_scale___0");
        g.addEdge("xstrtoul", "bkm_scale_by_power___0");
        g.addEdge("bkm_scale_by_power___0", "bkm_scale___0"); 
        g.addEdge("fadvise", "fdadvise");

        g.addEdge("quotearg_n_style", "quoting_options_from_style");
        g.addEdge("quotearg_n_style", "quotearg_n_options");
	g.addEdge("quotearg_n_options", "quotearg_buffer_restyled");
	g.addEdge("quotearg_n_options", "xcharalloc");
	g.addEdge("quotearg_n_options", "xrealloc");
	g.addEdge("quotearg_n_options", "xalloc_die");
        g.addEdge("main", "hard_locale");
        g.addEdge("main", "__xargmatch_internal");
        g.addEdge("main", "quote");
        g.addEdge("main", "posix2_version");
        g.addEdge("main", "version_etc");
        g.addEdge("main", "size_opt"); 	
        g.addEdge("main", "xstrtoul");
        g.addEdge("main", "usage"); 	
        g.addEdge("main", "set_program_name");
        g.addEdge("main", "check_file"); 	
        g.addEdge("x2nrealloc", "xalloc_die");
        g.addEdge("x2nrealloc", "xrealloc");
        g.addEdge("x2realloc", "x2nrealloc");
        g.addEdge("size_opt", "xstrtoul");

        g.addEdge("__xargmatch_internal", "argmatch");
        g.addEdge("__xargmatch_internal", "argmatch_invalid");
        g.addEdge("__xargmatch_internal", "argmatch_valid");
        g.addEdge("argmatch_invalid", "quotearg_n_style");
	g.addEdge("argmatch_invalid", "quote_n");
        g.addEdge("quote_n", "quotearg_n_options"); 
        g.addEdge("xrealloc", "xalloc_die"); 
        g.addEdge("argmatch_valid", "quote");
        g.addEdge("quote", "quote_n"); 
        g.addEdge("xcharalloc", "xnmalloc");
        g.addEdge("xcharalloc", "xmalloc");
        g.addEdge("xnmalloc", "xalloc_die");
        g.addEdge("xnmalloc", "xmalloc");
        g.addEdge("xmalloc", "xalloc_die"); 
        g.addEdge("usage", "emit_try_help");
        g.addEdge("usage", "emit_ancillary_info");
        g.addEdge("version_etc", "version_etc_va");
        g.addEdge("version_etc_va", "version_etc_arn"); 
        g.addEdge("emit_ancillary_info", "last_component");

        VertexScoringAlgorithm<String, Double> pr = new PageRank<>(g);
	System.out.println("close_stdout," + pr.getVertexScore("close_stdout"));
	System.out.println("close_stream," + pr.getVertexScore("close_stream"));
	System.out.println("c_strcasecmp," + pr.getVertexScore("c_strcasecmp"));
	System.out.println("c_tolower," + pr.getVertexScore("c_tolower"));
	System.out.println("last_component," + pr.getVertexScore("last_component"));	
	System.out.println("argmatch," + pr.getVertexScore("argmatch"));
	System.out.println("argmatch_invalid," + pr.getVertexScore("argmatch_invalid"));
	System.out.println("argmatch_valid," + pr.getVertexScore("argmatch_valid"));
	System.out.println("__xargmatch_internal," + pr.getVertexScore("__xargmatch_internal"));
	System.out.println("xstrtoul," + pr.getVertexScore("xstrtoul"));	
	System.out.println("xmemcoll," + pr.getVertexScore("xmemcoll"));
	System.out.println("xmalloc," + pr.getVertexScore("xmalloc"));
	System.out.println("xrealloc," + pr.getVertexScore("xrealloc"));
	System.out.println("x2realloc," + pr.getVertexScore("x2realloc"));
	System.out.println("xalloc_die," + pr.getVertexScore("xalloc_die"));	
	System.out.println("version_etc_arn," + pr.getVertexScore("version_etc_arn"));
	System.out.println("version_etc_va," + pr.getVertexScore("version_etc_va"));
	System.out.println("version_etc," + pr.getVertexScore("version_etc"));
	System.out.println("set_char_quoting," + pr.getVertexScore("set_char_quoting"));
	System.out.println("quotearg_n_style," + pr.getVertexScore("quotearg_n_style"));
	System.out.println("quotearg_n_style_mem," + pr.getVertexScore("quotearg_n_style_mem"));
	System.out.println("quotearg_char_mem," + pr.getVertexScore("quotearg_char_mem"));
	System.out.println("quotearg_char," + pr.getVertexScore("quotearg_char"));
	System.out.println("quotearg_colon," + pr.getVertexScore("quotearg_colon"));
	System.out.println("quote_n," + pr.getVertexScore("quote_n"));	
	System.out.println("quote," + pr.getVertexScore("quote"));
	System.out.println("set_program_name," + pr.getVertexScore("set_program_name"));
	System.out.println("posix2_version," + pr.getVertexScore("posix2_version"));
	System.out.println("memcoll," + pr.getVertexScore("memcoll"));
	System.out.println("memcasecmp," + pr.getVertexScore("memcasecmp"));	
	System.out.println("locale_charset," + pr.getVertexScore("locale_charset"));
	System.out.println("initbuffer," + pr.getVertexScore("initbuffer"));
	System.out.println("readlinebuffer_delim," + pr.getVertexScore("readlinebuffer_delim"));
	System.out.println("hard_locale," + pr.getVertexScore("hard_locale"));
	System.out.println("rpl_fseeko," + pr.getVertexScore("rpl_fseeko"));	
	System.out.println("freopen_safer," + pr.getVertexScore("freopen_safer"));
	System.out.println("rpl_fflush," + pr.getVertexScore("rpl_fflush"));
	System.out.println("rpl_fclose," + pr.getVertexScore("rpl_fclose"));
	System.out.println("fdadvise," + pr.getVertexScore("fdadvise"));
	System.out.println("fadvise," + pr.getVertexScore("fadvise"));	
	System.out.println("usage," + pr.getVertexScore("usage"));
	System.out.println("main," + pr.getVertexScore("main"));
	System.out.println("__argmatch_die," + pr.getVertexScore("__argmatch_die"));
	System.out.println("bkm_scale___0," + pr.getVertexScore("bkm_scale___0"));
	System.out.println("bkm_scale_by_power___0," + pr.getVertexScore("bkm_scale_by_power___0"));	
	System.out.println("collate_error," + pr.getVertexScore("collate_error"));
	System.out.println("x2nrealloc," + pr.getVertexScore("x2nrealloc"));
	System.out.println("quoting_options_from_style," + pr.getVertexScore("quoting_options_from_style"));
	System.out.println("quotearg_n_options," + pr.getVertexScore("quotearg_n_options"));
	System.out.println("quotearg_buffer_restyled," + pr.getVertexScore("quotearg_buffer_restyled"));	
	System.out.println("gettext_quote," + pr.getVertexScore("gettext_quote"));
	System.out.println("xcharalloc," + pr.getVertexScore("xcharalloc"));
	System.out.println("strcoll_loop," + pr.getVertexScore("strcoll_loop"));
	System.out.println("get_charset_aliases," + pr.getVertexScore("get_charset_aliases"));
	System.out.println("protect_fd," + pr.getVertexScore("protect_fd"));	
	System.out.println("clear_ungetc_buffer_preserving_position," + pr.getVertexScore("clear_ungetc_buffer_preserving_position"));
	System.out.println("emit_try_help," + pr.getVertexScore("emit_try_help"));
	System.out.println("emit_ancillary_info," + pr.getVertexScore("emit_ancillary_info"));
	System.out.println("size_opt," + pr.getVertexScore("size_opt"));
	System.out.println("check_file," + pr.getVertexScore("check_file"));	
	System.out.println("find_field," + pr.getVertexScore("find_field"));
	System.out.println("to_uchar," + pr.getVertexScore("to_uchar"));
	System.out.println("different," + pr.getVertexScore("different"));
	System.out.println("writeline," + pr.getVertexScore("writeline"));
	System.out.println("xnmalloc," + pr.getVertexScore("xnmalloc"));
    }
}
