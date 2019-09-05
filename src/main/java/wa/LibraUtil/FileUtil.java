package wa.LibraUtil;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileUtil {

	//Yamlファイルの読み込み
	private static InputStream readYamlFile(String fileName) {
		InputStream is = null;
		try
		{
			is = Files.newInputStream(Paths.get(fileName));
		} catch(Exception ex) {
		}
		return is;
	}
	
	//設定データファイルの読み込み
	public static String[] getUserProperties(String fileName) {
		String[] ret = new String[10];
		Yaml yaml = new Yaml();
		Map<String, Object> data = (Map<String, Object>) yaml.loadAs(readYamlFile(fileName), Map.class);
		int i = 0;
		for(Map.Entry<String, Object> entry : data.entrySet()) {
			if(entry.getValue() instanceof String) ret[i] = (String) entry.getValue();
			else ret[i] = String.valueOf(entry.getValue());
			i++;
		}
		return ret;
	}
	
	//テキストデータを配列として読み込み
	public static List<String> open_text_data(String filename) {
		List<String> ret = new ArrayList<String>();
		try {
			FileInputStream is = new FileInputStream(filename);
			InputStreamReader in = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(in);
			String line = "";
			while((line = br.readLine()) != null) {
				ret.add(line);
			}
			br.close();
			is.close();

		} catch(Exception ex) {}

		return ret;
	}
	
	//配列をテキストファイルとして書き込み
	public static void write_text_data(String[] rows, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			for(int i=0; i<rows.length; i++) {
				String row = rows[i];
				bw.write(row);
				if(i != (rows.length - 1)) bw.newLine();
			}
			bw.close();
			System.out.println("guideline_datas.txtをリセットできました。");
		} catch(IOException e) {
			System.out.println("エラーが発生しました。" + e.getStackTrace());
		}
	}
	
	//ヘルプを表示する
	public static void write_help() {
		String body = "";
		body += "\n\n**************************************************************************\n";
		body += "   [ 利用方法 ]\n";
		body += "**************************************************************************\n\n";
		body += "# 基本コマンド\n";
		body += " 指定した [projectID] の全ページ、\n";
		body += " [guideline_datas.txt] に示した達成基準のレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# ページ指定コマンド(その1)\n";
		body += " 指定した [projectID] の指定した [PID] のページのみ、\n";
		body += " [guideline_datas.txt] に示した達成基準のレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID] -p [PID]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# ページ指定コマンド(その2)\n";
		body += " 指定した [projectID] のカンマ区切りで指定した [PID] のページのみ、\n";
		body += " [guideline_datas.txt] に示した達成基準のレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID] -p [PID1,PID2]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# 達成基準指定コマンド(その1)\n";
		body += " 指定した [projectID] の全ページ、\n";
		body += " 指定した [guideline] の達成基準のみのレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID] -g [guideline]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# 達成基準指定コマンド(その2)\n";
		body += " 指定した [projectID] の全ページ、\n";
		body += " カンマ区切りで指定した [guideline] の達成基準のみのレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID] -g [guideline1,guideline2]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# ページと達成基準を指定したコマンド(その1)\n";
		body += " 指定した [projectID] の指定した [PID] のページのみ、\n";
		body += " 指定した [guideline] の達成基準のみのレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID] -p [PID] -g [guideline]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# ページと達成基準を指定したコマンド(その2)\n";
		body += " 指定した [projectID] のカンマ区切りで指定した [PID] のページのみ、\n";
		body += " カンマ区切りで指定した [guideline] の達成基準のみのレポートを生成\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -t [projectID] -p [PID1,PID2] -g [guideline1,guideline2]\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# ガイドライン一覧ファイルのリセット\n";
		body += " [guideline_datas.txt] を初期化(全実装番号を再記載)\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -o reset-guideline\n";
		body += "--------------------------------------------------------------------------\n\n";
		body += "# ヘルプ表示\n";
		body += "--------------------------------------------------------------------------\n";
		body += " java -jar App.jar -h\n";
		body += "--------------------------------------------------------------------------\n";
		System.out.println(body);
	}

}
