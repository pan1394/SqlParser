package com.linkstec.bee.UI.editor.task.search;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.editor.task.TaskPane;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.tree.FileExplorTree;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public class Search extends TaskPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1171226844945051371L;
	private FileExplorTree tree;

	public Search(Configuration config) {
		tree = new FileExplorTree(new SearchNode(null, null), config);
		tree.removeMouseLisener();
		this.getScroll().getViewport().setView(tree);

		MouseAdapter adapter = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getSource() == tree && e.getClickCount() == 2) {

					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if (selPath != null) {
						Object LastPath = selPath.getLastPathComponent();
						if (LastPath instanceof SearchNode) {
							SearchNode node = (SearchNode) LastPath;
							if (node.isLeaf()) {
								Object object = node.getUserObject();

								if (object instanceof SearchResult) {
									SearchResult result = (SearchResult) object;
									String keyword = result.getKeyword();
									String path = result.getPath();
									BProject project = result.getProject();
									File f = new File(path);
									if (result.getType().equals(SearchResult.TYPE_DESIGN)) {
										BWorkspace space = Application.getInstance().getDesignSpective().getWorkspace();
										List<BEditor> books = space.getAllOpenningEditors();
										boolean opened = false;
										for (BEditor book : books) {
											if (book.getFile().getAbsolutePath().equals(f.getAbsolutePath())) {
												if (!space.getSelectedComponent().equals(book)) {
													space.setSelectedComponent((Component) book);
												}
												opened = true;
											}
										}
										if (!opened) {
											try {

												BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
												ObjectInputStream ois = new ObjectInputStream(bis);
												Object obj = ois.readObject();
												ois.close();
												bis.close();

												if (obj instanceof BookModel) {
													BookModel b = (BookModel) obj;
													EditorBook editor = b.getBook(b, project, f, Application.getInstance().getDesignSpective().getWorkspace());
													Application.getInstance().getDesignSpective().getWorkspace().addEditor(editor);
												}

											} catch (Exception e1) {
												e1.printStackTrace();
											}
										}
										BeeGraphSheet sheet = Application.getInstance().getDesignSpective().getGraphSheet();
										if (sheet != null) {

											sheet.doSearch(keyword);
											sheet.scrollCellToVisible(result.getUserObject());
										}
									} else if (result.getType().equals(SearchResult.TYPE_SOURCE)) {
										Application.getInstance().getJavaSourceSpective().setSelected(f, project);
										BWorkspace space = Application.getInstance().getJavaSourceSpective().getWorkspace();
										BeeSourceSheet sheet = (BeeSourceSheet) space.getSelectedComponent();
										sheet.setKeyword(keyword);
									}
								}
							}
						}
					}

				}

			}

		};
		tree.addMouseListener(adapter);

	}

	public DefaultMutableTreeNode getRoot() {
		return tree.getRoot();
	}

	public void expandAllNode(boolean b) {
		tree.expandAllNode(b);

	}

}
