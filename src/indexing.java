import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

public class indexing {
	private static void addDoc(IndexWriter writer, String id, String content) throws IOException{
		Document doc = new Document();
        doc.add(new Field("id", id, TextField.TYPE_STORED));
        doc.add(new Field("content", content, TextField.TYPE_STORED));
        writer.addDocument(doc);
	}

    /**
     *
     * @param path freqfileのパス
     * @return スペース区切りの単語が連続する文字列
     * @throws IOException
     */
	public static HashMap<String, HashMap<String, Integer>> parseFreqfile(String path) throws IOException{
		HashMap<String, HashMap<String, Integer>> idWordTfHash = new HashMap<String, HashMap<String, Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		String id = "";
		HashMap<String, Integer> wordTf = new HashMap<String, Integer>();
		while((line = br.readLine()) != null){
			if (line.startsWith("@")){
				if (!id.isEmpty()){
				    idWordTfHash.put(id, wordTf);
				}

                wordTf = new HashMap<String, Integer>();
				id = line.substring(1);
			}
			else{
				String[] tfWord = line.split(" ");
				wordTf.put(tfWord[1], new Integer(tfWord[0]));
			}
		}
		br.close();
		return idWordTfHash;
	}

	public static void main(String[] args) throws IOException {
		SpokendocBaseline spokendoc = new SpokendocBaseline("lm.properties");

		// Indexing
		IndexWriter writer = spokendoc.getIndexWriter();
		// 同名の索引が存在するときは全消去して作り直し
		writer.deleteAll();
		HashMap<String, HashMap<String, Integer>> idWordTfHash = parseFreqfile(spokendoc.freqfilePath);
		for (Map.Entry<String, HashMap<String, Integer>> idWordTf : idWordTfHash.entrySet()){
			String docId = idWordTf.getKey();
			String indexString = "";
			HashMap<String, Integer> wordTfHash = idWordTf.getValue();
			for (Map.Entry<String, Integer> wordTf : wordTfHash.entrySet()){
				String word = wordTf.getKey();
				Integer num = wordTf.getValue();
				//numの回数だけwordを繰り返す
				//例: "word word word"
				String[] repeatStrings = SpokendocBaseline.repatStringWithNumber(word, num);
				String repeatString = SpokendocBaseline.joinWithSplitter(repeatStrings, " ");
				//docIdに含まれていた単語をスペース区切りで1つの文字列にする
				//例: "word1 word1 word2 word3"
				if (!indexString.isEmpty()){
					indexString += " ";
				}
				indexString += repeatString;
			}
			addDoc(writer, docId, indexString);
		}
	    writer.close();

	    System.out.println("Done indexing!");
	}
}
