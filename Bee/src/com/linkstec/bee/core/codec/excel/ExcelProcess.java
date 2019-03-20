package com.linkstec.bee.core.codec.excel;

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

import com.linkstec.bee.core.codec.excel.ExcelClassDesign.ExcelMethod;
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

public class ExcelProcess {

	private List<ExcelMethod> methods = new ArrayList<ExcelMethod>();

	private List<BClass> dtos = new ArrayList<BClass>();
	private ExcelStyles style;
	private BClass model;
	private static int maxRow = 500;
	private ExcelClassDesign classDesign;
	private boolean doInvoker;
	private boolean doStatic;
	private BProject project;
	private Sheet sheet;

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelProcess(BProject project, BClass logic, Sheet sheet, ExcelClassDesign ecd, ExcelStyles style, boolean doInvoker, boolean doStatic) {
		this.setAll(project, logic, sheet, ecd, style, doInvoker, doStatic);
		this.createSheet(logic, sheet, ecd, style, null);
		BProcess.end();
	}

	public ExcelProcess(BProject project, BClass logic, List<BMethod> ms, Sheet sheet, ExcelClassDesign ecd, ExcelStyles style, boolean doInvoker, boolean doStatic) {
		this.setAll(project, logic, sheet, ecd, style, doInvoker, doStatic);
		this.createSheet(logic, sheet, ecd, style, ms);

	}

	private void setAll(BProject project, BClass logic, Sheet sheet, ExcelClassDesign ecd, ExcelStyles style, boolean doInvoker, boolean doStatic) {
		this.doStatic = doStatic;
		this.sheet = sheet;
		this.project = project;
		this.doInvoker = doInvoker;
		this.style = style;
		this.model = logic;
		this.classDesign = ecd;
	}

