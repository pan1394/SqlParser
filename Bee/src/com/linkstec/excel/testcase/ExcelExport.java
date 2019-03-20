package com.linkstec.excel.testcase;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BeeClassExistsException;
import com.linkstec.bee.core.fw.basic.BSQLSet;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.export.IExcelExport;

public class ExcelExport implements IExcelExport {

	public static final String EXCEL_XLS = "xls";
	public static final String EXCEL_XLSX = "xlsx";
	private File file;

	public File export(BModule model, BProject project, String template, boolean doInvoker, boolean doStatic)
			throws Exception {
		return null;
	}

	public File doExport(BModule model, BProject project, String template, boolean doInvoker, boolean doStatic,
			List<BSQLSet> sqlSet, ITableSql sql) throws Exception {
		this.writeExcel(model, project, template, doInvoker, doStatic, sqlSet, sql);
		return this.file;
	}

	public File getFile() {
		return this.file;
	}

	public void writeExcel(BModule module, BProject project, String finalXlsxPath, boolean doInvoker, boolean doStatic,
			List<BSQLSet> sqlSet, ITableSql sql) throws IOException, ClassNotFoundException, BeeClassExistsException {

		FileOutputStream out = null;
		List<BClass> models = module.getClassList();

		if (models.isEmpty()) {
			return;
		}
		for (BClass bclass : models) {
			if (bclass.getLogicName().endsWith("JclServiceImpl")) {
				models.remove(bclass);
				break;
			}
		}

		BClass mainClass = null;

		for (BClass bclass : models) {
			if (bclass.getLogicName().endsWith("JobServiceImpl")) {
				mainClass = bclass;
				break;
			}
		}

		if (mainClass != null) {
			String mainClassName = mainClass.getLogicName();
			String mainSheetName = mainClassName.substring(0, mainClassName.length() - 4);
			String batchId = mainClassName.substring(0, mainClassName.length() - 14);
			String batchName = mainClass.getName();
			int ii = batchName.indexOf("JobService");
			if (ii > 0) {
				batchName = batchName.substring(0, ii);
			}

			File finalXlsxFile = new File(finalXlsxPath);
			Workbook workBook = getWorkbok(finalXlsxFile);
			ExcelStyles style = new ExcelStyles(workBook);

			BClass target = null;
			for (BClass bclass : models) {
				if (bclass.getLogicName().endsWith("SqlClient")) {
					target = bclass;
				}
			}
			if (target != null) {
				Sheet sqlSheet = this.createSheet(workBook, ExcelTemplate.TEMPLATE_NAME + "_SQL",
						ExcelTemplate.TEMPLATE_NAME + "_" + target.getLogicName());
				ExcelSqlSheet.createSQL(module, project, style, sqlSheet, sqlSet, sql);
				sqlSheet.getRow(1).getCell(8).setCellValue(batchId);
				// sqlSheet.getRow(1).getCell(14).setCellValue(batchName);
				// sqlSheet.getRow(4).getCell(2).setCellValue(target.getLogicName());
			}

			Sheet sheet = workBook.getSheet(ExcelTemplate.TEMPLATE_NAME);
			sheet = cloneSheet(workBook, ExcelTemplate.TEMPLATE_NAME,
					ExcelTemplate.TEMPLATE_NAME + "_" + mainSheetName);
			new ExcelProcess(project, mainClass, sheet, style, doInvoker, doStatic);

			String name = mainClass.getName();
			int index = name.indexOf("JobService");
			if (index > 0) {
				name = name.substring(0, index);
			}

			String filename = "(" + name + ")バッチ単体ケースデータタ .xlsx";

			String path = finalXlsxFile.getParentFile().getAbsolutePath();
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			path = path + File.separator + filename;
			File f = new File(path);
			f.createNewFile();

			out = new FileOutputStream(f);
			BufferedOutputStream buffer = new BufferedOutputStream(out);
			workBook.write(buffer);
			buffer.flush();
			buffer.close();
			out.close();

			this.file = f;
		}

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

	private Sheet createSheet(Workbook workBook, String templateName, String name) {
		Sheet sql = workBook.getSheet(templateName);
		int tempIndex = workBook.getSheetIndex(sql);
		Sheet sqlSheet = workBook.cloneSheet(tempIndex);
		int sqlIndex = workBook.getSheetIndex(sqlSheet);
		workBook.setSheetName(sqlIndex, name);
		workBook.removeSheetAt(tempIndex);
		return sqlSheet;
	}

	private Sheet cloneSheet(Workbook workBook, String templateName, String name) {
		Sheet tName = workBook.getSheet(templateName);
		int tempIndex = workBook.getSheetIndex(tName);
		Sheet sheet = workBook.cloneSheet(tempIndex);
		int id = workBook.getSheetIndex(sheet);
		workBook.setSheetName(id, name);
		return sheet;
	}
}
