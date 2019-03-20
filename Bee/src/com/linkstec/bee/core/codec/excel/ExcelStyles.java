package com.linkstec.bee.core.codec.excel;

import java.awt.Color;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExcelStyles {

	private XSSFCellStyle border, borderandgray, bold, boldandgray, gray, grayandline;
	private Workbook book;
	private XSSFCellStyle middleline, underLineStyle, style;
	private int sheetNumber = 0;

	public ExcelStyles(Workbook book) {
		this.book = book;
	}

	public int getSheetNumber() {
		return sheetNumber;
	}

	public void setSheetNumber(int sheetNumber) {
		this.sheetNumber = sheetNumber;
	}

	public XSSFCellStyle getBasicStyle() {
		if (this.style == null) {
			this.style = (XSSFCellStyle) book.createCellStyle();
			style.setFillPattern(FillPatternType.NO_FILL);
		}
		return (XSSFCellStyle) style.clone();
	}

	public XSSFCellStyle getUnderLine() {
		if (this.underLineStyle == null) {
			underLineStyle = this.getBasicStyle();
			underLineStyle.setFillPattern(FillPatternType.NO_FILL);
			underLineStyle.setBorderBottom(BorderStyle.MEDIUM);
		}
		return this.underLineStyle;
	}

	public XSSFCellStyle getBorderAndGray() {
		if (borderandgray == null) {
			borderandgray = this.getBasicStyle();

			borderandgray.setFillForegroundColor(getColor(Color.decode("#D9D9D9")));
			borderandgray.setFillBackgroundColor(getColor(Color.WHITE));

			borderandgray.setBorderTop(BorderStyle.THIN);
			borderandgray.setBorderBottom(BorderStyle.THIN);
			borderandgray.setBorderLeft(BorderStyle.THIN);
			borderandgray.setBorderRight(BorderStyle.THIN);
		}
		return borderandgray;
	}

	public XSSFColor getColor(Color color) {
		return new XSSFColor(color, new DefaultIndexedColorMap());
	}

	public XSSFCellStyle getBorder() {
		if (border == null) {
			border = this.getBasicStyle();
			border.setBorderTop(BorderStyle.THIN);
			border.setBorderBottom(BorderStyle.THIN);
			border.setBorderLeft(BorderStyle.THIN);
			border.setBorderRight(BorderStyle.THIN);
		}
		return border;
	}

	private Font font;

	public Font getBoldFont() {
		if (font == null) {
			font = book.createFont();
			font.setFontName("ＭＳ Ｐゴシック");
			font.setBold(true);
		}
		return font;
	}

	public XSSFCellStyle getBold() {
		if (bold == null) {
			bold = this.getBasicStyle();
			bold.setFont(this.getBoldFont());
		}
		return bold;
	}

	public CellStyle getBoldAndGray() {

		if (boldandgray == null) {
			boldandgray = (XSSFCellStyle) this.getGray().clone();
			boldandgray.setFont(this.getBoldFont());
		}

		return boldandgray;
	}

	public XSSFCellStyle getGray() {

		if (gray == null) {
			gray = this.getBasicStyle();

			gray.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			gray.setFillForegroundColor(getColor(Color.decode("#D9D9D9")));
			gray.setFillBackgroundColor(getColor(Color.WHITE));
		}

		return gray;
	}

	private XSSFCellStyle rightline;

	public XSSFCellStyle getGrayAndRightBoldLined() {
		if (rightline == null) {
			rightline = (XSSFCellStyle) this.getGray().clone();
			rightline.setBorderRight(BorderStyle.MEDIUM);
			rightline.setBorderTop(BorderStyle.MEDIUM);
			rightline.setBorderBottom(BorderStyle.MEDIUM);
			rightline.setFillBackgroundColor(getColor(Color.WHITE));
		}
		return rightline;
	}

	private XSSFCellStyle leftline;

	public XSSFCellStyle getGrayAndLeftBoldLined() {
		if (leftline == null) {
			leftline = (XSSFCellStyle) this.getGray().clone();
			leftline.setBorderLeft(BorderStyle.MEDIUM);
			leftline.setBorderTop(BorderStyle.MEDIUM);
			leftline.setBorderBottom(BorderStyle.MEDIUM);
			// leftline.setFillBackgroundColor(new XSSFColor(Color.WHITE));
		}
		return leftline;
	}

	private XSSFCellStyle middleborderline;

	public XSSFCellStyle getGrayAndMiddleBoldLined() {
		if (middleborderline == null) {
			middleborderline = (XSSFCellStyle) this.getGray().clone();
			middleborderline.setBorderTop(BorderStyle.MEDIUM);
			middleborderline.setBorderBottom(BorderStyle.MEDIUM);
			// middleborderline.setFillBackgroundColor(new XSSFColor(Color.WHITE));
		}
		return middleborderline;
	}

	private XSSFCellStyle rightlineonly;

	public XSSFCellStyle getRightBoldLinedOnly() {
		if (rightlineonly == null) {
			rightlineonly = this.getBasicStyle();
			rightlineonly.setBorderRight(BorderStyle.MEDIUM);
			// rightlineonly.setFillBackgroundColor(new XSSFColor(Color.WHITE));

		}
		return rightlineonly;
	}

	private XSSFCellStyle leftlineonly;

	public XSSFCellStyle getLeftBoldLinedOnly() {
		if (leftlineonly == null) {
			leftlineonly = (XSSFCellStyle) this.getBasicStyle();
			leftlineonly.setBorderLeft(BorderStyle.MEDIUM);
		}
		return leftlineonly;
	}

	private XSSFCellStyle middleborderlineonly;

	public XSSFCellStyle getMiddleBoldLinedOnly() {
		if (middleborderlineonly == null) {
			middleborderlineonly = this.getBasicStyle();
			middleborderlineonly.setBorderTop(BorderStyle.MEDIUM);
			middleborderlineonly.setBorderBottom(BorderStyle.MEDIUM);
		}
		return middleborderlineonly;
	}

	public XSSFCellStyle getGrayAndLined() {
		if (grayandline == null) {
			grayandline = (XSSFCellStyle) getGray().clone();
			grayandline.setBorderTop(BorderStyle.THIN);
			grayandline.setBorderBottom(BorderStyle.THIN);
			grayandline.setBorderLeft(BorderStyle.THIN);
			grayandline.setBorderRight(BorderStyle.THIN);
		}

		return grayandline;
	}

	public XSSFCellStyle getMiddleLined() {

		if (middleline == null) {
			middleline = this.getBasicStyle();

			// middleline.setFillForegroundColor(new XSSFColor(Color.WHITE));
			// middleline.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// middleline.setFillBackgroundColor(new XSSFColor(Color.WHITE));
			middleline.getFont().setBold(false);

			middleline.getFont().setFontHeight(10);
			middleline.setBorderTop(BorderStyle.THIN);
			middleline.setBorderBottom(BorderStyle.THIN);
		}
		return middleline;
	}

}
