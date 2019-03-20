package com.linkstec.bee.UI.spective.detail.tip;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.LoopNode;
import com.linkstec.bee.UI.node.OnewordNode;
import com.linkstec.bee.UI.node.ReturnNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.spective.detail.logic.VerifyHelper;
import com.linkstec.bee.UI.tip.TipAction;
import com.linkstec.bee.core.codec.encode.JavaGenUnit;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxICell;

public class DetailEditTip {
	public static void makeTipContents(BasicNode target, BeeGraphSheet bee, DetailEditToolTip tip) {
		if (target == null) {
			BeeModel model = bee.getModel();
			tip.addComp(new JLabel(model.getName()));
			tip.addLine();

			DetailEditTip.createSource(null, bee, tip);

		} else {

			if (target.toString() != null && !target.toString().contentEquals("")) {
				tip.addComp(new JLabel("<html>" + target.toString() + "</html>"));
				tip.addLine();
			}
			String desc = target.getNodeDesc();
			if (desc != null) {
				JLabel jdesc = new JLabel(desc);
				tip.addComp(jdesc);
				tip.addLine();
			}

			DetailEditTip.creatAction(target, bee, tip);
			DetailEditTip.createSolutions(target, bee, tip);
			DetailEditTip.createSource(target, bee, tip);
		}
	}

	private static void createSolutions(BasicNode target, BeeGraphSheet bee, DetailEditToolTip tip) {
		String alert = target.getAlert();
		if (alert != null) {
			JLabel aler = new JLabel(alert);
			aler.setIcon(BeeConstants.ERROR_ICON);

			tip.addComp(aler);

			List<TipAction> actions = target.getAlertObject().getActions();
			if (actions != null && !actions.isEmpty()) {
				JLabel solution = new JLabel("解決するには以下が考えられる");
				tip.addComp(solution);
				for (TipAction ation : actions) {
					tip.addLink(ation);
				}
			}
			tip.addLine();
		}
	}

