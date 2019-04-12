package com.linkstec.excel.testcase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.action.BJavaGen;
import com.linkstec.bee.core.fw.action.BProcess;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BModifiedBlock;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.linkstec.bee.core.fw.logic.BThrow;
import com.linkstec.bee.core.fw.logic.BTryUnit;
import com.linkstec.excel.testcase.ExcelClassDesign.ExcelMethod;

public class ExcelProcess {

	private List<ExcelMethod> methods = new ArrayList<ExcelMethod>();

	private ExcelStyles style;
	private BClass model;
	private boolean doInvoker;
	private boolean doStatic;
	private BProject project;
	private Sheet sheet;

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelProcess(BProject project, BClass logic, Sheet sheet, ExcelStyles style, boolean doInvoker,
			boolean doStatic) {
		this.setAll(project, logic, sheet, style, doInvoker, doStatic);
		this.createSheet(logic, sheet, style, null);
	}

	public ExcelProcess(BProject project, BClass logic, List<BMethod> ms, Sheet sheet, ExcelStyles style,
			boolean doInvoker, boolean doStatic) {
		this.setAll(project, logic, sheet, style, doInvoker, doStatic);
		this.createSheet(logic, sheet, style, ms);

	}

	private void setAll(BProject project, BClass logic, Sheet sheet, ExcelStyles style, boolean doInvoker,
			boolean doStatic) {
		this.doStatic = doStatic;
		this.sheet = sheet;
		this.project = project;
		this.doInvoker = doInvoker;
		this.style = style;
		this.model = logic;

	}

	private void createSheet(BClass logic, Sheet sheet, ExcelStyles style, List<BMethod> ms) {
		ExcelLogicProgress p = new ExcelLogicProgress();
		p.setDoInvoker(doInvoker);
		int initRow = 6;
		p.setInitRowNum(initRow);
		p.setRow(initRow);

		// serviceId cell
		Row serviceRow = sheet.getRow(initRow);
		ExcelUtils.setValueForNo(serviceRow, 1);

		Workbook book = sheet.getWorkbook();
		this.createClass(logic, sheet, p);

		clone2(logic, initRow, p, logic.getName(), "");
//		List<String> jclIds = ExcelTemplate.JCLService.list();
//		int i = 0;
//		for (String jclId : jclIds) {
//			clone(logic, initRow, p, jclId, (++i));
//		}
		book.removeSheetAt(book.getSheetIndex(sheet));
	}

	private void clone2(BClass logic, int initRow, ExcelLogicProgress p, String name, String append) {
		Workbook book = sheet.getWorkbook();
		// serviceName
		String tname = name;
		if (name.length() > 20) {
			tname = name.substring(5, name.length() - 12);
		}
		Sheet targetSheet = ExcelUtils.copySheet(book, sheet, initRow + 1, p.getRow(), tname, append);

		// serviceId
		Row serviceRow = ExcelUtils.getRow(targetSheet, initRow);
		Row titleRow = ExcelUtils.getRow(targetSheet, 2);
		ExcelUtils.setValueForServiceId(serviceRow, "なし");
		// serviceName
		ExcelUtils.setValueForBigCategory(serviceRow, name);
		ExcelUtils.setValue(titleRow, 8, name);
		ExcelUtils.setValue(titleRow, 9, name);
		ExcelUtils.setValue(titleRow, 11, "tester");

		CellRangeAddress region0 = new CellRangeAddress(initRow, initRow, 3, ExcelLogicProgress.maxWith);
		ExcelUtils.initRowsStyleForRegionCells(region0, targetSheet, style);
		CellRangeAddress region = new CellRangeAddress(initRow + 1, targetSheet.getLastRowNum(), 2,
				ExcelLogicProgress.maxWith);
		ExcelUtils.borderForRegionCells(region, targetSheet, style);
		CellRangeAddress region2 = new CellRangeAddress(initRow, targetSheet.getLastRowNum(), 1, 1);
		ExcelUtils.borderWithCenterAlignmentForRegionCells(region2, targetSheet, style);

		book.setPrintArea(book.getSheetIndex(targetSheet), 1, ExcelLogicProgress.maxWith, 1,
				targetSheet.getLastRowNum());
	}

	private void clone(BClass logic, int initRow, ExcelLogicProgress p, String jclId, int count) {
		Workbook book = sheet.getWorkbook();
		// serviceName
		String classA = logic.getLogicName();
		String serviceName = classA.substring(0, classA.length() - 10);// -4;
		// deviceName
		String name = logic.getName();
		int index = name.indexOf(Constants.ServiceName);
		if (index > 0) {
			name = name.substring(0, index);
		}

		String append = ExcelTemplate.JCLService.get(jclId);
		Sheet targetSheet = ExcelUtils.copySheet(book, sheet, initRow + 1, p.getRow(), serviceName + count, append);

		// serviceId
		Row serviceRow = ExcelUtils.getRow(targetSheet, initRow);
		Row titleRow = ExcelUtils.getRow(targetSheet, 2);
		ExcelUtils.setValueForServiceId(serviceRow, jclId);
		// serviceName
		ExcelUtils.setValueForBigCategory(serviceRow, serviceName);
		ExcelUtils.setValue(titleRow, 8, name);
		ExcelUtils.setValue(titleRow, 9, name);
		ExcelUtils.setValue(titleRow, 11, "tester");

		CellRangeAddress region0 = new CellRangeAddress(initRow, initRow, 3, ExcelLogicProgress.maxWith);
		ExcelUtils.initRowsStyleForRegionCells(region0, targetSheet, style);
		CellRangeAddress region = new CellRangeAddress(initRow + 1, targetSheet.getLastRowNum(), 2,
				ExcelLogicProgress.maxWith);
		ExcelUtils.borderForRegionCells(region, targetSheet, style);
		CellRangeAddress region2 = new CellRangeAddress(initRow, targetSheet.getLastRowNum(), 1, 1);
		ExcelUtils.borderWithCenterAlignmentForRegionCells(region2, targetSheet, style);

		book.setPrintArea(book.getSheetIndex(targetSheet), 1, ExcelLogicProgress.maxWith, 1,
				targetSheet.getLastRowNum());

	}

