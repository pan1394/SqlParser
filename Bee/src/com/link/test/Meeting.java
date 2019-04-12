package com.link.test;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Meeting {
	// バッチwkテーブルレイアウト一覧-rev19548.xlsx
	private static String path = "D:\\WORK\\developmeeting\\tongji.xlsx";
	private static String dataPath = "D:\\WORK\\developmeeting\\";
	// private static String dataPath = "D:\\Bee\\application\\beny\\basic\\CMN";

	public static void main(String[] args) {
		Workbook workbook = getWorkBook(path);

		Sheet tongji = workbook.getSheet("统计");

		File dir = new File(dataPath);
		File[] files = dir.listFiles();
		for (File folder : files) {
			if (folder.isDirectory()) {
				File[] datas = folder.listFiles();
				for (File d : datas) {
					if (!d.isDirectory()) {
						Workbook book = getWorkBook(d.getAbsolutePath());
						doBook(book, tongji);
					}
				}
			}
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(dataPath + File.separator + "result.xlsx");
			BufferedOutputStream buffer = new BufferedOutputStream(out);
			workbook.write(buffer);
			buffer.flush();
			buffer.close();
			out.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private static void doBook(Workbook book, Sheet tongji) {
		int count = book.getNumberOfSheets();
		for (int i = 0; i < count; i++) {
			Sheet sheet = book.getSheetAt(i);
			Row row = sheet.getRow(0);
			if (row != null) {
				Cell cell = row.getCell(0);
				if (cell != null) {
					String s = cell.getStringCellValue();
					if (s != null && s.equals("开发大会满意度调查")) {
						doSheet(sheet, tongji);
					}
				}
			}
		}
	}

	private static void doSheet(Sheet sheet, Sheet tongji) {
		String name = sheet.getRow(1).getCell(1).getStringCellValue();
		name = name.trim();
		Cell c = sheet.getRow(2).getCell(1);
		String number = null;
		try {
			number = c.getStringCellValue();
		} catch (Exception e) {
			number = c.getNumericCellValue() + "";
		}
		String dept = sheet.getRow(3).getCell(1).getStringCellValue();

		int rowIndex = -1;
		int run = -1;
		for (int i = 2; i <= 200; i++) {
			Row row = tongji.getRow(i);
			if (row == null) {
				break;
			}
			Cell cell = row.getCell(1);
			if (cell == null) {
				break;
			}
			String s = cell.getStringCellValue();
			if (s.toLowerCase().equals(number.toLowerCase())) {
				rowIndex = i;
				break;
			}
			run = i;
		}
		if (rowIndex == -1) {
			run++;
			// Row row = sheet.createRow(run);
			// row.createCell(0).setCellValue("Add");
			// row.createCell(1).setCellValue(name);
			// row.createCell(2).setCellValue(number);
			rowIndex = run;
		}

		Row header = tongji.getRow(0);
		for (int i = 4; i <= 22; i++) {
			Row row = tongji.getRow(rowIndex);
			if (row == null) {
				row = tongji.createRow(rowIndex);

				row.createCell(0).setCellValue("Add");
				row.createCell(1).setCellValue(name);
				row.createCell(2).setCellValue(number);
				row.createCell(3).setCellValue(dept);
			}
			Cell cell = row.getCell(i);
			if (cell == null) {
				cell = row.createCell(i);
			}
			Cell headerCell = header.getCell(i);
			String headerString = headerCell.getStringCellValue();
			if (headerString.indexOf("-") > 0) {
				String[] ss = headerString.split("-");
				String start = ss[0];
				String end = ss[1];
				int st = Integer.parseInt(start) - 1;
				int se = Integer.parseInt(end) - 1;

				boolean got = false;
				for (int k = st; k <= se; k++) {
					String value = sheet.getRow(k).getCell(1).getStringCellValue();

					if (value.equals("●")) {
						String v = sheet.getRow(k).getCell(2).getStringCellValue();
						cell.setCellValue(v);
						got = true;
					}
				}
				if (!got) {
					XSSFCellStyle style = (XSSFCellStyle) tongji.getWorkbook().createCellStyle();
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					style.setFillForegroundColor(getColor(Color.GREEN));
					style.setFillBackgroundColor(getColor(Color.WHITE));

					cell.setCellStyle(style);
				}

			} else {
				int r = Integer.parseInt(headerString) - 1;
				try {
					if (r == 97) {
						String value = sheet.getRow(r).getCell(2).getStringCellValue();
						cell.setCellValue(value);
					} else {
						String value = sheet.getRow(r).getCell(1).getStringCellValue();
						cell.setCellValue(value);
					}
				} catch (Exception e) {
					double value = sheet.getRow(r).getCell(1).getNumericCellValue();
					cell.setCellValue(value);
				}

			}
		}
	}

	public static XSSFColor getColor(Color color) {
		return new XSSFColor(color, new DefaultIndexedColorMap());
	}

	public static Workbook getWorkBook(String path) {
		// 获得文件名
		String fileName = path;
		// 创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;
		try {
			// 获取excel文件的io流
			// InputStream is = new FileInputStream(fileName);//
			// 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
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
