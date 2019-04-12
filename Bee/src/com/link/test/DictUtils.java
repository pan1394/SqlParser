package com.link.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Hashtable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;

public class DictUtils {

	private static String[] pathes = { "D:/Bee/beny/toAWS/online.xlsx",
			"D:/Bee/beny/toAWS/バッチwkテーブルレイアウト一覧-rev19548.xlsx" };
	private static String target = "D:\\Bee\\beny\\test\\dict\\dict.xlsx";
	private static String result = "D:\\Bee\\beny\\test\\dict\\result.xlsx";

	public static final String EXCEL_XLS = "xls";
	public static final String EXCEL_XLSX = "xlsx";

	public static void main(String[] args) {
		File file1 = new File(target);
		Workbook t = null;
		try {
			t = getWorkbok(file1);
			Sheet sheet = t.getSheet("Sheet1");
			int index = 1;
			for (String path : pathes) {
				index = get(path, sheet, index);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (t == null) {
			return;
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(new File(result));
			BufferedOutputStream buffer = new BufferedOutputStream(out);
			t.write(buffer);
			buffer.flush();
			buffer.close();
			out.close();
			System.out.println("write object  success!");
		} catch (IOException e) {
			System.out.println("write object failed");
			e.printStackTrace();
		}
	}

	static XSSFCellStyle style;
	static Font font;

	public static XSSFCellStyle getBasicStyle(Workbook book) {
		if (style == null) {
			style = (XSSFCellStyle) book.createCellStyle();
			style.setFillPattern(FillPatternType.NO_FILL);
			font = book.createFont();
			font.setFontName("ＭＳ ゴシック");
			font.setFontHeightInPoints((short) 9);
			style.setFont(font);

			// style.getFont().setFontHeightInPoints(fontSize);

		}
		return (XSSFCellStyle) style.clone();
	}

	public static int get(String path, Sheet s, int index) {

		XSSFCellStyle style = getBasicStyle(s.getWorkbook());
		style.getFont().setBold(true);

		Workbook workbook = getWorkBook(path);

		Sheet domain = workbook.getSheet("ドメイン一覧");
		Hashtable<String, String> domains = null;
		int first = 2;
		if (domain != null) {
			domains = makeDomain(domain);
			first = 3;
		}

		for (int i = first; i < workbook.getNumberOfSheets(); i++) {

			Sheet sheet = workbook.getSheetAt(i);

			String tableName = sheet.getRow(5).getCell(2).getStringCellValue();

			if (tableName == null) {
				continue;
			}

			int thisIndex = 0;

			boolean read = true;
			while (read) {
				Row row = sheet.getRow(13 + thisIndex);
				if (row == null) {
					break;
				}

				try {
					double no = row.getCell(0).getNumericCellValue();
					if (no < 0) {
						read = false;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

				Row r = s.createRow(index);
				Cell c = r.createCell(0);
				c.setCellStyle(style);
				c.setCellValue(tableName);
				// name
				r.createCell(1).setCellValue(row.getCell(1).getStringCellValue());
				// logic name
				r.createCell(2).setCellValue(row.getCell(2).getStringCellValue());
				// type

				String vartype = row.getCell(3).getStringCellValue();
				if (vartype.startsWith("*")) {
					vartype = vartype.substring(1);
					if (domains == null) {
						throw new RuntimeException();
					}
					String v = domains.get(vartype);
					if (v == null) {
						throw new RuntimeException();
					}
					if (v.startsWith("@")) {
						v = v.substring(1);
					}

					r.createCell(3).setCellValue(v);
				} else {
					r.createCell(3).setCellValue(vartype);
				}

				System.out.println(index + ":" + tableName + ":" + row.getCell(1).getStringCellValue() + ":" + vartype);
				index++;
				thisIndex++;

				if (index > 10) {
					return 20;
				}
			}

		}
		return index;
	}

	public static Workbook getWorkbok(File file) throws IOException {
		Workbook wb = null;
		FileInputStream in = new FileInputStream(file);
		if (file.getName().endsWith(EXCEL_XLS)) {
			wb = new HSSFWorkbook(in);
		} else if (file.getName().endsWith(EXCEL_XLSX)) {
			wb = new XSSFWorkbook(in);
		}
		return wb;
	}

	public static Hashtable<String, String> makeDomain(Sheet sheet) {
		Hashtable<String, String> hash = new Hashtable<String, String>();
		int index = 2;
		while (true) {
			Row row = sheet.getRow(index);

			if (row != null) {

				if (row.getCell(0) == null) {
					break;
				}
				String name = row.getCell(1).getStringCellValue();
				String type = row.getCell(2).getStringCellValue();
				hash.put(name, type);
			} else {
				break;
			}
			index++;
		}
		return hash;
	}

	public static String makeLogicName(String name) {
		if (name.indexOf("_") > 0) {
			String r = null;
			String[] ss = name.split("_");
			for (String s : ss) {
				if (r == null) {
					r = s;
				} else {
					r = r + s.substring(0, 1).toUpperCase() + s.substring(1);
				}
			}
			return r;
		} else {
			return name;
		}
	}

	public static String changeType(String type) {
		String dbType = "varchar";
		if (type.equals("String")) {
			return "varchar";
		}
		if (type.equals("BigDecimal")) {
			return "DECIMAL";
		}
		if (type.equals("Integer")) {
			return "INTEGER";
		}
		if (type.equals("Date")) {
			return "DATE";
		}
		if (type.equals("Timestamp")) {
			return "TIMESTAMP";
		}
		return dbType;
	}

	public static BClass getTypeClass(String type) {
		type = type.toUpperCase();
		if (type.startsWith("VARCHAR")) {
			return CodecUtils.BString();
		}
		if (type.startsWith("CHAR")) {
			return CodecUtils.BString();
		}
		if (type.startsWith("DECIMAL")) {
			return CodecUtils.getClassFromJavaClass(BigDecimal.class, null);
		}
		if (type.startsWith("INTEGER")) {
			return CodecUtils.getClassFromJavaClass(Integer.class, null);
		}
		if (type.startsWith("INT")) {
			return CodecUtils.getClassFromJavaClass(Integer.class, null);
		}
		if (type.startsWith("DATE")) {
			return CodecUtils.getClassFromJavaClass(Date.class, null);
		}
		if (type.startsWith("DATETIME")) {
			return CodecUtils.getClassFromJavaClass(Timestamp.class, null);
		}
		throw new RuntimeException();
	}

	public static Workbook getWorkBook(String path) {
		// 获得文件名
		String fileName = path;
		// 创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;
		try {
			// 获取excel文件的io流
			InputStream is = new FileInputStream(fileName);// 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
			FileInputStream in = new FileInputStream(new File(fileName));
			if (fileName.endsWith("xls")) {
				// 2003
				workbook = new HSSFWorkbook(in);
			} else if (fileName.endsWith("xlsx")) {
				// 2007
				workbook = new XSSFWorkbook(in);
			}
		} catch (IOException e) {
		}
		return workbook;
	}
}
