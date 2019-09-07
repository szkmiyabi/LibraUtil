package wa.LibraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class TextUtil {

	//brタグを改行コード変換
	public static String br_decode(String str) {
		return Pattern.compile("<br>").matcher(str).replaceAll("\r\n");
	}
	
	//タグをデコード
	public static String tag_decode(String str) {
		String data = str;
		data = Pattern.compile("&lt;").matcher(data).replaceAll("<");
		data = Pattern.compile("&gt;").matcher(data).replaceAll(">");
		return data;
	}
	
	//プロジェクトIDかどうか判定
	public static Boolean is_projectID(String str) {
		Pattern pt = Pattern.compile("[0-9]+");
		Matcher mt = pt.matcher(str);
		if(mt.find()) return true;
		else return false;
	}
	
	//カンマ区切りテキストかどうか判定
	public static Boolean is_csv(String str) {
		Pattern pt = Pattern.compile(",");
		Matcher mt = pt.matcher(str);
		if(mt.find()) return true;
		else return false;
	}
	
	//コロン区切りテキストかどうか判定
	public static Boolean is_colon_separate(String str) {
		Pattern pt = Pattern.compile(":");
		Matcher mt = pt.matcher(str);
		if(mt.find()) return true;
		else return false;
	}

	//レポートのヘッダー行を生成
	public static List<String> get_header() {
		List<String> head_row = new ArrayList<String>();
		head_row.add("管理番号");
		head_row.add("URL");
		head_row.add("達成基準");
		head_row.add("状況/要件");
		head_row.add("実装番号");
		head_row.add("検査結果");
		head_row.add("検査員");
		head_row.add("コメント");
		head_row.add("対象ソースコード");
		head_row.add("修正ソースコード");
		return head_row;
	}
	
	//達成基準番号をJIS2016形式に変換
	public static String jis2016_encode(String str) {
		String data = str;
		data = Pattern.compile("^7\\.").matcher(data).replaceAll("");
		return data;
	}
	
	//達成基準番号をJIS2016以前の形式かどうか判定
	public static boolean is_jis2016_lower(String str) {
		String data = "";
		Pattern pt = Pattern.compile("^7\\.");
		Matcher mt = pt.matcher(str);
		if(mt.find()) return true;
		else return false;
	}

}
