import java.text.Normalizer;
import java.util.ArrayList;

/**
 * テキスト処理などのユーティリティクラス
 */
public class Util {
	/**
	 * wordがrepeatNumだけ入っている配列を返す
	 * @param word 繰り返したい文字列
	 * @param repeatNum 繰り返す回数
	 * @return wordからrepeatNumだけ含まれている配列 <br> ex: repeatStringWithNumber("word", 2) -> ["word", "word"]
	 */
	public static String[] repatStringWithNumber(String word, Integer repeatNum) {
		ArrayList<String> strings = new ArrayList<String>();
		for (int i=0; i<repeatNum; i++){
			strings.add(word);
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * splitter区切りで文字列の配列を連結する。perlやpythonのjoinと同じ
	 * @param words 文字列の配列
	 * @param splitter 連結に使用する文字列
	 * @return 連結された文字列 <br> ex: joinWithSplitter(["word1","word2"], ":") -> "word1:word2"
	 */
	public static String joinWithSplitter(String[] words, String splitter){
		String string = "";
		for (String word: words){
			if (!string.isEmpty()){
				string += splitter;
			}
			string += word;
		}
		return string;
	}
	/**
	 * 文字列の正規化 
	 * <p>
	 * 全角英数字->半角英数字、大文字->小文字、半角カナ->全角
	 * @param rawString 元の文字列
	 * @return ユニコードのNFKCで正規化+小文字化された文字列
	 */
	public static String normalizeString(String rawString){
		return Normalizer.normalize(rawString, Normalizer.Form.NFKC).toLowerCase();
	}
}