	private void createLogicBody(BLogicBody body, Sheet sheet, ExcelLogicProgress p) {
		List<BLogicUnit> units = body.getUnits();
		p.setCurrentUnits(units);
		int count = units.size();
		p.increaseDepth();

		int flowIndex = 1;

		for (int i = 0; i < count; i++) {
			BLogicUnit unit = units.get(i);
			p.setCurrentUnitIndex(i);

			Object obj = unit.getUserAttribute("DESC");
			if (obj instanceof String) {
				String desc = (String) obj;

				// create a new row for note node.
				Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
				// set initial depth
				p.setCol(CaseColumn.BIG_CATEGORY.getColumn());
				ExcelUtils.setValueForBigCategory(row, desc);
				p.increaseDepth();
			}
			this.createUnit(unit, sheet, p, i);
		}

		p.decreaseDepth();
	}

	private void createClass(BClass bclass, Sheet sheet, ExcelLogicProgress p) {
		List<BAssignment> vars = bclass.getVariables();
		List<BMethod> methods = bclass.getMethods();
		this.createMethods(methods, sheet, p);
	}

	private void createMethods(List<BMethod> methods, Sheet sheet, ExcelLogicProgress p) {
		if (methods.size() > 0) {
			p.increaseMethod();
			for (int i = 0; i < methods.size(); i++) {
				BMethod method = methods.get(i);
				this.createMethod(sheet, p, method);
			}
		}
	}

	private boolean createUnit(BLogicUnit unit, Sheet sheet, ExcelLogicProgress p, int i) {

		// BProcess.go();

		if (unit instanceof BNote) {
			Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
			ExcelUtils.setValueForBigCategory(row, ((BNote) unit).getNote());
			return true;
		}

		if (unit instanceof BInvoker) {
			BInvoker in = (BInvoker) unit;
			if (in.isLinker()) {
				return true;
			}
		}

		if (unit instanceof IUnit) {
			// p.setRow(p.getRow() + 1);
			p.increaseUnit((IUnit) unit);
		}
		if (!(unit instanceof BInvoker)) {
			if (p.getUnitPath() != null) {
				p.getUnitPath().setContinuous(false);
			}
		}
		if (p.isDoInvoker()) {
			if (unit instanceof IUnit && !(unit instanceof BMethod)) {
				List<BInvoker> linkers = ExcelUtils.getLinkers((IUnit) unit);
				ExcelUtils.getLinkers((IUnit) unit);
				if (linkers.size() > 0) {
					if (p.getUnitPath() != null) {
						p.getUnitPath().setContinuous(false);
					}
				}
				for (BInvoker invoker : linkers) {
					this.createInvoker(invoker, sheet, p, true, null);
				}
			}
		}

		if (unit instanceof BMethod) {
			this.createMethod(sheet, p, (BMethod) unit);
			System.out.println("BMethod invocation missed..........................");
		} else if (unit instanceof BAssign) {
			this.createAssign((BAssign) unit, sheet, p);
		} else if (unit instanceof BInvoker) {
			this.createInvoker((BInvoker) unit, sheet, p, true, null);
		} else if (unit instanceof BReturnUnit) {
			this.createReturn((BReturnUnit) unit, sheet, p);
		} else if (unit instanceof BLoopUnit) {
			this.createLoop((BLoopUnit) unit, sheet, p);
			System.out.println("BMethod invocation missed..........................");
		} else if (unit instanceof BMultiCondition) {
			this.createIf((BMultiCondition) unit, sheet, p);
		} else if (unit instanceof BTryUnit) {
			this.createTry((BTryUnit) unit, sheet, p);
		} else if (unit instanceof BThrow) {
			this.createThrow((BThrow) unit, sheet, p);
		} else if (unit instanceof BModifiedBlock) {
			this.createLogicBody((BLogicBody) unit, sheet, p);
		} else {

		}

		if (unit instanceof IUnit && p.isCreateNewRow()) {
			ExcelUtils.makeBlankRow(sheet, p);
		}
		return true;
	}

