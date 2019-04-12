package com.link.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BStaticUtils {
	public static void main(String[] args) {
		makeStaticList();
		makeKubunList();
	}

	public static void makeStaticList() {
		String path = "D:\\Bee\\beny\\toAWS\\定数\\Benyバッチ定数一覧.xlsx";
		Workbook book = getWorkBook(path);
		Sheet con = book.getSheet("Const設計");
		Sheet enu = book.getSheet("Enum設計");
		File file = new File(path);
		makeStaticSheet(con, file.getParentFile().getAbsolutePath() + File.separator + "const.data");
		makeStaticSheet(enu, file.getParentFile().getAbsolutePath() + File.separator + "enum.data");
	}

	public static void makeStaticSheet(Sheet sheet, String targetPath) {
		List<BStaticInfoObject> list = new ArrayList<BStaticInfoObject>();
		for (int i = 6; i < 1000; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				break;
			}
			if (row.getCell(5) == null) {
				break;
			}

			String pack = row.getCell(7).getStringCellValue().trim();
			if (pack.equals("")) {
				break;
			}
			String className = row.getCell(8).getStringCellValue().trim();
			String name = row.getCell(9).getStringCellValue().trim();
			String logicName = row.getCell(10).getStringCellValue().trim();
			String type = row.getCell(11).getStringCellValue().trim();
			type = getType(type);
			String value = null;
			try {
				value = row.getCell(12).getStringCellValue().trim();
			} catch (Exception e) {
				try {
					value = row.getCell(12).getNumericCellValue() + "";
					if (value.indexOf(".") > 0) {
						value = value.substring(0, value.indexOf("."));
					}
				} catch (Exception e1) {
					value = row.getCell(12).getBooleanCellValue() + "";
				}

			}

			BStaticInfoObject obj = new BStaticInfoObject();
			obj.setPackageName(pack);
			obj.setClassName(className);
			obj.setName(name);
			obj.setLogicName(logicName);
			obj.setType(type);
			obj.setValue(value);
			list.add(obj);
		}
		try {
			writeObject(new File(targetPath), list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeObject(File f, Object obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		BufferedOutputStream bof = new BufferedOutputStream(fos, 1024 * 100);
		ObjectOutputStream oos = new ObjectOutputStream(bof);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bof.close();
		fos.close();
		bof = null;
		oos = null;
	}

	public static String getType(String type) {
		if (type.equals("String")) {
			return String.class.getName();
		} else if (type.equals(BigDecimal.class.getSimpleName())) {
			return BigDecimal.class.getName();
		}
		if (type.equals("Boolean")) {
			return boolean.class.getName();
		}
		return type;
	}

	public static void makeKubunList() {
		String path = "D:\\Bee\\beny\\toAWS\\定数\\区分一覧.xlsx";
		Workbook book = getWorkBook(path);
		Sheet sheet = book.getSheet("区分一覧");

		String targetPath = "D:\\Bee\\beny\\toAWS\\定数\\kubun.data";

		List<BStaticInfoObject> list = new ArrayList<BStaticInfoObject>();

		for (int i = 3; i < 1000; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				break;
			}
			String name = row.getCell(6).getStringCellValue().trim();
			if (name.equals("")) {
				break;
			}
			String id = row.getCell(5).getStringCellValue().trim();
			String value = row.getCell(7).getStringCellValue().trim();
			String kubun = row.getCell(8).getStringCellValue().trim();

			String pack = "jp.co.marubeni.beny.boxap.constants";
			String className = "BOXAP" + id + "Enum";
			String logicName = id + "_" + value;

			BStaticInfoObject obj = new BStaticInfoObject();
			obj.setPackageName(pack);
			obj.setClassName(className);
			obj.setName(name);
			obj.setLogicName(logicName);
			obj.setType(String.class.getName());
			obj.setValue(value);
			obj.setKubunParent(kubun);
			list.add(obj);
		}

		try {
			writeObject(new File(targetPath), list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
