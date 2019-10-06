package wa.LibraUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SrcAppMain {
	
	//basic認証フラグ
	static Boolean basic_authenicated = false;
	
	//指定したPID＋guideline＋techIDのソースコード一覧Excelデータの生成
	public static void create_pid_of_guideline_of_techId_report(String projectID, String any_pageID, String any_guideline, String any_techID) {
		
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
		String guidelineLevel = user_data[9];
		String basicAuth = user_data[10];
		int[] appWait = {systemWait, longWait, midWait, shortWait};
		
		//basicAuth=yesでheadless_flag=yesの場合、退出
		if(headless_flag.equals("yes") && basicAuth.equals("yes")) {
			System.out.println("basicAuthオプションがyesの場合、headless_flagオプションはnoにしてください。処理を停止します。(" + DateUtil.get_logtime() + ")");
			return;
		}
		
		//LibraDriverインスタンスの生成
		LibraDriver ldr = new LibraDriver(uid, pswd, projectID, appWait, os, driver_type, headless_flag, basicAuth);
		
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
		String save_filename = projectID + "_" + site_name + "_";
		
		if(any_pageID == "") save_filename += "ALL-PG_";
		else save_filename += TextUtil.colon_decode(any_pageID, " ") + "_";
		
		save_filename += TextUtil.colon_decode(any_guideline, " ") + "_";
		
		if(any_techID == "") save_filename += "ALL-TEC_";
		else save_filename += TextUtil.colon_decode(any_techID, " ");
		
		save_filename += ".xlsx";
		
		//Mapデータ取得
		ldr.browse_sv_mainpage();
		DateUtil.app_sleep(longWait);
		Map<String, String> page_list = ldr.get_page_list_data_from_sv_page();
		
		//処理配列
		Map<String, String> pages = new TreeMap<String, String>();
		List<String> qy_pages = new ArrayList<String>();
		List<String> guidelines = new ArrayList<String>();
		List<String> techs = new ArrayList<String>();
		
		//pagesの処理
		//any_pageIDが空の場合
		if(any_pageID.equals("")) {
			pages = page_list;
			
		//any_pageIDが：区切りの場合
		} else if(TextUtil.is_colon_separate(any_pageID)) {
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
					pages.put(key, val);
				}
				cnt++;
			}
			
		//any_pageIDが,区切りまたは単独指定の場合
		} else {
			if(TextUtil.is_csv(any_pageID)) {
				List<String> tmp_arr = Arrays.asList(any_pageID.split(","));
				for(String r : tmp_arr) {
					qy_pages.add(r);
				}
			} else {
				qy_pages.add(any_pageID);
			}
			for(String tmp_pid : qy_pages) {
				for(Map.Entry<String, String> line : page_list.entrySet()) {
					String key = line.getKey();
					String val = line.getValue();
					if(tmp_pid.equals(key)) {
						pages.put(key, val);
					}
				}
			}
			if(pages.size() < 1) {
				System.out.println("-p オプションで指定したPIDが存在しません。処理を停止します。");
				ldr.logout();
				DateUtil.app_sleep(shortWait);
				ldr.shutdown();
				return;
			}
		}
		
		//guidelineの処理
		if(TextUtil.is_csv(any_guideline)) {
			String[] tmp = any_guideline.split(",");
			for(String r : tmp) {
				guidelines.add(r);
			}
		} else {
			guidelines.add(any_guideline);
		}
		if(guidelines.size() < 1) {
			System.out.println("-g オプションでガイドライン番号が指定されていません。処理を停止します。");
			ldr.logout();
			DateUtil.app_sleep(shortWait);
			ldr.shutdown();
			return;
		}
		
		//techIDの処理
		
		//techIDが空かつguidelineが単一指定なら全techID自動取得
		if(any_techID.equals("") && guidelines.size() == 1) {
			techs = ldr.get_tech_list_data_by_guideline(guidelines.get(0));
		//,区切りならsplitする
		} else if(TextUtil.is_csv(any_techID)) {
			String[] tmp = any_techID.split(",");
			for(String r : tmp) {
				techs.add(r);
			}
		//それ以外
		} else {
			techs.add(any_techID);
		}
		
		//Excel生成用データ
		List<List<String>> src_rows = new ArrayList<List<String>>();
		List<String> head_row = new ArrayList<String>();
		head_row.add("PID");
		head_row.add("達成基準");
		head_row.add("実装番号");
		head_row.add("対象ソースコード");
		src_rows.add(head_row);
		
		//PIDのループ処理
		for(Map.Entry<String, String> rows : pages.entrySet()) {
			String pageID = rows.getKey();
			
			//PID選択
			if(!ldr.is_selected_url(pageID)) ldr.select_url(pageID);
			DateUtil.app_sleep(longWait);
			
			//guidelineのループ処理
			for(String guideline_row : guidelines) {
				
				//guideline選択
				ldr.select_guideline(guideline_row);
				DateUtil.app_sleep(midWait);
				
				//techIDのループ処理
				for(String tech_row : techs) {
					
					//tech選択
					ldr.select_techlist(tech_row);
					DateUtil.app_sleep(longWait);

					//srccodeタブ選択
					ldr.select_sv_srccode_tab();
					DateUtil.app_sleep(shortWait);
					
					System.out.println(pageID + "," + guideline_row + "," + tech_row + " を処理しています。(" + DateUtil.get_logtime() + ")");
					List<String> src_lines = ldr.get_srccode_list_from_allsv_page();

					//srccodeリストのループ処理
					for(String tmp_line : src_lines) {
						List<String> tmp_row = new ArrayList<String>();
						tmp_row.add(pageID);
						tmp_row.add(guideline_row);
						tmp_row.add(tech_row);
						tmp_row.add(tmp_line);
						src_rows.add(tmp_row);
					}
				}
			}
		}
		
		
		
		//logout
		ldr.logout();
		DateUtil.app_sleep(shortWait);
		
		//shutdown
		ldr.shutdown();
		
		System.out.println("Excelファイルの書き出し処理を開始します。(" + DateUtil.get_logtime() + ")");
		ExcelUtil.save_xlsx_as(src_rows, save_filename);
		System.out.println("処理が終了しました。(" + DateUtil.get_logtime() + ")");
		
		
	}

}
