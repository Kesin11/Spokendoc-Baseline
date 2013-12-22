import java.io.File;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;

public class SpokendocBaseline {
	private static void addDoc(IndexWriter writer, String id, String content) throws IOException{
		Document doc = new Document();
        doc.add(new Field("id", id, TextField.TYPE_STORED));
        doc.add(new Field("content", content, TextField.TYPE_STORED));
        writer.addDocument(doc);
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		// Analyzer
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

		// Indexing
		//メモリにインデックス保存する。テスト用
		//Directory directory = new RAMDirectory();
		//MMapDirectory: 読み込みはメモリ、書き出しはファイルシステムらしい
		Directory directory = MMapDirectory.open(new File("index"));
		// デフォルトの類似度は改良TFIDF.他にBM25, LanguageModelなどがある
		//BM25Similarity similarity = new BM25Similarity(); 
		LMDirichletSimilarity similarity = new LMDirichletSimilarity();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		// Index側でも類似度を変更することに注意
		config.setSimilarity(similarity);
        IndexWriter writer = new IndexWriter(directory, config);
        addDoc(writer, "doc1", "This is the text to be indexed");
        addDoc(writer, "doc2", "High score text text");
        addDoc(writer, "doc3", "Lower score. Score is maybe normalized using text length");
        addDoc(writer, "doc4", "text");
	    writer.close();
	    
	    // Search
	    DirectoryReader reader = DirectoryReader.open(directory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    // 類似度を変更
	    searcher.setSimilarity(similarity);
	    QueryParser parser = new QueryParser(Version.LUCENE_46,"content", analyzer);
	    Query query = parser.parse("text");
	    TopDocs results = searcher.search(query, null, 1000);
	    
	    // Show results
	    // スコアの値は生の値なのか、正規化されてるのかわからないが、後でTFIDFにでも変えれば分かりそう。後回し
	    for (ScoreDoc sd: results.scoreDocs){
	    	int docID = sd.doc;
	    	float score = sd.score;
	    	Document doc = searcher.doc(docID);
	    	System.out.println(doc.get("id") + ", " + doc.get("content") + ", score:" + Float.toString(score));
	    }
	    reader.close();
	    directory.close();
	}
}
