package wa.LibraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlAppMain {

	//PID+URLのTSVファイル出力
	public static void do_create_url_list(String projectID, String any_pageID, String operationMode) {

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

		//サイト名
		String site_name = ldr.get_site_name();
		//サイト名
		String save_filename = projectID + "_" + site_name + " URL.txt";
		//データ配列
		List<List<String>> datas = new ArrayList<List<String>>();
		
		//URLのMapデータ取得
		Map<String, String> page_list = null;
		
		//通常はレポートインデックスページから取得
		if(operationMode.equals("")) {
			page_list = ldr.get_page_list_data();
		//検査開始前は検査メインページから取得
		} else {
			ldr.browse_sv_mainpage();
			DateUtil.app_sleep(longWait);
			page_list = ldr.get_page_list_data_from_sv_page();
		}
		
		//any_pageIDが指定されている場合
		if(!any_pageID.equals("") && TextUtil.is_colon_separate(any_pageID)) {

			String[] tmp = any_pageID.split(":");
			String start = tmp[0];
			String end = tmp[1];
			int cnt = 0;
			List<Integer> tmpcnt = new ArrayList<Integer>();
			for(Map.Entry<String, String> line : page_list.entrySet()) {
				String key = line.getKey();
				if(key.equals(start)) tmpcnt.add(cnt);
				if(key.equals(end)) tmpcnt.add(cnt);
				cnt++;
			}
			int start_cnt = tmpcnt.get(0);
			int end_cnt = tmpcnt.get(1);
			cnt = 0;
			for(Map.Entry<String, String> line : page_list.entrySet()) {
				if(cnt >= start_cnt && cnt <= end_cnt) {
					String key = line.getKey();
					String val = line.getValue();
					List<String> tmp_row = new ArrayList<String>();
					tmp_row.add(key);
					tmp_row.add(val);
					datas.add(tmp_row);
				} else {
					continue;
				}
				cnt++;
			}
		
		//それ以外
		} else {

			for(Map.Entry<String, String> rows : page_list.entrySet()) {
				String key = rows.getKey();
				String val = rows.getValue();
				List<String> tmp_row = new ArrayList<String>();
				tmp_row.add(key);
				tmp_row.add(val);
				datas.add(tmp_row);
			}

		}
		
		//ログアウト
		ldr.logout();
		DateUtil.app_sleep(shortWait);
		ldr.shutdown();
		
		System.out.println("テキストファイルの書き出し処理を開始します。(" + DateUtil.get_logtime() + ")");
		
		FileUtil.write_tsv_data(datas, save_filename);
		System.out.println("処理が完了しました。(" + DateUtil.get_logtime() + ")");

	}
	
	//PID+URLのExcelファイルを出力
	public static void do_create_url_list_as_excel(String projectID, String any_pageID, String operationMode) {
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

		//サイト名
		String site_name = ldr.get_site_name();
		//サイト名
		String save_filename = projectID + "_" + site_name + " URL.xlsx";
		//データ配列
		List<List<String>> datas = new ArrayList<List<String>>();
		
		//header
		List<String> head_row = new ArrayList<String>();
		head_row.add("PID");
		head_row.add("URL");
		datas.add(head_row);
		
		//URLのMapデータ取得
		Map<String, String> page_list = null;
		
		//通常はレポートインデックスページから取得
		if(operationMode.equals("")) {
			page_list = ldr.get_page_list_data();
		//検査開始前は検査メインページから取得
		} else {
			ldr.browse_sv_mainpage();
			DateUtil.app_sleep(longWait);
			page_list = ldr.get_page_list_data_from_sv_page();
		}
		
		//any_pageIDが指定されている場合
		if(!any_pageID.equals("") && TextUtil.is_colon_separate(any_pageID)) {

			String[] tmp = any_pageID.split(":");
			String start = tmp[0];
			String end = tmp[1];
			int cnt = 0;
			List<Integer> tmpcnt = new ArrayList<Integer>();
			for(Map.Entry<String, String> line : page_list.entrySet()) {
				String key = line.getKey();
				if(key.equals(start)) tmpcnt.add(cnt);
				if(key.equals(end)) tmpcnt.add(cnt);
				cnt++;
			}
			int start_cnt = tmpcnt.get(0);
			int end_cnt = tmpcnt.get(1);
			cnt = 0;
			for(Map.Entry<String, String> line : page_list.entrySet()) {
				if(cnt >= start_cnt && cnt <= end_cnt) {
					String key = line.getKey();
					String val = line.getValue();
					List<String> tmp_row = new ArrayList<String>();
					tmp_row.add(key);
					tmp_row.add(val);
					datas.add(tmp_row);
				} else {
					continue;
				}
				cnt++;
			}
		
		//それ以外
		} else {

			for(Map.Entry<String, String> rows : page_list.entrySet()) {
				String key = rows.getKey();
				String val = rows.getValue();
				List<String> tmp_row = new ArrayList<String>();
				tmp_row.add(key);
				tmp_row.add(val);
				datas.add(tmp_row);
			}

		}
		
		//ログアウト
		ldr.logout();
		DateUtil.app_sleep(shortWait);
		ldr.shutdown();
		
		System.out.println("Excelファイルの書き出し処理を開始します。(" + DateUtil.get_logtime() + ")");
		
		ExcelUtil.save_xlsx_as(datas, save_filename);
		System.out.println("処理が完了しました。(" + DateUtil.get_logtime() + ")");
	}
	
	//PIDのTEXTファイル出力
	public static void do_create_pid_list(String projectID, String any_pageID, String operationMode) {

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

		//サイト名
		String site_name = ldr.get_site_name();
		//サイト名
		String save_filename = projectID + "_PID.txt";
		//データ配列
		List<String> datas = new ArrayList<String>();
		
		//URLのMapデータ取得
		Map<String, String> page_list = null;
		
		//通常はレポートインデックスページから取得
		if(operationMode.equals("")) {
			page_list = ldr.get_page_list_data();
		//検査開始前は検査メインページから取得
		} else {
			ldr.browse_sv_mainpage();
			DateUtil.app_sleep(longWait);
			page_list = ldr.get_page_list_data_from_sv_page();
		}
		
		//any_pageIDが指定されている場合
		if(!any_pageID.equals("") && TextUtil.is_colon_separate(any_pageID)) {

			String[] tmp = any_pageID.split(":");
			String start = tmp[0];
			String end = tmp[1];
			int cnt = 0;
			List<Integer> tmpcnt = new ArrayList<Integer>();
			for(Map.Entry<String, String> line : page_list.entrySet()) {
				String key = line.getKey();
				if(key.equals(start)) tmpcnt.add(cnt);
				if(key.equals(end)) tmpcnt.add(cnt);
				cnt++;
			}
			int start_cnt = tmpcnt.get(0);
			int end_cnt = tmpcnt.get(1);
			cnt = 0;
			for(Map.Entry<String, String> line : page_list.entrySet()) {
				if(cnt >= start_cnt && cnt <= end_cnt) {
					String key = line.getKey();
					datas.add(key);
				} else {
					continue;
				}
				cnt++;
			}

		//それ以外
		} else {
			
			for(Map.Entry<String, String> rows : page_list.entrySet()) {
				String key = rows.getKey();
				datas.add(key);
			}

		}
		
		//ログアウト
		ldr.logout();
		DateUtil.app_sleep(shortWait);
		ldr.shutdown();
		
		System.out.println("テキストファイルの書き出し処理を開始します。(" + DateUtil.get_logtime() + ")");
		String[] arr = datas.toArray(new String[datas.size()]);
		FileUtil.write_text_data(arr, save_filename);
		System.out.println("処理が完了しました。(" + DateUtil.get_logtime() + ")");

	}
}
