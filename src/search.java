import java.io.IOException;

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
        String tokenizedString = SpokendocBaseline.joinWithSplitter(Tokenizer.tokenize(q), " ");
        System.out.println(tokenizedString);
		QueryParser parser = spokendoc.getQueryParser("content");
	    Query query = parser.parse(tokenizedString);
		IndexSearcher searcher = spokendoc.getIndexSearcher();
	    TopDocs results = searcher.search(query, null, 1000);
	    
	    // Show results
	    // スコアの値は生の値なのか、正規化されてるのかわからないが、後でTFIDFにでも変えれば分かりそう。後回し
	    for (ScoreDoc sd: results.scoreDocs){
	    	int docID = sd.doc;
	    	float score = sd.score;
	    	Document doc = searcher.doc(docID);
//	    	System.out.println(doc.get("id") + ", " + doc.get("content") + ", score:" + Float.toString(score));
	    	System.out.println(doc.get("id") + ", score:" + Float.toString(score));
	    }
	    System.out.println("Done searching!");
	}

}
