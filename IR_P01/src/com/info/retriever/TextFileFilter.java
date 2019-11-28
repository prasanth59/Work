package com.info.retriever;

import java.io.File;
import java.io.FileFilter;

public class TextFileFilter implements FileFilter {

   @Override
   public boolean accept(File pathname) {	   
	   if(pathname.getName().toLowerCase().endsWith(".txt")){
		   return pathname.getName().toLowerCase().endsWith(".txt");
	   }
	   else if(pathname.getName().toLowerCase().endsWith(".html")) {
		   return pathname.getName().toLowerCase().endsWith(".html");
	   }
	return false;
   }
}