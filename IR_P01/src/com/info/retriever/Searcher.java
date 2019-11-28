package com.info.retriever;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.select.QueryParser;

public class Searcher {

	IndexSearcher indexSearcher;
	QueryParser queryParser;
	MultiFieldQueryParser multiqueryParser;
	Query query;
	WildcardQuery wildCard;
	DirectoryReader indexReader;

	public Searcher(String indexDirectoryPath) throws IOException {

		Path indexPath = Paths.get(indexDirectoryPath);
		Directory indexDirectory = FSDirectory.open(indexPath);

		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);

		// Set similarity to class similarity in lucene
		ClassicSimilarity sim = new ClassicSimilarity();
		indexSearcher.setSimilarity(sim);

		// Using English Analyzer for query pre-processing
		multiqueryParser = new MultiFieldQueryParser(new String[] { LuceneFields.FILE_NAME, LuceneFields.CONTENTS,
				LuceneFields.TITLE, LuceneFields.LAST_UPDATED }, new EnglishAnalyzer());

	}

	public TopDocs getResults(String searchQuery) throws IOException, ParseException {
		try {

			query = multiqueryParser.parse(searchQuery);
			multiqueryParser.setAllowLeadingWildcard(true);
			

		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TopDocs top = indexSearcher.search(query, LuceneFields.MAX_SEARCH);
		return top;
	}

	public Document getResultDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		Document doc = indexSearcher.doc(scoreDoc.doc);
		return doc;
	}

	public void close() throws IOException {
		indexReader.close();
	}
}
