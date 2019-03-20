package com.linkstec.bee.core.codec.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.action.BDocIF;
import com.linkstec.bee.core.fw.action.BJavaGen;
import com.linkstec.bee.core.fw.editor.BProject;

public class ExcelDto {
	ExcelStyles style;
	private boolean has = false;

	public ExcelDto(Sheet dto, BClass logic, BProject project, ExcelProcess p, ExcelStyles style) {
		this.style = style;
		this.process(dto, logic, project, p);
	}

	private void process(Sheet dto, BClass logic, BProject project, ExcelProcess p) {
		List<String> items = new ArrayList<String>();
		int number = 1;
		int dtosNumber = 0;

		List<BClass> list = p.getDtos();
		has = true;
		for (BClass bclass : list) {

			Class<?> cls = BJavaGen.getClassByName(project, bclass.getQualifiedName());
			String className = cls.getName();
			getDtoRowCell(dtosNumber, 4, dto, false).setCellValue(logic.getPackage());
			getDtoRowCell(dtosNumber, 5, dto, false).setCellValue(logic.getLogicName());
			getDtoRowCell(dtosNumber, 6, dto, false).setCellValue("-");
			if (cls.getSuperclass() != null && cls.getSuperclass().getName().equals("com.nri.istar.ref.std.web.base.command.StdAbstractWizardCommandForm")) {
				getDtoRowCell(dtosNumber, 7, dto, true).setCellValue("WEB");
			} else {
				getDtoRowCell(dtosNumber, 7, dto, true).setCellValue("DATA");
			}
			getDtoRowCell(dtosNumber, 8, dto, false).setCellValue(className.substring(0, className.lastIndexOf(".") - 1));

			getDtoRowCell(dtosNumber, 9, dto, false).setCellValue(cls.getSimpleName());

			BDocIF doc = BJavaGen.getDoc(project, cls.getName());

			Field[] fs = cls.getDeclaredFields();

			for (Field f : fs) {

				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}
				String logicName = f.getName();
				String name = doc.getVariableDoc(logicName);

				if (!items.contains(logicName)) {
					items.add(logicName);
					Row row = dto.getRow(9 + number);
					if (row == null) {
						row = dto.createRow(9 + number);
					}
					Cell cell = row.getCell(1);
					if (cell == null) {
						cell = row.createCell(1);
					}
					cell.setCellValue(number);

					cell = row.getCell(2);
					if (cell == null) {
						cell = row.createCell(2);
					}
					cell.setCellValue(name == null ? logicName : name);

					cell = row.getCell(3);
					if (cell == null) {
						cell = row.createCell(3);
					}
					cell.setCellValue(logicName);

					cell = row.getCell(4);

					if (cell == null) {
						cell = row.createCell(4);
					}
					cell.setCellValue(f.getType().getSimpleName());

					Type type = f.getGenericType();

					BVariable var = BJavaGen.makeValuableByType(f.getType(), type, project);

					List<BType> paras = var.getParameterizedTypeValue().getParameterizedTypes();
					String parasName = "";
					int i = 0;
					if (paras != null) {
						for (BType bcl : paras) {
							if (i != 0) {
								parasName = parasName + ",";
							}
							parasName = parasName + bcl.getLogicName();
						}
						if (paras.size() != 0) {
							cell = row.getCell(5);
							if (cell == null) {
								cell = row.createCell(5);
							}
							cell.setCellValue(parasName);
						}
					}
					Cell value = row.createCell(dtosNumber + 9);
					ExcelUtils.borderCell(value, style);
					value.setCellValue("〇");

					for (int a = 1; a < dtosNumber + 10; a++) {
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
					number++;
				} else {
					int r = items.indexOf(logicName) + 9;
					Row row = dto.getRow(r);
					Cell value = row.createCell(dtosNumber + 9);
					ExcelUtils.borderCell(value, style);
					value.setCellValue("〇");
				}

			}
			dtosNumber++;

		}

		dto.getWorkbook().setPrintArea(4, 0, dtosNumber + 9, 0, dto.getLastRowNum() + 2);// number + 9);
	}

	private Cell getDtoRowCell(int number, int row, Sheet dto, boolean fill) {
		Cell cell = dto.getRow(row).createCell(9 + number);
		if (fill) {
			ExcelUtils.borderAdnFillCell(cell, style);
		} else {
			ExcelUtils.borderCell(cell, style);
		}

		return cell;
	}

}
