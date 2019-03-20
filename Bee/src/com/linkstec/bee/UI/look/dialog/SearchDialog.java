package com.linkstec.bee.UI.look.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.text.BeeTextField;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class SearchDialog extends JPanel implements BeeDialogCloseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8809132908403220419L;
	private static List<String> keywords = new ArrayList<String>();
	private Search searchAction;
	private BeeTextField keyword;
	int m = BeeUIUtils.getDefaultFontSize();
	private JPanel selectionPanel;

	public SearchDialog(Search search) {

		this.setBorder(new EmptyBorder(m, m, m, m));
		this.searchAction = search;
		BorderLayout layout = new BorderLayout();
		layout.setHgap(m);
		this.setLayout(layout);
		this.add(this.makeContents(), BorderLayout.CENTER);
		this.add(this.makeButtons(), BorderLayout.SOUTH);
	}

	private JPanel makeContents() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		GridLayout layout = new GridLayout(0, 1);
		layout.setVgap(m);
		panel.setLayout(layout);

		panel.add(this.makeKeyword());
		panel.add(this.makeSelections());

		return panel;
	}

	private JPanel makeKeyword() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEADING);
		panel.setLayout(layout);

		JLabel label = new JLabel("キーワード");
		label.setIcon(BeeConstants.SEARCH_ICON);
		panel.add(label);

		keyword = new BeeTextField();

		int m = BeeUIUtils.getDefaultFontSize();
		keyword.setPreferredSize(new Dimension(m * 30, m * 2));
		keyword.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (!keywords.isEmpty()) {
					JPopupMenu menu = new JPopupMenu();

					for (String s : keywords) {
						JMenuItem item = new JMenuItem();
						item.setText(s);
						item.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								keyword.setText(item.getText());
							}

						});
						menu.add(item);
					}

					menu.setBackground(Color.WHITE);
					menu.setSize(new Dimension(keyword.getWidth(), (int) menu.getSize().getHeight()));
					menu.show(keyword, 0, keyword.getHeight());

					keyword.requestFocus();
				}

			}

			@Override
			public void focusLost(FocusEvent e) {

			}

		});
		keyword.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					doSearch();
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

		});
		panel.add(keyword);
		return panel;
	}

	private JPanel makeSelections() {
		selectionPanel = new JPanel();
		selectionPanel.setOpaque(false);

		BoxLayout layout = new BoxLayout(selectionPanel, BoxLayout.Y_AXIS);
		selectionPanel.setLayout(layout);

		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		List<BProject> projects = config.getProjects();

		for (BProject project : projects) {
			JCheckBox box = new JCheckBox(project.getName() + "内をすべて検索");
			box.putClientProperty("P", project);
			selectionPanel.add(box);
		}

		return selectionPanel;
	}

	public List<BProject> getProject() {
		List<BProject> project = new ArrayList<BProject>();
		int count = this.selectionPanel.getComponentCount();
		for (int i = 0; i < count; i++) {
			JCheckBox box = (JCheckBox) this.selectionPanel.getComponent(i);
			if (box.isSelected()) {
				project.add((BProject) box.getClientProperty("P"));
			}
		}
		return project;
	}

	private JPanel makeButtons() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.RIGHT);

		JButton search = new JButton("検索");
		JButton cancel = new JButton("キャンセル");
		search.setFont(BeeUIUtils.getDefaultFont());
		cancel.setFont(BeeUIUtils.getDefaultFont());

		panel.add(cancel);
		panel.add(search);

		ActionListener l = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(search)) {
					doSearch();
				} else if (e.getSource().equals(cancel)) {
					SearchDialog.this.setVisible(false);
				}

			}

		};
		cancel.addActionListener(l);
		search.addActionListener(l);

		panel.setLayout(layout);

		return panel;
	}

	private void doSearch() {
		String key = keyword.getText().trim();
		if (!key.equals("")) {
			if (!keywords.contains(key)) {
				keywords.add(key);
			}
			searchAction.execute(key, SearchDialog.this);
		}
	}

	public static SearchDialog showDialog(String title, Search search) {
		SearchDialog dialog = new SearchDialog(search);
		BeeDialog.showDialog(title, dialog, dialog);
		return dialog;
	}

	@Override
	public void onclose() {

	}

	public static interface Search {
		public void execute(String keyword, SearchDialog dialog);
	}

}
