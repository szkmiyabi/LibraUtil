package wa.LibraUtil;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;
import java.util.ArrayList;

public class ExcelUtil {

	//最大文字数32767に収める
	public static String fetch_overflow_characters(String data) {
		if(data.length() >= 32767) {
			String prefix = "【注意】セルに入力可能な文字数の上限を超えました。32767文字以降は切り捨てられます。\n\n";
			int prefix_cnt = prefix.length() + 1;
			return prefix + data.substring(0, (32767 - prefix_cnt));
		} else {
			return data;
		}
	}


	//Excelファイルに出力(RepoApp)
	public static void repo_app_save_xlsx(List<List<String>> datas, String filename) {
		SXSSFWorkbook wb = null;
		SXSSFSheet sh = null;
		FileOutputStream sw = null;
		
		try {
			wb = new SXSSFWorkbook();
			sh = wb.createSheet("検査結果");
			Row row;
			Cell cell;
			
			//hederセルのスタイル
			CellStyle s_header = wb.createCellStyle();
			s_header.setBorderTop(BorderStyle.THIN);
			s_header.setBorderBottom(BorderStyle.THIN);
			s_header.setBorderLeft(BorderStyle.THIN);
			s_header.setBorderRight(BorderStyle.THIN);
			s_header.setAlignment(HorizontalAlignment.CENTER);
			Font s_font = wb.createFont();
			s_font.setBold(true);
			s_header.setFont(s_font);
			
			//適合スタイル
			CellStyle s_ok = wb.createCellStyle();
			s_ok.setBorderTop(BorderStyle.THIN);
			s_ok.setBorderBottom(BorderStyle.THIN);
			s_ok.setBorderLeft(BorderStyle.THIN);
			s_ok.setBorderRight(BorderStyle.THIN);
			s_ok.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			s_ok.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
			
			//適合(注記)スタイル
			CellStyle s_ok2 = wb.createCellStyle();
			s_ok2.setBorderTop(BorderStyle.THIN);
			s_ok2.setBorderBottom(BorderStyle.THIN);
			s_ok2.setBorderLeft(BorderStyle.THIN);
			s_ok2.setBorderRight(BorderStyle.THIN);
			s_ok2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			s_ok2.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
			
			//不適合スタイル
			CellStyle s_fail = wb.createCellStyle();
			s_fail.setBorderTop(BorderStyle.THIN);
			s_fail.setBorderBottom(BorderStyle.THIN);
			s_fail.setBorderLeft(BorderStyle.THIN);
			s_fail.setBorderRight(BorderStyle.THIN);
			s_fail.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			s_fail.setFillForegroundColor(HSSFColor.HSSFColorPredefined.CORAL.getIndex());
			
			//非適用スタイル
			CellStyle s_na = wb.createCellStyle();
			s_na.setBorderTop(BorderStyle.THIN);
			s_na.setBorderBottom(BorderStyle.THIN);
			s_na.setBorderLeft(BorderStyle.THIN);
			s_na.setBorderRight(BorderStyle.THIN);
			s_na.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			s_na.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
			
			//それ以外のスタイル
			CellStyle s_etc = wb.createCellStyle();
			s_etc.setBorderTop(BorderStyle.THIN);
			s_etc.setBorderBottom(BorderStyle.THIN);
			s_etc.setBorderLeft(BorderStyle.THIN);
			s_etc.setBorderRight(BorderStyle.THIN);

			int sv_index = 5;
			
			double row_max = (double)datas.size();
			Boolean perc25 = true;
			Boolean perc50 = true;
			Boolean perc75 = true;
			Boolean perc99 = true;
			
			for(int i=0; i<datas.size(); i++) {
				List<String> data_rows = datas.get(i);
				row = sh.createRow(i);
				
				//進捗状況の表示
				double counter = (double)(i + 1) / row_max;
				if(counter >= 0.25 && perc25) {
					System.out.println("...25%完了");
					perc25 = false;
				} else if(counter >= 0.5 && perc50) {
					System.out.println("...50%完了");
					perc50 = false;
				} else if(counter >= 0.75 && perc75) {
					System.out.println("...75%完了");
					perc75 = false;
				} else if(counter >= 0.99 && perc99) {
					System.out.println("...99%完了");
					perc99 = false;
				}
				
				for(int j=0; j<data_rows.size(); j++) {
					String col = data_rows.get(j);
					
					//32767文字を超える文字列処理
					col = fetch_overflow_characters(col);
					
					//達成基準番号をJIS2016形式に変換
					if(i > 0 && j == 2) {
						col = TextUtil.jis2016_encode(col);
					}
					
					cell = row.createCell(j);
					cell.setCellValue(col);
					
					//header cell
					if(i == 0) {
						cell.setCellStyle(s_header);
					//data cell
					} else {
						String sv_val = data_rows.get(sv_index);
						//System.out.println(sv_val);
						if(sv_val.equals("適合")) {
							cell.setCellStyle(s_ok);
						} else if(sv_val.equals("適合(注記)")) {
							cell.setCellStyle(s_ok2);
						} else if(sv_val.equals("不適合")) {
							cell.setCellStyle(s_fail);
						} else if(sv_val.equals("非適用")) {
							cell.setCellStyle(s_na);
						} else {
							cell.setCellStyle(s_etc);
						}
					}
				}
			}
			sw = new FileOutputStream(filename);
			wb.write(sw);
			
		} catch(Exception e) {
			//Errorメッセージ
			System.out.println("Runtime Error: \n" + e.getMessage());
		} finally {
			if(sw != null) {
				try { sw.close(); } catch(Exception e) {}
			}
			if(wb != null) {
				try { ((SXSSFWorkbook) wb).dispose(); } catch(Exception e) {}
			}
		}
		
	}
	
