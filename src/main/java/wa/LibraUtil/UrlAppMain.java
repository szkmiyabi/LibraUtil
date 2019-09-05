package wa.LibraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlAppMain {

	//PID+URLのTSVファイル出力
	public static void do_create_url_list(String projectID) {

		//設定ファイルの読み込み
		String[] user_data = FileUtil.getUserProperties("user.yaml");
		String uid = user_data[0];
		String pswd = user_data[1];
		int systemWait = Integer.parseInt(user_data[2]);
		int longWait = Integer.parseInt(user_data[3]);
		int midWait = Integer.parseInt(user_data[4]);
		int shortWait = Integer.parseInt(user_data[5]);
		String os = user_data[6];
		String driver_type = user_data[7];
		String headless_flag = user_data[8];
		int[] appWait = {systemWait, longWait, midWait, shortWait};
		
		//LibraDriverインスタンスの生成
		LibraDriver ldr = new LibraDriver(uid, pswd, projectID, appWait, os, driver_type, headless_flag);
		
		System.out.println("処理を開始します。(" + DateUtil.get_logtime() + ")");
		
		//ログイン
		ldr.login();
		DateUtil.app_sleep(shortWait);
		
		System.out.println("URLを取得しています。(" + DateUtil.get_logtime() + ")");

		//レポートindexページ
		ldr.browse_repo();
		DateUtil.app_sleep(shortWait);
		
		//データ配列
		List<List<String>> datas = new ArrayList<List<String>>();
		
		//サイト名
		String site_name = ldr.get_site_name();
		
		//サイト名
		String save_filename = projectID + "_" + site_name + " URL.txt";

		//URLのMapデータ取得
		Map<String, String> page_list = ldr.get_page_list_data();
		for(Map.Entry<String, String> rows : page_list.entrySet()) {
			String key = rows.getKey();
			String val = rows.getValue();
			List<String> tmp_row = new ArrayList<String>();
			tmp_row.add(key);
			tmp_row.add(val);
			datas.add(tmp_row);
		}
		
		//ログアウト
		ldr.logout();
		DateUtil.app_sleep(shortWait);
		ldr.shutdown();
		
		System.out.println("テキストファイルの書き出し処理を開始します。(" + DateUtil.get_logtime() + ")");
		
		FileUtil.write_tsv_data(datas, save_filename);
		System.out.println("処理が完了しました。(" + DateUtil.get_logtime() + ")");

	}
}
