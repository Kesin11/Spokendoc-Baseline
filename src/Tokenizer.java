import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 形態素解析を行うシェルスクリプトを呼び出して文字列をトークンに分割する
 */
public class Tokenizer {
	/**
	 * 形態素解析を行うシェルスクリプトを呼び出して文字列をトークンに分割する
	 * @param rawString 元の文字列
	 * @param tokenizerPath 形態素解析シェルスクリプトのパス
	 * @return トークン文字列の配列
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String[] tokenize(String rawString, String tokenizerPath) throws IOException, InterruptedException{
		// サブコマンド実行
		String command = "echo " + '"' + rawString + '"' + " | sh " + tokenizerPath;
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
}