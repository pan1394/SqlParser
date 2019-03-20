package com.link.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.impl.BAssignmentImpl;
import com.linkstec.bee.core.impl.BParameterImpl;

public class TableReaderUtils {

	private static String path = "D:/Bee/beny/test/batchtabls.xlsx";
	private static String dataPath = "D:\\Bee\\application\\beny\\basic\\CMN";

	public static void main(String[] args) {
		Workbook workbook = getWorkBook();

		List<BasicComponentModel> TableEntitys = new ArrayList<BasicComponentModel>();
		for (int i = 2; i < workbook.getNumberOfSheets(); i++) {

			Sheet sheet = workbook.getSheetAt(i);
			ComponentTypeModel type = new ComponentTypeModel();
			type.setIconPath(ModelConstants.DB_ICON);
			BasicComponentModel model = new BasicComponentModel(type);
			model.setLogicName(sheet.getRow(5).getCell(2).getStringCellValue());
			model.setName(sheet.getRow(4).getCell(2).getStringCellValue());

			if (model.getLogicName() == null) {
				Debug.a();
			}
			if (model.getName() == null) {
				Debug.a();
			}

			boolean read = true;
			int index = 0;
			while (read) {
				Row row = sheet.getRow(13 + index);
				if (row == null) {
					break;
				}
				TableEntity entity = new TableEntity();
				double no = row.getCell(0).getNumericCellValue();
				if (no < 0) {
					read = false;
					break;
				}
				entity.setName(row.getCell(1).getStringCellValue());
				entity.setLogiccName(row.getCell(2).getStringCellValue());
				entity.setDataType(row.getCell(3).getStringCellValue());
				entity.setNullAble(row.getCell(4).getStringCellValue());
				entity.setDefaultSet(row.getCell(5).getStringCellValue());
				entity.setComment(row.getCell(6).getStringCellValue());

				index++;

				BAssignment var = new BAssignmentImpl();
				BParameter left = new BParameterImpl();
				left.setBClass(getTypeClass(entity.getDataType()));
				left.setName(entity.getName());
				left.setLogicName(makeLogicName(entity.getLogiccName()));
				var.setLeft(left);

				if (left.getBClass() == null) {
					Debug.a();
				}

				if (left.getLogicName() == null) {
					Debug.a();
				}

				if (left.getName() == null) {
					Debug.a();
				}

				model.addVar(var);

			}

			TableEntitys.add(model);

			File file1 = new File(dataPath + File.separator + model.getLogicName() + ".cm");
			FileOutputStream out;
			try {
				out = new FileOutputStream(file1);
				ObjectOutputStream objOut = new ObjectOutputStream(out);
				objOut.writeObject(model);
				objOut.flush();
				objOut.close();
				System.out.println(file1.getAbsolutePath());
			} catch (IOException e) {
				System.out.println("write object failed");
				e.printStackTrace();
			}
		}
		File file1 = new File("test.dat");
		FileOutputStream out;
		try {
			out = new FileOutputStream(file1);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(TableEntitys);
			objOut.flush();
			objOut.close();
			System.out.println("write object  success!");
		} catch (IOException e) {
			System.out.println("write object failed");
			e.printStackTrace();
		}
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
		if (type.startsWith("DATE")) {
			return CodecUtils.getClassFromJavaClass(Date.class, null);
		}
		if (type.startsWith("DATETIME")) {
			return CodecUtils.getClassFromJavaClass(Timestamp.class, null);
		}
		return null;
	}

	public static Workbook getWorkBook() {
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
