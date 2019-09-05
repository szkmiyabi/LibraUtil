package wa.LibraUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	private final static DateTimeFormatter dtf_file = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private final static DateTimeFormatter dtf_log = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	//現在の時刻からファイル名を生成
	public static String fetch_filename_from_datetime(String extension) {
		LocalDateTime ld = LocalDateTime.now();
		return ld.format(dtf_file) + "." + extension;
	}
	
	//ログ出力の時刻文字列を生成
	public static String get_logtime() {
		LocalDateTime ld = LocalDateTime.now();
		return ld.format(dtf_log);
	}
	
	//アプリのスリープ
	public static void app_sleep(int second) {
		try {
			int milisec = second * 1000;
			Thread.sleep(milisec);
		} catch(InterruptedException e) {
		}
	}
}
