package wa.LibraUtil;

import gnu.getopt.Getopt;

public class PreSvApp {
	
	static String projectID = "";
	static String any_pageID = "";
	static String any_operation = "";
	static String operationMode = "";
	static Boolean layerd_flag = true;
	static Boolean args_flag = true;

	public static void main(String[] args) {

    	//コマンドライン引数処理
    	Getopt options = new Getopt("PreSvApp", args, "p:o:t:m:l:");
    	int c;
    	while( (c = options.getopt()) != -1) {
    		switch(c) {
    		case 'p':
    			any_pageID = options.getOptarg();
    			break;
    		case 'o':
    			any_operation = options.getOptarg();
    			break;
    		case 't':
    			projectID = options.getOptarg();
    			break;
    		case 'm':
    			operationMode = options.getOptarg();
    			break;
    		case 'l':
    			String tmp = options.getOptarg();
    			if(!tmp.equals("")) {
    				layerd_flag = false;
    			}
    			break;
    		default:
    			break;
    		}
    	}
    	
    	if(projectID.equals("") && any_pageID.equals("") && any_operation.equals("")) {
    		args_flag = false;
    	}
    	
    	if(args_flag) {

    		if(TextUtil.is_projectID(projectID)) {
    			PreSvAppMain.do_exec(projectID, any_pageID, any_operation, layerd_flag, operationMode);
    		} else {
				System.out.println("不正なプロジェクトIDが指定されました。処理を中止します。");
    		}

    	}

	}

}
