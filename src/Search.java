import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Search {
	/**
	 * 索引から実際に検索を担当するクラス 
	 * @param propertiesPath .propertiesのパス
	 * @param query 検索クエリ。文字列、又はクエリファイルへのパス
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static void searching(String propertiesPath, String query) throws IOException, ParseException, InterruptedException, ParserConfigurationException, TransformerException {
		SpokendocBaseline spokendoc = new SpokendocBaseline(propertiesPath);

		// クエリファイルから検索、XMLに出力
		if (new File(query).exists()){
    		System.out.println("Search from queries file...");
		    searchFromFile(spokendoc, query);
		}
		// 1行のクエリから検索、標準出力
		else{
    		System.out.println("Search from query string");
	        System.out.println("Query: " + query);
	        // クエリ文字列正規化
	        if (spokendoc.normalization) {
	            query = Util.normalizeString(query);
			}
    		String tokenizedString = Util.joinWithSplitter(Tokenizer.tokenize(query, spokendoc.tokenizerPath), " ");
    		tokenizedString = QueryParser.escape(tokenizedString);
    		System.out.println(tokenizedString);
            TopDocs results = searchFromString(spokendoc, query);
	        printResult(spokendoc, results);
		}
	}

	/**
	 * クエリ文字列から1クエリの検索を行う
	 * @param spokendoc {@link SpokendocBaseline}のインスタンス
	 * @param queryString 検索クエリ文字列
	 * @return 検索結果のTopDocsオブジェクト
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	private static TopDocs searchFromString(SpokendocBaseline spokendoc, String queryString)
			throws IOException, InterruptedException, ParseException {
		if (spokendoc.normalization) {
			queryString = Util.normalizeString(queryString);
		}
		String tokenizedString = Util.joinWithSplitter(Tokenizer.tokenize(queryString, spokendoc.tokenizerPath), " ");
		tokenizedString = QueryParser.escape(tokenizedString);

		QueryParser parser = spokendoc.getQueryParser("content");
	    Query query = parser.parse(tokenizedString);
		IndexSearcher searcher = spokendoc.getIndexSearcher();
	    TopDocs results = searcher.search(query, null, 1000);
	    return results;
	}

	/**
	 * 検索結果からスコアを標準出力する
	 * @param spokendoc {@link SpokendocBaseline}のインスタンス
	 * @param results 検索結果のTopDocs
	 * @throws IOException
	 */
	private static void printResult(SpokendocBaseline spokendoc, TopDocs results)
			throws IOException {
		for (ScoreDoc sd: results.scoreDocs){
	    	int docID = sd.doc;
	    	float score = sd.score;
	    	Document doc = spokendoc.getIndexSearcher().doc(docID);
	    	System.out.println(doc.get("id") + ", score:" + Float.toString(score));
	    }
	}
	/**
	 * クエリファイルをパーズして複数クエリの連続検索を行う
	 * @param spokendoc {@link SpokendocBaseline}のインスタンス
	 * @param queryFile クエリファイルへのパス
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private static void searchFromFile(SpokendocBaseline spokendoc, String queryFile) throws IOException, InterruptedException, ParseException, ParserConfigurationException, TransformerException{
		SpokenDocXML spokenDocXML = new SpokenDocXML(spokendoc.getIndexSearcher());
		BufferedReader br = new BufferedReader(new FileReader(queryFile));
		String line;
		ArrayList<HashMap<String, String>> queries = new ArrayList<HashMap<String,String>>();
		// クエリファイルからid, クエリ文字列読み取り
		while((line = br.readLine()) != null){
			// #から開始する行はコメントとして処理しない
			if (line.startsWith("#")){
				continue;
			}
			HashMap<String, String> idQuery = new HashMap<String, String>();
			// クエリファイルのidとクエリを取得
            Pattern pattern = Pattern.compile("(.+?) (.+)");
            Matcher result = pattern.matcher(line);
            result.find();
			idQuery.put("id", result.group(1));
			idQuery.put("query", result.group(2));
			queries.add(idQuery);
		}
		br.close();
		for (HashMap<String, String> hashMap: queries) {
			String queryId = hashMap.get("id");
			TopDocs results = searchFromString(spokendoc, hashMap.get("query"));
			spokenDocXML.queryArrayList.add(queryId);
			spokenDocXML.topDocArrayList.add(results);
		}
		// XML出力
		spokenDocXML.createXml(spokendoc.resultPath, spokendoc.task);
	}
}
