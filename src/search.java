import java.io.BufferedReader;
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

public class search {
	public static void main(String[] args) throws IOException, ParseException, InterruptedException, ParserConfigurationException, TransformerException {
		SpokendocBaseline spokendoc = new SpokendocBaseline("index");
	    // Search
		String q = "音声ドキュメント処理のパッセージ検索はこれから利便性の高い検索手法になる";
//        TopDocs results = searchFromString(spokendoc, q);
//	    System.out.println("Query: " + q);
//	    printResult(spokendoc, results);

		searchFromFile(spokendoc, "queries.txt", "result.xml");
	    System.out.println("Done searching!");
	}

	// クエリの文字列から検索
	private static TopDocs searchFromString(SpokendocBaseline spokendoc, String q)
			throws IOException, InterruptedException, ParseException {
		String tokenizedString = SpokendocBaseline.joinWithSplitter(Tokenizer.tokenize(q), " ");
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
	private static void searchFromFile(SpokendocBaseline spokendoc, String queryFile, String outXml) throws IOException, InterruptedException, ParseException, ParserConfigurationException, TransformerException{
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
		spokenDocXML.createXml(outXml);
	}
}
