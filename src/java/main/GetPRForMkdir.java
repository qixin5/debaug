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


public class GetPRForMkdir
{
    public static void main(String[] args) {
	DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        g.addVertex("strip_trailing_slashes");
        g.addVertex("base_name");
        g.addVertex("base_len");
        g.addVertex("same_name");
        g.addVertex("dir_len");
        g.addVertex("version_etc");
        g.addVertex("version_etc_va");
        g.addVertex("close_stdout");
        g.addVertex("dir_name");
        g.addVertex("rpl_getcwd");
        g.addVertex("rpl_free");
        g.addVertex("xmalloc");
        g.addVertex("xrealloc");
        g.addVertex("quotearg_colon");
        g.addVertex("bkm_scale___0");
        g.addVertex("free_cwd");
        g.addVertex("quotearg_char");
        g.addVertex("xstrtoul");
        g.addVertex("bkm_scale_by_power___0");
        g.addVertex("mode_free");
        g.addVertex("save_cwd");
        g.addVertex("xgetcwd");
        g.addVertex("rpl_getcwd");
        g.addVertex("quotearg_n_options");
        g.addVertex("set_char_quoting");
        g.addVertex("make_node_op_equals");
        g.addVertex("mode_append_entry");
        g.addVertex("mode_adjust");
        g.addVertex("main");
        g.addVertex("quotearg_buffer_restyled");
        g.addVertex("quotearg_n_style");
        g.addVertex("mode_compile");
        g.addVertex("usage");
        g.addVertex("quotearg_buffer");
        g.addVertex("gettext_quote");
        g.addVertex("quoting_options_from_style");
        g.addVertex("xmalloc");
        g.addVertex("xnmalloc_inline");
        g.addVertex("xalloc_die");
        g.addVertex("make_path");
        g.addVertex("quote_n");
        g.addVertex("xrealloc");
        g.addVertex("xnrealloc_inline");
        g.addVertex("restore_cwd");
        g.addVertex("make_dir");
        g.addVertex("quote");
	
        g.addEdge("strip_trailing_slashes", "base_name");
        g.addEdge("strip_trailing_slashes", "base_len");
        g.addEdge("same_name", "base_name");
        g.addEdge("same_name", "base_len");
        g.addEdge("same_name", "dir_name");
        g.addEdge("dir_len", "base_name");
        g.addEdge("version_etc", "version_etc_va");
        g.addEdge("close_stdout", "quotearg_colon");

        g.addEdge("dir_name", "dir_len");
        g.addEdge("dir_name", "xmalloc");  
        g.addEdge("rpl_getcwd", "same_name");
        g.addEdge("rpl_getcwd", "rpl_free");	
        g.addEdge("quotearg_colon", "quotearg_char");

        g.addEdge("free_cwd", "rpl_getcwd");	
        g.addEdge("quotearg_char", "set_char_quoting");
        g.addEdge("quotearg_char", "quotearg_n_options");

        g.addEdge("xstrtoul", "bkm_scale___0");
        g.addEdge("xstrtoul", "bkm_scale_by_power___0");	
        g.addEdge("mode_free", "rpl_free");
        g.addEdge("save_cwd", "xgetcwd");	
        g.addEdge("xgetcwd", "rpl_getcwd");
        g.addEdge("xgetcwd", "xalloc_die");	
        g.addEdge("xgetcwd", "rpl_getcwd");
        g.addEdge("quotearg_n_options", "rpl_free");	
        g.addEdge("quotearg_n_options", "xmalloc");
        g.addEdge("quotearg_n_options", "xrealloc");	
        g.addEdge("quotearg_n_options", "xalloc_die");
        g.addEdge("quotearg_n_options", "xrealloc");	
        g.addEdge("quotearg_n_options", "quotearg_buffer");

        g.addEdge("main", "version_etc");	
        g.addEdge("main", "close_stdout");
        g.addEdge("main", "quote");	
        g.addEdge("main", "mode_compile");
        g.addEdge("main", "usage");	
        g.addEdge("main", "make_dir");
        g.addEdge("main", "make_path");	
        g.addEdge("main", "xalloc_die");
        g.addEdge("main", "mode_adjust");	
        g.addEdge("quotearg_buffer_restyled", "quotearg_buffer_restyled"); //Ok?
        g.addEdge("quotearg_buffer_restyled", "gettext_quote");	
        g.addEdge("quotearg_n_style", "quotearg_n_options");
        g.addEdge("quotearg_n_style", "quoting_options_from_style");	

        g.addEdge("mode_compile", "make_node_op_equals");
        g.addEdge("mode_compile", "xstrtoul");	
        g.addEdge("mode_compile", "mode_append_entry");
        g.addEdge("mode_compile", "mode_free");	
        g.addEdge("quotearg_buffer", "quotearg_buffer_restyled");

        g.addEdge("xmalloc", "xnmalloc_inline");	
        g.addEdge("xnmalloc_inline", "xalloc_die");
        g.addEdge("make_path", "strip_trailing_slashes");
        g.addEdge("make_path", "free_cwd");	
        g.addEdge("make_path", "save_cwd");
        g.addEdge("make_path", "quote");
        g.addEdge("make_path", "make_dir");
        g.addEdge("make_path", "restore_cwd");
        g.addEdge("quote_n", "quotearg_n_style");

        g.addEdge("xrealloc", "xnrealloc_inline");		
        g.addEdge("xnrealloc_inline", "xalloc_die");
        g.addEdge("make_dir", "quote");		
        g.addEdge("quote", "quote_n");

        VertexScoringAlgorithm<String, Double> pr = new PageRank<>(g);
	System.out.println("strip_trailing_slashes,"+ pr.getVertexScore("strip_trailing_slashes"));
	System.out.println("base_name,"+ pr.getVertexScore("base_name"));
	System.out.println("base_len,"+ pr.getVertexScore("base_len"));
	System.out.println("same_name,"+ pr.getVertexScore("same_name"));
	System.out.println("dir_len,"+ pr.getVertexScore("dir_len"));
	System.out.println("version_etc,"+ pr.getVertexScore("version_etc"));
	System.out.println("version_etc_va,"+ pr.getVertexScore("version_etc_va"));
	System.out.println("close_stdout,"+ pr.getVertexScore("close_stdout"));
	System.out.println("dir_name,"+ pr.getVertexScore("dir_name"));
	System.out.println("rpl_getcwd,"+ pr.getVertexScore("rpl_getcwd"));
	System.out.println("rpl_free,"+ pr.getVertexScore("rpl_free"));
	System.out.println("xmalloc,"+ pr.getVertexScore("xmalloc"));
	System.out.println("xrealloc,"+ pr.getVertexScore("xrealloc"));
	System.out.println("quotearg_colon,"+ pr.getVertexScore("quotearg_colon"));
	System.out.println("bkm_scale___0,"+ pr.getVertexScore("bkm_scale___0"));
	System.out.println("free_cwd,"+ pr.getVertexScore("free_cwd"));
	System.out.println("quotearg_char,"+ pr.getVertexScore("quotearg_char"));
	System.out.println("xstrtoul,"+ pr.getVertexScore("xstrtoul"));
	System.out.println("bkm_scale_by_power___0,"+ pr.getVertexScore("bkm_scale_by_power___0"));
	System.out.println("mode_free,"+ pr.getVertexScore("mode_free"));
	System.out.println("save_cwd,"+ pr.getVertexScore("save_cwd"));
	System.out.println("xgetcwd,"+ pr.getVertexScore("xgetcwd"));
	System.out.println("rpl_getcwd,"+ pr.getVertexScore("rpl_getcwd"));
	System.out.println("quotearg_n_options,"+ pr.getVertexScore("quotearg_n_options"));
	System.out.println("set_char_quoting,"+ pr.getVertexScore("set_char_quoting"));
	System.out.println("make_node_op_equals,"+ pr.getVertexScore("make_node_op_equals"));
	System.out.println("mode_append_entry,"+ pr.getVertexScore("mode_append_entry"));
	System.out.println("mode_adjust,"+ pr.getVertexScore("mode_adjust"));
	System.out.println("main,"+ pr.getVertexScore("main"));
	System.out.println("quotearg_buffer_restyled,"+ pr.getVertexScore("quotearg_buffer_restyled"));
	System.out.println("quotearg_n_style,"+ pr.getVertexScore("quotearg_n_style"));
	System.out.println("mode_compile,"+ pr.getVertexScore("mode_compile"));
	System.out.println("usage,"+ pr.getVertexScore("usage"));
	System.out.println("quotearg_buffer,"+ pr.getVertexScore("quotearg_buffer"));
	System.out.println("gettext_quote,"+ pr.getVertexScore("gettext_quote"));
	System.out.println("quoting_options_from_style,"+ pr.getVertexScore("quoting_options_from_style"));
	System.out.println("xmalloc,"+ pr.getVertexScore("xmalloc"));
	System.out.println("xnmalloc_inline,"+ pr.getVertexScore("xnmalloc_inline"));
	System.out.println("xalloc_die,"+ pr.getVertexScore("xalloc_die"));
	System.out.println("make_path,"+ pr.getVertexScore("make_path"));
	System.out.println("quote_n,"+ pr.getVertexScore("quote_n"));
	System.out.println("xrealloc,"+ pr.getVertexScore("xrealloc"));
	System.out.println("xnrealloc_inline,"+ pr.getVertexScore("xnrealloc_inline"));
	System.out.println("restore_cwd,"+ pr.getVertexScore("restore_cwd"));
	System.out.println("make_dir,"+ pr.getVertexScore("make_dir"));
	System.out.println("quote,"+ pr.getVertexScore("quote"));
    }
}