	private ExcelProcess makeAnotherSheet(Sheet sheet, BClass bclass) {
		style.setSheetNumber(style.getSheetNumber() + 1);

		Workbook book = sheet.getWorkbook();

		Sheet template = book.getSheet("モジュールクラス");
		int tempIndex = book.getSheetIndex(template);
		Sheet another = book.cloneSheet(tempIndex);
		int index = book.getSheetIndex(another);
		// String className = bclass.getLogicName();
		// String type = this.classDesign.getClassType(className);
		String name = "モジュールクラス(" + bclass.getName() + ")";
		book.setSheetName(index, name);

		return new ExcelProcess(project, bclass, another, style, this.doInvoker, this.doStatic);
	}

	private void createThrow(BThrow unit, Sheet sheet, ExcelLogicProgress p) {
		BValuable obj = unit.getException();
		String s = p.getPath().toString() + "スロー ";
		Row row = ExcelUtils.getRow(sheet, p.getRow());
		s += this.createValuable(obj, p, false);

		Cell cell8 = ExcelUtils.getCell(row, CaseColumn.EXPECTED_RESULT.getColumn());
		String origin = cell8.getStringCellValue();
		StringBuilder sb = new StringBuilder(origin);
		if (origin == null || "".equals(origin.trim())) {
		} else {
			sb.append(ExcelUtils.getReturnStr());
		}
		cell8.setCellValue(sb.append(s).toString());
	}

	private void createTry(BTryUnit unit, Sheet sheet, ExcelLogicProgress p) {
		Row row = ExcelUtils.getRow(sheet, p.getRow());
		Cell c = row.createCell(p.getCol());
		// c.setCellValue("try{");

		// p.setRow(p.getRow() + 1);

		p.decreaseDepth(); // 没有处理
		this.createLogicBody(unit.getTryEditor(), sheet, p);

		// p.setRow(p.getRow() + 1);

		List<BCatchUnit> cates = unit.getCatches();
		for (BCatchUnit cats : cates) {
//			BParameter var = cats.getVariable();
//			row = ExcelUtils.getRow(sheet, p.getRow());
//			c = row.createCell(p.getCol());
//			c.setCellValue("}catch(" + var.getBClass().getLogicName() + " " + var.getLogicName() + " ){");
//			p.setRow(p.getRow() + 1);
//			this.createLogicBody(cats.getEditor(), sheet, p);
//			p.setRow(p.getRow() + 2);

			BParameter var = cats.getVariable();
			row = ExcelUtils.makeRowWithRowNum(sheet, p);
			String excptClassStr = String.format("%s異常を捕獲する", var.getBClass().getLogicName());
			String excptVarName = var.getLogicName();
			ExcelUtils.setValueForMidCategory(row, excptClassStr);
			ExcelUtils.setValueForCase(row, ExcelTemplate.NULL_STRING);
			// do not create a new row
			p.setCreateNewRow(false);
			this.createLogicBody(cats.getEditor(), sheet, p);
			p.setCreateNewRow(true);
		}
//		if (cates.size() > 1) {
//			row = ExcelUtils.getRow(sheet, p.getRow());
//			c = row.createCell(p.getCol());
//			c.setCellValue("}");
//		}
//		BLogicBody f = unit.getFinalEditor();
//		if (f != null) {
//			p.setRow(p.getRow() + 1);
//			row = ExcelUtils.getRow(sheet, p.getRow());
//			c = row.createCell(p.getCol());
//			c.setCellValue(((IUnit) unit).getNumber().getString() + "finally");
//			this.createLogicBody(f, sheet, p);
//		}
//		p.setRow(p.getRow() + 2);

	}

	private void createReturn(BReturnUnit returnUnit, Sheet sheet, ExcelLogicProgress p) {
		/*
		 * BValuable obj = returnUnit.getReturnValue(); String title =
		 * p.getPath().toString() + "以下を返す"; this.makeReturnLine(title, sheet, p, "",
		 * false); String s = "  " + this.createValuable(obj, p, true);
		 * p.setRow(p.getRow() + 1);
		 * 
		 * this.makeReturnLine(s, sheet, p, title, false); p.setRow(p.getRow() + 1);
		 * 
		 * Row row = newRow(p); p.setCreateNewRow(true);
		 */

		BValuable obj = returnUnit.getReturnValue();
		String s = this.createValuable(obj, p, true);
		Row r = ExcelUtils.makeRowWithRowNum(sheet, p);
		ExcelUtils.setValueForCase(r, "戻り値返却");
		ExcelUtils.setValueForExpectedResult(r, String.format("戻り値:%s", s));
	}

