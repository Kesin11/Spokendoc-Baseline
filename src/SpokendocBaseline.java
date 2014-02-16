import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

/** 
 * 索引付けと検索で共通の様々な設定を管理するクラス.
 * <p> 
 * AnalyzerやSimilarityは色々と使うので一元管理しておく<br>
 * {@link Indexer}、{@link Search}はそれぞれIndexWriterとIndexSearcherをこのクラスのインスタンスから取得する<br>
 * propertiesから取得した各パラメータの値もこのクラスのインスタンスが管理する<br>
 */
public class SpokendocBaseline {
	public Analyzer analyzer;
	// directoryのclose()は呼び出さなくても大丈夫なのか？
	public Directory indexDirectory;
	public String task;
	public String freqfilePath;
	public String tokenizerPath;
	public String resultPath;
	public Similarity similarity;
	public Boolean normalization;
	
	/**
	 *  
	 * @param propatiesPath 設定ファイル.propertiesのパス
	 * @throws IOException
	 */
	public SpokendocBaseline(String propatiesPath) throws IOException {
		Properties conf = new Properties();
		FileInputStream fis = new FileInputStream(new File(propatiesPath));
		conf.load(fis);
		this.analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		this.task = conf.getProperty("task");
		this.freqfilePath = conf.getProperty("freqfile");
		this.tokenizerPath = conf.getProperty("tokenizer");
		this.resultPath = conf.getProperty("result");
		this.normalization = new Boolean(conf.getProperty("normalization"));
		//メモリにインデックス保存する。テスト用
		//this.directory = new RAMDirectory();
		//MMapDirectory: 読み込みはメモリ、書き出しはファイルシステムらしい
		String indexPath = conf.getProperty("index");
		this.indexDirectory = MMapDirectory.open(new File(indexPath));

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

	/**
	 * indexに書き込むためのIndexWriterを取得する 
	 * @return analyzerとsimilarityがセット済みのIndexWriter 
	 * @throws IOException
	 */
	public IndexWriter getIndexWriter() throws IOException{
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		// Index側でも類似度を変更することに注意
		config.setSimilarity(this.similarity);
        IndexWriter writer = new IndexWriter(this.indexDirectory, config);

		return writer;
	}
	/**
	 * indexから検索を行うためのIndexSearcherを取得する 
	 * 
	 * @return indexDirectoryとsimilarityがセット済みのIndexSearcher 
	 * @throws IOException
	 */
	public IndexSearcher getIndexSearcher() throws IOException {
		// reader.close()を呼ばなくて大丈夫？
	    DirectoryReader reader = DirectoryReader.open(this.indexDirectory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    // 類似度を変更
	    searcher.setSimilarity(this.similarity);

		return searcher;
	}
	/**
	 * 検索に使用するQueryParserを取得する 
	 * @param field 検索に使用するindexのフィールド名
	 * @return analyzerがセット済みのQueryParser 
	 */
	public QueryParser getQueryParser(String field) {
	    QueryParser parser = new QueryParser(Version.LUCENE_46,field, this.analyzer);
		return parser;
	}
}