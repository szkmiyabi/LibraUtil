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

	//レポート処理実行
	static void do_exec(String projectID, String any_pageID, String any_operation, String layerd_flag, String operationMode) {
		
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
		
		if(any_pageID.equals("")) {
			new_page_rows = page_list;
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
			operations.add(any_operation);
		}
		
		//PIDのループ処理
		for(Map.Entry<String, String> rows : new_page_rows.entrySet()) {
			String pageID = rows.getKey();
			String pageURL = rows.getValue();
			System.out.println(pageID + " を処理しています。(" + DateUtil.get_logtime() + ")");
			ldr.getWd().get(pageURL);
			
			//screenshot
			Path save_path = save_dir.resolve(pageID + ".png");
			try { ldr.fullpage_screenshot_as(save_path);
			} catch (Exception e) {}
		}
		
		//shutdown
		ldr.shutdown();
		System.out.println("処理が終了しました。(" + DateUtil.get_logtime() + ")");

	}

}