	private void createAssign(BAssign a, Sheet sheet, ExcelLogicProgress p) {
		log("createAssign:" + a.toString());
		int col = p.getCol();

		BValuable right = a.getRight();
		if (a instanceof BAssignment) {
			BAssignment assign = (BAssignment) a;
			BParameter left = assign.getLeft();
			BValuable value = assign.getRight();
			boolean createInvoker = false;
			if (value instanceof BInvoker) {
				BInvoker in = (BInvoker) value;
				BValuable cv = in.getInvokeChild();
				BValuable pv = in.getInvokeParent();
				List<BValuable> prms = in.getParameters();
				createInvoker = !(cv instanceof BConstructor);
				boolean getter = createInvoker && prms.size() == 0;
				if (getter) {
					String name = left.getName();
					String init = String.format("%sの初期化", name);
					String casea = String.format("変数.%sの設定", name);
					String expected = String.format("変数.%sに%sが設定されること", name, String.format("%s.%s", pv, cv));
					Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
					ExcelUtils.setValueForLitCategory(row, init);
					ExcelUtils.setValueForCase(row, casea);
					ExcelUtils.setValueForExpectedResult(row, expected);
				} else if (createInvoker) {
					this.createInvoker(in, sheet, p, true, left);
				} else {// initiliazer
					String init = left.getLogicName() + "の宣言";
					String casea = left.getLogicName() + "を初期化";
					String expected = "インスタンス生成がされ、処理継続されること。";
					Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
					ExcelUtils.setValueForLitCategory(row, casea);
					ExcelUtils.setValueForCase(row, init);
					ExcelUtils.setValueForExpectedResult(row, expected);
				}
			} else {
				String name = left.getLogicName();
				String casea = String.format("変数.%sの設定", name);
				System.out.println(casea);
				String express = this.createValuable(right, p, false);
				String expected = String.format("変数.%sに%sが設定されること", name, express);
				Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
				ExcelUtils.setValueForCase(row, casea);
				ExcelUtils.setValueForExpectedResult(row, expected);
			}
		} else {
			BAssignExpression assign = (BAssignExpression) a;
			BValuable left = assign.getLeft();
			BValuable value = assign.getRight();
			BVariable o = null;
			if (left instanceof BVariable) {
				o = (BVariable) left;
			}

			if (value instanceof BInvoker && left instanceof BVariable) {
				BInvoker in = (BInvoker) value;
				BValuable v = in.getInvokeChild();
				if (!(v instanceof BConstructor)) {
					this.createInvoker(in, sheet, p, true, (BVariable) left);
					// createInvoker = true;
				}
			} else {
				// when it is expression(value set)
				// String name = ((IUnit) assign).getNumber().toString();
				String name = "";// p.getPath().toString();
				if (right == null || (right instanceof BVariable && right.getBClass() == null)) {
					name = name + "NULLを設定する ";
				} else {
					name = o.getLogicName();
					String casea = String.format("変数.%sの設定", name);
					String expected = String.format("変数.%sに%sが設定されること", name, this.createValuable(right, p, false));
					Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
					ExcelUtils.setValueForCase(row, casea);
					ExcelUtils.setValueForExpectedResult(row, expected);
				}
				// name = name + this.createValuable(left, p, false);
				// this.makeReturnLine(name, sheet, p, p.getPath().toString(), false);
			}

		}
	}

	private void createLoop(BLoopUnit loop, Sheet sheet, ExcelLogicProgress p) {
		// String title = ((IUnit) loop).getNumber().toString();
		String title = "";// p.getPath().toString();
		String start = "";
		String end = "";
		if (loop.getLoopType() == BLoopUnit.TYPE_ENHANCED) {
			BObject ex = loop.getEnhanceExpression();
			BParameter left = loop.getEnhanceVariable();

			BVariable lvar = (BVariable) left;

			title = title + lvar.getName() + "を";

			if (ex instanceof BVariable) {
				BVariable evar = (BVariable) ex;
				title = title + evar.getLogicName() + "で";
			}
			title = title + lvar.getName() + "をベースにループを実施する";
			start = lvar.getName() + "をベースにループを実施する";
			title = lvar.getName();
			end = lvar.getName() + "を終わりする";

		} else if (loop.getLoopType() == BLoopUnit.TYPE_DOWHILE) {
			BValuable obj = loop.getCondition();
			title = title + "下記のようにする  " + this.createValuable(obj, p, false) + " がTRUEにまで";
		} else if (loop.getLoopType() == BLoopUnit.TYPE_FORLOOP) {
			String counter = "";
			List<BAssign> cs = loop.getForLoopInitializers();
			int i = 0;
			for (BAssign assign : cs) {
				BValuable obj;
				if (assign instanceof BAssignment) {
					BAssignment bas = (BAssignment) assign;
					obj = bas.getLeft();
				} else {
					BAssignExpression bas = (BAssignExpression) assign;
					obj = bas.getLeft();
				}
				if (i > 0) {
					counter = counter + ",";
				}
				counter = counter + this.createValuable(obj, p, false);
			}

			title = title + "ループ " + counter + " 回、 " + createValuable(loop.getCondition(), p, false) + "がTRUEの場合";
		} else if (loop.getLoopType() == BLoopUnit.TYPE_WHILE) {
			title = title + createValuable(loop.getCondition(), p, false) + " がTRUEにまで";
		}

		// this.makeReturnLine(title, sheet, p, ((IUnit) loop).getNumber().toString(),
		// false);
		this.makeLine(title, sheet, p);
		Row row = sheet.getRow(p.getRow());
		ExcelUtils.setValueForCase(row, ExcelTemplate.NULL_STRING);
		ExcelUtils.setValueForExpectedResult(row, start);
		this.createLogicBody(loop.getEditor(), sheet, p);
		this.makeLine(title, sheet, p);
		Row rowx = sheet.getRow(p.getRow());
		ExcelUtils.setValueForCase(rowx, ExcelTemplate.NULL_STRING);
		ExcelUtils.setValueForExpectedResult(rowx, end);
	}

