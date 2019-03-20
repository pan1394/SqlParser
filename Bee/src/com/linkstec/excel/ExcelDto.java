package com.linkstec.excel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;

public class ExcelDto {
	ExcelStyles style;

	public ExcelDto(Sheet dto, BClass logic, BProject project, ExcelStyles style, List<BClass> list) {
		this.style = style;
		this.process(dto, logic, project, list);
	}

	private void process(Sheet dto, BClass logic, BProject project, List<BClass> list) {
		List<String> items = new ArrayList<String>();
		int number = 1;
		int dtosNumber = 0;
		int firstRow = number + 6;
		for (BClass bclass : list) {
			if (!bclass.isData()) {
				continue;
			}
			if (bclass.getLogicName().endsWith("TblDto")) {
				continue;
			}

			getDtoRowCell(dtosNumber, 4, dto, false).setCellValue(bclass.getPackage());
			CellStyle cellStyle = dto.getWorkbook().createCellStyle();
			cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			Cell cell1 = dto.getRow(6).createCell(10 + dtosNumber);
			if (bclass.getQualifiedName().startsWith("java.lang")) {
				cell1.setCellValue(bclass.getLogicName());
			} else {
				cell1.setCellValue(bclass.getQualifiedName());
			}
			cell1.setCellStyle(cellStyle);
			// CellRangeAddress range = new CellRangeAddress(4, 5, 10 + dtosNumber, 10 +
			// dtosNumber);
			// ExcelUtils.borderRegionAndMerge(range, dto, HorizontalAlignment.LEFT, style);

			dto.setColumnWidth(10 + dtosNumber, 25 * 256);

			List<BAssignment> vars = bclass.getVariables();

			for (BAssignment var : vars) {
				BParameter p = var.getLeft();

				if (Modifier.isStatic(p.getModifier())) {
					continue;
				}
				String logicName = p.getLogicName();
				String name = p.getName();

				if (!items.contains(logicName)) {
					items.add(logicName);
					Row row = dto.getRow(6 + number);
					if (row == null) {
						row = dto.createRow(6 + number);
					}
					Cell cell = row.getCell(5);
					if (cell == null) {
						cell = row.createCell(5);
					}
					cell.setCellValue(number);

					cell = row.getCell(6);
					if (cell == null) {
						cell = row.createCell(6);
					}
					cell.setCellValue(name == null ? logicName : name);

					cell = row.getCell(7);
					if (cell == null) {
						cell = row.createCell(7);
					}
					cell.setCellValue(logicName);

					cell = row.getCell(8);

					if (cell == null) {
						cell = row.createCell(8);
					}
					if (p.getBClass().getQualifiedName().startsWith("java.lang")) {
						cell.setCellValue(p.getBClass().getLogicName());
					} else {
						cell.setCellValue(p.getBClass().getQualifiedName());
					}

					Cell value = row.createCell(dtosNumber + 10);
					value.setCellValue("〇");

					number++;
				} else {
					int r = items.indexOf(logicName) + 7;
					Row row = dto.getRow(r);
					if (row == null) {
						row = dto.createRow(r);
					}
					Cell value = row.createCell(dtosNumber + 10);
					value.setCellValue("〇");
				}

			}

			dtosNumber++;

		}

		CellRangeAddress range = new CellRangeAddress(4, 6, 10 + dtosNumber, 10 + dtosNumber);
		ExcelUtils.borderRegionAndMerge(range, dto, HorizontalAlignment.CENTER, style);
		dto.getRow(4).getCell(10 + dtosNumber).setCellValue("備考");
		dto.setColumnWidth(10 + dtosNumber, 15 * 256);
		// border all
		for (int i = 0; i < items.size(); i++) {
			Row row = dto.getRow(firstRow + i);
			if (row == null) {
				break;
			}
			for (int a = 5; a < dtosNumber + 11; a++) {
				Cell c = row.getCell(a);
				if (c == null) {
					c = row.createCell(a);
				}
				if (a == 8) {
					ExcelUtils.borderAdnFillCell(c, style);
				} else {
					ExcelUtils.borderCell(c, style);
				}
			}
		}

		// merge sames
		Row row = dto.getRow(4);
		String pack = null;
		int first = 10;
		for (int a = 10; a < dtosNumber + 11; a++) {
			Cell c = row.getCell(a);
			String s = "";
			if (c != null) {
				s = c.getStringCellValue();
			}
			if (pack == null) {

				pack = s;
			} else {
				if (!s.equals(pack)) {
					if (a - first > 1) {
						CellRangeAddress region = new CellRangeAddress(4, 5, first, a - 1);
						ExcelUtils.borderRegionAndMerge(region, dto, HorizontalAlignment.CENTER, style);
						row.getCell(first).setCellValue(pack);
					} else {
						CellRangeAddress region = new CellRangeAddress(4, 5, first, first);
						ExcelUtils.borderRegionAndMerge(region, dto, HorizontalAlignment.CENTER, style);
					}
					first = a;
					pack = s;
				}
			}
		}

		Workbook book = dto.getWorkbook();
		book.setPrintArea(book.getSheetIndex(dto), 3, 3 + dtosNumber + 10, 1, 1 + dto.getLastRowNum() + 3);
	}

	private Cell getDtoRowCell(int number, int row, Sheet dto, boolean fill) {
		Cell cell = dto.getRow(row).createCell(10 + number);
		if (fill) {
			ExcelUtils.borderAdnFillCell(cell, style);
		} else {
			ExcelUtils.borderCell(cell, style);
		}

		return cell;
	}

}
