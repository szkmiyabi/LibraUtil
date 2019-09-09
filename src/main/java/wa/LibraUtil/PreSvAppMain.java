package wa.LibraUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PreSvAppMain {
	
	//basic認証フラグ
	static Boolean basic_authenicated = false;

	//レポート処理実行
	static void do_exec(String projectID, String any_pageID, String any_operation, Boolean layerd_flag, String operationMode) {
		
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
		
		//Mapデータ取得
		Map<String, String> page_list = null;
		if(operationMode.equals("")) {
			//検査開始済みの場合
			page_list = ldr.get_page_list_data();
		} else {
			//検査開始してない場合
			ldr.browse_sv_mainpage();
			DateUtil.app_sleep(longWait);
			page_list = ldr.get_page_list_data_from_sv_page();
		}
		
		//ログアウト
		ldr.logout();
		DateUtil.app_sleep(shortWait);
		
		//処理配列の条件分岐
		List<String> qy_page_rows = new ArrayList<String>();
		Map<String, String> new_page_rows = new TreeMap<String, String>();
		
		//any_pageIDが空の場合
		if(any_pageID.equals("")) {
			new_page_rows = page_list;
			
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
					new_page_rows.put(key, val);
				} else {
					continue;
				}
				cnt++;
			}
			
		//any_pageIDが,区切りまたは単独指定の場合
		} else {
			if(TextUtil.is_csv(any_pageID)) {
				List<String> tmp_arr = Arrays.asList(any_pageID.split(","));
				for(String r : tmp_arr) {
					qy_page_rows.add(r);
				}
			} else {
				qy_page_rows.add(any_pageID);
			}
			for(String tmp_pid : qy_page_rows) {
				for(Map.Entry<String, String> line : page_list.entrySet()) {
					String key = line.getKey();
					String val = line.getValue();
					if(tmp_pid.equals(key)) {
						new_page_rows.put(key, val);
					}
				}
			}
			if(new_page_rows.size() < 1) {
				System.out.println("-p オプションで指定したPIDが存在しません。処理を停止します。");
				return;
			}
		}
		
		//directory作成
		Path save_dir = Paths.get(projectID + "-presv");
		try { Files.createDirectories(save_dir);
		} catch (IOException e) {}
		
		
		//operation配列の整備
		List<String> operations = new ArrayList<String>();
		if(TextUtil.is_csv(any_operation)) {
			List<String> tmp_arr = Arrays.asList(any_operation.split(","));
			for(String r : tmp_arr) {
				operations.add(r);
			}
		} else {
			if(!any_operation.equals("")) {
				operations.add(any_operation);
			}
		}
			
		//PIDのループ処理
		for(Map.Entry<String, String> rows : new_page_rows.entrySet()) {
			String pageID = rows.getKey();
			String pageURL = rows.getValue();
			System.out.println(pageID + " を処理しています。(" + DateUtil.get_logtime() + ")");
			ldr.getWd().get(pageURL);
			
			//basic認証の処理
			if(basicAuth.equals("yes") && basic_authenicated == false) {
				System.out.println("basicAuthオプションが有効化されています。ログインアラートで認証を済ませた後、Enterキーを入力してください。...");
				TextUtil.wait_enter_key();
				basic_authenicated = true;
			}
			
			//operationリストのループ処理
			for(String opt : operations) {
				if(opt.equals("css-cut") || opt.equals("cc")) {
					ldr.getJsExe().executeScript(JsUtil.css_cut());
				} else if(opt.equals("document-link") || opt.equals("dl")) {
					ldr.getJsExe().executeScript(JsUtil.document_link());
				} else if(opt.equals("target-attr") || opt.equals("ta")) {
					ldr.getJsExe().executeScript(JsUtil.target_attr());
				} else if(opt.equals("image-alt") || opt.equals("ia")) {
					ldr.getJsExe().executeScript(JsUtil.image_alt());
				} else if(opt.equals("lang-attr") || opt.equals("la")) {
					ldr.getJsExe().executeScript(JsUtil.lang_attr());
				} else if(opt.equals("semantic-check") || opt.equals("sc")) {
					ldr.getJsExe().executeScript(JsUtil.semantic_check());
				} else if(opt.equals("tag-label-and-title-attr") || opt.equals("tl-ta")) {
					ldr.getJsExe().executeScript(JsUtil.tag_label_and_title_attr());
				}
				//layerd=falseなら個別screenshot
				if(layerd_flag == false) {
					try { ldr.fullpage_screenshot_as(save_dir.resolve(pageID + "." + opt + ".png")); } catch(Exception e) {}
					ldr.getWd().get(pageURL); //reload
					//DateUtil.app_sleep(shortWait);
				}
			}
			
			//layerd=trueなら最後でscreenshot
			if(layerd_flag == true) {
				String sufix = "";
				for(String cop : operations) {
					sufix += cop + ".";
				}
				try { ldr.fullpage_screenshot_as(save_dir.resolve(pageID + "." + sufix + "png")); } catch(Exception e) {}
			}
			
		}
		
		//shutdown
		ldr.shutdown();
		System.out.println("処理が終了しました。(" + DateUtil.get_logtime() + ")");

	}

}