	private void createIf(BMultiCondition ifUnit, Sheet sheet, ExcelLogicProgress p) {

		List<BConditionUnit> units = ifUnit.getConditionUnits();
		String s = "";
		p.setCol(4);
		int column = p.getCol();
		String happy = "の場合";
		String excp = "以外";
		boolean both = false;
		for (BConditionUnit unit : units) {

			BValuable ex = unit.getCondition();
			if (ex != null) {
				s = this.createValuable(ex, p, false);
			}
			System.out.println("createIf:" + s);
			if (!unit.isLast()) { // if statement
				s = ExcelUtils.replace(s, "(\\S+) is null", "%sの値がNULLの場合");
				s = ExcelUtils.replace(s, "\\S+isEmpty\\((\\S+)\\)", "%sの値がNULLの場合");
				s = ExcelUtils.replace(s, "StringUtils.isNotEmpty\\((\\S+)\\)", "%sの値がNULL以外の場合");
				Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
				ExcelUtils.setValueForMidCategory(row, s);
				// this.makeLine(s, sheet, p);
			} else { // else statement
				both = true;
				s = reverse(s, happy, excp);
				Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
				ExcelUtils.setValueForMidCategory(row, s);
			}
//			String desc = String.format("%s", s);
//			this.makeLine(desc, sheet, p);
//			System.out.println(desc);
			p.setCreateNewRow(false);
			this.createLogicBody(unit.getLogicBody(), sheet, p);
			p.setCreateNewRow(true);
		}

		if (!both) {
			Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
			ExcelUtils.setValueForMidCategory(row, reverse(s, happy, excp));
			ExcelUtils.setValueForCase(row, ExcelTemplate.NULL_STRING);
			ExcelUtils.setValueForExpectedResult(row, "下記処理を継続する");
		}

	}

	private String reverse(String s, String happy, String excp) {
		StringBuffer b = new StringBuffer(s);
		if (s.contains(excp)) {
			int start = s.indexOf(excp);
			int end = start + excp.length();
			b.delete(start, end);
		} else {
			int offset = s.indexOf(happy);
			if (offset != -1) {
				b.insert(offset, excp);
			}
		}
		return b.toString();
	}

	private void makeLine(String s, Sheet sheet, ExcelLogicProgress p) {
		Row row = sheet.getRow(p.getRow());
		if (row == null) {
			row = ExcelUtils.makeBlankRow(sheet, p);
			System.err.println(String.format("row {%d} doens't exit, now created", p.getRow()));
		}
		ExcelUtils.setValueForNo(row, p.getRowNum());
		ExcelUtils.setValueForLitCategory(row, s);
		// ExcelUtils.setValue(row, p.getCol(), s);
	}

	private void makeReturnLine(String s, Sheet sheet, ExcelLogicProgress p, String title, boolean mindTitle) {
		int length = s.length() + p.getCol() * 2;
		int maxLength = 180;
		if (length > maxLength) {
			int returnMark = maxLength - p.getCol() * 2;
			String s1 = s.substring(0, returnMark);
			Row row = ExcelUtils.getRow(sheet, p.getRow());
			row.createCell(p.getCol()).setCellValue(s1);

			p.setRow(p.getRow() + 1);

			String s2 = s.substring(returnMark);

			makeReturnLine(s2, sheet, p, title, true);

		} else {

			Row row = ExcelUtils.getRow(sheet, p.getRow());
			if (mindTitle) {
				row.createCell(p.getCol() + title.length() / 3).setCellValue(s);
			} else {
				row.createCell(p.getCol()).setCellValue(s);
			}

		}
	}

