package com.linkstec.bee.UI.look.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.button.BeeCheckBox;

public class BeeListDialog extends JPanel implements BeeDialogCloseAction, ActionListener, DocumentListener {
	/**
	 * 
	 */

	public static interface BeforeActionLisener {
		public boolean beforeAction();
	}

	private static final long serialVersionUID = -1242835729788180161L;
	private List<Object> selected = new ArrayList<Object>();
	private List<BeeCheckBox> checkes = new ArrayList<BeeCheckBox>();
	private JButton submit;
	private JTextField text;
	private JLabel result;
	private JPanel checkPanel;
	private JPanel contentsPanel;
	private BeforeActionLisener beforeSubmit, beforeCancel;

	public BeeListDialog(List<Object> list, List<Object> defualts) {
		this.setLayout(new BorderLayout());
		this.setBackground(BeeConstants.BACKGROUND_COLOR);

		contentsPanel = new JPanel();
		contentsPanel.setOpaque(false);
		BorderLayout layout = new BorderLayout();
		layout.setVgap(BeeUIUtils.getDefaultFontSize());
		contentsPanel.setLayout(layout);
		contentsPanel.add(this.makeListArea(list, defualts), BorderLayout.CENTER);

		this.add(contentsPanel);
		this.add(makeButtonArea(), BorderLayout.NORTH);
		this.actionPerformed(null);

	}

	public BeforeActionLisener getBeforeSubmit() {
		return beforeSubmit;
	}

	public void setBeforeSubmit(BeforeActionLisener beforeSubmit) {
		this.beforeSubmit = beforeSubmit;
	}

	public BeforeActionLisener getBeforeCancel() {
		return beforeCancel;
	}

	public void setBeforeCancel(BeforeActionLisener beforeCancel) {
		this.beforeCancel = beforeCancel;
	}

	public void addComponent(JComponent componet) {
		this.contentsPanel.add(componet, BorderLayout.NORTH);
	}

	private JComponent makeListArea(List<Object> list, List<Object> defualts) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		BorderLayout layout = new BorderLayout();
		layout.setVgap(BeeUIUtils.getDefaultFontSize() / 3);
		panel.setLayout(layout);

		panel.add(makeOthers(), BorderLayout.NORTH);

		JComponent options = this.makeList(list, defualts);
		JScrollPane pane = new JScrollPane(options);
		pane.setBorder(new LineBorder(Color.LIGHT_GRAY));
		pane.getViewport().setOpaque(false);
		pane.getVerticalScrollBar().setUnitIncrement(BeeUIUtils.getDefaultFontSize() * 5);
		pane.setPreferredSize(new Dimension((int) (options.getPreferredSize().getWidth() + BeeUIUtils.getDefaultFontSize() * 5), BeeUIUtils.getDefaultFontSize() * 20));

