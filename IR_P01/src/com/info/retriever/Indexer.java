package com.info.retriever;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

public class Indexer {

	private IndexWriter writer;

	public Indexer(String indexDirectoryPath) throws IOException {
		// this directory will contain the indexes

		Path indexPath = Paths.get(indexDirectoryPath);
		Directory indexDirectory = FSDirectory.open(indexPath);

		// Used English analyzer to preprocess the content
		IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
		File dirObj = new File(indexDirectoryPath);
		File[] files = dirObj.listFiles();
		for (File item : files) {
			if (item.exists()) {
				item.delete();
			} else {
				System.out.println("cannot delete the file");
			}
		}

		writer = new IndexWriter(indexDirectory, config);
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	// Returns the documents with fields for a file
	private Document getDocument(File file) throws IOException {
		Document document = new Document();
		// index last modified date
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		StringField last_modified = new StringField(LuceneFields.LAST_UPDATED,
				dateFormat.format(file.lastModified()).toString(), Field.Store.YES);

		// index file name
		StringField fileNameField = new StringField(LuceneFields.FILE_NAME, file.getName(), Field.Store.YES);

		// index file path
		StringField filePathField = new StringField(LuceneFields.FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

		if (file.getName().contains(".html")) {
			String d_title = "";
			String d_summary = "";
			String d_content = "";
			StringBuilder contentBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
			String content = contentBuilder.toString();

			org.jsoup.nodes.Document documenthtml = Jsoup.parse(content);

			d_title = documenthtml.getElementsByTag("title").text();
			d_summary = documenthtml.getElementsByTag("summary").text();
			d_content = documenthtml.getElementsByTag("body").text();

			// index file contents
			TextField contentField = new TextField(LuceneFields.CONTENTS, d_content, Field.Store.YES);

			// Index summary
			StringField summaryField = new StringField(LuceneFields.SUMMARY, d_summary, Field.Store.YES);

			// Index title
			TextField titleField = new TextField(LuceneFields.TITLE, d_title, Field.Store.YES);

			document.add(contentField);
			document.add(summaryField);
			document.add(titleField);
			document.add(fileNameField);
			document.add(filePathField);
			document.add(last_modified);

		}

		else {
			// index file contents
			TextField contentField = new TextField(LuceneFields.CONTENTS, new FileReader(file));

			document.add(contentField);
			document.add(fileNameField);
			document.add(filePathField);
			document.add(last_modified);

		}

		return document;
	}

	private void indexFile(File file) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}

	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		// get all files present in the directory and sub directories
		File[] files = new File(dataDirPath).listFiles();

		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file);
			} else if (file.isDirectory()) {
				createIndex(file + "", filter);
			}
		}
		return writer.numRamDocs();
	}
}