	public String createValuable(BValuable value, ExcelLogicProgress p, boolean logical) {

		if (value instanceof BInvoker) {

			BInvoker in = (BInvoker) value;
			BValuable v = in.getInvokeChild();
			if (v instanceof BConstructor) {
				BConstructor con = (BConstructor) v;
				if (con.getBody() != null) {
					BClass bclass = con.getBody();
					ExcelProcess another = this.makeAnotherSheet(this.sheet, bclass);
					return ExcelUtils.createInvoker(value, p, logical) + " は「 " + another.getSheet().getSheetName()
							+ " 」を参照してください ";
				}
			} else {
				if (this.doStatic) {
					if (v instanceof BVariable) {
						BValuable parent = in.getInvokeParent();
						if (parent instanceof BVariable) {
							BVariable var = (BVariable) parent;
							if (var.isClass()) {
								String name = var.getBClass().getQualifiedName();
								Class<?> cls = BJavaGen.getClassByName(project, name);
								try {
									Field f = cls.getField(((BVariable) v).getLogicName());
									if (Modifier.isStatic(f.getModifiers())) {
										Class<?> type = f.getType();
										if (type.isPrimitive() || type.equals(String.class)) {
											f.setAccessible(true);
											Object obj = f.get(null);
											if (obj != null) {
												if (obj instanceof String) {
													return "\"" + obj.toString() + "\"";
												} else {
													return obj.toString();
												}
											}
										}
									}
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		// String s = BJavaGen.getValuableSource(project, this.model, value);
		// if (s == null) {
		return ExcelUtils.createValuable(value, p, logical);
		// } else {
		// return s;
		// }

	}

	private void createInvoker(BInvoker invoker, Sheet sheet, ExcelLogicProgress p, boolean withTitle,
			BVariable param) {
		log("createInvoker:" + invoker.toString());
		if (p.isDoInvoker()) {
			List<BInvoker> linkers = ExcelUtils.getLinkers((IUnit) invoker);
			for (BInvoker bin : linkers) {
				if (!bin.equals(invoker)) {
					this.createInvoker(bin, sheet, p, true, null);
				}
			}
		}

		if (this.createStringBuilder(invoker, sheet, p)) {
			if (invoker.isLinker()) {
				p.setRow(p.getRow() + 2);
			}
			return;
		}
		BValuable parentValue = invoker.getInvokeParent();
		BValuable childValue = invoker.getInvokeChild();
		List<BValuable> parameters = invoker.getParameters();

		if (parentValue != null) {
			if (parentValue.getBClass().isData() || childValue instanceof BVariable) {
				// setMethod
				if (parameters.size() == 1) {
					String parent = this.getDataSetValue(invoker, p);// this.createValuable(invoker, p);
					String value = this.getDataSetValue(parameters.get(0), p);// this.createValuable(parameters.get(0),
																				// p);
					ExcelUtils.createDataSet(sheet, p, value, parent, style, (IUnit) invoker);
					if (!p.getUnitPath().isContinuous()) {
						if (invoker.isLinker()) {
							p.setRow(p.getRow() + 2);
						}
					}
					return;
				} else if (parameters.size() == 0) {
					// getMethod
				}
			}
		}
		if (childValue instanceof BMethod) {
			BMethod mt = (BMethod) childValue;

			String parent = this.createValuable(parentValue, p, true);
			if (parentValue == null) {
				this.createMethodInvoker(sheet, p, withTitle, mt, null, parent, invoker, param);
			} else {
				this.createMethodInvoker(sheet, p, withTitle, mt, parentValue.getBClass().getLogicName(), parent,
						invoker, param);
			}
			// if (invoker.isLinker()) {
			// p.setRow(p.getRow() + 2);
			// }
		} else {
			// may be invoker but it will made other place

		}

	}

	public String getDataSetValue(BValuable value, ExcelLogicProgress p) {
		if (value == null) {
			return "null";
		}
		String s = value.toString();

		s = s.replace('#', '.');

		if (value instanceof BInvoker) {
			BInvoker invoker = (BInvoker) value;
			if (invoker.isLinker()) {
				s = this.createValuable(value, p, false);
			} else {
				BValuable parent = invoker.getInvokeParent();
				BValuable child = invoker.getInvokeChild();
				// BClass bclass = parent.getBClass();

				s = this.createValuable(parent, p, true) + "." + this.createValuable(child, p, false);
			}
		} else {
			return this.createValuable(value, p, false);
		}

		return s;
	}

	private void createLoggerInvoker(Sheet sheet, ExcelLogicProgress p, boolean withTitle, BMethod mt,
			String parentClassName, String varName, BInvoker invoker, BVariable returnValueParam) {
		List<BValuable> paras = invoker.getParameters();

		String title = p.getPath().toString() + "logger." + mt.getLogicName() + "でログの出力" + ExcelUtils.getReturnStr();
		Row row = sheet.getRow(p.getRow());

		// row.createCell(p.getCol()).setCellValue(title);

		if (paras.size() == 1) {
			BValuable value = paras.get(0);
			// logMessageSource.getMessage(messageID, paras, Locale.JAPANESE)

			if (value instanceof BVariable) {
				List<BLogicUnit> units = p.getCurrentUnits();
				if (units != null) {
					int index = p.getCurrentUnitIndex();
					if (index - 1 >= 0) {
						BLogicUnit unit = units.get(index - 1);
						if (unit instanceof BAssignment) {
							BAssignment a = (BAssignment) unit;
							BValuable child = a.getRight();
							if (child instanceof BInvoker) {
								value = child;
							}
						}
					}
				}
			}

			if (value instanceof BInvoker) {
				BInvoker in = (BInvoker) value;
				List<BValuable> params = in.getParameters();
				BValuable logId = params.get(0);
				BValuable logParas = params.get(1);
				StringBuilder logIdString = new StringBuilder();
				// id
				if (logId instanceof BInvoker) {
					BInvoker bin = (BInvoker) logId;
					logId = bin.getInvokeChild();
				}
				if (logId instanceof BVariable) {
					BVariable idVar = (BVariable) logId;
					logIdString.append("ログid:");
					String messageId = idVar.getLogicName();
					if (messageId.startsWith("\"")) {
						messageId = messageId.substring(1);
					}
					if (messageId.endsWith("\"")) {
						messageId = messageId.substring(0, messageId.length() - 1);
					}
					logIdString.append(messageId);
					logIdString.append(ExcelUtils.getReturnStr());
				}

				// paras

				StringBuilder parasString = new StringBuilder();
				parasString.append("ログパラメーター：");
				if (logParas.getBClass() == null || logParas.getBClass().getLogicName().equals(BClass.NULL)) {
					parasString.append("NULL");
				} else {
					if (logParas instanceof BVariable) {
						BVariable pVar = (BVariable) logParas;
						List<BValuable> values = pVar.getInitValues();
						String s = "{";
						for (BValuable pv : values) {
							if (!s.equals("{")) {
								s = s + ",";
							}
							s = s + ExcelUtils.createValuable(pv, p, false);
						}
						s = s + "}";
						parasString.append(s);
					} else {

					}
				}

				title += logIdString.toString() + parasString.toString();
			}

			Cell cell8 = ExcelUtils.getCell(row, CaseColumn.EXPECTED_RESULT.getColumn());
			ExcelUtils.borderCell(cell8, style);
			String origin = cell8.getStringCellValue();
			StringBuilder sb = new StringBuilder(origin);
			if (origin == null || "".equals(origin.trim())) {
			} else {
				sb.append(ExcelUtils.getReturnStr());
			}
			cell8.setCellValue(sb.append(title).toString());
		}
	}

	private void createMethodInvoker(Sheet sheet, ExcelLogicProgress p, boolean withTitle, BMethod mt,
			String parentClassName, String varName, BInvoker invoker, BVariable returnValueParam) {
		List<BValuable> paras = invoker.getParameters();
		List<BParameter> params = mt.getParameter();
		if (parentClassName.equals("MessageSource") && mt.getLogicName().equals("getMessage")) {
			List<BLogicUnit> units = p.getCurrentUnits();
			if (units != null) {
				int index = p.getCurrentUnitIndex();
				if (units.size() > index + 1) {
					BLogicUnit unit = units.get(index + 1);
					if (unit instanceof BInvoker) {
						BInvoker bin = (BInvoker) unit;
						if (bin.getInvokeParent().getBClass().getLogicName().equals("Logger")) {
							// p.setRow(p.getRow() - 2);
							p.getPath().setNumber(p.getPath().getNumber() - 1);
							return;
						}
					}
				}
			}
		}

		if (parentClassName.equals("Logger")) {
			this.createLoggerInvoker(sheet, p, withTitle, mt, parentClassName, varName, invoker, returnValueParam);
			return;
		}

		String title = p.getPath().toString() + "下記のメソッドを呼び出し、 " + mt.getName() + "する";
		if (mt instanceof BConstructor) {
			title = p.getPath().toString() + parentClassName + "を作成する ";
		}

		log("createMethodInvoker:" + title);
		Cell cell7, cell8 = null;
		StringBuffer caseBuffer = new StringBuffer();
		StringBuffer descBuffer = new StringBuffer();

		Row rowx = ExcelUtils.makeRowWithRowNum(sheet, p);
		String lit = String.format("%sクラスの%sを呼び出して", parentClassName, mt.getLogicName());
		ExcelUtils.setValueForLitCategory(rowx, lit);
		caseBuffer.append(String.format("メソッド「%s.%s」を呼び出し", invoker.getInvokeParent(), invoker.getInvokeChild()));

		if (paras.size() > 0) {
			String paramName = "";
			String name = "";
			for (int i = 0; i < paras.size(); i++) {
				BValuable obj = paras.get(i);
				String type = BJavaGen.getTypeSource(project, this.model, obj);
				if (type == null) {
					type = "null";
				}

				if (obj instanceof BVariable) {
					BVariable para = (BVariable) obj;
					paramName = para.getLogicName();
					name = para.getName();

				} else if (obj instanceof BInvoker) {
					BInvoker bin = (BInvoker) obj;
					paramName = params.get(i).getLogicName();
					paramName = (this.createValuable(bin, p, false));
				}

				caseBuffer.append(String.format("\n%d.入力パラメータ： %s", i + 1, paramName));
				if (StringUtils.isNotEmpty(name)) {
					descBuffer.append(String.format("%sに「上記編集した%s」を設定する ", paramName, name));
				}
//				cell7 = ExcelUtils.getCell(rowx, CaseColumn.CASE.getColumn());
//				cell7.setCellValue("入力パラメータ：" + paramName);
//
//				cell8 = ExcelUtils.getCell(rowx, CaseColumn.EXPECTED_RESULT.getColumn());
//				cell8.setCellValue(String.format("%sに「上記編集した%s」を設定する", paramName, name));
//
//				p.setRow(p.getRow() + 1);
//				rowx = sheet.createRow(p.getRow());
//				ExcelUtils.setValueForNo(rowx, p.getRowNum());
			}
		}

		cell7 = ExcelUtils.getCell(rowx, CaseColumn.EXPECTED_RESULT.getColumn());
		if (returnValueParam != null) {
			String retStr = returnValueParam.getLogicName();
			descBuffer.append(String.format("\n戻り値：%s", retStr));
			Pattern ptn = Pattern.compile("([A-Z])(\\d{4})");
			Matcher m = ptn.matcher(retStr);
			if (m.find()) {
				String tbl1 = "", tbl2 = "", tblName = "";
				tblName = m.group();
				descBuffer.append(String.format("\nテーブル名：%s", tblName));
			}
		} else {
			descBuffer.append("\n戻り値：なし");
		}

		if (descBuffer.charAt(0) == 10) {
			descBuffer.deleteCharAt(0);
		}
		cell7.setCellValue(descBuffer.toString());
		cell8 = ExcelUtils.getCell(rowx, CaseColumn.CASE.getColumn());
		cell8.setCellValue(caseBuffer.toString());
	}

	private int createClassHeader(BClass logic, Sheet sheet) {
		int start = 2;
		// CellStyle boldStyle = style.getBoldAndGray();
		Row row = ExcelUtils.getRow(sheet, start);
		Cell c = ExcelUtils.getCell(row, 10);
		c.setCellValue(logic.getPackage());
		start = start + 2;

		List<String> annotations = BJavaGen.getAnnotation(project, logic);
		for (String anno : annotations) {
			row = sheet.createRow(start);
			ExcelUtils.makeGray(sheet, start, 4, start, 62, style);
			row = ExcelUtils.getRow(sheet, start);
			c = ExcelUtils.getCell(row, 4);
			c.setCellValue(anno);
			start++;
		}

		return start;
	}

	private void createMethod(Sheet sheet, ExcelLogicProgress p, BMethod method) {
		ExcelMethod em = new ExcelMethod(sheet);
		methods.add(em);
		em.setMethod(method);
//		Row row = sheet.createRow(p.getRow());
//		c = ExcelUtils.getCell(row, 6);
//		c.setCellValue("処理詳細");
//		c.setCellStyle(boldStyle);
//		p.setRow(p.getRow() + 2);
		this.createLogicBody(method.getLogicBody(), sheet, p);
	}

	private void createRelatedClass(Sheet sheet, ExcelLogicProgress p) {
		List<BAssignment> vars = model.getVariables();
		for (BAssignment var : vars) {
			BParameter left = var.getLeft();
			BClass bclass = left.getBClass();
			if (bclass.getLogicName().endsWith("TableDaoWrapper") || bclass.getLogicName().endsWith("SqlClient")) {
				p.setRow(p.getRow() + 1);
				Row row = sheet.createRow(p.getRow());
				Cell c = ExcelUtils.getCell(row, 9);
				c.setCellValue(bclass.getQualifiedName());
			}
		}
	}

	private void createMethodFlow(BMethod method, Sheet sheet, ExcelLogicProgress p) {
		List<BLogicUnit> units = method.getLogicBody().getUnits();
		if (units.size() > 0) {
			BLogicUnit first = units.get(0);
			if (first instanceof BTryUnit) {
				BTryUnit tu = (BTryUnit) first;
				units = tu.getTryEditor().getUnits();
			}
		}
		int index = 1;
		for (BLogicUnit unit : units) {
			Object obj = unit.getUserAttribute("DESC");
			if (obj instanceof String) {
				String desc = (String) obj;
				p.setRow(p.getRow() + 1);
				Row row = sheet.createRow(p.getRow());
				Cell c = ExcelUtils.getCell(row, 9);
				c.setCellValue("(" + index + ") " + desc);
				index++;
			}
		}
	}

	public boolean createStringBuilder(BInvoker invoker, Sheet sheet, ExcelLogicProgress p) {
		BValuable parent = invoker.getInvokeParent();
		BValuable c = invoker.getInvokeChild();
		// this(),super();
		if (parent == null) {
			return false;
		}

		if (parent.getBClass() == null) {
			// Debug.d();
		}
		if (parent.getBClass() == null || parent.getBClass().getQualifiedName() == null) {
			return false;
		}
		if (parent.getBClass().getQualifiedName().equals("java.lang.StringBuilder")
				|| parent.getBClass().getQualifiedName().equals("java.lang.AbstractStringBuilder")) {
			if (c instanceof BMethod) {
				BMethod mt = (BMethod) c;
				if (mt.getLogicName().equals("append")) {
					BVariable var = null;
					if (parent instanceof BVariable) {
						var = (BVariable) parent;
					} else if (parent instanceof BInvoker) {
						BInvoker getVar = (BInvoker) parent;
						while (var == null && getVar != null) {
							BObject bob = getVar.getInvokeParent();
							if (bob instanceof BVariable) {
								var = (BVariable) bob;
								break;
							} else if (bob instanceof BInvoker) {
								getVar = (BInvoker) bob;
							} else {
								getVar = null;
							}
						}
					}
					if (var != null) {
						if (invoker.getParameters().size() == 0) {
							// Debug.d("");
						}
						String value = this.getDataSetValue(invoker.getParameters().get(0), p);// this.createValuable(invoker.getParameters().get(0),
																								// p);

						ExcelUtils.createDataSet(sheet, p, value, var.getLogicName(), style, (IUnit) invoker);
						return true;
					}
				}
			}

		}
		return false;

	}

	private void log(String s) {
		BProcess.check(null);
		System.out.println(s);
	}

	private String getModifier(int mods) {
		if ((mods & Modifier.PUBLIC) != 0)
			return "public";
		if ((mods & Modifier.PROTECTED) != 0)
			return "protected";
		if ((mods & Modifier.PRIVATE) != 0)
			return "private";
		if ((mods & Modifier.FINAL) != 0)
			return "final";
		if ((mods & Modifier.STATIC) != 0)
			return "static";
		if ((mods & Modifier.ABSTRACT) != 0)
			return "abstract";
		if ((mods & Modifier.NATIVE) != 0)
			return "native";
		if ((mods & Modifier.SYNCHRONIZED) != 0)
			return "synchronized";
		if ((mods & Modifier.TRANSIENT) != 0)
			return "transient";
		if ((mods & Modifier.VOLATILE) != 0)
			return "volatile";
		return "";
	}
}
