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

public class GenPreSvAppMain {
	//basic認証フラグ
	static Boolean basic_authenicated = false;
	
	//処理実行
	static void do_exec(String projectName, String urls_filename, String any_operation, Boolean layerd_flag) {
		
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
		String basicAuth = user_data[10];
		int[] appWait = {systemWait, longWait, midWait, shortWait};
		
		//basicAuth=yesでheadless_flag=yesの場合、退出
		if(headless_flag.equals("yes") && basicAuth.equals("yes")) {
			System.out.println("basicAuthオプションがyesの場合、headless_flagオプションはnoにしてください。処理を停止します。(" + DateUtil.get_logtime() + ")");
			return;
		}
		
		//GenericDriverインスタンスの生成
		GenDriver gdr = new GenDriver(appWait, os, driver_type, headless_flag, basicAuth);
		
		System.out.println("処理を開始します。(" + DateUtil.get_logtime() + ")");
		
		//PID+URLデータ取得
		Map<String, String> page_rows = FileUtil.open_tsv_data(urls_filename);
		
		//directory作成
		Path save_dir = Paths.get(projectName + "-presv");
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
		for(Map.Entry<String, String> rows : page_rows.entrySet()) {
			String pageID = rows.getKey();
			String pageURL = rows.getValue();
			System.out.println(pageID + " を処理しています。(" + DateUtil.get_logtime() + ")");
			gdr.getWd().get(pageURL);
			
			//basic認証の処理
			if(basicAuth.equals("yes") && (basic_authenicated == false || gdr.getBasicAuthenicated() == false)) {
				System.out.println("basicAuthオプションが有効化されています。ログインアラートで認証を済ませた後、Enterキーを入力してください。...");
				TextUtil.wait_enter_key();
				if(!basic_authenicated) basic_authenicated = true;
				if(!gdr.getBasicAuthenicated()) gdr.setBasicAuthenicated(true);
			}
			
			//operationリストのループ処理
			for(String opt : operations) {
				if(opt.equals("css-cut") || opt.equals("cc")) {
					gdr.getJsExe().executeScript(JsUtil.css_cut());
				} else if(opt.equals("document-link") || opt.equals("dl")) {
					gdr.getJsExe().executeScript(JsUtil.document_link());
				} else if(opt.equals("target-attr") || opt.equals("ta")) {
					gdr.getJsExe().executeScript(JsUtil.target_attr());
				} else if(opt.equals("image-alt") || opt.equals("ia")) {
					gdr.getJsExe().executeScript(JsUtil.image_alt());
				} else if(opt.equals("lang-attr") || opt.equals("la")) {
					gdr.getJsExe().executeScript(JsUtil.lang_attr());
				} else if(opt.equals("semantic-check") || opt.equals("sc")) {
					gdr.getJsExe().executeScript(JsUtil.semantic_check());
				} else if(opt.equals("tag-label-and-title-attr") || opt.equals("tl-ta")) {
					gdr.getJsExe().executeScript(JsUtil.tag_label_and_title_attr());
				}
				//layerd=falseなら個別screenshot
				if(layerd_flag == false) {
					try { gdr.fullpage_screenshot_as(save_dir.resolve(pageID + "." + opt + ".png")); } catch(Exception e) {}
					gdr.getWd().get(pageURL); //reload
					//DateUtil.app_sleep(shortWait);
				}
			}
			
			//layerd=trueなら最後でscreenshot
			if(layerd_flag == true) {
				String sufix = "";
				for(String cop : operations) {
					sufix += cop + ".";
				}
				try { gdr.fullpage_screenshot_as(save_dir.resolve(pageID + "." + sufix + "png")); } catch(Exception e) {}
			}
			
		}
		
		//shutdown
		gdr.shutdown();
		System.out.println("処理が終了しました。(" + DateUtil.get_logtime() + ")");
		
	}

}
