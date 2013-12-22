import java.io.IOException;

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

	public static void main(String[] args) throws IOException {
		SpokendocBaseline spokendoc = new SpokendocBaseline("index");

		// Indexing
		IndexWriter writer = spokendoc.getIndexWriter();
        addDoc(writer, "doc1", "This is the text to be indexed");
        addDoc(writer, "doc2", "High score text text");
        addDoc(writer, "doc3", "Lower score. Score is maybe normalized using text length");
        addDoc(writer, "doc4", "text");
	    writer.close();
	    
	    System.out.println("Done indexing!");
	}
}
