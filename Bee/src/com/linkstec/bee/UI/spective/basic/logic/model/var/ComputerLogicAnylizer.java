package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.model.var.ComputerEditor.DisplayCell;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ComputerEditor.ParentizedDisplayCell;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BLogiker;

public class ComputerLogicAnylizer {

	public static List<Object> parseExpression(BExpression ex) {
		List<Object> list = new ArrayList<Object>();
		BValuable left = ex.getExLeft();
		BValuable right = ex.getExRight();
		BLogiker logiker = ex.getExMiddle();
		if (ex.getUserAttribute("PARENTIZED") != null) {
			ParentizedDisplayCell pl = new ParentizedDisplayCell(ParentizedDisplayCell.TYPE_LEFT);
			list.add(pl);
		}

		// left
		if (left instanceof BExpression) {
			BExpression bx = (BExpression) left;
			List<Object> child = parseExpression(bx);
			list.addAll(child);
		} else {
			DisplayCell d = new DisplayCell();
			d.setValue(left);
			d.setText("<html>" + BValueUtils.createValuable(left, false) + "</html>");
			list.add(d);
		}

		// middle
		DisplayCell ll = new DisplayCell();
		ll.setValue(logiker);

		String value = logiker.getLogicName();
		value = value.replace(">", "&gt;");
		value = value.replace("<", "&lt;");
		String s = "<html>" + value + "</html>";
		ll.setText(s);
		list.add(ll);

		// right
		if (right instanceof BExpression) {
			BExpression bx = (BExpression) right;
			List<Object> child = parseExpression(bx);
			list.addAll(child);
		} else {
			DisplayCell d = new DisplayCell();
			d.setValue(right);
			d.setText("<html>" + BValueUtils.createValuable(right, false) + "</html>");
			list.add(d);
		}

		if (ex.getUserAttribute("PARENTIZED") != null) {
			ParentizedDisplayCell pl = new ParentizedDisplayCell(ParentizedDisplayCell.RIGHT);
			list.add(pl);
		}

		return list;
	}

	public static String getString(List<DisplayCell> cells) throws ParseExcetion {
		BExpression ex = ComputerLogicAnylizer.AnylizeCells(cells);
		return BValueUtils.createValuable(ex, false);
	}

	public static BExpression AnylizeCells(List<DisplayCell> cells) throws ParseExcetion {
		ExpressionCell ex = ComputerLogicAnylizer.makePrentized(cells);
		ComputerLogicAnylizer.AnylizePriority(ex);
		ComputerLogicAnylizer.AnylizePlain(ex);
		return ComputerLogicAnylizer.makeExpression(ex);
	}

	public static ExpressionCell makePrentized(List<DisplayCell> cells) throws ParseExcetion {

		ExpressionCell expression = new ExpressionCell();

		int start = 0;
		int end = 0;
		for (DisplayCell cell : cells) {
			if (cell instanceof ParentizedDisplayCell) {
				ParentizedDisplayCell pdc = (ParentizedDisplayCell) cell;
				if (pdc.getType() == ParentizedDisplayCell.TYPE_LEFT) {
					start++;
					ExpressionCell child = new ExpressionCell();
					child.setPrentized(true);
					child.setParent(expression);
					expression = child;
				} else {
					end++;
					expression = expression.getParent();
				}
			} else {
				if (start >= end) {
					expression.getCells().add(cell);
				} else if (start < end) {
					throw new ParseExcetion("( が必要です");
				}
			}
		}
		if (start != end) {
			throw new ParseExcetion("()は正しくありません");
		}
		return expression;
	}

