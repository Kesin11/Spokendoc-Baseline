import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;

/** 
 * 索引付けと検索で共通のAnalyzer, Directory, Similarityを管理するクラス
 * 索引付け、検索クラスはそれぞれIndexWriterとIndexSearcherをこのクラスのインスタンスから取得する 
 * そのうち設定ファイルを読み込むように修正したい
 */
public class SpokendocBaseline {
	public Analyzer analyzer;
	// directoryのclose()は呼び出さなくても大丈夫なのか？
	public Directory indexDirectory;
	public String freqfilePath;
	public String tokenizerPath;
	public String resultPath;
	public Similarity similarity;
	
	public SpokendocBaseline(String propatiesPath) throws IOException {
		Properties conf = new Properties();
		FileInputStream fis = new FileInputStream(new File(propatiesPath));
		conf.load(fis);
		this.analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		this.freqfilePath = conf.getProperty("freqfile");
		this.tokenizerPath = conf.getProperty("tokenizer");
		this.resultPath = conf.getProperty("result");
		//メモリにインデックス保存する。テスト用
		//this.directory = new RAMDirectory();
		//MMapDirectory: 読み込みはメモリ、書き出しはファイルシステムらしい
		String indexPath = conf.getProperty("index");
		this.indexDirectory = MMapDirectory.open(new File(indexPath));
        // デフォルトの類似度は改良TFIDF.他にBM25, LanguageModelなどがある
		String selectedSimilarity = conf.getProperty("similarity");
		if (selectedSimilarity.equals("LMDirichlet")) {
			float mu = Float.valueOf(conf.getProperty("mu"));
		    this.similarity = new LMDirichletSimilarity(mu);
		}
		else if (selectedSimilarity.equals("BM25")) {
			float k1 = Float.valueOf(conf.getProperty("k1"));
			float b = Float.valueOf(conf.getProperty("b"));
		    this.similarity = new BM25Similarity(k1, b);
		}
		else {
		    this.similarity = new DefaultSimilarity();	
		}
		fis.close();
	}
	public IndexWriter getIndexWriter() throws IOException{
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		// Index側でも類似度を変更することに注意
		config.setSimilarity(this.similarity);
        IndexWriter writer = new IndexWriter(this.indexDirectory, config);

		return writer;
	}
	public IndexSearcher getIndexSearcher() throws IOException {
		// reader.close()を呼ばなくて大丈夫？
	    DirectoryReader reader = DirectoryReader.open(this.indexDirectory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    // 類似度を変更
	    searcher.setSimilarity(this.similarity);

		return searcher;
	}
	public QueryParser getQueryParser(String field) {
	    QueryParser parser = new QueryParser(Version.LUCENE_46,field, this.analyzer);
		return parser;
	}
}