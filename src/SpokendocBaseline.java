import java.io.File;
import java.io.IOException;

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
	public Directory directory;
	// デフォルトの類似度は改良TFIDF.他にBM25, LanguageModelなどがある
	// public BM25Similarity similarity
	public LMDirichletSimilarity similarity;
	
	public SpokendocBaseline(String path) throws IOException {
		this.analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		//メモリにインデックス保存する。テスト用
		//this.directory = new RAMDirectory();
		//MMapDirectory: 読み込みはメモリ、書き出しはファイルシステムらしい
		this.directory = MMapDirectory.open(new File(path));
		this.similarity = new LMDirichletSimilarity();
	}
	public IndexWriter getIndexWriter() throws IOException{
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		// Index側でも類似度を変更することに注意
		config.setSimilarity(similarity);
        IndexWriter writer = new IndexWriter(directory, config);

		return writer;
	}
	public IndexSearcher getIndexSearcher() throws IOException {
		// reader.close()を呼ばなくて大丈夫？
	    DirectoryReader reader = DirectoryReader.open(this.directory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    // 類似度を変更
	    searcher.setSimilarity(similarity);

		return searcher;
	}
	public QueryParser getQueryParser(String field) {
	    QueryParser parser = new QueryParser(Version.LUCENE_46,field, this.analyzer);
		return parser;
	}
}