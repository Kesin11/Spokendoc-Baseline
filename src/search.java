import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class search {
	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
		SpokendocBaseline spokendoc = new SpokendocBaseline("index");
	    // Search
		String q = "音声ドキュメント処理のパッセージ検索はこれから利便性の高い検索手法になる";
//        searchFromString(spokendoc, q);
		searchFromFile(spokendoc, "queries.txt");
	    System.out.println("Done searching!");
	}

	// クエリの文字列から検索
	private static void searchFromString(SpokendocBaseline spokendoc, String q)
			throws IOException, InterruptedException, ParseException {
		String tokenizedString = SpokendocBaseline.joinWithSplitter(Tokenizer.tokenize(q), " ");
		QueryParser parser = spokendoc.getQueryParser("content");
	    Query query = parser.parse(tokenizedString);
		IndexSearcher searcher = spokendoc.getIndexSearcher();
	    TopDocs results = searcher.search(query, null, 1000);

	    // Show results
        System.out.println(tokenizedString);
	    for (ScoreDoc sd: results.scoreDocs){
	    	int docID = sd.doc;
	    	float score = sd.score;
	    	Document doc = searcher.doc(docID);
	    	System.out.println(doc.get("id") + ", score:" + Float.toString(score));
	    }
	}
	private static void searchFromFile(SpokendocBaseline spokendoc, String filePath) throws IOException, InterruptedException, ParseException{
		BufferedReader br = new BufferedReader(new FileReader(filePath));
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
			System.out.println(hashMap.get("id"));
			searchFromString(spokendoc, hashMap.get("query"));
		}
	}
}
