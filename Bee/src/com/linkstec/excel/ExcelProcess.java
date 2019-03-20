package com.linkstec.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import com.linkstec.excel.ExcelClassDesign.ExcelMethod;

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

		// new ExcelSheetHeader(sheet);
		CellStyle boldStyle = style.getBoldAndGray();
		int row = this.createClassHeader(logic, sheet);
		ExcelLogicProgress p = new ExcelLogicProgress();
		p.setRow(row + 3);
		p.setDoInvoker(doInvoker);
		Row newRow = sheet.createRow(row);
		ExcelUtils.makeGray(sheet, row, 4, row, 62, style);
		Cell c = ExcelUtils.getCell(newRow, 10);

		String classA = logic.getLogicName();
		List<BValuable> inters = logic.getInterfaces();
		int i = 0;
		for (BValuable var : inters) {
			if (i == 0) {
				classA = classA + " implements ";
			} else {
				classA = classA + ",";
			}
			classA = classA + var.getBClass().getLogicName();
			i++;
		}
		c.setCellValue(classA);
		c = ExcelUtils.getCell(newRow, 4);
		c.setCellValue("class");
		c.setCellStyle(boldStyle);

		newRow = sheet.createRow(row + 2);
		ExcelUtils.makeGray(sheet, row + 2, 4, row + 2, 62, style);
		c = ExcelUtils.getCell(newRow, 4);
		c.setCellValue("グローバル変数");
		c.setCellStyle(boldStyle);

		this.createClass(logic, sheet, p);

		Workbook book = sheet.getWorkbook();
		CellRangeAddress region = new CellRangeAddress(1, p.getRow(), 3, ExcelLogicProgress.maxWith);
		ExcelUtils.borderRegion(region, sheet, style);
		book.setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), 3, ExcelLogicProgress.maxWith, 1, p.getRow());
	}

	private void createLogicBody(BLogicBody body, Sheet sheet, ExcelLogicProgress p) {
		List<BLogicUnit> units = body.getUnits();
		p.setCurrentUnits(units);
		int count = units.size();

		// p.increaseDepth();

		if (count == 0) {
			p.setRow(p.getRow() + 1);
			sheet.createRow(p.getRow()).createCell(p.getCol()).setCellValue("Do nothing.");
			p.setRow(p.getRow() + 1);
		}

		int flowIndex = 1;

		for (int i = 0; i < count; i++) {

			BLogicUnit unit = units.get(i);
			p.setCurrentUnitIndex(i);

			Object obj = unit.getUserAttribute("DESC");
			if (obj instanceof String) {
				if (p.getCol() > 9) {
					p.clearIndent(2);
					p.decreaseDepth();
					flowIndex++;
				}
				String desc = (String) obj;
				p.setRow(p.getRow() + 2);
				Row row = sheet.createRow(p.getRow());
				Cell c = ExcelUtils.getCell(row, 9);
				c.setCellValue("(" + flowIndex + ") " + desc);
				p.setRow(p.getRow() + 1);
				p.increaseDepth();
			}
			this.createUnit(unit, sheet, p, i);

		}

		p.decreaseDepth();

	}

	private void createClass(BClass bclass, Sheet sheet, ExcelLogicProgress p) {

		List<BAssignment> vars = bclass.getVariables();
		List<BMethod> methods = bclass.getMethods();

		if (vars.size() > 0) {

			p.increaseGlobalVar();
			ExcelUtils.makeGray(sheet, p.getRow(), 4, p.getRow(), 62, style);
			p.setRow(p.getRow() + 1);
			Row row = ExcelUtils.getRow(sheet, p.getRow());
			ExcelUtils.makeGray(sheet, p.getRow(), 4, p.getRow(), 62, style);

			for (BAssignment assin : vars) {
				row = ExcelUtils.getRow(sheet, p.getRow());
				String s = BJavaGen.getUnitSource(project, bclass, assin);
				String[] ss = s.split("\r\n");
				for (String k : ss) {
					k = k.trim();
					if (!k.startsWith("/*") && !k.startsWith("//") && !k.startsWith("*")) {
						ExcelUtils.makeGray(sheet, p.getRow(), 4, p.getRow(), 62, style);
						Cell c = ExcelUtils.getCell(row, 4);

						int index = k.lastIndexOf(" ");
						if (index > 1) {
							String name = k.substring(0, index);
							String value = k.substring(index);
							if (name.indexOf("=") >= 0) {
								value = name.substring(name.lastIndexOf(" ") + 1) + value;
								name = name.substring(0, name.lastIndexOf(" "));

								value = name.substring(name.lastIndexOf(" ") + 1) + " " + value;
								name = name.substring(0, name.lastIndexOf(" "));
							}
							c.setCellStyle(style.getBoldAndGray());
							c.setCellValue(name);
							c = ExcelUtils.getCell(row, 19);
							c.setCellValue(value.substring(0, value.length() - 1).trim());

						} else {
							c.setCellValue(k);
						}

						p.setRow(p.getRow() + 1);
						row = ExcelUtils.getRow(sheet, p.getRow());
						ExcelUtils.makeGray(sheet, p.getRow(), 4, p.getRow(), 62, style);
					}
				}
				p.setRow(p.getRow() + 1);
			}
		}
		ExcelUtils.makeGray(sheet, p.getRow(), 4, p.getRow(), 62, style);

		p.setRow(p.getRow() + 3);

		Row row = sheet.createRow(p.getRow());
		Cell c = ExcelUtils.getCell(row, 4);
		CellStyle boldStyle = style.getBold();
		c.setCellValue("Method");
		c.setCellStyle(boldStyle);
		CellRangeAddress className = new CellRangeAddress(p.getRow() - 1, p.getRow() - 1, 4, 62);
		ExcelUtils.borderRegionBottom(className, sheet);

		this.createMethods(methods, sheet, p);
	}

	private void createMethods(List<BMethod> methods, Sheet sheet, ExcelLogicProgress p) {
		if (methods.size() > 0) {
			p.increaseMethod();
			for (int i = 0; i < methods.size(); i++) {
				BMethod method = methods.get(i);
				p.setRow(p.getRow() + 1);
				this.createMethod(sheet, p, method);
			}
		}
	}

	private boolean createUnit(BLogicUnit unit, Sheet sheet, ExcelLogicProgress p, int i) {

		BProcess.go();

		if (unit instanceof BNote) {
			return true;
		}

		if (unit instanceof BInvoker) {
			BInvoker in = (BInvoker) unit;
			if (in.isLinker()) {
				return true;
			}
		}

		if (unit instanceof IUnit) {
			p.setRow(p.getRow() + 1);
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
		} else if (unit instanceof BAssign) {
			this.createAssign((BAssign) unit, sheet, p);
		} else if (unit instanceof BInvoker) {
			this.createInvoker((BInvoker) unit, sheet, p, true, null);
		} else if (unit instanceof BReturnUnit) {
			this.createReturn((BReturnUnit) unit, sheet, p);
		} else if (unit instanceof BLoopUnit) {
			this.createLoop((BLoopUnit) unit, sheet, p);
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

		if (unit instanceof IUnit) {
			p.setRow(p.getRow() + 1);
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
		Cell c = row.createCell(p.getCol());
		c.setCellValue(s);
		p.setRow(p.getRow() + 1);
		row = ExcelUtils.getRow(sheet, p.getRow());
		c = row.createCell(p.getCol() + 1);
		c.setCellValue(this.createValuable(obj, p, false));
	}

	private void createTry(BTryUnit unit, Sheet sheet, ExcelLogicProgress p) {
		Row row = ExcelUtils.getRow(sheet, p.getRow());
		Cell c = row.createCell(p.getCol());
		c.setCellValue("try{");

		p.setRow(p.getRow() + 1);

		this.createLogicBody(unit.getTryEditor(), sheet, p);

		p.setRow(p.getRow() + 1);

		List<BCatchUnit> cates = unit.getCatches();
		for (BCatchUnit cats : cates) {
			BParameter var = cats.getVariable();
			row = ExcelUtils.getRow(sheet, p.getRow());
			c = row.createCell(p.getCol());
			c.setCellValue("}catch(" + var.getBClass().getLogicName() + " " + var.getLogicName() + " ){");
			p.setRow(p.getRow() + 1);
			this.createLogicBody(cats.getEditor(), sheet, p);
			p.setRow(p.getRow() + 2);
		}
		if (cates.size() > 1) {
			row = ExcelUtils.getRow(sheet, p.getRow());
			c = row.createCell(p.getCol());
			c.setCellValue("}");
		}
		BLogicBody f = unit.getFinalEditor();
		if (f != null) {
			p.setRow(p.getRow() + 1);
			row = ExcelUtils.getRow(sheet, p.getRow());
			c = row.createCell(p.getCol());
			c.setCellValue(((IUnit) unit).getNumber().getString() + "finally");
			this.createLogicBody(f, sheet, p);
		}
		p.setRow(p.getRow() + 2);

	}

	private void createReturn(BReturnUnit returnUnit, Sheet sheet, ExcelLogicProgress p) {

		BValuable obj = returnUnit.getReturnValue();
		String title = p.getPath().toString() + "以下を返す";
		this.makeReturnLine(title, sheet, p, "", false);
		String s = "  " + this.createValuable(obj, p, true);
		p.setRow(p.getRow() + 1);

		this.makeReturnLine(s, sheet, p, title, false);
		p.setRow(p.getRow() + 1);
	}

	private void createAssign(BAssign a, Sheet sheet, ExcelLogicProgress p) {
		log("createAssign:" + a.toString());

		BValuable right = a.getRight();
		if (a instanceof BAssignment) {
			BAssignment assign = (BAssignment) a;
			BParameter left = assign.getLeft();
			BValuable value = assign.getRight();
			boolean createInvoker = false;
			if (value instanceof BInvoker) {
				BInvoker in = (BInvoker) value;
				BValuable v = in.getInvokeChild();
				if (!(v instanceof BConstructor)) {
					this.createInvoker(in, sheet, p, true, left);
					createInvoker = true;
				}
			}
			if (!createInvoker) {
				String name = p.getPath().toString() + left.getLogicName() + "の宣言";
				Row row = ExcelUtils.getRow(sheet, p.getRow());
				row.createCell(p.getCol()).setCellValue(name);

				p.setRow(p.getRow() + 2);
				String line = BJavaGen.getUnitSource(project, this.model, assign);
				if (line != null) {
					int index = line.indexOf("*/");
					if (index > 0) {
						line = line.substring(index + 2);
					}
					row = ExcelUtils.getRow(sheet, p.getRow());
					row.createCell(p.getCol() + 1).setCellValue(line);
				}

				// p.setRow(p.getRow() + 1);
			}
		} else {
			BAssignExpression assign = (BAssignExpression) a;
			BValuable left = assign.getLeft();

			// when it is expression(value set)
			// String name = ((IUnit) assign).getNumber().toString();
			String name = p.getPath().toString();

			if (right == null || (right instanceof BVariable && right.getBClass() == null)) {
				name = name + "NULLを設定する ";
			} else {
				name = name + "が" + this.createValuable(right, p, false) + " を設定する";
			}
			name = name + this.createValuable(left, p, false);
			this.makeReturnLine(name, sheet, p, p.getPath().toString(), false);

		}
	}

	private void createLoop(BLoopUnit loop, Sheet sheet, ExcelLogicProgress p) {
		// String title = ((IUnit) loop).getNumber().toString();
		String title = p.getPath().toString();
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

		this.makeReturnLine(title, sheet, p, ((IUnit) loop).getNumber().toString(), false);

		p.setRow(p.getRow() + 1);
		this.createLogicBody(loop.getEditor(), sheet, p);

	}

	private void createIf(BMultiCondition ifUnit, Sheet sheet, ExcelLogicProgress p) {

		List<BConditionUnit> units = ifUnit.getConditionUnits();
		int i = 0;
		for (BConditionUnit unit : units) {

			// p.increaseDepth();
			String s = p.getPath().toString();

			String title = s;

			BValuable ex = unit.getCondition();
			if (ex == null) {

			} else {
				s = s + this.createValuable(ex, p, false);
			}
			if (unit.isLast()) {
				s = s + "上記以外の場合 ";
			} else {
				s = s + "の場合 ";
				// if (i != 0) {
				// s = s + "の場合";// else
				// } else {
				// if (ex instanceof BInvoker) {
				// s = s + "の場合 ";
				// } else {
				// s = s + "の場合 ";
				// }
				// }
			}
			i++;
			// make return
			this.makeReturnLine(s, sheet, p, title, false);
			// p.setRow(p.getRow() + 1);

			this.createLogicBody(unit.getLogicBody(), sheet, p);
			p.setRow(p.getRow() + 1);
			// p.decreaseDepth();
		}
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

		String title = p.getPath().toString() + "logger." + mt.getLogicName() + "でログの出力";
		Row row = sheet.createRow(p.getRow());
		row.createCell(p.getCol()).setCellValue(title);

		p.setRow(p.getRow() + 1);
		row = sheet.createRow(p.getRow());

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

				// id
				if (logId instanceof BInvoker) {
					BInvoker bin = (BInvoker) logId;
					logId = bin.getInvokeChild();
				}
				if (logId instanceof BVariable) {
					BVariable idVar = (BVariable) logId;
					row.createCell(p.getCol() + 1).setCellValue("ログid:");
					String messageId = idVar.getLogicName();
					if (messageId.startsWith("\"")) {
						messageId = messageId.substring(1);
					}
					if (messageId.endsWith("\"")) {
						messageId = messageId.substring(0, messageId.length() - 1);
					}
					row.createCell(p.getCol() + 4).setCellValue(messageId);
				}

				p.setRow(p.getRow() + 1);
				row = sheet.createRow(p.getRow());

				// paras
				row.createCell(p.getCol() + 1).setCellValue("ログパラメーター：");
				if (logParas.getBClass() == null || logParas.getBClass().getLogicName().equals(BClass.NULL)) {
					row.createCell(p.getCol() + 7).setCellValue("NULL");
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
						row.createCell(p.getCol() + 7).setCellValue(s);
					} else {

					}
				}
			}
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
							p.setRow(p.getRow() - 2);
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

		if (withTitle || paras.size() == 0) {
			// title

			Row row = sheet.createRow(p.getRow());
			row.createCell(p.getCol()).setCellValue(title);
		}

		if (paras.size() == 0) {
			String rvalue = null;
			if (mt.getBClass() != null && !mt.getBClass().getLogicName().equals(BClass.VOID)) {
				rvalue = mt.getBClass().getLogicName();
			}
			if (rvalue != null) {
				title = BJavaGen.getValuableSource(project, this.model, invoker);
				Row row = ExcelUtils.getRow(sheet, p.getRow());
				row.getCell(p.getCol()).setCellValue(title);
			}
			return;
		}

		p.setRow(p.getRow() + 1);

		// body

		int start = p.getCol() + 1;
		int end = p.getWidth() - 1;
		int middle = (end - start) * 2 / 3;
		int width = end - start;
		int title_width = width / 2 - ExcelLogicProgress.invoker_title_value_width;

		// explanation name
		CellRangeAddress exp = new CellRangeAddress(p.getRow(), p.getRow(), start, start + width);
		ExcelUtils.fillAndBorderRegion(exp, sheet, style);
		Cell expc = ExcelUtils.getRow(sheet, p.getRow()).getCell(start);
		expc.setCellValue("メソッド概要");
		p.setRow(p.getRow() + 1);

		// メソッド name
		CellRangeAddress className = new CellRangeAddress(p.getRow(), p.getRow(), start, start + title_width);
		ExcelUtils.fillAndBorderRegion(className, sheet, style);
		Cell cc = ExcelUtils.getRow(sheet, p.getRow()).getCell(start);
		cc.setCellValue("メソッド名");

		CellRangeAddress classNameValue = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width + 1, middle);
		ExcelUtils.borderRegion(classNameValue, sheet, style);
		ExcelUtils.getRow(sheet, p.getRow()).getCell(start + title_width + 1).setCellValue(mt.getLogicName());

		// 実装クラス name
		CellRangeAddress methodName = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, middle + title_width);
		ExcelUtils.fillAndBorderRegion(methodName, sheet, style);
		cc = ExcelUtils.getRow(sheet, p.getRow()).getCell(middle + 1);
		cc.setCellValue("実装クラス");

		CellRangeAddress methodNameValue = new CellRangeAddress(p.getRow(), p.getRow(), middle + title_width + 1, end);
		ExcelUtils.borderRegion(methodNameValue, sheet, style);
		ExcelUtils.getRow(sheet, p.getRow()).getCell(middle + title_width + 1).setCellValue(parentClassName);

		if (paras.size() > 0) {
			p.setRow(p.getRow() + 1);

			// parameters title
			CellRangeAddress parameters = new CellRangeAddress(p.getRow(), p.getRow() + paras.size(), start,
					start + title_width);
			ExcelUtils.fillAndBorderRegion(parameters, sheet, style);
			ExcelUtils.getRow(sheet, p.getRow()).getCell(start).setCellValue("入力パラメータ");

			// parameters title

			width = width - title_width;
			start = start + title_width + 1;

			// No.
			CellRangeAddress parameterNumber = new CellRangeAddress(p.getRow(), p.getRow(), start, start);
			ExcelUtils.fillAndBorderRegion(parameterNumber, sheet, style);
			Row titles = ExcelUtils.getRow(sheet, p.getRow());
			titles.getCell(start).setCellValue("No.");

			// parameter
			CellRangeAddress parameterstitle = new CellRangeAddress(p.getRow(), p.getRow(), start + 1, middle);
			ExcelUtils.fillAndBorderRegion(parameterstitle, sheet, style);
			titles.getCell(start + 1).setCellValue("パラメータ");

			// type
			CellRangeAddress edtistitle = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1,
					start + title_width * 2 + 14);
			ExcelUtils.fillAndBorderRegion(edtistitle, sheet, style);
			titles.getCell(middle + 1).setCellValue("データ型");

			// value
			CellRangeAddress typetitle = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width * 2 + 15,
					end);
			ExcelUtils.fillAndBorderRegion(typetitle, sheet, style);
			titles.getCell(start + title_width * 2 + 15).setCellValue("設定値");

			// values
			for (int i = 0; i < paras.size(); i++) {
				BValuable obj = paras.get(i);
				String type = BJavaGen.getTypeSource(project, this.model, obj);
				if (type == null) {
					type = "null";
				}
				p.setRow(p.getRow() + 1);

				// No.
				parameterstitle = new CellRangeAddress(p.getRow(), p.getRow(), start, start);
				ExcelUtils.borderRegion(parameterstitle, sheet, style);
				titles = ExcelUtils.getRow(sheet, p.getRow());
				titles.getCell(start).setCellValue("" + (i + 1));

				if (obj instanceof BVariable) {
					// name
					BVariable para = (BVariable) obj;
					parameterstitle = new CellRangeAddress(p.getRow(), p.getRow(), start + 1, middle);
					ExcelUtils.borderRegion(parameterstitle, sheet, style);
					titles = ExcelUtils.getRow(sheet, p.getRow());
					titles.getCell(start + 1).setCellValue(params.get(i).getLogicName());

					// type
					edtistitle = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, start + title_width * 2 + 14);
					ExcelUtils.borderRegion(edtistitle, sheet, style);
					titles.getCell(middle + 1).setCellValue(type);

					// value
					typetitle = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width * 2 + 15, end);
					ExcelUtils.borderRegion(typetitle, sheet, style);
					titles.getCell(start + title_width * 2 + 15).setCellValue(para.getLogicName());

				} else if (obj instanceof BInvoker) {
					// name
					BInvoker bin = (BInvoker) obj;
					parameterstitle = new CellRangeAddress(p.getRow(), p.getRow(), start + 1, middle);
					ExcelUtils.borderRegion(parameterstitle, sheet, style);
					titles = ExcelUtils.getRow(sheet, p.getRow());
					titles.getCell(start + 1).setCellValue(params.get(i).getLogicName());

					// type
					edtistitle = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, start + title_width * 2 + 10);
					ExcelUtils.borderRegion(edtistitle, sheet, style);
					titles.getCell(middle + 1).setCellValue(type);

					// value
					typetitle = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width * 2 + 11, end);
					ExcelUtils.borderRegion(typetitle, sheet, style);
					titles.getCell(start + title_width * 2 + 11).setCellValue(this.createValuable(bin, p, false));
				}
			}
		}

		// return
		p.setRow(p.getRow() + 1);
		start = p.getCol() + 1;
		width = p.getWidth() - 1;

		// return title
		CellRangeAddress returns = new CellRangeAddress(p.getRow(), p.getRow(), start, start + title_width);
		ExcelUtils.fillAndBorderRegion(returns, sheet, style);
		ExcelUtils.getRow(sheet, p.getRow()).getCell(start).setCellValue("戻り値");

		// return value
		CellRangeAddress returnValue = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width + 1, middle);
		ExcelUtils.borderRegion(returnValue, sheet, style);
		String rvalue = "-";
		if (returnValueParam != null) {
			rvalue = returnValueParam.getName();
		}
		ExcelUtils.getRow(sheet, p.getRow()).getCell(start + title_width + 1).setCellValue(rvalue);

		// return type
		CellRangeAddress returnType = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, width);
		ExcelUtils.borderRegion(returnType, sheet, style);
		String rtvalue = "-";
		if (mt.getBClass() != null && !mt.getBClass().getLogicName().equals(BClass.VOID)) {
			rtvalue = mt.getBClass().getLogicName();
		}
		ExcelUtils.getRow(sheet, p.getRow()).getCell(middle + 1).setCellValue(rtvalue);
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

		if (sheet.getRow(p.getRow() - 1) != null) {
			p.setRow(p.getRow() + 1);
		}

		CellStyle boldStyle = style.getBold();

		Row row = sheet.createRow(p.getRow());
		ExcelUtils.makeGray(sheet, p.getRow(), 5, p.getRow(), p.getCol() + p.getWidth() + 1, style);

		Cell c = ExcelUtils.getCell(row, 5);
		em.setAddress(c.getAddress());
		if (method instanceof BConstructor) {
			c.setCellValue("構造関数");
		} else {
			c.setCellValue(p.getPath().toString() + method.getLogicName());
		}
		c.setCellStyle(style.getBoldAndGray());

		c = ExcelUtils.getCell(row, 55);
		c.setCellValue("修飾子：" + getModifier(method.getModifier()));
		c.setCellStyle(style.getBoldAndGray());

		p.setRow(p.getRow() + 2);

		row = sheet.createRow(p.getRow());
		c = ExcelUtils.getCell(row, 6);
		c.setCellValue("パラメータ");
		c.setCellStyle(boldStyle);

		p.setRow(p.getRow() + 1);

		List<BParameter> parameters = method.getParameter();
		if (parameters != null && parameters.size() != 0) {
			for (BParameter var : parameters) {
				row = sheet.createRow(p.getRow());
				c = ExcelUtils.getCell(row, 9);
				c.setCellValue(var.getBClass().getLogicName());

				c = ExcelUtils.getCell(row, p.getCol() + 15);
				c.setCellValue(var.getLogicName());
			}
		}
		p.setRow(p.getRow() + 1);

		BClass ret = method.getReturn().getBClass();
		if (ret != null && !ret.getLogicName().equals(BClass.VOID)) {
			p.setRow(p.getRow() + 1);
			row = sheet.createRow(p.getRow());
			c = ExcelUtils.getCell(row, 6);
			c.setCellValue("戻り値");
			c.setCellStyle(boldStyle);

			p.setRow(p.getRow() + 1);
			row = sheet.createRow(p.getRow());
			c = ExcelUtils.getCell(row, 9);
			c.setCellValue(ret.getLogicName());

		}

		if (method.getLogicName().equals("execute")) {
			p.setRow(p.getRow() + 2);

			row = sheet.createRow(p.getRow());
			c = ExcelUtils.getCell(row, 6);
			c.setCellValue("関連クラス");
			c.setCellStyle(boldStyle);
			this.createRelatedClass(sheet, p);
		}

		p.setRow(p.getRow() + 2);

		row = sheet.createRow(p.getRow());
		c = ExcelUtils.getCell(row, 6);
		c.setCellValue("メインフロー");
		c.setCellStyle(boldStyle);
		this.createMethodFlow(method, sheet, p);

		p.setRow(p.getRow() + 2);

		row = sheet.createRow(p.getRow());
		c = ExcelUtils.getCell(row, 6);
		c.setCellValue("処理詳細");
		c.setCellStyle(boldStyle);
		p.setRow(p.getRow() + 2);
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
