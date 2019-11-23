package wa.LibraUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class GenDriver {
	private WebDriver wd;
	private int systemWait;
	private int longWait;
	private int midWait;
	private int shortWait;
	private String driver_path;
	private String windowID;
	private String app_url = "https://www.google.com/";
	private String basic_auth_flag;
	private Boolean basic_authenicated;
	
	//コンストラクタ
	public GenDriver(int[] appWait,  String os, String driver_type, String headless_flag, String basic_auth_flag) {
		systemWait = appWait[0];
		longWait = appWait[1];
		midWait = appWait[2];
		shortWait = appWait[3];
		
		//basic認証フラグ
		this.basic_auth_flag = basic_auth_flag;
		basic_authenicated = false;

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
		windowID = wd.getWindowHandle();
		
	}
	
	//シャットダウン
	public void shutdown() {
		wd.quit();
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
	
	//basic認証済みフラグのゲッター
	public Boolean getBasicAuthenicated() {
		return basic_authenicated;
	}
	
	//basic認証済みフラグのセッター
	public void setBasicAuthenicated(Boolean flag) {
		basic_authenicated = flag;
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
	
	//DOMオブジェクトを取得
	public org.jsoup.nodes.Document get_dom() {
		String html_str = wd.getPageSource();
		org.jsoup.nodes.Document doc = Jsoup.parse(html_str);
		return doc;
	}
}
