package com.linkstec.bee.UI.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeProjectConfigView extends BeeConfigView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1381064781065401179L;
	public JButton apply;
	private JButton cancel;
	private boolean isNew = false;
	private ActionListener actionListener;
	private JLabel error;

	public BeeProjectConfigView(BeeProject project, BeeConfig config) {
		super(project, config);
	}

	public void setNew() {
		isNew = true;
		apply.setText("プロジェクト追加");
	}

	@Override
	protected boolean save() {
		if (this.isNew) {
			List<BProject> projects = this.config.getConfig().getProjects();
			for (BProject p : projects) {
				if (p.getName().equals(project.getName())) {
					JOptionPane.showMessageDialog(null, "同じ名前のプロジェクトが存在してます");
					return false;
				}
			}
			this.config.getConfig().getProjects().add(project);
		}
		boolean result = super.save();
		if (this.isNew && result) {
			Application.getInstance().addNewProject(project);
		}
		return result;
	}

	@Override
	public JPanel makeActions() {

		apply = new JButton("適用");
		apply.setEnabled(false);
		cancel = new JButton("キャンセル");
		return this.AddButtons(new JButton[] { apply, cancel });
	}

	@Override
	protected void buttonClicked(JButton b) {
		ActionEvent e = new ActionEvent(b, 0, "");
		if (b.equals(apply)) {
			if (!save()) {
				e.setSource("ERROR");
			}
		}
		if (this.actionListener != null) {
			this.actionListener.actionPerformed(e);
		}
	}

	@Override
	public JPanel makeContents() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());
		panel.add(this.makeError(), BorderLayout.SOUTH);
		panel.add(this.makeRows(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel makeRows() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		GridLayout layout = new GridLayout(0, 1);

		panel.setLayout(layout);
		panel.add(makeRow(this, "プロジェクト名", project.getName(), "name", false));
		panel.add(makeRow(this, "プロジェクトルートパス", project.getRootPath(), "root", true));
		panel.add(makeRow(this, "プロジェクトソースパス", project.getSourcePath(), "source", true));
		panel.add(makeRow(this, "プロジェクトクラスパス", project.getClassPath(), "class", true));
		panel.add(makeRow(this, "プロジェクトグラフパス", project.getDesignPath(), "design", true));
		return panel;
	}

	private JLabel makeError() {
		error = new JLabel();
		error.setForeground(Color.RED);
		int s = BeeUIUtils.getDefaultFontSize();
		error.setBorder(new EmptyBorder(s, 0, s, s));
		return error;
	}

	private BeeConfigEditRow makeRow(BeeConfigView view, String title, String defaultValue, String targetProperty, boolean select) {
		BeeConfigEditRow row = new BeeConfigEditRow(view, title, defaultValue, targetProperty, select);
		row.setRequired(true);
		return row;
	}

	protected void rowValueChanged(String name, String value) {

		if (name.equals("name")) {
			this.project.setName(value);
		} else if (name.equals("root")) {
			this.project.setRootPath(value);
		} else if (name.equals("class")) {
			this.project.setClassPath(value);
		} else if (name.equals("source")) {
			this.project.setSourcePath(value);
		} else if (name.equals("design")) {
			this.project.setDesignPath(value);
		}
		apply.setEnabled(isInputComplete(name));

	}

	private boolean isInputComplete(String name) {
		boolean falsed = false;
		if (project.getName() == null || project.getName().equals("")) {
			if (name.equals("name")) {
				this.error.setText("プロジェクト名が必須です。");
			}
			falsed = true;
		}
		if (project.getClassPath() == null || project.getClassPath().equals("")) {
			if (name.equals("class")) {
				this.error.setText("プロジェクト名が必須です。");
			}
			falsed = true;
		} else {
			if (!checkError(project.getClassPath(), "クラスパス")) {
				falsed = true;
			}
		}
		if (project.getSourcePath() == null || project.getSourcePath().equals("")) {
			if (name.equals("source")) {
				this.error.setText("ソースパスが必須です。");
			}
			falsed = true;
		} else {
			if (!checkError(project.getSourcePath(), "ソースパス")) {
				falsed = true;
			}
		}
		if (project.getRootPath() == null || project.getRootPath().equals("")) {
			if (name.equals("root")) {
				this.error.setText("ルートパスが必須です。");
			}
			falsed = true;
		} else {
			if (!checkError(project.getRootPath(), "ルートパス")) {
				falsed = true;
			}
		}
		if (project.getDesignPath() == null || project.getDesignPath().equals("")) {
			if (name.equals("design")) {
				this.error.setText("グラフパスが必須です。");
			}
			falsed = true;
		} else {
			if (!checkError(project.getDesignPath(), "グラフパス")) {
				falsed = true;
			}
		}
		if (!falsed) {
			this.error.setText("");
		}
		return !falsed;
	}

	private boolean checkError(String value, String title) {
		File f = new File(value);
		if (!f.exists()) {
			this.error.setText("指定した" + title + "のフォルダが存在しません");
			return false;
		} else {
			this.error.setText("");
			return true;
		}
	}

	public void setListener(ActionListener actionListener) {
		this.actionListener = actionListener;

	}

}
