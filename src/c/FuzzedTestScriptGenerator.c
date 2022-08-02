#include<sys/types.h>
#include<sys/stat.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<dirent.h>
#include<errno.h>
#include<ctype.h>
#include<unistd.h>
#include<stdbool.h>
#include<libgen.h>


char* readFileToString(char* fpath) {
  struct stat info;
  stat(fpath, &info);
  
  char *content = (char *)malloc(info.st_size * sizeof(char));
  FILE *fp = fopen(fpath, "rb");
  fread(content, info.st_size, 1, fp);
  fclose(fp);

  return content;
}

//NOTE: str shouldn't be binary.
void writeStringToFile(FILE* out_f, char* str) {
  if (out_f) {
    fputs(str, out_f);
    fclose(out_f);
  }
}

char* concat(const char* a, const char* b) {
  int lena = strlen(a);
  int lenb = strlen(b);
  char* con = malloc(lena + lenb + 1);
  memcpy(con, a, lena);
  memcpy(con+lena, b, lenb+1);
  return con;
}

char* intToString(int i) {
  char* str = malloc(50);
  snprintf(str, 49, "%d", i);
  return str;
}

int stringToInt(const char* str) {
  return atoi(str);
}

char* trim(const char* str) {
  const char* end;
  size_t out_size;

  //Trim leading space
  while (isspace((unsigned char)*str)) str++;
  if (*str == 0) { return 0; }

  //Trim trailing space
  end = str + strlen(str) - 1;
  while (end > str && isspace((unsigned char)*end)) end --;
  end++;

  //Set output size to minimum of trimmed string length and buffer size minus 1
  out_size = (end - str);

  //Copy trimmed string and add null terminator
  char* out = malloc(out_size+1);
  memcpy(out, str, out_size);
  out[out_size] = 0;

  return out;
}

char* trimEndingNewLine(char* str) {
  size_t out_size;

  //Trim trailing space
  if (str[strlen(str) - 1] == '\n') {
    //==========
    //printf("Ending with new line\n");
    //==========    
    out_size = strlen(str) - 1;
    char* out = malloc(out_size + 1);
    memcpy(out, str, out_size);
    out[out_size] = 0;
    return out;
  }
  else {
    return str;
  }
}


char* addQuoteIfNeeded(const char* str) {
  if (strstr(str, "\'") != NULL || //E.g., Don't
      strstr(str, "*") != NULL) { //E.g., randomtext*
    char* new_str = concat("\"", str);
    new_str = concat(new_str, "\"");
    return new_str;
  }
  else {
    char* new_str = concat("\'", str);
    new_str = concat(new_str, "\'");
    return new_str;
  }
}

/* tar_path should represent a copy. */
char* fuzzFileOrDir(const char* tar_path) {
  char* fuzz_bin_path = "${DEBAUG_DIR}/bin/radamsafuzz.sh";
  char* fuzz_cmd = concat(fuzz_bin_path, " ");
  fuzz_cmd = concat(fuzz_cmd, tar_path);
  system(fuzz_cmd);
}

bool isFile(const char* path) {
    struct stat buf;
    stat(path, &buf);
    return S_ISREG(buf.st_mode);
}

bool isDir(const char* path) {
    struct stat buf;
    stat(path, &buf);
    return S_ISDIR(buf.st_mode);
}

