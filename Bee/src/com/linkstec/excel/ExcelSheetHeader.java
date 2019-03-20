package com.linkstec.excel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Sheet;

public class ExcelSheetHeader {
	public ExcelSheetHeader(Sheet sheet) {
		Date now = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		sheet.getRow(1).getCell(35).setCellValue("Updated Date:" + formatter.format(now));
	}
}