	private static void createSource(BasicNode target, BeeGraphSheet bee, DetailEditToolTip tip) {
		String source = null;
		if (target == null) {
			source = JavaGenUnit.getAllSource(bee.getProject(), bee.getModel());
		} else if (target instanceof BMethod) {
			BMethod v = (BMethod) target;
			source = JavaGenUnit.getMethodSource(bee.getProject(), bee.getModel(), v);
		} else if (target instanceof BLogicUnit) {
			BLogicUnit v = (BLogicUnit) target;
			source = JavaGenUnit.getUnitSource(bee.getProject(), bee.getModel(), v);
		} else if (target instanceof BValuable) {
			BValuable v = (BValuable) target;
			source = JavaGenUnit.getTypeSource(bee.getProject(), bee.getModel(), v);
		} else if (target instanceof BClassHeader) {
			BClassHeader header = (BClassHeader) target;
			BClass bclass = header.getBClass();
			source = JavaGenUnit.getClassHeaderSource(bee.getProject(), bclass);
		}
		if (source != null) {
			int s = BeeUIUtils.getDefaultFontSize();

			JTextPane text = new JTextPane() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 6177147560681612512L;

				@Override
				public Insets getInsets() {

					return new Insets(s / 2, s, s / 2, s);
				}

			};
			text.setBorder(new EtchedBorder());
			text.setFont(BeeUIUtils.getDefaultFont());
			text.addMouseListener(tip.getAdapter());
			text.setText(source);

			JScrollPane scroll = new JScrollPane(text);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(tip.getTipWidth() - s * 3, text.getPreferredSize().height));
			tip.addComp(scroll);
		}
	}

	private static void creatAction(BasicNode target, BeeGraphSheet bee, DetailEditToolTip tip) {
		mxICell parent = target.getParent();
		if (parent instanceof BLogicBody) {
			int index = parent.getIndex(target);
			mxICell pp = parent.getParent();
			BMethod method = VerifyHelper.findMethod((BasicNode) parent);
			if (method != null) {
				tip.addLink(new TipAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6558668459403391239L;

					@Override
					public String getTitle() {
						return target.toString() + "の下に戻り値を追加する";
					}

					@Override
					public ImageIcon getIcon() {
						return BeeConstants.ACTION_ICON;
					}

					@Override
					public void clicked() {
						ReturnNode bnode = new ReturnNode();
						BValuable returnValue = method.getReturn();
						if (returnValue == null) {
							bnode.setRuturnNullValue();
						} else {
							BClass bclass = returnValue.getBClass();
							if (bclass == null) {
								bnode.setRuturnNullValue();
							} else {
								bnode.setRuturnValue(ComplexNode.makeDefaultValue(bclass));
							}
						}
						parent.insert(bnode, index + 1);
						LayoutUtils.makeNumber((mxICell) bee.getGraph().getDefaultParent(), null);
						LayoutUtils.RelayoutAll(bee);
					}

				});

				tip.addLink(new TipAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6558668459403391239L;

					@Override
					public String getTitle() {
						return target.toString() + "の上に戻り値を追加する";
					}

					@Override
					public ImageIcon getIcon() {
						return BeeConstants.ACTION_ICON;
					}

					@Override
					public void clicked() {
						ReturnNode bnode = new ReturnNode();
						BValuable returnValue = method.getReturn();
						if (returnValue == null) {
							bnode.setRuturnNullValue();
						} else {
							BClass bclass = returnValue.getBClass();
							if (bclass == null) {
								bnode.setRuturnNullValue();
							} else {
								bnode.setRuturnValue(ComplexNode.makeDefaultValue(bclass));
							}
						}
						parent.insert(bnode, index);
						LayoutUtils.makeNumber((mxICell) bee.getGraph().getDefaultParent(), null);
						LayoutUtils.RelayoutAll(bee);
					}

				});
			}

			if (pp instanceof LoopNode) {

				tip.addLink(new TipAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6558668459403391239L;

					@Override
					public String getTitle() {
						return target.toString() + "の下にループを中断する";
					}

					@Override
					public ImageIcon getIcon() {
						return BeeConstants.ACTION_ICON;
					}

					@Override
					public void clicked() {
						OnewordNode bnode = new OnewordNode();
						bnode.setWord(OnewordNode.WORD_BREAK);
						parent.insert(bnode, index + 1);
						LayoutUtils.makeNumber((mxICell) bee.getGraph().getDefaultParent(), null);
						LayoutUtils.RelayoutAll(bee);
					}

				});

				tip.addLink(new TipAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6558668459403391239L;

					@Override
					public String getTitle() {
						return target.toString() + "の上にループを中断する";
					}

					@Override
					public ImageIcon getIcon() {
						return BeeConstants.ACTION_ICON;
					}

					@Override
					public void clicked() {
						OnewordNode bnode = new OnewordNode();
						bnode.setWord(OnewordNode.WORD_BREAK);
						parent.insert(bnode, index);
						LayoutUtils.makeNumber((mxICell) bee.getGraph().getDefaultParent(), null);
						LayoutUtils.RelayoutAll(bee);
					}

				});
				tip.addLink(new TipAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6558668459403391239L;

					@Override
					public String getTitle() {
						return target.toString() + "が完了後に直に次のループ処理へ飛ぶ";
					}

					@Override
					public ImageIcon getIcon() {
						return BeeConstants.ACTION_ICON;
					}

					@Override
					public void clicked() {
						OnewordNode bnode = new OnewordNode();
						bnode.setWord(OnewordNode.WORD_CONTINUE);
						parent.insert(bnode, index + 1);
						LayoutUtils.makeNumber((mxICell) bee.getGraph().getDefaultParent(), null);
						LayoutUtils.RelayoutAll(bee);
					}

				});

				tip.addLink(new TipAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6558668459403391239L;

					@Override
					public String getTitle() {
						return target.toString() + "の前に次のループ処理へ飛ぶ";
					}

					@Override
					public ImageIcon getIcon() {
						return BeeConstants.ACTION_ICON;
					}

					@Override
					public void clicked() {
						OnewordNode bnode = new OnewordNode();
						bnode.setWord(OnewordNode.WORD_CONTINUE);
						parent.insert(bnode, index);
						LayoutUtils.makeNumber((mxICell) bee.getGraph().getDefaultParent(), null);
						LayoutUtils.RelayoutAll(bee);
					}

				});
			}
		}
	}
}
