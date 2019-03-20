package com.linkstec.bee.UI.look.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.linkstec.bee.UI.look.menu.BeeObjectItem;
import com.linkstec.bee.UI.look.text.BeePopedTextField;
import com.linkstec.bee.UI.look.text.BeeTextField;
import com.linkstec.bee.UI.spective.detail.data.BeeDataAnnotation;

public class BeeTableStringCellEditor extends BeeTableAbstractCellEditor implements TableCellEditor {
	private JTextField text;
	private int r;
	private int c;

	private BeeTable table;
	private JComponent comp;

	public BeeTableStringCellEditor(BeeTable table, JComponent comp) {
		this.comp = comp;
		this.table = table;
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					disableEditor();
				}
			}

		});
		if (comp != null) {
			text = new BeePopedTextField();
			BeePopedTextField pop = (BeePopedTextField) text;
			pop.setPopConstants(comp);
		} else {
			text = new BeeTextField();
		}

		text.setBackground(Color.WHITE);
		text.setBorder(BeeTable.normalBorder);
		text.setDisabledTextColor(Color.BLACK);
		text.setMargin(new Insets(0, 25, 0, 0));

	}

	private void disableEditor() {
		text.setBorder(BeeTable.normalBorder);
		text.setEditable(false);
		text.setEnabled(false);
		this.cancelCellEditing();
		this.fireEditingCanceled();
		if (text instanceof BeeTextField) {
			BeeTextField bee = (BeeTextField) text;
			bee.cancelled();
		}
	}

	@Override
	public boolean stopCellEditing() {
		if (text instanceof BeeTextField) {
			BeeTextField bee = (BeeTextField) text;
			if (bee.getUserObject() == null) {
				table.setValueAt(text.getText(), r, c);
			} else {
				table.setValueAt(bee.getUserObject(), r, c);
			}
			bee.cancelled();
		} else

		if (text instanceof BeePopedTextField) {
			BeePopedTextField pop = (BeePopedTextField) text;
			pop.hidePop();
		}

		return super.stopCellEditing();
	}

	@Override
	public boolean isCellEditable(EventObject e) {

		boolean editable = super.isCellEditable(e);
		if (!editable) {
			disableEditor();
		}

		return editable;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {

		this.r = r;
		this.c = c;
		BeeTableModelAdapter adapter = (BeeTableModelAdapter) table.getModel();
		BeeTableModel model = adapter.getTreeTableModel();

		if (model instanceof BeeTableModel) {
			BeeTableModel tableModel = (BeeTableModel) model;

			if (text instanceof BeeTextField) {
				BeeTextField bee = (BeeTextField) text;
				bee.setUserObject(null);

				List<BeeObjectItem> list = tableModel.getPulldownList(c);
				if (list != null && list.size() > 0) {
					JComboBox<BeeObjectItem> box = new JComboBox<BeeObjectItem>();

					for (BeeObjectItem obj : list) {
						box.addItem(obj);
						if (value != null) {
							if (value.equals(obj.getUserObject())) {
								box.setSelectedItem(obj);
							}
						}
					}
					box.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							BeeObjectItem item = (BeeObjectItem) box.getSelectedItem();
							bee.setUserObject(item.getUserObject());
						}

					});
					return box;
				}

				TableColumn column = table.getColumnModel().getColumn(c);
				Object v = column.getHeaderValue();
				if (v instanceof BeeTableModel) {
					JComboBox<String> box = new JComboBox<String>();
					box.addItem("");
					box.addItem("〇");
					box.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							Object obj = box.getSelectedItem();
							bee.setUserObject(obj);
						}

					});
					return box;
				} else {
					tableModel.editText(bee, c, r);
				}

			} else if (text instanceof BeePopedTextField) {

				if (comp != null && comp instanceof BeeDataAnnotation) {
					BeeDataAnnotation data = (BeeDataAnnotation) comp;
					data.setRow(r);
					data.setModel(model);
					data.setValue(adapter.nodeForRow(r));
				}

			}
		}

		if (isSelected) {
			text.setBorder(BeeTable.selectedBorder);
			text.setEditable(true);
			text.setEnabled(true);
		} else {
			text.setBorder(BeeTable.normalBorder);
		}

		if (value != null) {
			text.setText(value.toString());
		} else {
			text.setText(null);
		}

		return text;
	}

}
