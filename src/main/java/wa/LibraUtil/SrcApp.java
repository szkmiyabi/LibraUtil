package wa.LibraUtil;

import gnu.getopt.Getopt;

public class SrcApp {

	static String projectID = "";
	static String any_pageID = "";
	static String any_guideline = "";
	static String any_techID = "";
	static Boolean args_flag = true;
	
	public static void main(String[] args) {

    	//コマンドライン引数処理
    	Getopt options = new Getopt("SrcApp", args, "p:g:t:e:");
    	int c;
    	while( (c = options.getopt()) != -1) {
    		switch(c) {
    		case 'p':
    			any_pageID = options.getOptarg();
    			break;
    		case 'g':
    			any_guideline = options.getOptarg();
    			break;
    		case 't':
    			projectID = options.getOptarg();
    			break;
    		case 'e':
    			any_techID = options.getOptarg();
    			break;
    		default:
    			break;
    		}
    	}
    	
    	if(projectID.equals("") && any_guideline.equals("") && any_techID.equals("")) {
    		args_flag = false;
    	}
    	
    	if(args_flag) {
    		//処理実行
    		SrcAppMain.create_pid_of_guideline_of_techId_report(projectID, any_pageID, any_guideline, any_techID);
    	}
		
	}

}
