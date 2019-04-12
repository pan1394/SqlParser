package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.linkstec.bee.UI.editor.task.search.SearchNode;
import com.linkstec.bee.UI.editor.task.search.SearchResult;
import com.linkstec.bee.UI.look.dialog.BeeDialogCloseAction;
import com.linkstec.bee.UI.look.dialog.BeePropertyDialog;
import com.linkstec.bee.UI.look.dialog.SearchDialog;
import com.linkstec.bee.UI.look.dialog.SearchDialog.Search;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.CodecAction.BasicAction;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BEditorUndo;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

public class BEditorActions {

	public static class DeleteAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4266410089972771445L;
		private BEditor editor;

		public DeleteAction(BEditor editor) {
			this.editor = editor;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File file = this.editor.getFile();
			if (file == null) {
				return;
			}
			file.delete();
			editor.getFileExplore().updateNode(file.getParentFile(), editor.getProject());
		}

	}

	public static class DeleteSelectAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4266410089972771445L;
		private BEditor editor;

		public DeleteSelectAction(BEditor editor) {
			this.editor = editor;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.deleteSelect(e);
		}

	}

	public static class RefreshAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private BEditor editor;

		public RefreshAction(BEditor editor) {
			this.editor = editor;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			this.editor.refresh();
		}

	}

	public static class FilePropertyAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2849968538194965679L;
		private File file;

		public FilePropertyAction(File file) {
			this.file = file;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Hashtable<String, String> hash = new Hashtable<String, String>();
			if (file == null) {
				return;
			}

			if (file.exists()) {
				String path = file.getParentFile().getAbsolutePath();
				long last = file.lastModified();
				Date date = new Date(last);
				long size = file.length();
				hash.put("名前", file.getName());
				hash.put("場所", path);

				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String d = format.format(date);
				hash.put("更新時刻", d);

				String s = "";
				if (size < 1000) {
					s = size + "B";
				} else if (size < 1000 * 1000) {
					s = size / 1000 + "K";
				} else {
					s = size / 1000 / 1000 + "M";
				}
				hash.put("サイズ", s);
				hash.put("書き込み", file.canWrite() ? "可能" : "不可");
				hash.put("種類", file.isDirectory() ? "フォルダ" : "ファイル");

				BeePropertyDialog.showDialog(file.getName(), hash);

			}
		}

	}

	public static class SaveAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private BEditor editor;

		public SaveAction(BEditor sheet) {
			this.editor = sheet;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new BeeThread(new Runnable() {

				@Override
				public void run() {
					File file = editor.save();
					editor.getFileExplore().updateNode(file, editor.getProject());
				}

			}).start();

		}

	}

	public static class SaveAsAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private BEditor editor;

		public SaveAsAction(BEditor sheet) {
			this.editor = sheet;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.editor.saveAs(e);
		}

	}

	public static class NewAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private BEditorManager manager;

		public NewAction(BEditorManager manager) {
			this.manager = manager;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.manager.makeNew();
		}

	}

	public static class LayoutAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private BLayoutable layoutable;

		public LayoutAction(BLayoutable layoutable) {
			this.layoutable = layoutable;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.layoutable.layoutEditor();
		}

	}

	public static class GenerateAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private BGeneratable generatable;

		public GenerateAction(BGeneratable generatable) {
			this.generatable = generatable;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.generatable.generate();
		}

	}

	public static class UndoAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 29465135225804924L;
		private BEditorUndo manager;

		public UndoAction(BEditorUndo manager) {
			this.manager = manager;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.undo();
		}

	}

	public static class RedoAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 29465135225804924L;
		private BEditorUndo manager;

		public RedoAction(BEditorUndo manager) {
			this.manager = manager;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.redo();
		}

	}

	public static class SearchAction extends BasicAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 146386556150507397L;
		private BEditor editor;

		public SearchAction(BEditor editor) {
			this.editor = editor;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (editor instanceof BeeSourceSheet) {
				SearchSourceAction action = new SearchSourceAction();
				action.actionPerformed(e);
			} else if (editor instanceof BeeGraphSheet) {
				SearchDesignAction action = new SearchDesignAction();
				action.actionPerformed(e);
			}
		}

	}

	public static class SelecAllAction extends BasicAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 146386556150507397L;
		private BEditor editor;

		public SelecAllAction(BEditor editor) {
			this.editor = editor;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.selectAll(e);
		}

	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ColorAction extends AbstractAction {
		/**
		 * 
		 */
		protected String name, key;

		/**
		 * 
		 * @param key
		 */
		public ColorAction(String name, String key) {
			this.name = name;
			this.key = key;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			BEditor editor = Application.getInstance().getCurrentEditor();
			if (editor instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) editor;
				mxGraph graph = graphComponent.getGraph();

				if (!graph.isSelectionEmpty()) {

					// Color newColor = JColorChooser.showDialog(graphComponent, name, null);

					int s = BeeUIUtils.getDefaultFontSize();
					JColorChooser chooser = new JColorChooser();
					chooser.getComponents()[0].setPreferredSize(new Dimension(s * 20, s * 10));

					ActionListener ok = new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							Color newColor = chooser.getColor();

							if (newColor != null) {
								graph.setCellStyles(key, mxUtils.hexString(newColor));
							}
						}

					};

					JDialog dialog = JColorChooser.createDialog(graphComponent, name, true, chooser, ok, null);
					dialog.setLayout(new BorderLayout());
					dialog.add(chooser, BorderLayout.CENTER);
					// dialog.setPreferredSize(new Dimension(1000, 2000));
					dialog.setVisible(true);

				}
			}
		}
	}

	public static class SearchSourceAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6173245822508532441L;

		@Override
		public void actionPerformed(ActionEvent e) {
			SearchDialog.showDialog("Javaソース検索", new Search() {

				@Override
				public void execute(String keyword, SearchDialog dialog) {

					dialog.setVisible(false);
					doSearch(keyword, dialog);
				}

			});
		}

		private void doSearch(String keyword, SearchDialog dialog) {
			List<BProject> projects = dialog.getProject();
			if (projects.isEmpty()) {
				this.doCurrentSearch(keyword);
			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						doProjectSearch(keyword, projects);
					}

				}).start();

			}
		}

		private void doCurrentSearch(String keyword) {
			BeeTabbedPane pane = Application.getInstance().getJavaSourceSpective().getWorkspace();
			Component comp = pane.getSelectedComponent();
			if (comp instanceof JScrollPane) {
				BeeSourceSheet source = (BeeSourceSheet) comp;
				String text = source.getText();
				searchCurrent(keyword, text, source);
			}
		}

		private void doProjectSearch(String keyword, List<BProject> projects) {
			com.linkstec.bee.UI.editor.task.search.Search pane = Application.getInstance().getJavaSourceSpective()
					.getTask().getSearch();
			pane.getRoot().removeAllChildren();
			Application.getInstance().setCurrentSpactive(BeeSpective.JAVA_SOURCE);
			Application.getInstance().getJavaSourceSpective().getTask().setTaskSearchSelected();
			for (BProject project : projects) {
				SearchNode node = new SearchNode(null, project);
				node.setUserObject(project);
				node.setDisplay(project.getName());
				node.setImageIcon(BeeConstants.PROJECT_ICON);
				List<File> files = CodecUtils.getAllSourceFile(project);
				for (File f : files) {

					byte[] buffer = new byte[1024];
					String source = "";
					try {
						FileInputStream in = new FileInputStream(f);
						int len = 0;

						while ((len = in.read(buffer)) != -1) {
							source = source + new String(buffer, 0, len);
						}
						in.close();

						SearchNode child = searchText(keyword, source, f, project);
						if (child != null) {
							node.add(child);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (node.getChildCount() > -1) {
					pane.getRoot().add(node);
					pane.expandAllNode(true);
				}
			}
		}

		private SearchNode searchText(String keyword, String text, File f, BProject project) {

			if (text.contains(keyword)) {
				SearchNode node = new SearchNode(f, project);

				node.setDisplay(f.getName());

				SearchResult result = new SearchResult();
				result.setKeyword(keyword);
				result.setPath(f.getAbsolutePath());
				result.setProject(project);
				result.setType(SearchResult.TYPE_SOURCE);
				node.setUserObject(result);
				node.setImageIcon(BeeConstants.JAVA_SOURCE_ICON);
				return node;
			}
			return null;
		}

		private void searchCurrent(String keyword, String text, BeeSourceSheet source) {
			if (text.contains(keyword)) {
				source.setKeyword(keyword);
			}
		}

		@Override
		public void onclose() {

		}

	}

	public static class SearchDesignAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6173245822508532441L;

		@Override
		public void actionPerformed(ActionEvent e) {
			SearchDialog.showDialog("設計検索", new Search() {

				@Override
				public void execute(String keyword, SearchDialog dialog) {
					dialog.setVisible(false);
					doSearch(keyword, dialog);
				}

			});
		}

		private void doSearch(String keyword, SearchDialog dialog) {
			List<BProject> projects = dialog.getProject();
			if (projects.isEmpty()) {
				this.doCurrentSearch(keyword);
			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						doProjectSearch(keyword, projects);
					}

				}).start();

			}
		}

		private void doCurrentSearch(String keyword) {
			BeeGraphSheet sheet = (BeeGraphSheet) Application.getInstance().getDesignSpective().getGraphSheet();
			List<BasicNode> list = sheet.doSearch(keyword);
			if (!list.isEmpty()) {
				sheet.scrollCellToVisible(list.get(0));
			}

		}

		private void doProjectSearch(String keyword, List<BProject> projects) {
			com.linkstec.bee.UI.editor.task.search.Search pane = Application.getInstance().getDesignSpective().getTask()
					.getSearch();
			pane.getRoot().removeAllChildren();
			Application.getInstance().setCurrentSpactive(BeeSpective.DETAIL_DESIGN);
			Application.getInstance().getDesignSpective().getTask().setTaskSelectedComponent(pane);
			for (BProject project : projects) {
				SearchNode node = new SearchNode(null, project);
				node.setUserObject(project);
				node.setDisplay(project.getName());
				node.setImageIcon(BeeConstants.PROJECT_ICON);
				List<File> files = CodecUtils.getAllDesignFile(null, null, project);
				for (File f : files) {
					try {

						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
						ObjectInputStream ois = new ObjectInputStream(bis);
						Object obj = ois.readObject();
						ois.close();
						bis.close();
						SearchNode child = new SearchNode(f, project);
						child.setDisplay(f.getName());
						child.setUserObject("keyword");
						child.setImageIcon(BeeConstants.BOOK_ICON);
						if (obj instanceof BookModel) {
							BookModel b = (BookModel) obj;
							List<BEditorModel> list = b.getList();
							for (BEditorModel model : list) {
								Object ss = model.doSearch(keyword);
								if (ss != null) {
									if (ss instanceof List) {
										List l = (List) ss;
										if (!l.isEmpty()) {

											for (Object object : l) {
												SearchNode ba = new SearchNode(f, project);
												ba.setDisplay(object.toString());
												ba.setImageIcon(BeeConstants.VAR_ICON);

												SearchResult result = new SearchResult();
												result.setKeyword(keyword);
												result.setProject(project);
												result.setType(SearchResult.TYPE_DESIGN);
												result.setPath(f.getAbsolutePath());
												result.setUserObject(object);
												ba.setUserObject(result);
												child.add(ba);
											}

											node.add(child);
										}
									}
								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (node.getChildCount() > -1) {
					pane.getRoot().add(node);
					pane.expandAllNode(true);
					pane.updateUI();
				}
			}
		}

		@Override
		public void onclose() {

		}

	}

}
