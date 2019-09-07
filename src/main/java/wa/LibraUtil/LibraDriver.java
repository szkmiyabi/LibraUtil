package wa.LibraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TakesScreenshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LibraDriver {
	private String projectID;
	private WebDriver wd;
	private int systemWait;
	private int longWait;
	private int midWait;
	private int shortWait;
	private String driver_path;
	private String uid;
	private String pswd;
	private String app_url = "https://accessibility.jp/libra/";
	private String index_url = "https://jis.infocreate.co.jp/";
	private String rep_index_url_base = "http://jis.infocreate.co.jp/diagnose/indexv2/report/projID/";
	private String rep_detail_url_base = "http://jis.infocreate.co.jp/diagnose/indexv2/report2/projID/";
	private String sv_mainpage_url_base = "http://jis.infocreate.co.jp/diagnose/indexv2/index/projID/";
	private String guideline_file_name = "guideline_datas.txt";
	private List<List<String>> rep_data;
	
	//コンストラクタ
	public LibraDriver(String uid, String pswd,  String projectID, int[] appWait,  String os, String driver_type, String headless_flag) {
		this.uid = uid;
		this.pswd = pswd;
		this.projectID = projectID;
		systemWait = appWait[0];
		longWait = appWait[1];
		midWait = appWait[2];
		shortWait = appWait[3];
		
		//レポートデータ初期化
		rep_data = new ArrayList<List<String>>();
		
		//driverパスの設定
		if(os.equals("windows")) {
			if(driver_type.equals("firefox")) {
				driver_path = "./geckodriver.exe";
				System.setProperty("webdriver.firefox.driver", driver_path);
			} else if(driver_type.equals("chrome")) {
				driver_path = "./chromedriver.exe";
				System.setProperty("webdriver.chrome.driver", driver_path);
			}
		} else if(os.equals("mac") || os.equals("unix")) {
			if(driver_type.equals("firefox")) {
				driver_path = "geckodriver";
				System.setProperty("webdriver.firefox.driver", driver_path);
			} else if(driver_type.equals("chrome")) {
				driver_path = "chromedriver";
				System.setProperty("webdriver.chrome.driver", driver_path);
			}
		}
		//driverオプションの設定
		if(driver_type.equals("firefox")) {
			FirefoxOptions fxopt = new FirefoxOptions();
			if(headless_flag.equals("yes")) fxopt.addArguments("-headless");
			wd = new FirefoxDriver(fxopt);
		} else if(driver_type.equals("chrome")) {
			ChromeOptions chopt = new ChromeOptions();
			if(headless_flag.equals("yes")) chopt.addArguments("--headless");
			wd = new ChromeDriver(chopt);
		}
		wd.manage().timeouts().implicitlyWait(systemWait, TimeUnit.SECONDS);
		wd.manage().window().setSize(new Dimension(1280, 900));
		wd.get(app_url);
		
	}
	
	//WebDriverのゲッター
	public WebDriver getWd() {
		return wd;
	}
	
	//JavasciptExecutorのゲッター
	public JavascriptExecutor getJsExe() {
		JavascriptExecutor jsx = (JavascriptExecutor) wd;
		return jsx;
	}
	
	//rep_dataのゲッター
	public List<List<String>> getRepData() {
		return rep_data;
	}
	
	//スクリーンショットを取る
	public void screenshot(String filename) throws Exception {
		TakesScreenshot sc = (TakesScreenshot)wd;
		Path save_dir = Paths.get("screenshots");
		Files.createDirectories(save_dir);
		Path save_path = save_dir.resolve(filename);
		Files.write(save_path, sc.getScreenshotAs(OutputType.BYTES));
	}
	
	//スクリーンショットを撮る（ディレクトリも指定）
	public void screenshot_as(Path save_path) throws Exception {
		TakesScreenshot sc = (TakesScreenshot)wd;
		Files.write(save_path, sc.getScreenshotAs(OutputType.BYTES));
	}

	//fullpage screenshotを撮る
	public void fullpage_screenshot(String filename) throws Exception {
		JavascriptExecutor jsexe = (JavascriptExecutor) wd;
		int require_height = Integer.parseInt(jsexe.executeScript("return document.body.parentNode.scrollHeight").toString());
		//windowsサイズのheight指定はCentOS7-Chromeでは上限がある
		wd.manage().window().setSize(new Dimension(1280, require_height));
		DateUtil.app_sleep(longWait);
		TakesScreenshot sc = (TakesScreenshot) wd;
		Path save_dir = Paths.get("screenshots");
		Files.createDirectories(save_dir);
		Path save_path = save_dir.resolve(filename);
		Files.write(save_path, sc.getScreenshotAs(OutputType.BYTES));
		wd.manage().window().setSize(new Dimension(1280, 900));
	}
	
	//fullpage screenshotを撮る（ディレクトリも指定）
	public void fullpage_screenshot_as(Path save_path) throws Exception {
		JavascriptExecutor jsexe = (JavascriptExecutor) wd;
		int require_height = Integer.parseInt(jsexe.executeScript("return document.body.parentNode.scrollHeight").toString());
		//windowsサイズのheight指定はChromeでは上限がある
		wd.manage().window().setSize(new Dimension(1280, require_height));
		DateUtil.app_sleep(longWait);
		TakesScreenshot sc = (TakesScreenshot) wd;
		Files.write(save_path, sc.getScreenshotAs(OutputType.BYTES));
		wd.manage().window().setSize(new Dimension(1280, 900));
	}
	
	//シャットダウン
	public void shutdown() {
		wd.quit();
	}
	
	//ログイン
	public void login() {
		wd.findElement(By.id("uid")).sendKeys(uid);
		wd.findElement(By.id("pswd")).sendKeys(pswd);
		wd.findElement(By.id("btn")).click();
	}
	
	//ログアウト
	public void logout() {
		wd.get(index_url);
		try { Thread.sleep(shortWait); } catch(InterruptedException e) {}
		WebElement btnWrap = wd.findElement(By.id("btn"));
		WebElement btnBase = btnWrap.findElement(By.tagName("ul"));
		WebElement btnBaseInner = btnBase.findElement(By.className("btn2"));
		WebElement btnA = btnBaseInner.findElement(By.tagName("a"));
		btnA.click();
	}
	
	//レポートインデックスページに遷移
	public void browse_repo() {
		wd.get(rep_index_url_base + projectID);
	}
	
	//検査メインページ遷移
	public void browse_sv_mainpage() {
		wd.get(sv_mainpage_url_base + projectID);
	}
	
	//レポート詳細ページのURL生成
	public String fetch_report_detail_path(String pageID, String guidelineID) {
		return rep_detail_url_base + projectID + "/controlID/"  + pageID + "/guideline/" + guidelineID + "/";
	}
	
	//DOMオブジェクトを取得
	public org.jsoup.nodes.Document get_dom() {
		String html_str = wd.getPageSource();
		org.jsoup.nodes.Document doc = Jsoup.parse(html_str);
		return doc;
	}
	
	//達成基準番号選択
	public void select_guideline(String guidelineID) {
		WebElement guidelineSelect = wd.findElement(By.id("guideline"));
		List<WebElement> guidelineOptions = guidelineSelect.findElements(By.tagName("option"));
		for(int i=0; i<guidelineOptions.size(); i++) {
			WebElement guidelineOP = guidelineOptions.get(i);
			String guidelineOP_value = guidelineOP.getText();
			String rgx = guidelineID + ".*";
			Pattern pt = Pattern.compile(rgx);
			Matcher mt = pt.matcher(guidelineOP_value);
			if(mt.find()) {
				guidelineOP.click();
				break;
			}
		}
	}
	
	//実装番号選択
	public void select_techlist(String techID) {
		WebElement techSelect = wd.findElement(By.id("techList"));
		List<WebElement> techOptions = techSelect.findElements(By.tagName("option"));
		for(int i=0; i<techOptions.size(); i++) {
			WebElement techOP = techOptions.get(i);
			String techOP_value = techOP.getText();
			String rgx = techID + ".*";
			Pattern pt = Pattern.compile(rgx);
			Matcher mt = pt.matcher(techOP_value);
			if(mt.find()) {
				techOP.click();
				wd.findElement(By.id("footer")).click();
				break;
			}
		}
	}
	
	//URL選択
	public void select_url(String url) {
		int sample_num = _get_url_list_num(url);
		wd.findElement(By.id("urlList-" + sample_num)).click();
		wd.findElement(By.id("submitURL")).click();
	}
	private int _get_url_list_num(String url) {
		int cnt = 0;
		WebElement urlSelect = wd.findElement(By.id("urlList"));
		List<WebElement> urlOptions = urlSelect.findElements(By.tagName("option"));
		for(int i=0; i<urlOptions.size(); i++) {
			WebElement urlOP = urlOptions.get(i);
			String urlOP_value = urlOP.getText();
			String rgx = "\\[.*" + url + ".*?\\]";
			Pattern pt = Pattern.compile(rgx);
			Matcher mt = pt.matcher(urlOP_value);
			if(mt.find()) {
				break;
			}
			cnt++;
		}
		return cnt;
	}
	
	//URLが選択されているか判定
	public boolean is_selected_url(String url) {
		Select select = new Select(wd.findElement(By.id("urlList")));
		WebElement opt = select.getFirstSelectedOption();
		String opt_value = opt.getText();
		String rgx = "\\[.*" + url + ".*?\\]";
		Pattern pt = Pattern.compile(rgx);
		Matcher mt = pt.matcher(opt_value);
		if(mt.find()) return true;
		else return false;
	}
	
	//試験結果詳細ビュー選択
	public void select_sv_detail_tab() {
		WebElement tabWrap = wd.findElement(By.id("tabsA"));
		WebElement tabUL = tabWrap.findElements(By.className("ui-tabs-nav")).get(0);
		WebElement li = tabUL.findElements(By.tagName("li")).get(2);
		WebElement atag = li.findElements(By.tagName("a")).get(0);
		atag.click();
	}
	
	//ソースコードビュー選択
	public void select_sv_srccode_tab() {
		WebElement tabWrap = wd.findElement(By.id("tabsA"));
		WebElement tabUL = tabWrap.findElements(By.className("ui-tabs-nav")).get(0);
		WebElement li = tabUL.findElements(By.tagName("li")).get(1);
		WebElement atag = li.findElements(By.tagName("a")).get(0);
		atag.click();
	}
	
	//ページビューを選択
	public void select_sv_pv_tab() {
		WebElement tabWrap = wd.findElement(By.id("tabsA"));
		WebElement tabUL = tabWrap.findElements(By.className("ui-tabs-nav")).get(0);
		WebElement li = tabUL.findElements(By.tagName("li")).get(0);
		WebElement atag = li.findElements(By.tagName("a")).get(0);
		atag.click();
	}
	
	
	//Libraページビューの高さをスクロールバーなしのheightにする
	public void pv_height_adjust() {
		StringBuilder jsc = new StringBuilder();
		jsc.append("var ifm_h=document.getElementById('sample').contentWindow.document.body.scrollHeight;");
		jsc.append("var tg=document.getElementsByClassName('view_d')[0];");
		jsc.append("tg.setAttribute('style', 'height:' + ifm_h + 'px');");
		JavascriptExecutor jsexe = (JavascriptExecutor) wd;
		jsexe.executeScript(jsc.toString());
	}
	
	//サイト名を取得
	public String get_site_name() {
		String sname = "";
		org.jsoup.nodes.Document dom = get_dom();
		org.jsoup.nodes.Element tbl = null;
		org.jsoup.select.Elements tbls = dom.select("table");
		for(int i=0; i<tbls.size(); i++) {
			if(i == 1) {
				tbl = (org.jsoup.nodes.Element)tbls.get(i);
			}
		}
		String tbl_html = tbl.outerHtml();
		org.jsoup.nodes.Document tbl_dom = Jsoup.parse(tbl_html);
		org.jsoup.select.Elements tds = tbl_dom.select("tr td");
		org.jsoup.nodes.Element td = tds.get(0);
		String td_val = td.text();
		Pattern pt = Pattern.compile("(\\[)([a-zA-Z0-9]+)(\\])(\\s*)(.+)");
		Matcher mt = pt.matcher(td_val);
		if(mt.find()) {
			sname = mt.group(5);
		}
		return sname;
	}
	
	//PID一覧＋URL一覧データ生成
	public Map<String, String> get_page_list_data() {
		Map<String, String> datas = new TreeMap<String, String>();
		org.jsoup.nodes.Document dom = get_dom();
		org.jsoup.nodes.Element tbl = null;
		org.jsoup.select.Elements tbls = dom.select("table");
		for(int i=0; i<tbls.size(); i++) {
			if(i == 2) {
				tbl = (org.jsoup.nodes.Element)tbls.get(i);
			}
		}

		String tbl_html = tbl.outerHtml();
		org.jsoup.nodes.Document tbl_dom = Jsoup.parse(tbl_html);

		org.jsoup.select.Elements rows = tbl_dom.select("tr td:first-child");
		List<String> pids = new ArrayList<String>();
		for(org.jsoup.nodes.Element row : rows) {
			String td_val = row.text();
			pids.add(td_val);
		}
		int map_cnt = pids.size();
		rows = null;
		rows = tbl_dom.select("tr td:nth-child(2)");
		List<String> urls = new ArrayList<String>();
		for(org.jsoup.nodes.Element row : rows) {
			String td_val = row.text();
			urls.add(td_val);
		}
		for(int i=0; i<map_cnt; i++) {
			datas.put(pids.get(i), urls.get(i));
		}
		return datas;
	}
	
	//PID一覧＋URL一覧データ生成 （検査メイン画面から）
	public Map<String, String> get_page_list_data_from_sv_page(){
		Map<String, String> datas = new TreeMap<String, String>();
		WebElement url_ddl = wd.findElement(By.id("urlList"));
		List<WebElement> opts = url_ddl.findElements(By.tagName("option"));
		for(int i=0; i<opts.size(); i++) {
			WebElement opt = opts.get(i);
			String key = opt.getAttribute("value");
			String val = _get_option_urltext(opt);
			datas.put(key, val);
		}
		return datas;
	}
	private String _get_option_urltext(WebElement opt) {
		String ret = "";
		String val = opt.getText();
		Pattern pt = Pattern.compile("(\\[[a-zA-Z0-9]+\\] )(.+)");
		Matcher mt = pt.matcher(val);
		if(mt.find()) {
			ret = mt.group(2);
		}
		return ret;
	}
	
	//レポート詳細ページから検査結果データを生成
	List<List<String>> get_detail_table_data(String pageID, String pageURL, String guideline) {
		List<List<String>> datas = new ArrayList<List<String>>();
		org.jsoup.nodes.Document dom = get_dom();
		org.jsoup.nodes.Element tbl = null;
		org.jsoup.select.Elements tbls = dom.select("table");
		for(int i=0; i<tbls.size(); i++) {
			if(i == 2) {
				tbl = (org.jsoup.nodes.Element)tbls.get(i);
			}
		}
		String tbl_html = tbl.outerHtml();
		org.jsoup.nodes.Document tbl_dom = Jsoup.parse(tbl_html);
		org.jsoup.select.Elements trs = tbl_dom.select("tr");
		for(int i=0; i<trs.size(); i++) {
			if(i == 0) continue;
			List<String> row_datas = new ArrayList<String>();
			row_datas.add(pageID);
			row_datas.add(pageURL);
			row_datas.add(guideline);
			org.jsoup.nodes.Element tr = (org.jsoup.nodes.Element)trs.get(i);
			String tr_html = tr.outerHtml();
			//Jsoupパースバグの解消
			tr_html = "<html><head><meta charset='utf8'></head><body><table><tr>" + tr_html + "</tr></table></body></html>";
			org.jsoup.nodes.Document tr_dom = Jsoup.parse(tr_html);
			org.jsoup.select.Elements tds = tr_dom.select("td");
			int col_num = 0;
			for(int j=0; j<tds.size(); j++) {
				org.jsoup.nodes.Element td = (org.jsoup.nodes.Element)tds.get(j);
				String td_val = td.html();
				//コメント列はbrタグも含め実体参照デコード
				if(col_num == 4) {
					td_val = TextUtil.br_decode(td_val);
					td_val = TextUtil.tag_decode(td_val);
				//それ以外は、実体参照のみデコード
				} else {
					td_val = TextUtil.tag_decode(td_val);
				}
				if(td_val.equals("")) {
					row_datas.add("");
				} else {
					row_datas.add(td_val);
				}
				col_num++;
			}
			datas.add(row_datas);
		}
		return datas;
	}
	
	//レポートデータ生成
	public void fetch_report_sequential() {

		//header
		rep_data.add(TextUtil.get_header());
		wd.get(rep_index_url_base + projectID + "/");
		DateUtil.app_sleep(shortWait);
		
		List<String> guideline_rows = FileUtil.open_text_data(guideline_file_name);
		Map<String, String> page_rows = get_page_list_data();
		//guidelineのループ
		for(int i=0; i<guideline_rows.size(); i++) {			
			String guideline = guideline_rows.get(i);
			String guideline_disp = guideline; //println用
			//jis2016以前の達成基準番号に変換
			if(!TextUtil.is_jis2016_lower(guideline)) guideline = "7." + guideline;
			//pageのループ
			for(Map.Entry<String, String> page_row : page_rows.entrySet()) {
				String pageID = page_row.getKey();
				String pageURL = page_row.getValue();
				System.out.println(pageID + ", " + guideline_disp + " を処理しています。 (" + DateUtil.get_logtime() + ")");
				String path = fetch_report_detail_path(pageID, guideline);
				wd.get(path);
				DateUtil.app_sleep(shortWait);

				List<List<String>> tbl_data = get_detail_table_data(pageID, pageURL, guideline);
				rep_data.addAll(tbl_data);
			}
		}
		
	}
	
	//ページIDとガイドラインIDを個別に指定してレポートデータ作成
	public void fetch_report_single(String any_pageID, String any_guideline) {

		wd.get(rep_index_url_base + projectID + "/");
		DateUtil.app_sleep(shortWait);
		
		//処理対象PIDデータの処理
		List<String> qy_page_rows = new ArrayList<String>();
		Map<String, String> new_page_rows = new TreeMap<String, String>();
		Map<String, String> page_rows = get_page_list_data();
		if(any_pageID == "") {
			new_page_rows = page_rows;
		} else {
			//ループ用PIDマップの生成
			if(TextUtil.is_csv(any_pageID)) {
				String[] tmp_arr = any_pageID.split(",", 0);
				for(int i=0; i<tmp_arr.length; i++) {
					qy_page_rows.add(tmp_arr[i]);
				}
			} else {
				qy_page_rows.add(any_pageID);
			}
			for(int i=0; i<qy_page_rows.size(); i++) {
				String tmp_pid = qy_page_rows.get(i);
				for(Map.Entry<String, String> tmp_row : page_rows.entrySet()) {
					String key = tmp_row.getKey();
					String val = tmp_row.getValue();
					if(tmp_pid.equals(key)) {
						new_page_rows.put(key, val);
					}
				}
			}
			if(new_page_rows.isEmpty()) {
				System.out.println("-p オプションで指定したPIDが存在しません。処理を停止します。");
				return;
			}
		}
		
		//処理対象ガイドラインデータの処理
		List<String> guideline_rows = new ArrayList<String>();
		if(any_guideline == "") {
			guideline_rows = FileUtil.open_text_data(guideline_file_name);
		} else {
			if(TextUtil.is_csv(any_guideline)) {
				String[] tmp_arr = any_guideline.split(",", 0);
				for(int i=0; i<tmp_arr.length; i++) {
					guideline_rows.add(tmp_arr[i]);
				}
			} else {
				guideline_rows.add(any_guideline);
			}
		}

		//header
		rep_data.add(TextUtil.get_header());

		//guidelineのループ
		for(int i=0; i<guideline_rows.size(); i++) {			
			String guideline = guideline_rows.get(i);
			String guideline_disp = guideline; //println用
			//jis2016以前の達成基準番号に変換
			if(!TextUtil.is_jis2016_lower(guideline)) guideline = "7." + guideline;
			//pageのループ
			for(Map.Entry<String, String> page_row : new_page_rows.entrySet()) {
				String pageID = page_row.getKey();
				String pageURL = page_row.getValue();
				System.out.println(pageID + ", " + guideline_disp + " を処理しています。 (" + DateUtil.get_logtime() + ")");
				String path = fetch_report_detail_path(pageID, guideline);
				wd.get(path);
				DateUtil.app_sleep(shortWait);
				
				List<List<String>> tbl_data = get_detail_table_data(pageID, pageURL, guideline);
				rep_data.addAll(tbl_data);
			}
		}

	}
}
