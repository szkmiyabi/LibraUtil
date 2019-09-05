package wa.LibraUtil;

import gnu.getopt.Getopt;

public class UrlApp {
	static String projectID = "";
	static String operationName = "";
	static String operationMode = "";
	static Boolean args_flag = true;
	
	public static void main(String[] args) {
		
    	//コマンドライン引数処理
    	Getopt options = new Getopt("UrlApp", args, "m:o:t:");
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
    			UrlAppMain.do_create_url_list(projectID, operationMode);
    		//PIDのみTEXTファイル出力
    		} else {
    			UrlAppMain.do_create_pid_list(projectID, operationMode);
    		}
    	} else {
    		System.out.println("コマンドライン引数が指定されていないため処理を開始できません。");
    	}

	}

}
