package com.linkstec.bee.core.codec.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BeeClassExistsException;
import com.linkstec.bee.core.fw.action.BProcess;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.export.IExcelExport;

public class ExcelExport implements IExcelExport {

	public static final String EXCEL_XLS = "xls";
	public static final String EXCEL_XLSX = "xlsx";
	private File file;

	public File export(BModule model, BProject project, String template, boolean doInvoker, boolean doStatic)
			throws Exception {
		this.writeExcel(model, project, template, doInvoker, doStatic);
		return this.file;
	}

	public File getFile() {
		return this.file;
	}

	public void writeExcel(BModule model, BProject project, String finalXlsxPath, boolean doInvoker, boolean doStatic)
			throws IOException, ClassNotFoundException, BeeClassExistsException {
		BProcess.start(2);
		FileOutputStream out = null;

		List<BClass> blist = model.getClassList();

		List<BClass> models = new ArrayList<BClass>();
		for (BClass m : blist) {
			if (!m.isData()) {
				if (!m.isAnonymous()) {
					models.add(m);
				}

			}
		}

		if (models.isEmpty()) {
			return;
		}

		File finalXlsxFile = new File(finalXlsxPath);
		Workbook workBook = getWorkbok(finalXlsxFile);
		ExcelStyles style = new ExcelStyles(workBook);
		Sheet classDesign = workBook.getSheet("Class Design");

		new ExcelSheetHeader(classDesign);
		ExcelClassDesign ecd = null;
		for (BClass m : models) {
			ecd = new ExcelClassDesign(classDesign, m, style);
			break;
		}
		if (ecd != null) {
			if (models.size() > 0) {
				Sheet cover = workBook.getSheet("Cover");
				cover.getRow(19).getCell(11).setCellValue(models.get(0).getLogicName());
			}

			Sheet template = workBook.getSheet("Process Design");
			BProcess.start(models.size());
			boolean generateDto = false;
			for (BClass m : models) {
				BProcess.go();
				int tempIndex = workBook.getSheetIndex(template);
				Sheet sheet = workBook.cloneSheet(tempIndex);

				int index = workBook.getSheetIndex(sheet);
				style.setSheetNumber(style.getSheetNumber() + 1);
				String name = "Process Design(" + style.getSheetNumber() + ")";
				workBook.setSheetName(index, name);
				if (generateSheet(m, sheet, ecd, project, style, doInvoker, doStatic)) {
					generateDto = true;
				}
			}
			BProcess.end();
			int tempIndex = workBook.getSheetIndex(template);
			workBook.removeSheetAt(tempIndex);

			if (!generateDto) {
				Sheet dto = workBook.getSheet("Dto Design");
				int dtoIndex = workBook.getSheetIndex(dto);
				workBook.removeSheetAt(dtoIndex);
			}

		}
		BProcess.go();

		File file = finalXlsxFile;

		String filename = "CORE_ClassDesign_" + model.getLogicName() + ".xlsx";

		String ppath = null;
		List<BEditorModel> list = model.getList();
		for (BEditorModel bee : list) {
			BClass b = (BClass) bee;
			String pack = b.getPackage();
			if (pack != null) {
				ppath = pack.replace('.', File.separatorChar);
				ppath = file.getParentFile().getAbsolutePath() + File.separator + ppath;
				File dir = new File(ppath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File f = new File(filename);
				filename = ppath + File.separator + f.getName();

				break;
			}
		}

		File f = new File(filename);
		f.createNewFile();

		out = new FileOutputStream(filename);
		BufferedOutputStream buffer = new BufferedOutputStream(out);
		workBook.write(buffer);
		buffer.flush();
		buffer.close();
		out.close();

		BProcess.go();
		BProcess.end();

		this.file = new File(filename);

	}

	private boolean generateSheet(BClass model, Sheet sheet, ExcelClassDesign ecd, BProject project, ExcelStyles style,
			boolean doInvoker, boolean doStatic) {
		if (!model.isData()) {

			ExcelProcess p = new ExcelProcess(project, model, sheet, ecd, style, doInvoker, doStatic);

			Sheet dto = sheet.getWorkbook().getSheet("Dto Design");
			new ExcelDto(dto, model, project, p, style);

			return p.getDtos().size() != 0;
		}
		return false;
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

}