	//Excelファイルに出力
	public static void save_xlsx_as(List<List<String>> datas, String filename) {
		SXSSFWorkbook wb = null;
		SXSSFSheet sh = null;
		FileOutputStream sw = null;
		try {
			
			wb = new SXSSFWorkbook();
			sh = wb.createSheet("Sheet1");
			Row row;
			Cell cell;
			
			//cell style
			CellStyle s_header = wb.createCellStyle();
			s_header.setBorderTop(BorderStyle.THIN);
			s_header.setBorderBottom(BorderStyle.THIN);
			s_header.setBorderLeft(BorderStyle.THIN);
			s_header.setBorderRight(BorderStyle.THIN);
			s_header.setAlignment(HorizontalAlignment.CENTER);
			Font f_header = wb.createFont();
			f_header.setBold(true);
			s_header.setFont(f_header);
			
			CellStyle s_normal = wb.createCellStyle();
			s_normal.setBorderTop(BorderStyle.THIN);
			s_normal.setBorderBottom(BorderStyle.THIN);
			s_normal.setBorderLeft(BorderStyle.THIN);
			s_normal.setBorderRight(BorderStyle.THIN);

			//行のループ
			for(int i=0; i<datas.size(); i++) {
				List<String> data_rows = datas.get(i);
				row = sh.createRow(i);
				
				//列のループ
				for(int j=0; j<data_rows.size(); j++) {
					String col = data_rows.get(j);
					col = fetch_overflow_characters(col);
					
					//cell insert
					cell = row.createCell(j);
					cell.setCellValue(col);
					
					//cell style
					//header
					if(i == 0) {
						cell.setCellStyle(s_header);
					//other
					} else {
						cell.setCellStyle(s_normal);
					}
					
				}
			}
			sw = new FileOutputStream(filename);
			wb.write(sw);
				
			
		} catch(Exception e) {
			//Errorメッセージ
			System.out.println("Runtime Error: \n" + e.getMessage());
		} finally {
			if(sw != null) {
				try { sw.close(); } catch(Exception e) {}
			}
			if(wb != null) {
				try { ((SXSSFWorkbook) wb).dispose(); } catch(Exception e) {}
			}
		}
	}

}