	public static void AnylizePriority(ExpressionCell ex) throws ParseExcetion {
		List<Object> cells = ex.getCells();
		List<Object> anylized = new ArrayList<Object>();
		int index = 0;
		boolean skipOnce = false;
		for (Object cell : cells) {
			if (skipOnce) {
				skipOnce = false;
				index++;
				continue;
			}
			if (cell instanceof DisplayCell) {
				DisplayCell dc = (DisplayCell) cell;
				Object value = dc.getValue();
				if (value instanceof BLogiker) {
					BLogiker logiker = (BLogiker) value;
					if (index == 0) {
						throw new ParseExcetion(logiker.toString() + "を先頭に配置してはいけない");
					}
					if (index == cells.size() - 1) {
						throw new ParseExcetion(logiker.toString() + "を最後に配置してはいけない");
					}
					if (isHigher(logiker)) {
						ExpressionCell expression = new ExpressionCell();
						// expression.setParent(ex);

						Object before = anylized.get(anylized.size() - 1);
						Object after = ex.getCells().get(index + 1);

						anylized.remove(before);

						if (before instanceof ExpressionCell) {
							ExpressionCell nes = (ExpressionCell) before;
							nes.setParent(expression);
						} else {
							expression.getCells().add(before);
						}

						expression.getCells().add(cell);

						if (after instanceof ExpressionCell) {
							ExpressionCell nes = (ExpressionCell) after;
							nes.setParent(expression);
						} else {
							expression.getCells().add(after);
						}
						anylized.add(expression);

						skipOnce = true;
					} else {
						anylized.add(cell);
					}
				} else {
					anylized.add(cell);
					if (cell instanceof ExpressionCell) {
						ExpressionCell ec = (ExpressionCell) cell;
						ComputerLogicAnylizer.AnylizePriority(ec);
					}
				}
			} else {
				anylized.add(cell);
				if (cell instanceof ExpressionCell) {
					ExpressionCell ec = (ExpressionCell) cell;
					ComputerLogicAnylizer.AnylizePriority(ec);
				}
			}
			index++;
		}
		ex.setCells(anylized);
	}

	public static void AnylizePlain(ExpressionCell ex) throws ParseExcetion {
		List<Object> oldCells = ex.getCells();

		List<Object> anylized = new ArrayList<Object>();
		int index = 0;

		if (oldCells.size() == 1) {
			Object cell = oldCells.get(0);
			if (cell instanceof ExpressionCell) {
				ExpressionCell ec = (ExpressionCell) cell;
				AnylizePlain(ec);
				return;
			}
		}
		boolean skipOnce = false;
		for (Object cell : oldCells) {
			if (skipOnce) {
				skipOnce = false;
				index++;
				continue;
			}
			if (cell instanceof DisplayCell) {
				DisplayCell dc = (DisplayCell) cell;
				Object value = dc.getValue();
				if (value instanceof BLogiker) {
					BLogiker logiker = (BLogiker) value;
					if (index == 0) {
						throw new ParseExcetion(logiker.toString() + "を先頭に配置してはいけない");
					}
					if (index == oldCells.size() - 1) {
						throw new ParseExcetion(logiker.toString() + "を最後に配置してはいけない");
					}

					Object before = anylized.get(anylized.size() - 1);
					Object after = oldCells.get(index + 1);
					anylized.remove(before);

					ExpressionCell expression = new ExpressionCell();

					if (before instanceof ExpressionCell) {
						ExpressionCell nes = (ExpressionCell) before;
						nes.setParent(expression);
					} else {
						expression.getCells().add(before);
					}

					expression.getCells().add(cell);

					if (after instanceof ExpressionCell) {
						ExpressionCell nes = (ExpressionCell) after;
						nes.setParent(expression);
					} else {
						expression.getCells().add(after);
					}

					anylized.add(expression);
					skipOnce = true;
				} else {
					anylized.add(cell);
				}
			} else if (cell instanceof ExpressionCell) {
				ExpressionCell ec = (ExpressionCell) cell;
				AnylizePlain(ec);
			}
			index++;
		}
		ex.setCells(anylized);
	}

