import java.util.ArrayList;

/*
 * テキスト処理などのユーティリティー
 */
public class Util {	//repeatNumの数だけwordが入っている配列を作成する
	public static String[] repatStringWithNumber(String word, Integer repeatNum) {
		ArrayList<String> strings = new ArrayList<String>();
		for (int i=0; i<repeatNum; i++){
			strings.add(word);
		}
		return strings.toArray(new String[0]);
	}

	//splitter区切りでwordsを連結する。perlやpythonのjoinと同じ
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
}
