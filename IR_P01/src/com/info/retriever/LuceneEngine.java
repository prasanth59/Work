package com.info.retriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneEngine {

	String indexDir = "D:\\Edu\\IR\\IR_P01\\data\\index1";
	// String dataDir = "/Users/vikram/eclipse-workspace/IR_P01/data/input1";
	Indexer indexer;
	Searcher searcher;

	public static void main(String[] args) {
		LuceneEngine engine;
		try {
			engine = new LuceneEngine();
			engine.createIndex(args[0]);
			// Continuous checking of search functionality
			while (true) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Enter search Term: ");
				String name = reader.readLine();
				if (name.equalsIgnoreCase("exit")) {
					System.exit(0);
				}
				engine.performSearch(name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createIndex(String dataDir) throws IOException {
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
	}

	private void performSearch(String searchQuery) throws IOException, java.text.ParseException {
		searcher = new Searcher(indexDir);
//		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.getResults(searchQuery);
//		long endTime = System.currentTimeMillis();
		int rank = 1;
//		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
		if (hits.scoreDocs.length == 0) {
			System.out.println("No results found");
		} else {
			// Print the details of the results retrieved for the searched terms
			System.out.println("Retrieved results");
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				System.out.println("---------------------------------------------------------");
				Document doc = searcher.getResultDocument(scoreDoc);
				System.out.println("Rank: " + (rank));
				System.out.println("Path: " + doc.get(LuceneFields.FILE_PATH));
				System.out.println("Last updated: " + doc.get(LuceneFields.LAST_UPDATED));
				System.out.println("Relevance Score:" + scoreDoc.score);
				if (doc.get(LuceneFields.TITLE) != null && !doc.get(LuceneFields.TITLE).contentEquals(" ")) {
					System.out.println("Title: " + doc.get(LuceneFields.TITLE));
				}
				if (doc.get(LuceneFields.SUMMARY) != null && !doc.get(LuceneFields.SUMMARY).contentEquals(" ")) {
					System.out.println("Summary: " + doc.get(LuceneFields.SUMMARY));
				}
				System.out.println("---------------------------------------------------------");
				rank += 1;
			}
		}
		searcher.close();
	}
}