		panel.add(pane, BorderLayout.CENTER);
		return panel;

	}

	private JPanel makeOthers() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(0, 1));
		text = new JTextField();
		text.getDocument().addDocumentListener(this);
		text.setBorder(new LineBorder(Color.LIGHT_GRAY));
		text.setFont(BeeUIUtils.getDefaultFont());
		text.setFocusCycleRoot(true);
		panel.add(text);

		this.result = new JLabel();
		panel.add(result);

		return panel;
	}

	private JComponent makeButtonArea() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		int margin = BeeUIUtils.getDefaultFontSize();
		panel.setBorder(new EmptyBorder(margin, margin, margin, margin));
		FlowLayout layout = new FlowLayout();
		layout.setHgap(BeeUIUtils.getDefaultFontSize());
		layout.setAlignment(FlowLayout.CENTER);
		panel.setLayout(layout);

		BeeCheckBox selectAll = new BeeCheckBox("すべて選択");
		int gap = BeeUIUtils.getDefaultFontSize();
		selectAll.setFont(BeeUIUtils.getDefaultFont());

		Insets insets = new Insets(gap / 3, gap, gap / 3, gap);
		submit = new JButton("適用");
		submit.setUI(new BeeButtonUI());
		submit.setMargin(insets);

		JButton cancel = new JButton("キャンセル");
		cancel.setUI(new BeeButtonUI());
		cancel.setMargin(insets);

		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source.equals(selectAll)) {
					selecteAll(selectAll.isSelected());
				} else if (source.equals(submit)) {
					if (beforeSubmit != null) {
						if (beforeSubmit.beforeAction()) {
							BeeListDialog.this.setVisible(false);
						}
					} else {
						BeeListDialog.this.setVisible(false);
					}
				} else if (source.equals(cancel)) {
					if (beforeCancel != null) {
						if (beforeCancel.beforeAction()) {
							selecteAll(false);
							BeeListDialog.this.setVisible(false);
						}
					} else {
						selecteAll(false);
						BeeListDialog.this.setVisible(false);
					}
				}
			}

		};
		selectAll.addActionListener(action);

		submit.addActionListener(action);
		cancel.addActionListener(action);
		panel.add(selectAll);

		JPanel space = new JPanel();
		space.setOpaque(false);
		space.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 5, 1));
		panel.add(space);

		panel.add(cancel);
		panel.add(submit);

		return panel;
	}

	private JComponent makeList(List<Object> list, List<Object> defualts) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		FlowLayout l = new FlowLayout();
		l.setAlignment(FlowLayout.LEFT);
		panel.setLayout(l);

		Collections.sort(list, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				if (o1 == null || o2 == null) {
					return 0;
				}
				String s1 = o1.toString();
				String s2 = o2.toString();

				int mx = Math.min(s1.length(), s2.length());
				for (int i = 0; i < mx; i++) {
					char c1 = s1.charAt(i);
					char c2 = s2.charAt(i);
					if (c1 != c2) {
						return c1 - c2;
					}
				}

				return 0;
			}

		});

		checkPanel = new JPanel();
		checkPanel.setOpaque(false);
		int margin = BeeUIUtils.getDefaultFontSize() / 3;
		checkPanel.setBorder(new EmptyBorder(margin, margin, margin, margin));

		GridLayout layout = new GridLayout(0, 1);
		layout.setHgap(BeeUIUtils.getDefaultFontSize() / 3);
		checkPanel.setLayout(layout);
		if (defualts != null) {
			for (Object obj : defualts) {
				checkPanel.add(this.makeItem(obj, true));
			}
		}

		for (Object obj : list) {
			if (defualts != null) {
				if (!defualts.contains(obj)) {
					checkPanel.add(this.makeItem(obj, false));
				}
			} else {
				checkPanel.add(this.makeItem(obj, false));
			}
		}
		this.result.setText(defualts.size() + "件選択された");
		panel.add(checkPanel);
		return panel;
	}

	private JPanel makeItem(Object s, boolean checked) {
		JPanel checkPanel = new JPanel();
		checkPanel.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEADING);
		checkPanel.setLayout(layout);
		String name = s.toString();
		BeeCheckBox check = new BeeCheckBox(name);
		check.setSelected(checked);
		check.setFont(BeeUIUtils.getDefaultFont());
		checkPanel.add(check);
		check.setUserObject(s);
		check.addActionListener(this);

		try {
			Method m = s.getClass().getMethod("getImgeIcon");
			ImageIcon icon = (ImageIcon) m.invoke(s);
			check.setUserIcon(icon);
		} catch (Exception e) {
		}

		try {
			Method m = s.getClass().getMethod("isError");
			Boolean b = (Boolean) m.invoke(s);
			if (b.booleanValue()) {
				check.setEnabled(false);
			}
		} catch (Exception e) {
		}

		this.checkes.add(check);
		checkPanel.add(check);
		return checkPanel;

	}

	public void showDialog(String title) {
		BeeDialog.showDialog(title, this, this);
	}

	public static BeeListDialog showDialog(String title, List<Object> list, List<Object> defualts) {
		BeeListDialog dialog = new BeeListDialog(list, defualts);
		BeeDialog.showDialog(title, dialog, dialog);
		return dialog;
	}

	private void selecteAll(boolean select) {
		int i = 0;
		for (BeeCheckBox check : checkes) {
			if (check.getParent().getParent() != null) {
				check.setSelected(select);
				check.updateUI();
				if (select) {
					i++;
				}
			}
		}
		if (i > 0) {
			this.submit.setEnabled(true);
		}
		this.result.setText(i + "件選択された");
	}

	public List<Object> getSelected() {

		this.selected.clear();
		for (BeeCheckBox check : checkes) {
			if (check.isSelected()) {
				this.selected.add(check.getUserObject());

			}
		}

		List<Object> list = new ArrayList<Object>();
		list.addAll(selected);

		return list;
	}

	@Override
	public void onclose() {
		this.selecteAll(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		BeeCheckBox box = null;
		boolean selected = false;
		if (e != null) {
			Object obj = e.getSource();
			if (obj instanceof BeeCheckBox) {
				box = (BeeCheckBox) obj;
				selected = box.isSelected();
			}
		}
		boolean shift = false;
		if (e != null) {
			int mod = e.getModifiers();
			if ((mod & ActionEvent.SHIFT_MASK) > 0) {
				shift = true;
			}
		}
		this.submit.setEnabled(false);
		int i = 0;
		BeeCheckBox first = null;
		boolean end = false;
		for (BeeCheckBox check : checkes) {
			if (check.isSelected()) {
				if (first == null) {
					first = check;
				}
			}
			if (check.equals(box)) {
				end = true;
			}
			if (shift) {
				if (first != null) {
					if (!end) {
						check.setSelected(selected);
					}
				}
			}

			if (check.isSelected()) {
				i++;
			}
		}
		if (i > 0) {
			this.submit.setEnabled(true);
		}
		this.result.setText(i + "件選択された");
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChanged();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChanged();

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChanged();

	}

	private void documentChanged() {
		String s = text.getText().toLowerCase();

		boolean changed = false;
		int i = 0;
		for (BeeCheckBox check : checkes) {
			String name = check.getText().toLowerCase();
			if (name.indexOf(s) > -1 || s.equals("")) {
				if (check.getParent().getParent() == null) {
					checkPanel.add(check.getParent());
					changed = true;
				}
			} else {
				if (check.getParent().getParent() != null) {
					check.setSelected(false);
					checkPanel.remove(check.getParent());
					changed = true;
				}
			}
			if (check.isSelected()) {
				i++;
			}
		}
		if (changed) {
			checkPanel.updateUI();
			checkPanel.getParent().repaint();
		}
		if (i > 0) {
			this.submit.setEnabled(true);
		} else {
			this.submit.setEnabled(false);
		}
	}
}