	private void createSheet(BClass logic, Sheet sheet, ExcelClassDesign ecd, ExcelStyles style, List<BMethod> ms) {

		new ExcelSheetHeader(sheet);

		int row = this.createClassHeader(logic, sheet);
		ExcelLogicProgress p = new ExcelLogicProgress(sheet, style);
		p.setRow(row);
		p.setDoInvoker(doInvoker);

		List<BMethod> remained;
		if (ms == null) {
			remained = this.createClass(logic, sheet, p);
		} else {
			remained = this.createMethods(ms, sheet, p, false);
		}
		ecd.createRows(logic);

		ecd.makeMethods(methods);

		this.setPrintArea(sheet, p.getRow());
		if (remained != null) {
			sheet.getWorkbook().setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), 0, ExcelLogicProgress.maxWith, 0, p.getRow());
			BProcess.start(1);
			this.makeAnotherSheet(sheet, remained);
			BProcess.end();
		} else {
			this.setPrintArea(ecd.getSheet(), ecd.getRow() + 2);
		}
	}

	private void setPrintArea(Sheet sheet, int height) {
		Workbook book = sheet.getWorkbook();
		CellRangeAddress region = new CellRangeAddress(0, height, 0, ExcelLogicProgress.maxWith);
		ExcelUtils.borderRegion(region, sheet, style);
		book.setPrintArea(book.getSheetIndex(sheet), 0, ExcelLogicProgress.maxWith, 0, height);
	}

	private boolean createLogicBody(BLogicBody body, Sheet sheet, ExcelLogicProgress p) {
		List<BLogicUnit> units = body.getUnits();
		int count = units.size();

		BProcess.start(count);
		p.increaseDepth();
		if (count == 0) {
			p.setRow(p.getRow() + 1);
			sheet.createRow(p.getRow()).createCell(p.getCol()).setCellValue("Do nothing.");
			p.setRow(p.getRow() + 1);
		}

		for (int i = 0; i < count; i++) {
			BProcess.go();
			BLogicUnit unit = units.get(i);
			if (!this.createUnit(unit, sheet, p, i)) {
				return false;
			}
		}

		p.decreaseDepth();
		BProcess.end();

		return true;
	}

	private List<BMethod> createClass(BClass bclass, Sheet sheet, ExcelLogicProgress p) {

		List<BAssignment> vars = bclass.getVariables();
		List<BMethod> methods = bclass.getMethods();
		List<BLogicBody> blocks = bclass.getBlocks();

		BProcess.start(vars.size() + methods.size() + blocks.size());
		boolean hasOver = false;
		if (vars.size() > 0) {
			hasOver = true;
			if (sheet.getRow(p.getRow() - 1) != null) {
				p.setRow(p.getRow() + 1);
			}
			p.increaseGlobalVar();
			this.createGloableVarAreaTitle(sheet, p);
			for (BAssignment assin : vars) {
				p.setRow(p.getRow() + 1);
				this.createAssign(assin, sheet, p);
			}
		}

		if (blocks.size() > 0) {
			hasOver = true;
			p.increaseGlobalBlock();
			this.createGloableBlockAreaTitle(sheet, p);
			for (BLogicBody body : blocks) {
				p.setRow(p.getRow() + 1);
				this.createLogicBody(body, sheet, p);
			}
		}
		return this.createMethods(methods, sheet, p, hasOver);
	}

	private List<BMethod> createMethods(List<BMethod> methods, Sheet sheet, ExcelLogicProgress p, boolean hasOver) {
		if (methods.size() > 0) {
			p.increaseMethod();

			if (hasOver) {
				p.setRow(p.getRow() + 3);
			}
			this.createMehtodAreaTitle(sheet, p);
			for (int i = 0; i < methods.size(); i++) {
				BMethod method = methods.get(i);

				p.setRow(p.getRow() + 1);
				this.createMethod(sheet, p, method);
				if (p.getRow() > ExcelProcess.maxRow) {

					if (i == methods.size() - 1) {
						break;
					}

					List<BMethod> ms = new ArrayList<BMethod>();
					for (int j = i + 1; j < methods.size(); j++) {
						ms.add(methods.get(j));
					}
					return ms;
				}
			}

		}
		return null;
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
					this.createInvoker(invoker, sheet, p, true);
				}
			}
		}

		if (unit instanceof BMethod) {
			this.createMethod(sheet, p, (BMethod) unit);
		} else if (unit instanceof BAssign) {
			this.createAssign((BAssign) unit, sheet, p);
		} else if (unit instanceof BInvoker) {
			this.createInvoker((BInvoker) unit, sheet, p, true);
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
		}
		if (unit instanceof IUnit) {
			p.setRow(p.getRow() + 1);
		}
		return true;
	}

	private ExcelProcess makeAnotherSheet(Sheet sheet, BClass bclass) {
		style.setSheetNumber(style.getSheetNumber() + 1);

		Workbook book = sheet.getWorkbook();

		Sheet template = book.getSheet("Process Design");
		int tempIndex = book.getSheetIndex(template);
		Sheet another = book.cloneSheet(tempIndex);
		int index = book.getSheetIndex(another);
		String name = "Process Design(" + style.getSheetNumber() + ")";
		book.setSheetName(index, name);

		return new ExcelProcess(project, bclass, another, classDesign, style, this.doInvoker, this.doStatic);
	}

	private void makeAnotherSheet(Sheet sheet, List<BMethod> methods) {
		style.setSheetNumber(style.getSheetNumber() + 1);

		Workbook book = sheet.getWorkbook();

		Sheet template = book.getSheet("Process Design");
		int tempIndex = book.getSheetIndex(template);
		Sheet another = book.cloneSheet(tempIndex);
		int index = book.getSheetIndex(another);
		String name = "Process Design(" + style.getSheetNumber() + ")";
		book.setSheetName(index, name);

		new ExcelProcess(project, this.model, methods, another, classDesign, style, this.doInvoker, this.doStatic);
	}

	private void createThrow(BThrow unit, Sheet sheet, ExcelLogicProgress p) {
		BValuable obj = unit.getException();
		String s = ((IUnit) unit).getNumber().toString() + "Throw " + this.createValuable(obj, p);
		Row row = ExcelUtils.getRow(sheet, p.getRow());
		Cell c = row.createCell(p.getCol());

		c.setCellValue(s);
	}

	private void createTry(BTryUnit unit, Sheet sheet, ExcelLogicProgress p) {
		Row row = ExcelUtils.getRow(sheet, p.getRow());
		Cell c = row.createCell(p.getCol());
		c.setCellValue(((IUnit) unit).getNumber().toString() + "Try to watch follows in case that process fails");

		p.setRow(p.getRow() + 1);

		this.createLogicBody(unit.getTryEditor(), sheet, p);

		p.setRow(p.getRow() + 1);

		List<BCatchUnit> cates = unit.getCatches();
		for (BCatchUnit cats : cates) {
			BParameter var = cats.getVariable();
			row = ExcelUtils.getRow(sheet, p.getRow());
			c = row.createCell(p.getCol());
			c.setCellValue(((IUnit) unit).getNumber().getString() + ".When " + var.getBClass().getLogicName() + " named " + var.getLogicName() + " occurred above then do as follows");
			this.createLogicBody(cats.getEditor(), sheet, p);
		}
		BLogicBody f = unit.getFinalEditor();
		if (f != null) {
			p.setRow(p.getRow() + 1);
			row = ExcelUtils.getRow(sheet, p.getRow());
			c = row.createCell(p.getCol());
			c.setCellValue(((IUnit) unit).getNumber().getString() + ".Finally  do as follows");
			this.createLogicBody(f, sheet, p);
		}

	}

	private void createReturn(BReturnUnit returnUnit, Sheet sheet, ExcelLogicProgress p) {

		BValuable obj = returnUnit.getReturnValue();
		String title = ((IUnit) returnUnit).getNumber().toString() + "Return ";
		String s = title + this.createValuable(obj, p);

		log(s);

		this.makeReturnLine(s, sheet, p, title, false);
		p.setRow(p.getRow() + 1);
	}

	private void createAssign(BAssign a, Sheet sheet, ExcelLogicProgress p) {
		log("createAssign:" + a.toString());

		BValuable right = a.getRight();
		if (a instanceof BAssignment) {
			BAssignment assign = (BAssignment) a;
			BParameter left = assign.getLeft();
			String className = BJavaGen.getTypeSource(this.project, this.model, left);

			String name = ((IUnit) assign).getNumber().toString() + "Creates an instance of " + className + " named " + this.createValuable(left, p);
			if (right == null || (right instanceof BVariable && right.getBClass() == null)) {
				name = name + ",and sets null to the instance";
			} else {
				name = name + ",and sets " + this.createValuable(right, p) + " into the instance";
			}

			this.makeReturnLine(name, sheet, p, ((IUnit) assign).getNumber().toString(), false);

		} else {
			BAssignExpression assign = (BAssignExpression) a;
			BValuable left = assign.getLeft();

			// when it is expression(value set)
			String name = ((IUnit) assign).getNumber().toString();

			if (right == null || (right instanceof BVariable && right.getBClass() == null)) {
				name = name + "Sets null into ";
			} else {
				name = name + "Sets " + this.createValuable(right, p) + " into ";
			}
			name = name + this.createValuable(left, p);
			this.makeReturnLine(name, sheet, p, ((IUnit) assign).getNumber().toString(), false);

		}
	}

	private void createLoop(BLoopUnit loop, Sheet sheet, ExcelLogicProgress p) {
		String title = ((IUnit) loop).getNumber().toString();
		if (loop.getLoopType() == BLoopUnit.TYPE_ENHANCED) {
			BObject ex = loop.getEnhanceExpression();
			BParameter left = loop.getEnhanceVariable();

			BVariable lvar = (BVariable) left;
			title = title + "Loops every " + lvar.getBClass().getLogicName() + " as " + lvar.getLogicName();

			if (ex instanceof BVariable) {
				BVariable evar = (BVariable) ex;
				title = title + " in " + evar.getLogicName() + " as follows";
			}

		} else if (loop.getLoopType() == BLoopUnit.TYPE_DOWHILE) {
			BValuable obj = loop.getCondition();
			title = title + "do as follows While " + this.createValuable(obj, p) + " is true";
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
				counter = counter + this.createValuable(obj, p);
			}

			title = title + "Loops as follows with counter " + counter + " when " + createValuable(loop.getCondition(), p) + " is true";
		} else if (loop.getLoopType() == BLoopUnit.TYPE_WHILE) {
			title = title + "While " + createValuable(loop.getCondition(), p) + " is true";
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
			String s = ((IUnit) ifUnit).getNumber().toString() + (i + 1) + ")";
			if (units.size() == 1) {
				s = ((IUnit) ifUnit).getNumber().toString();
			}

			if (unit.isLast()) {
				s = s + " Else ";
			} else {
				if (i != 0) {
					s = s + " Else if ";
				} else {
					s = s + " If ";
				}
			}
			String title = s;
			i++;
			BValuable ex = unit.getCondition();
			if (ex == null) {

			} else {
				s = s + this.createValuable(ex, p);
				if (ex instanceof BInvoker) {
					s = s + " is true";
				}
			}
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
		int maxLength = 130;
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

	public String createValuable(BValuable value, ExcelLogicProgress p) {

		if (value instanceof BInvoker) {

			BInvoker in = (BInvoker) value;
			BValuable v = in.getInvokeChild();
			if (v instanceof BConstructor) {
				BConstructor con = (BConstructor) v;
				if (con.getBody() != null) {
					BClass bclass = con.getBody();
					ExcelProcess another = this.makeAnotherSheet(this.sheet, bclass);
					return ExcelUtils.createInvoker(value, p) + " width values made by " + another.getSheet().getSheetName();
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

		String s = BJavaGen.getValuableSource(project, this.model, value);
		if (s == null) {
			return ExcelUtils.createValuable(value, p);
		} else {
			return s;
		}

	}

	private void createInvoker(BInvoker invoker, Sheet sheet, ExcelLogicProgress p, boolean withTitle) {
		log("createInvoker:" + invoker.toString());
		if (p.isDoInvoker()) {
			List<BInvoker> linkers = ExcelUtils.getLinkers((IUnit) invoker);
			for (BInvoker bin : linkers) {
				if (!bin.equals(invoker)) {
					this.createInvoker(bin, sheet, p, true);
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
					String value = this.getDataSetValue(parameters.get(0), p);// this.createValuable(parameters.get(0), p);
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

			String parent = this.createValuable(parentValue, p);
			if (parentValue == null) {
				this.createMethodInvoker(sheet, p, withTitle, mt, null, parent, invoker);
			} else {
				this.createMethodInvoker(sheet, p, withTitle, mt, parentValue.getBClass().getLogicName(), parent, invoker);
			}
			if (invoker.isLinker()) {
				p.setRow(p.getRow() + 2);
			}
		} else {
			// may be invoker but it will made other place

		}

	}

	public String getDataSetValue(BValuable value, ExcelLogicProgress p) {

		this.createValuable(value, p);
		String s = value.toString();

		s = s.replace('#', '.');

		if (value instanceof BInvoker) {
			BInvoker invoker = (BInvoker) value;
			if (invoker.isLinker()) {
				s = this.createValuable(value, p);
			} else {
				BValuable parent = invoker.getInvokeParent();
				BValuable child = invoker.getInvokeChild();
				s = this.getDataSetValue(parent, p) + "." + this.getDataSetValue(child, p);
			}
		}

		return s;
	}

	private void createMethodInvoker(Sheet sheet, ExcelLogicProgress p, boolean withTitle, BMethod mt, String parentClassName, String varName, BInvoker invoker) {
		// List<BParameter> paraType = mt.getParameter();
		List<BValuable> paras = invoker.getParameters();
		String title = ((IUnit) invoker).getNumber().toString() + "Calls method ";
		if (mt instanceof BConstructor) {
			title = ((IUnit) invoker).getNumber().toString() + "Creates an instanceof of " + parentClassName;
			if (paras.size() > 0) {
				title = title + " as follows";
			}
		} else {
			if (invoker.isStatic()) {
				title = title + parentClassName + "#" + mt.getLogicName();
				if (paras.size() > 0) {
					title = title + " as follows";
				}
			} else {
				if (varName == null) {
					title = title + mt.getLogicName();
				} else {
					if (varName.equals("this")) {
						title = title + mt.getLogicName();
					} else {
						title = title + varName + "#" + mt.getLogicName();
					}
					if (paras.size() > 0) {
						title = title + " as follows";
					}
				}
			}
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
				title = title + " to get value typed " + rvalue;
				Row row = ExcelUtils.getRow(sheet, p.getRow());
				row.getCell(p.getCol()).setCellValue(title);
			}
			return;
		}

		p.setRow(p.getRow() + 1);

		// body

		int start = p.getCol() + 1;
		int end = start + p.getWidth() - 1;
		int middle = (end - start) / 2 + start;
		int width = end - start;
		int title_width = width / 2 - ExcelLogicProgress.invoker_title_value_width;

		// class name
		CellRangeAddress className = new CellRangeAddress(p.getRow(), p.getRow(), start, start + title_width);
		ExcelUtils.fillAndBorderRegion(className, sheet, style);
		Cell cc = ExcelUtils.getRow(sheet, p.getRow()).getCell(start);
		cc.setCellValue("Class Name");

		CellRangeAddress classNameValue = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width + 1, middle);
		ExcelUtils.borderRegion(classNameValue, sheet, style);
		ExcelUtils.getRow(sheet, p.getRow()).getCell(start + title_width + 1).setCellValue(parentClassName);

		// method name
		CellRangeAddress methodName = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, middle + title_width);
		ExcelUtils.fillAndBorderRegion(methodName, sheet, style);
		cc = ExcelUtils.getRow(sheet, p.getRow()).getCell(middle + 1);
		cc.setCellValue("Method Name");

		CellRangeAddress methodNameValue = new CellRangeAddress(p.getRow(), p.getRow(), middle + title_width + 1, end);
		ExcelUtils.borderRegion(methodNameValue, sheet, style);
		String methodNameValueString = mt.getLogicName();
		if (mt instanceof BConstructor) {
			methodNameValueString = "Constructor";
		}
		ExcelUtils.getRow(sheet, p.getRow()).getCell(middle + title_width + 1).setCellValue(methodNameValueString);

		if (paras.size() > 0) {
			p.setRow(p.getRow() + 1);

			// parameters title
			CellRangeAddress parameters = new CellRangeAddress(p.getRow(), p.getRow(), start, start + title_width);
			ExcelUtils.fillAndBorderRegion(parameters, sheet, style);
			ExcelUtils.getRow(sheet, p.getRow()).getCell(start).setCellValue("Parameters");

			CellStyle rbsytle = style.getRightBoldLinedOnly();
			ExcelUtils.getRow(sheet, p.getRow()).createCell(end).setCellStyle(rbsytle);

			p.setRow(p.getRow() + 1);

			// blank line
			createInvokerBlank(sheet, p, p.getCol() + 1, p.getWidth() - 1);

			p.setRow(p.getRow() + 1);
			createInvokerBlank(sheet, p, p.getCol() + 1, p.getWidth() - 1);

			// parameters title

			width = width - 4;
			start = start + 2;
			CellRangeAddress parameterstitle = new CellRangeAddress(p.getRow(), p.getRow(), start, start + title_width);
			ExcelUtils.fillAndBorderRegion(parameterstitle, sheet, style);
			Row titles = ExcelUtils.getRow(sheet, p.getRow());
			titles.getCell(start).setCellValue("Parameters");

			CellRangeAddress edtistitle = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width + 1, start + width);
			ExcelUtils.fillAndBorderRegion(edtistitle, sheet, style);
			titles.getCell(start + title_width + 1).setCellValue("Edits");
			for (int i = 0; i < paras.size(); i++) {
				BValuable obj = paras.get(i);
				String type = BJavaGen.getTypeSource(project, this.model, obj);
				if (type == null) {
					type = "null";
				}
				if (obj instanceof BVariable) {
					BVariable para = (BVariable) obj;
					this.createInvokerParameterLine(sheet, p, type, para.getLogicName(), start, start + title_width, start + width);
				} else if (obj instanceof BInvoker) {
					BInvoker bin = (BInvoker) obj;
					this.createInvokerParameterLine(sheet, p, type, this.createValuable(bin, p), start, start + title_width, start + width);
				}
			}

			p.setRow(p.getRow() + 1);
			createInvokerBlank(sheet, p, p.getCol() + 1, p.getWidth() - 1);
		}

		// return
		p.setRow(p.getRow() + 1);
		start = p.getCol() + 1;
		width = p.getWidth() - 1;
		createInvokerBlank(sheet, p, p.getCol() + 1, p.getWidth() - 1);
		CellRangeAddress returns = new CellRangeAddress(p.getRow(), p.getRow(), start, start + title_width);
		ExcelUtils.fillAndBorderRegion(returns, sheet, style);
		ExcelUtils.getRow(sheet, p.getRow()).getCell(start).setCellValue("Return");
		CellRangeAddress returnValue = new CellRangeAddress(p.getRow(), p.getRow(), start + title_width + 1, start + width);
		ExcelUtils.borderRegion(returnValue, sheet, style);
		String rvalue = "-";
		if (mt.getBClass() != null && !mt.getBClass().getLogicName().equals(BClass.VOID)) {
			rvalue = mt.getBClass().getLogicName();

		}
		ExcelUtils.getRow(sheet, p.getRow()).getCell(start + title_width + 1).setCellValue(rvalue);
	}

	private void createGloableBlockAreaTitle(Sheet sheet, ExcelLogicProgress p) {
		Row row = sheet.createRow(p.getRow());
		Cell c = row.createCell(1);
		c.setCellValue("Global block");
		p.setRow(p.getRow() + 2);

		c.setCellStyle(style.getBold());
	}

	private void createMehtodAreaTitle(Sheet sheet, ExcelLogicProgress p) {
		if (sheet.getRow(p.getRow() - 1) != null) {
			p.setRow(p.getRow() + 1);
		}

		Row row = sheet.createRow(p.getRow());
		Cell c = row.createCell(1);
		c.setCellValue("Method");
		p.setRow(p.getRow() + 2);

		c.setCellStyle(style.getBold());
	}

	private void createGloableVarAreaTitle(Sheet sheet, ExcelLogicProgress p) {

		Row row = ExcelUtils.getRow(sheet, p.getRow());
		Cell c = row.createCell(1);
		c.setCellValue("Global variable");
		p.setRow(p.getRow() + 2);

		c.setCellStyle(style.getBold());
	}

	private int createClassHeader(BClass logic, Sheet sheet) {
		int start = 5;
		// Row row = sheet.createRow(start);
		// CellStyle titleStyle = style.getGray();
		CellStyle boldStyle = style.getBoldAndGray();

		// package
		ExcelUtils.makeGray(sheet, start, 1, start, 47, style);
		Row row = ExcelUtils.getRow(sheet, start);
		Cell c = ExcelUtils.getCell(row, 1);
		c.setCellValue("Package");
		c.setCellStyle(boldStyle);
		c = ExcelUtils.getCell(row, 5);
		c.setCellValue(logic.getPackage());

		int line = start;

		// super class
		if (logic.getSuperClass() != null) {
			line++;
			ExcelUtils.makeGray(sheet, line, 1, line, 47, style);
			row = ExcelUtils.getRow(sheet, line);
			c = ExcelUtils.getCell(row, 1);
			c.setCellValue("Super Class");
			c.setCellStyle(boldStyle);
			c = ExcelUtils.getCell(row, 5);
			c.setCellValue(logic.getSuperClass().getBClass().getQualifiedName());
		}

		// interfaces
		List<BValuable> interfaces = logic.getInterfaces();
		for (BValuable name : interfaces) {
			line++;
			ExcelUtils.makeGray(sheet, line, 1, line, 47, style);
			row = ExcelUtils.getRow(sheet, line);
			c = ExcelUtils.getCell(row, 1);
			c.setCellValue("Interface");
			c.setCellStyle(boldStyle);
			c = ExcelUtils.getCell(row, 5);
			c.setCellValue(name.getBClass().getQualifiedName());
		}

		// class
		line++;
		ExcelUtils.makeGray(sheet, line, 1, line, 47, style);
		row = ExcelUtils.getRow(sheet, line);
		c = ExcelUtils.getCell(row, 1);
		c.setCellValue("Class");
		c.setCellStyle(boldStyle);
		c = ExcelUtils.getCell(row, 5);
		c.setCellValue(logic.getLogicName());

		line = line + 2;
		row = ExcelUtils.getRow(sheet, line);
		for (int i = 1; i <= 47; i++) {
			ExcelUtils.getCell(row, i).setCellStyle(style.getUnderLine());
		}

		return line + 1;
	}

	private void createMethod(Sheet sheet, ExcelLogicProgress p, BMethod method) {
		ExcelMethod em = new ExcelMethod(sheet);
		methods.add(em);
		em.setMethod(method);

		if (sheet.getRow(p.getRow() - 1) != null) {
			p.setRow(p.getRow() + 1);
		}

		CellStyle boldStyle = style.getBoldAndGray();

		ExcelUtils.makeGray(sheet, p.getRow(), p.getCol(), p.getRow(), p.getCol() + p.getWidth(), style);
		Row row = sheet.getRow(p.getRow());

		Cell c = row.getCell(p.getCol());
		em.setAddress(c.getAddress());
		if (method instanceof BConstructor) {
			c.setCellValue(((IUnit) method).getNumber().toString() + " Constructor");
		} else {
			c.setCellValue(((IUnit) method).getNumber().toString() + " " + method.getLogicName());
		}
		c.setCellStyle(style.getBoldAndGray());

		List<BParameter> parameters = method.getParameter();
		if (parameters != null && parameters.size() != 0) {
			for (BParameter var : parameters) {

				p.setRow(p.getRow() + 1);

				ExcelUtils.makeGray(sheet, p.getRow(), p.getCol(), p.getRow(), p.getCol() + p.getWidth(), style);

				row = sheet.getRow(p.getRow());
				c = row.getCell(p.getCol() + 1);
				c.setCellValue("Parameter");
				c.setCellStyle(boldStyle);

				c = row.getCell(p.getCol() + 5);
				c.setCellValue(var.getBClass().getLogicName());

				c = row.getCell(p.getCol() + 25);
				c.setCellValue(var.getLogicName());

				BClass bclass = var.getBClass();

				if (!Modifier.isPrivate(method.getModifier())) {
					if (bclass.isData()) {
						addDto(bclass);
					}
				}
			}
		}

		BClass ret = method.getBClass();
		if (ret != null && !ret.getLogicName().equals(BClass.VOID)) {
			if (!Modifier.isPrivate(method.getModifier())) {
				if (ret.isData()) {
					addDto(ret);
				}
			}
			p.setRow(p.getRow() + 1);

			row = sheet.createRow(p.getRow());
			ExcelUtils.makeGray(sheet, p.getRow(), p.getCol(), p.getRow(), p.getCol() + p.getWidth(), style);

			c = ExcelUtils.getCell(row, p.getCol() + 1);
			c.setCellValue("Return");
			c.setCellStyle(boldStyle);

			c = ExcelUtils.getCell(row, p.getCol() + 5);
			c.setCellValue(ret.getLogicName());

		}

		List<BVariable> ts = method.getThrows();
		if (ts != null && ts.size() != 0) {
			for (BVariable var : ts) {
				p.setRow(p.getRow() + 1);

				ExcelUtils.makeGray(sheet, p.getRow(), p.getCol(), p.getRow(), p.getCol() + p.getWidth(), style);

				row = sheet.getRow(p.getRow());
				c = row.getCell(p.getCol() + 1);
				c.setCellValue("Throw");
				c.setCellStyle(boldStyle);

				c = row.getCell(p.getCol() + 5);
				c.setCellValue(var.getBClass().getLogicName());
			}
		}

		p.setRow(p.getRow() + 1);

		this.createLogicBody(method.getLogicBody(), sheet, p);
	}

	private void createInvokerParameterLine(Sheet sheet, ExcelLogicProgress p, String name, String valaue, int start, int middle, int end) {
		p.setRow(p.getRow() + 1);

		createInvokerBlank(sheet, p, p.getCol() + 1, p.getWidth() - 1);

		CellRangeAddress parameterstitle = new CellRangeAddress(p.getRow(), p.getRow(), start, middle);
		ExcelUtils.borderRegion(parameterstitle, sheet, style);
		Row titles = sheet.getRow(p.getRow());
		titles.getCell(start).setCellValue(name);

		CellRangeAddress edtistitle = new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, end);
		ExcelUtils.borderRegion(edtistitle, sheet, style);
		titles.getCell(middle + 1).setCellValue(valaue);
	}

	private void createInvokerBlank(Sheet sheet, ExcelLogicProgress p, int start, int width) {
		// blank line
		Row blank = sheet.createRow(p.getRow());
		CellStyle bsytle = style.getLeftBoldLinedOnly();
		blank.createCell(start).setCellStyle(bsytle);

		CellStyle rbsytle = style.getRightBoldLinedOnly();
		blank.createCell(start + width).setCellStyle(rbsytle);
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
		if (parent.getBClass().getQualifiedName().equals("java.lang.StringBuilder") || parent.getBClass().getQualifiedName().equals("java.lang.AbstractStringBuilder")) {
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
						String value = this.getDataSetValue(invoker.getParameters().get(0), p);// this.createValuable(invoker.getParameters().get(0), p);

						ExcelUtils.createDataSet(sheet, p, value, var.getLogicName(), style, (IUnit) invoker);
						return true;
					}
				}
			}

		}
		return false;

	}

	private void addDto(BClass bclass) {
		boolean contains = false;
		for (BClass b : dtos) {
			if (b.getQualifiedName().equals(bclass.getQualifiedName())) {
				contains = true;
			}
		}
		if (!contains) {
			dtos.add(bclass);
		}
	}

	public List<BClass> getDtos() {
		return this.dtos;
	}

	private void log(String s) {
		BProcess.check(null);
		System.out.println(s);
	}

}