	public static BExpression makeExpression(ExpressionCell cell) throws ParseExcetion {
		IPatternCreator view = PatternCreatorFactory.createView();
		BExpression ex = view.createExpression();
		if (cell.isPrentized()) {
			ex.addUserAttribute("PARENTIZED", "PARENTIZED");
		}
		List<Object> cells = cell.getCells();
		if (cells.size() == 1) {
			Object obj = cells.get(0);
			if (obj instanceof ExpressionCell) {
				ExpressionCell ec = (ExpressionCell) obj;
				BExpression e = makeExpression(ec);
				if (cell.isPrentized()) {
					e.addUserAttribute("PARENTIZED", "PARENTIZED");
				}
				return e;
			} else {
				throw new ParseExcetion("計算式は正しくありません");
			}
		}
		if (cells.size() != 3) {
			throw new ParseExcetion("計算式は正しくありません");
		} else {
			Object left = cells.get(0);
			Object middle = cells.get(1);
			Object right = cells.get(2);

			BValuable l, r;

			// left
			if (left instanceof ExpressionCell) {
				ExpressionCell lc = (ExpressionCell) left;
				l = makeExpression(lc);
			} else if (left instanceof DisplayCell) {
				DisplayCell dc = (DisplayCell) left;
				Object value = dc.getValue();
				if (value instanceof BValuable) {
					l = (BValuable) value;
				} else {
					throw new ParseExcetion(value.toString() + "は正しい場所にいません");
				}
			} else {
				throw new ParseExcetion(left.toString() + "は正しい場所にいません");
			}

			// right
			if (right instanceof ExpressionCell) {
				ExpressionCell lc = (ExpressionCell) right;
				r = makeExpression(lc);
			} else if (right instanceof DisplayCell) {
				DisplayCell dc = (DisplayCell) right;
				Object value = dc.getValue();
				if (value instanceof BValuable) {
					r = (BValuable) value;
				} else {
					throw new ParseExcetion(value.toString() + "は正しい場所にいません");
				}
			} else {
				throw new ParseExcetion(right.toString() + "は正しい場所にいません");
			}

			// middle
			if (middle instanceof DisplayCell) {
				DisplayCell d = (DisplayCell) middle;
				Object value = d.getValue();

				if (value instanceof BLogiker) {

					BLogiker logiker = (BLogiker) value;
					ex.setExLeft(l);
					ex.setExRight(r);
					ex.setExMiddle(logiker);
				} else {
					throw new ParseExcetion("計算式は正しくありません");
				}
			} else {
				throw new ParseExcetion("計算式は正しくありません");
			}
		}
		return ex;
	}

	public static boolean isHigher(BLogiker logiker) {
		if (logiker.getLogicName().equals(BLogiker.MULTIPLY.getLogicName())) {
			return true;
		}
		if (logiker.getLogicName().equals(BLogiker.DIVIDE.getLogicName())) {
			return true;
		}
		if (logiker.getLogicName().equals(BLogiker.MOD.getLogicName())) {
			return true;
		}
		return false;
	}

	public static class ExpressionCell {
		private ExpressionCell parent;
		private List<Object> cells = new ArrayList<Object>();
		private boolean prentized = false;

		public List<Object> getCells() {
			return cells;
		}

		public boolean isPrentized() {
			return prentized;
		}

		public void setPrentized(boolean prentized) {
			this.prentized = prentized;
		}

		public void setCells(List<Object> cells) {
			this.cells = cells;
		}

		public ExpressionCell getParent() {
			return parent;
		}

		public void setParent(ExpressionCell parent) {
			this.parent = parent;
			parent.getCells().add(this);
		}

	}

	public static class ParseExcetion extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6357647845825502595L;
		private int location;
		private Object cell;

		public ParseExcetion(String string) {
			super(string);
		}

		public int getLocation() {
			return location;
		}

		public void setLocation(int location) {
			this.location = location;
		}

		public Object getCell() {
			return cell;
		}

		public void setCell(Object cell) {
			this.cell = cell;
		}

	}
}
