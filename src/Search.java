import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
	public static void searching(String propertiesPath, String query) throws IOException, ParseException, InterruptedException, ParserConfigurationException, TransformerException {
		SpokendocBaseline spokendoc = new SpokendocBaseline(propertiesPath);

		// クエリファイルから検索、XMLに出力
		if (new File(query).exists()){
    		System.out.println("Search from queries file...");
		    searchFromFile(spokendoc, "queries.txt");
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
    		System.out.println(tokenizedString);
            TopDocs results = searchFromString(spokendoc, query);
	        printResult(spokendoc, results);
		}
	}

	// クエリの文字列から検索
	private static TopDocs searchFromString(SpokendocBaseline spokendoc, String queryString)
			throws IOException, InterruptedException, ParseException {
		if (spokendoc.normalization) {
			queryString = Util.normalizeString(queryString);
		}
		String tokenizedString = Util.joinWithSplitter(Tokenizer.tokenize(queryString, spokendoc.tokenizerPath), " ");
		tokenizedString = tokenizedString.replaceAll("([¥+¥-¥&¥|¥!¥(¥)¥{¥}¥[¥]¥^¥~¥*¥?¥:¥¥])", "¥"+"$1");

		QueryParser parser = spokendoc.getQueryParser("content");
	    Query query = parser.parse(tokenizedString);
		IndexSearcher searcher = spokendoc.getIndexSearcher();
	    TopDocs results = searcher.search(query, null, 1000);
	    return results;
	}

	// 検索結果を標準出力
	private static void printResult(SpokendocBaseline spokendoc, TopDocs results)
			throws IOException {
		for (ScoreDoc sd: results.scoreDocs){
	    	int docID = sd.doc;
	    	float score = sd.score;
	    	Document doc = spokendoc.getIndexSearcher().doc(docID);
	    	System.out.println(doc.get("id") + ", score:" + Float.toString(score));
	    }
	}
	private static void searchFromFile(SpokendocBaseline spokendoc, String queryFile) throws IOException, InterruptedException, ParseException, ParserConfigurationException, TransformerException{
		SpokenDocXML spokenDocXML = new SpokenDocXML(spokendoc.getIndexSearcher());
		BufferedReader br = new BufferedReader(new FileReader(queryFile));
		String line;
		ArrayList<HashMap<String, String>> queries = new ArrayList<HashMap<String,String>>();
		// クエリファイルからid, クエリ文字列読み取り
		while((line = br.readLine()) != null){
			HashMap<String, String> idQuery = new HashMap<String, String>();
			idQuery.put("id", line.split(" ")[0]);
			idQuery.put("query", line.split(" ")[1]);
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
		spokenDocXML.createXml(spokendoc.resultPath);
	}
}
