package com.linkstec.excel.testcase;

import java.awt.Color;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExcelStyles {

	private XSSFCellStyle border, borderandgray, bold, boldandgray, gray, grayandline, olive;
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
			font = book.createFont();
			// font.setFontName("ＭＳ Ｐゴシック");
			font.setFontName("メイリオ");
			font.setFontHeightInPoints((short) 10);
			// font.setFontHeight((short) 2);
			style.setFont(font);
			style.setWrapText(true);
			style.setAlignment(HorizontalAlignment.LEFT);
			style.setVerticalAlignment(VerticalAlignment.TOP);
			style.setFillBackgroundColor(getColor(Color.WHITE));

		}
		return (XSSFCellStyle) style.clone();
	}

	public XSSFCellStyle getCenterAlignmentStyle() {
		XSSFCellStyle st = (XSSFCellStyle) getBorder().clone();
		st.setAlignment(HorizontalAlignment.CENTER);
		st.setVerticalAlignment(VerticalAlignment.CENTER);
		return (XSSFCellStyle) st.clone();
	}

	public XSSFCellStyle getHeaderRowStyle() {
		XSSFCellStyle st = getBasicStyle();
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		st.setFillForegroundColor(getColor(Color.decode("#00FFFF")));
		st.setFillBackgroundColor(getColor(Color.WHITE));
		return (XSSFCellStyle) st.clone();
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

			font.setFontHeightInPoints((short) 5);
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

	public XSSFCellStyle getOlive() {

		if (olive == null) {
			olive = this.getBasicStyle();
			olive.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			olive.setFillForegroundColor(getColor(Color.decode("#C4D79B")));
			olive.setFillBackgroundColor(getColor(Color.WHITE));
		}

		return olive;
	}

	private XSSFCellStyle rightline;

	public XSSFCellStyle getGrayAndRightBoldLined() {
		if (rightline == null) {
			rightline = (XSSFCellStyle) this.getOlive().clone();
			rightline.setBorderRight(BorderStyle.THIN);
			rightline.setBorderTop(BorderStyle.THIN);
			rightline.setBorderBottom(BorderStyle.THIN);
			rightline.setFillBackgroundColor(getColor(Color.WHITE));
		}
		return rightline;
	}

	private XSSFCellStyle leftline;

	public XSSFCellStyle getGrayAndLeftBoldLined() {
		if (leftline == null) {
			leftline = (XSSFCellStyle) this.getOlive().clone();
			leftline.setBorderLeft(BorderStyle.THIN);
			leftline.setBorderTop(BorderStyle.THIN);
			leftline.setBorderBottom(BorderStyle.THIN);
			// leftline.setFillBackgroundColor(new XSSFColor(Color.WHITE));
		}
		return leftline;
	}

	private XSSFCellStyle middleborderline;

	public XSSFCellStyle getGrayAndMiddleBoldLined() {
		if (middleborderline == null) {
			middleborderline = (XSSFCellStyle) this.getOlive().clone();
			middleborderline.setBorderTop(BorderStyle.THIN);
			middleborderline.setBorderBottom(BorderStyle.THIN);
			// middleborderline.setFillBackgroundColor(new XSSFColor(Color.WHITE));
		}
		return middleborderline;
	}

	private XSSFCellStyle rightlineonly;

	public XSSFCellStyle getRightBoldLinedOnly() {
		if (rightlineonly == null) {
			rightlineonly = this.getBasicStyle();
			rightlineonly.setBorderRight(BorderStyle.THIN);
			// rightlineonly.setFillBackgroundColor(new XSSFColor(Color.WHITE));

		}
		return rightlineonly;
	}

	private XSSFCellStyle leftlineonly;

	public XSSFCellStyle getLeftBoldLinedOnly() {
		if (leftlineonly == null) {
			leftlineonly = (XSSFCellStyle) this.getBasicStyle();
			leftlineonly.setBorderLeft(BorderStyle.THIN);
		}
		return leftlineonly;
	}

	private XSSFCellStyle toplineonly;

	public XSSFCellStyle getTopBoldLinedOnly() {
		if (toplineonly == null) {
			toplineonly = (XSSFCellStyle) this.getBasicStyle();
			toplineonly.setBorderTop(BorderStyle.THIN);
		}
		return toplineonly;
	}

	private XSSFCellStyle bottomlineonly;

	public XSSFCellStyle getBottomBoldLinedOnly() {
		if (bottomlineonly == null) {
			bottomlineonly = (XSSFCellStyle) this.getBasicStyle();
			bottomlineonly.setBorderBottom(BorderStyle.THIN);
		}
		return bottomlineonly;
	}

	private XSSFCellStyle middleborderlineonly;

	public XSSFCellStyle getMiddleBoldLinedOnly() {
		if (middleborderlineonly == null) {
			middleborderlineonly = this.getBasicStyle();
			middleborderlineonly.setBorderTop(BorderStyle.THIN);
			middleborderlineonly.setBorderBottom(BorderStyle.THIN);
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
			middleline.getFont().setBold(false);
			middleline.getFont().setFontHeight(10);
			middleline.setBorderTop(BorderStyle.THIN);
			middleline.setBorderBottom(BorderStyle.THIN);
		}
		return middleline;
	}

}
