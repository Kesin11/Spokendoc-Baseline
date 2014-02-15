import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

public class Index {
	private static void addDoc(IndexWriter writer, String id, String content) throws IOException{
		Document doc = new Document();
        doc.add(new Field("id", id, TextField.TYPE_STORED));
        doc.add(new Field("content", content, TextField.TYPE_STORED));
        writer.addDocument(doc);
	}

	public static void indexing(String propertiesPath) throws IOException {
		SpokendocBaseline spokendoc = new SpokendocBaseline(propertiesPath);

		// Indexing
		IndexWriter writer = spokendoc.getIndexWriter();
		// 同名の索引が存在するときは全消去して作り直し
		writer.deleteAll();
		BufferedReader bReader = new BufferedReader(new FileReader(spokendoc.freqfilePath));
		String line;
		String docId = "";
		String indexString = "";
		while((line = bReader.readLine()) != null){
			if (line.startsWith("@")) {
				if (!docId.isEmpty()) {
			        // 文字列正規化
			        if (spokendoc.normalization) {
				        indexString = Util.normalizeString(indexString);
			        }
				    // TF, id書き出し
			        addDoc(writer, docId, indexString);
				}
				//"@"以降の文字列を取得
				docId = line.substring(1);
		        indexString = "";
			}
			else {
				String[] tfWord = line.split(" ");
				Integer tf = Integer.valueOf(tfWord[0]);
				String word = tfWord[1];
				// スペース区切りの単語の連続に加工する 
				String[] repeatStrings = Util.repatStringWithNumber(word, tf);
				String repeatString = Util.joinWithSplitter(repeatStrings, " ");
				// 既に単語が入力されているときはスペースを追加してから加える
				if (!indexString.isEmpty()){
					indexString += " ";
				}
				indexString += repeatString;
			}
		}
        // 末端処理
        if (spokendoc.normalization) {
                indexString = Util.normalizeString(indexString);
        }
        addDoc(writer, docId, indexString);

		bReader.close();
	    writer.close();

	    System.out.println("Done indexing!");
	}
}
