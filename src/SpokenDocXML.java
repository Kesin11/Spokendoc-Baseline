import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SpokenDocフォーマットのXML作成
 */
public class SpokenDocXML {
	private IndexSearcher usedSearcher;
	public ArrayList<TopDocs> topDocArrayList;
	public ArrayList<String> queryArrayList;

    public SpokenDocXML(IndexSearcher usedSearcher){
    	this.usedSearcher = usedSearcher;
    	this.topDocArrayList = new ArrayList<TopDocs>();
    	this.queryArrayList = new ArrayList<String>();
    }

	public void createXml(String filePath, String task) throws ParserConfigurationException, TransformerException, IOException{
		// DOMオブジェクト作成
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();

		//検索結果以外のタグ
		Element root = document.createElement("ROOT");
		document.appendChild(root);
		Element run = document.createElement("RUN");
		root.appendChild(run);
		Element subtask = document.createElement("SUBTASK");
		run.appendChild(subtask);
		subtask.appendChild(document.createTextNode("SCR"));
		Element system = document.createElement("SYSTEM");
		root.appendChild(system);
		Element systemDescription = document.createElement("SYSTEM-DESCRIPTION");
		system.appendChild(systemDescription);
		systemDescription.appendChild(document.createTextNode("Lucene baseline"));

		//検索結果の追加
		Element resultElement = document.createElement("RESULTS");
		root.appendChild(resultElement);
		for (int i = 0; i < queryArrayList.size(); i++) {
			String query = queryArrayList.get(i);
		    Element queryElement = document.createElement("QUERY");
		    queryElement.setAttribute("id", query);
		    resultElement.appendChild(queryElement);

			TopDocs topDocs = topDocArrayList.get(i);
		    for (int j = 0; j < topDocs.scoreDocs.length; j++) {
		    	ScoreDoc sd = topDocs.scoreDocs[j];
		    	int docId = sd.doc;
//		    	float score = sd.score;
		        Element candidateElement = document.createElement("CANDIDATE");

		    	String docName = usedSearcher.doc(docId).get("id");
		        // パッセージ検索では開始発話と終了発話の要素も必要
		        // freqfileの"@documentID:from-to"パターンからid, from, toを抽出
		    	if (task.equals("passage")) {
		    	    Pattern pattern = Pattern.compile("(.+):(.+)-(.+)");
		    	    Matcher result = pattern.matcher(docName);
		    	    result.find();
		            candidateElement.setAttribute("document", result.group(1));
		            candidateElement.setAttribute("ipu-from", result.group(2));
		            candidateElement.setAttribute("ipu-to", result.group(3));
				}
		    	else {
		            candidateElement.setAttribute("document", docName);
				}
		    	String rank = Integer.toString(j + 1);
		        candidateElement.setAttribute("rank", rank);
		        queryElement.appendChild(candidateElement);
			}
		}
		//DOMの体裁を整える
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer();
		tf.setOutputProperty("indent", "yes");
		//インデント幅
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		tf.setOutputProperty("encoding", "UTF-8");
		tf.setOutputProperty("method", "xml");

		//XMLファイル生成
		File file = new File(filePath);
		tf.transform(new DOMSource(document), new StreamResult(file));
	}
}
