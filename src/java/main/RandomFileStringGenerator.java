package edu.gatech.cc.debaug;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomFileStringGenerator
{
    public static void main(String[] args) {
	System.out.println(getRandomString());
    }
    
    public static String getRandomString() {
	//Generate a file string
	int lnum = getRandom(1, 100);
	StringBuilder sb = null;
	for (int i=0; i<lnum; i++) {
	    if (sb == null) { sb = new StringBuilder(); }
	    else { sb.append("\n"); }
	    int cnum = getRandom(1, 100);	    
	    sb.append(RandomStringUtils.randomAscii(cnum));
	}
	return sb.toString();
    }
    
    public static int getRandom(int l, int h) {
	return l + (int) ((h-l+1) * Math.random());
    }
}
