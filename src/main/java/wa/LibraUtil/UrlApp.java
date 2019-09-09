package wa.LibraUtil;

import gnu.getopt.Getopt;

public class UrlApp {
	static String projectID = "";
	static String any_pageID = "";
	static String operationName = "";
	static String operationMode = "";
	static Boolean args_flag = true;
	
	public static void main(String[] args) {
		
    	//コマンドライン引数処理
    	Getopt options = new Getopt("UrlApp", args, "m:o:t:p:");
    	int c;
    	while( (c = options.getopt()) != -1) {
    		switch(c) {
    		case 'm':
    			operationMode = options.getOptarg();
    			break;
    		case 'o':
    			operationName = options.getOptarg();
    			break;
    		case 't':
    			projectID = options.getOptarg();
    			break;
    		case 'p':
    			any_pageID = options.getOptarg();
    			break;
    		default:
    			break;
    		}
    	}
    	
    	if(projectID.equals("")) {
    		args_flag = false;
    	}
    	
    	if(args_flag) {
    		
    		//PID＋URLのTSVファイル出力
    		if(operationName.equals("")) {
    			UrlAppMain.do_create_url_list(projectID, any_pageID, operationMode);
    		//PID+URLのExcelファイル出力
    		} else if(operationName.equals("excel")) {
    			UrlAppMain.do_create_url_list_as_excel(projectID, any_pageID, operationMode);
    		//PIDのみTEXTファイル出力
    		} else {
    			UrlAppMain.do_create_pid_list(projectID, any_pageID, operationMode);
    		}
    	} else {
    		System.out.println("コマンドライン引数が指定されていないため処理を開始できません。");
    	}

	}

}
