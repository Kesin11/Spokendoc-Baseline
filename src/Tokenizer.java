import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 形態素解析を行うシェルスクリプトを呼び出して文字列をトークンに分割する
 * http://www.ne.jp/asahi/hishidama/home/tech/java/process.html
 */
public class Tokenizer {
	String tokenizerPath = "tokenizer.sh";
	public static String[] tokenize(String rowString) throws IOException, InterruptedException{
		// サブコマンド実行
		String command = "echo " + rowString +  " | sh src/tokenizer.sh";
		Process process = new ProcessBuilder("sh", "-c", command).start();
		// デバッグ用。シェルのエラーコード
//		int ret = process.waitFor();
//		System.out.println(ret);
		
		// 標準出力受け取り
		InputStream iStream = process.getInputStream();
		BufferedReader brReader = new BufferedReader(new InputStreamReader(iStream));
		ArrayList<String> tokens = new ArrayList<String>();
		String line = "";
		while((line = brReader.readLine()) != null){
			tokens.add(line);
		}
		return tokens.toArray(new String[0]);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String rawString = args[0];
		String[] tokens = tokenize(rawString);
		for (String token: tokens){
            System.out.println(token);
		}
	}

}
