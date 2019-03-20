package com.linkstec.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BMethod;

public class ExcelClassDesign {
	private int line = 6;
	private Sheet sheet;
	private ExcelStyles style;

	public ExcelClassDesign(Sheet sheet, BClass model, ExcelStyles style) {
		this.style = style;
		this.sheet = sheet;
	}

	public Sheet getSheet() {
		return this.sheet;
	}

	public void createRows(BClass model) {

		Row row = ExcelUtils.getRow(sheet, line);
		ExcelUtils.getCell(row, 1).setCellValue(line - 7);

		String className = model.getLogicName();
		String name = getClassType(className);

		ExcelUtils.getCell(row, 1).setCellValue(model.getLogicName());
		// ExcelUtils.getCell(row, 7).setCellValue(name);
		// ExcelUtils.getCell(row, 8).setCellValue(model.getPackage());

	}

	public void makeMethods(List<ExcelMethod> methods) {

		for (ExcelMethod m : methods) {

			BMethod mthod = m.getMethod();

			Row row = this.sheet.getRow(line);
			if (row == null) {
				row = this.sheet.createRow(line);
			}
			// CellRangeAddress range = new CellRangeAddress(line, line, 1, 2);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet,
			// HorizontalAlignment.CENTER, style);
			// ExcelUtils.getCell(row, 1).setCellValue(line - 7);
			//
			// range = new CellRangeAddress(line, line, 3, 8);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// range = new CellRangeAddress(line, line, 9, 15);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// range = new CellRangeAddress(line, line, 16, 22);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// range = new CellRangeAddress(line, line, 23, 29);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// range = new CellRangeAddress(line, line, 30, 37);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// range = new CellRangeAddress(line, line, 38, 42);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// range = new CellRangeAddress(line, line, 43, 47);
			// ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT,
			// style);
			//
			// Hyperlink link =
			// sheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
			// link.setAddress("'" + m.getSheet().getSheetName() + "'!" +
			// m.getAddress().formatAsString());
			// link.setLabel(m.getSheet().getSheetName());

			if (mthod instanceof BConstructor) {
				// ExcelUtils.getCell(row, 23).setCellValue("-");
				// Cell cell = ExcelUtils.getCell(row, 30);
				// cell.setCellValue("Constructor");
				// cell.setHyperlink(link);
			} else {
				// ExcelUtils.getCell(row, 9).setCellValue(mthod.getName());
				// ExcelUtils.getCell(row, 10).setCellValue(mthod.getLogicName());
				// ExcelUtils.getCell(row,
				// 11).setCellValue(mthod.getParameter().get(0).getLogicName());
				// ExcelUtils.getCell(row,
				// 12).setCellValue(mthod.getReturn().getBClass().getLogicName());
				//// ExcelUtils.getCell(row, 23).setCellValue(mthod.getLogicName());
				Cell cell = ExcelUtils.getCell(row, 30);
				// cell.setCellValue("No." + ((IUnit) mthod).getNumber().getString());
				// cell.setCellValue("No." + "1");
				// cell.setHyperlink(link);
			}
			line++;
		}
	}

	public int getRow() {
		return this.line;
	}

	public static class ExcelMethod {
		private BMethod method;
		private CellAddress address;
		private Sheet sheet;

		ExcelMethod(Sheet sheet) {
			this.sheet = sheet;
		}

		public Sheet getSheet() {
			return this.sheet;
		}

		public BMethod getMethod() {
			return method;
		}

		public void setMethod(BMethod method) {
			this.method = method;
		}

		public CellAddress getAddress() {
			return address;
		}

		public void setAddress(CellAddress address) {
			this.address = address;
		}

	}

	public String getClassType(String className) {
		String name = "Others";
		if (className.toLowerCase().endsWith("validator")) {
			name = "Validator";
		} else if (className.toLowerCase().endsWith("dto") || className.toLowerCase().endsWith("bean")
				|| className.toLowerCase().endsWith("entity")) {
			name = "Dto";
		} else if (className.toLowerCase().endsWith("logic")) {
			name = "Logic";
		} else if (className.toLowerCase().endsWith("dao") || className.toLowerCase().endsWith("client")) {
			name = "Dao";
		} else if (className.toLowerCase().endsWith("controller")) {
			name = "Controller";
		} else if (className.toLowerCase().endsWith("commandform")) {
			name = "CommandForm";
		} else if (className.toLowerCase().endsWith("job")) {
			name = "Job";
		} else if (className.toLowerCase().endsWith("processor")) {
			name = "MessageProcessor";
		} else if (className.toLowerCase().endsWith("creator")) {
			name = "MessageCreator";
		} else if (className.toLowerCase().endsWith("spi")) {
			name = "SPI";
		} else if (className.toLowerCase().endsWith("calculator")) {
			name = "Calculator";
		} else if (className.toLowerCase().endsWith("utils")) {
			name = "Utility";
		} else if (className.toLowerCase().endsWith("serviceimpl") || className.toLowerCase().endsWith("service")) {
			name = "Service";
		}
		return name;
	}
}