int main (int argc, char** argv) {
  //*******************
  //Macros
  //FUZZIDIR: Save fuzzed files.
  //FUZZSDIR: Save fuzz scripts.
  //FUZZNUM: # of Fuzzed versions.
  //NOFUZZ: Non-arg macro.
  //*******************

  int fuzzid, fuzznum;
  #ifdef NOFUZZ
      fuzzid = 0;
      fuzznum = 0;
  #else
      fuzzid = 1;
      fuzznum = FUZZNUM;
  #endif

  srand(time(0)); //Initialize rand()

  char *stdin_str = NULL;        //This is the standard input
  unsigned long fileLen = 0;     //File length of standard input (treated as a file)

  for (; fuzzid<=fuzznum; fuzzid++) {
    
  //char* test_command =
  //  "#!/bin/bash\n\nBIN=$1\nOUTDIR=$2\nTIMEOUT=$3\nINDIR=$4\nARGDIR=$5\nOUTFILE=$6\n\ntimeout -k 9 ${TIMEOUT}s $BIN";
  char* test_command =
    "#!/bin/bash\n\nBIN=$1\nTIMEOUT=$2\n\ntimeout -k 9 ${TIMEOUT}s $BIN";

    

  //Set up the fuzz dir
  char* target_dpath = concat(FUZZIDIR, "/");
  target_dpath = concat(target_dpath, intToString(fuzzid));
  FILE* target_dir = fopen(target_dpath, "rb");
  if (target_dir) {
    //Remove content under target_dir
    fclose(target_dir);
    char* rm_fuzzed_ctnt_cmd = concat("rm -r ", target_dpath);
    rm_fuzzed_ctnt_cmd = concat(rm_fuzzed_ctnt_cmd, "/*");
    system(rm_fuzzed_ctnt_cmd);
  }
  else {
    //Mkdir for target_dir
    char* mkdir_target_dir_cmd = concat("mkdir -p ", target_dpath);
    system(mkdir_target_dir_cmd);
  }


  //Process arguments
  int fid = 0, aid = 0;
  int arg_i;
  for (arg_i=0; arg_i<argc; arg_i++) {
    char* curr_arg = argv[arg_i];
    int isfile = 0;
    if (arg_i != 0) {
      //=======================
      //printf("Arg: %s\n", curr_arg);
      //=======================	  
      
      //Check if the argument starts with "-" (if so, this argument is not a file path)
      if (strncmp(curr_arg, "-", strlen("-")) == 0) {
	//If so, do nothing (will add curr_arg to test_command later)
	//Add the exact arg
	test_command = concat(test_command, " ");
	test_command = concat(test_command, addQuoteIfNeeded(curr_arg));
      }
      else {
	//Check if the argument is a file
	FILE* curr_arg_f = fopen(curr_arg, "rb");
	if (curr_arg_f) {
	  isfile = 1;
	  fclose(curr_arg_f);

	  //=======================
	  //printf("File to Process: %s\n", curr_arg);
	  //=======================

	  //To-be-fuzzed file/dir
	  char* curr_arg_cp = strdup(curr_arg);
	  char* fname = basename(curr_arg_cp);
	  //char* target_fpath = concat(target_dpath, "/f");
	  //target_fpath = concat(target_fpath, intToString(fid));
	  char* target_fpath = concat(target_dpath, "/");
	  target_fpath = concat(target_fpath, fname); //We need to use the original name

	  //First copy the original to fuzz dir
	  char* cp_to_fuzz_dir_cmd = concat("cp -r ", curr_arg);
	  cp_to_fuzz_dir_cmd = concat(cp_to_fuzz_dir_cmd, " ");
	  cp_to_fuzz_dir_cmd = concat(cp_to_fuzz_dir_cmd, target_fpath);
	  system(cp_to_fuzz_dir_cmd);
	  
	  //Fuzz the copied file/dir
	  if (fuzzid > 0) {
	    fuzzFileOrDir(target_fpath); //0 is for original test
	  }
	  fid += 1;
	  
	  test_command = concat(test_command, " ");
	  test_command = concat(test_command, target_fpath);
	}
	else {
	  //=======================
	  //printf("Arg to Process: %s\n", curr_arg);
	  //=======================	  

	  char* target_apath = concat(target_dpath, "/a");
	  target_apath = concat(target_apath, intToString(aid));

	  //Write arg to a file (target_apath)
	  FILE* target_afw = fopen(target_apath, "wb");
	  writeStringToFile(target_afw, curr_arg);
	  aid += 1;

	  if (fuzzid > 0) {
	    fuzzFileOrDir(target_apath);
	  }
	  else {
	    //=======================
	    //printf("No fuzz for %s\n", target_apath);
	    //=======================
	  }
	  
	  //============
	  //printf("Arg fpath: %s\n", target_apath);
	  //============
	  //char* fuzzed_arg = trim(readFileToString(target_apath));
	  char* fuzzed_arg = trimEndingNewLine(readFileToString(target_apath));
	  if (fuzzed_arg)
	  //=======================
	  //printf("Final Arg: %s\n", fuzzed_arg);
	  //=======================
	  test_command = concat(test_command, " ");
	  test_command = concat(test_command, addQuoteIfNeeded(fuzzed_arg));
	}
      }
    }
  }

  //Process stdin input
  if (!isatty(STDIN_FILENO)) { //Otherwise, no stdin to process
  //=======================
  //printf("Input captured as stdin\n");
  //=======================
  if (stdin_str == NULL) {
    FILE* stdin_f = freopen(NULL, "rb", stdin);
    if (stdin_f) {
      //=======================
      //printf("Stdin to Process\n");
      //=======================
      
      fseek(stdin_f, 0, SEEK_END);
      fileLen = ftell(stdin_f);
      fseek(stdin_f, 0, SEEK_SET);
      stdin_str = (char *)malloc(fileLen+1);
      
      fread(stdin_str, fileLen, 1, stdin_f);
      fclose(stdin_f);
    }      
  }

  if (stdin_str != NULL) {
    //=======================
    //printf("Stdin string: %s\n", stdin_str);
    //=======================
    char* target_fpath = concat(target_dpath, "/f");
    target_fpath = concat(target_fpath, intToString(fid));

    //Write stdin string to fuzz dir
    FILE* target_f = fopen(target_fpath, "wb");
    fwrite(stdin_str, fileLen, 1, target_f);
    fid += 1;
    
    //Fuzz the copied file/dir
    if (fuzzid > 0) {
      fuzzFileOrDir(target_fpath);
    }

    test_command = concat(test_command, " <");
    test_command = concat(test_command, target_fpath);
  }
  }

  //Add output part
  //char* output_fpath = "$OUTDIR/$OUTFILE";
  //test_command = concat(test_command, " >");
  //test_command = concat(test_command, output_fpath);

  //Write test command to file
  //printf("Test Command:\n%s\n", test_command);
  char* script_fpath = concat(FUZZSDIR, "/"); //E.g., XXX/1_0, where 1 is testid and 0 is fuzzid.
  //script_fpath = concat(script_fpath, OLDTESTID);
  //script_fpath = concat(script_fpath, "_");
  script_fpath = concat(script_fpath, intToString(fuzzid));
  FILE* script_f = fopen(script_fpath, "wb");
  writeStringToFile(script_f, test_command); //File closed within this function

  } //Loop for fuzz num
}

