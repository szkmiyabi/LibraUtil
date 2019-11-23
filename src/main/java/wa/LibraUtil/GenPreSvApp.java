package wa.LibraUtil;

import gnu.getopt.Getopt;

public class GenPreSvApp {
	
	static String projectName = "";
	static String urls_filename = "";
	static String any_operation = "";
	static Boolean layerd_flag = true;
	static Boolean args_flag = true;

	public static void main(String[] args) {

    	//コマンドライン引数処理
    	Getopt options = new Getopt("PreSvApp", args, "p:o:t:l:");
    	int c;
    	while( (c = options.getopt()) != -1) {
    		switch(c) {
    		case 'p':
    			urls_filename = options.getOptarg();
    			break;
    		case 'o':
    			any_operation = options.getOptarg();
    			break;
    		case 't':
    			projectName = options.getOptarg();
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
    	
    	if(projectName.equals("") || urls_filename.equals("")) {
    		args_flag = false;
    	}
    	
    	if(args_flag) {
    		GenPreSvAppMain.do_exec(projectName, urls_filename, any_operation, layerd_flag);
    	}
    	
	}

}
