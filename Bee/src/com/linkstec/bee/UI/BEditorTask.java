package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.editor.task.console.ConsoleDisplay;
import com.linkstec.bee.UI.editor.task.problem.Problems;
import com.linkstec.bee.UI.editor.task.search.Search;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;

public class BEditorTask extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4885991566648601074L;
	private BeeTabbedPane tab;
	private ConsoleDisplay console;
	private Problems problems;
	private Search search;

	public BEditorTask(Configuration config) {
		this.setLayout(new BorderLayout());

		tab = new BeeTabbedPane();
		tab.setEditable(false);
		tab.setBorder(null);
		tab.setTabPlacement(JTabbedPane.TOP);

		console = new ConsoleDisplay();
		problems = new Problems();
		search = new Search(config);

		tab.insertTab("問題", BeeConstants.PROBLEMS_ICON, problems, "問題", 0);
		tab.insertTab("検索", BeeConstants.SEARCH_ICON, search, "検索", 1);
		tab.insertTab("コンソール", BeeConstants.CONSOLE_ICON, console, "コンソール", 2);

		this.add(tab, BorderLayout.CENTER);
	}

	public Search getSearch() {
		if (this.search.getParent() == null) {
			tab.insertTab("検索", BeeConstants.SEARCH_ICON, search, "検索", tab.getTabCount());
		}
		return this.search;
	}

	public Problems getProblems() {
		if (this.problems.getParent() == null) {
			tab.insertTab("問題", BeeConstants.PROBLEMS_ICON, problems, "問題", tab.getTabCount());
		}
		return this.problems;
	}

	public ConsoleDisplay getConsole() {
		if (this.console.getParent() == null) {
			tab.insertTab("コンソール", BeeConstants.CONSOLE_ICON, console, "コンソール", tab.getTabCount());
		}
		return this.console;
	}

	public void setTaskSelectedComponent(Component comp) {
		int count = tab.getTabCount();

		for (int i = count - 1; i >= 0; i--) {
			Component c = this.tab.getComponentAt(i);
			if (c instanceof JScrollPane) {
				JScrollPane scroll = (JScrollPane) c;
				Component cc = scroll.getViewport().getView();
				if (cc.equals(comp)) {
					tab.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public void setTaskConsoleSelected() {
		tab.setSelectedComponent(console);
	}

	public void setTaskProblemsSelected() {
		tab.setSelectedComponent(this.problems);
	}

	public void setTaskSearchSelected() {
		tab.setSelectedComponent(this.search);
	}
}
