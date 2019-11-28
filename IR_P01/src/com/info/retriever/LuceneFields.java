package com.info.retriever;

//Field names to be used for indexing the documents
public class LuceneFields {
   public static final String CONTENTS = "contents";
   public static final String FILE_NAME = "filename";
   public static final String FILE_PATH = "filepath";
   public static final String LAST_UPDATED = "last_update";
   public static final int MAX_SEARCH = 10;
   
   // For html docs
   public static final String TITLE = "title";
   public static final String SUMMARY = "summary";
}
