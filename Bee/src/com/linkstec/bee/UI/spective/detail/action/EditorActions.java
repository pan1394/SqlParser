
package com.linkstec.bee.UI.spective.detail.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.w3c.dom.Document;

import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.config.BeeProject;
import com.linkstec.bee.UI.config.BeeProjectConfigView;
import com.linkstec.bee.UI.look.dialog.BeeDialog;
import com.linkstec.bee.UI.look.dialog.BeeDialogCloseAction;
import com.linkstec.bee.UI.look.filechooser.BeeFileChooser;
import com.linkstec.bee.UI.spective.basic.config.CodeConfig;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.EditorWorkSpace;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.CodecAction.BasicAction;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.analysis.mxDistanceCostFunction;
import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

/**
 *
 */
public class EditorActions {
	/**
	 * 
	 * @param e
	 * @return Returns the graph for the given action event.
	 */
	public static final EditorWorkSpace getEditor(ActionEvent e) {

		return (EditorWorkSpace) Application.getInstance().getDesignSpective().getWorkspace();
	}

	public static class ImportProjectAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 203518304841604714L;

		@Override
		public void actionPerformed(ActionEvent e) {
			BeeFileChooser dialog = new BeeFileChooser();
			dialog.setFileSelectionMode(BeeFileChooser.DIRECTORIES_ONLY);
			dialog.setDialogTitle("Importするプロジェクトルートパス選択");
			dialog.showDialog(Application.FRAME, "プロジェクトルートパス選択");

			dialog.setVisible(false);
			File file = dialog.getSelectedFile();
			if (file != null && file.isDirectory()) {
				String path = file.getAbsolutePath();
				String name = path + File.separator + "src";
				File f = new File(name);
				if (f.exists() && f.isDirectory()) {
					name = path + File.separator + "classes";
					f = new File(name);
					if (f.exists() && f.isDirectory()) {
						name = path + File.separator + "detail";
						f = new File(name);
						if (f.exists() && f.isDirectory()) {
							name = path + File.separator + "lib";
							f = new File(name);
							if (f.exists() && f.isDirectory()) {
								name = path + File.separator + ".config";
								f = new File(name);
								if (f.exists()) {
									BeeConfig config = Application.getInstance().getConfigSpective();

									String pname = file.getName();
									List<BProject> ps = config.getConfig().getProjects();
									boolean hasSame = false;

									for (BProject p : ps) {
										if (p.getName().equals(pname)) {
											hasSame = true;
											break;
										}
									}

									if (!hasSame) {
										BeeProject project = new BeeProject(BeeUIUtils.createID());
										project.setName(file.getName());
										project.setRootPath(path);
										project.setDesignPath(path + File.separator + "detail");
										project.setClassPath(path + File.separator + "classes");
										project.setSourcePath(path + File.separator + "src");
										config.getConfig().getProjects().add(project);
										config.save();
										Application.getInstance().addNewProject(project);
									} else {
										JOptionPane.showMessageDialog(null, "プロジェクト" + pname + "はすでに存在しています",
												"プロジェクトImport", JOptionPane.INFORMATION_MESSAGE);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ExitAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {

			Application.getInstance().exit();

		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class StylesheetAction extends AbstractAction {
		/**
		 * 
		 */
		protected String stylesheet;

		/**
		 * 
		 */
		public StylesheetAction(String stylesheet) {
			this.stylesheet = stylesheet;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxCodec codec = new mxCodec();
				Document doc = mxUtils.loadDocument(EditorActions.class.getResource(stylesheet).toString());

				if (doc != null) {
					codec.decode(doc.getDocumentElement(), graph.getStylesheet());
					graph.refresh();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ZoomPolicyAction extends AbstractAction {
		/**
		 * 
		 */
		protected int zoomPolicy;

		/**
		 * 
		 */
		public ZoomPolicyAction(int zoomPolicy) {
			this.zoomPolicy = zoomPolicy;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				graphComponent.setPageVisible(true);
				graphComponent.setZoomPolicy(zoomPolicy);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class GridStyleAction extends AbstractAction {
		/**
		 * 
		 */
		protected int style;

		/**
		 * 
		 */
		public GridStyleAction(int style) {
			this.style = style;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				graphComponent.setGridStyle(style);
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class GridColorAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent, mxResources.get("gridColor"),
						graphComponent.getGridColor());

				if (newColor != null) {
					graphComponent.setGridColor(newColor);
					graphComponent.repaint();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ScaleAction extends AbstractAction {
		/**
		 * 
		 */
		protected double scale;

		/**
		 * 
		 */
		public ScaleAction(double scale) {
			this.scale = scale;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				double scale = this.scale;

				if (scale == 0) {
					String value = (String) JOptionPane.showInputDialog(graphComponent, mxResources.get("value"),
							mxResources.get("scale") + " (%)", JOptionPane.PLAIN_MESSAGE, null, null, "");

					if (value != null) {
						scale = Double.parseDouble(value.replace("%", "")) / 100;
					}
				}

				if (scale > 0) {
					graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PageSetupAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			BEditor editor = Application.getInstance().getCurrentEditor();
			if (editor != null) {

				PrinterJob pj = PrinterJob.getPrinterJob();
				PageFormat format = pj.pageDialog(editor.getPageFormat());

				if (format != null) {
					editor.setPageFormat(format);
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PrintAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			BEditor editor = Application.getInstance().getCurrentEditor();
			if (editor != null) {
				PrinterJob pj = PrinterJob.getPrinterJob();
				PageFormat pf = editor.getPageFormat();
				Paper paper = new Paper();
				double margin = 0;// 36;
				paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
				pf.setPaper(paper);

				if (pj.printDialog()) {

					pj.setPrintable(editor, pf);
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								pj.print();
							} catch (PrinterException e2) {
								e2.printStackTrace();
							}
						}

					}).start();

				}

			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction {
		/**
		 * 
		 */
		protected boolean showDialog;

		/**
		 * 
		 */
		protected String lastDir = null;

		/**
		 * 
		 */
		public SaveAction(boolean showDialog) {
			this.showDialog = showDialog;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			BEditor editor = Application.getInstance().getCurrentEditor();

			if (editor != null) {
				BeeActions.SaveAs(editor);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SelectShortestPathAction extends AbstractAction {
		/**
		 * 
		 */
		protected boolean directed;

		/**
		 * 
		 */
		public SelectShortestPathAction(boolean directed) {
			this.directed = directed;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxIGraphModel model = graph.getModel();

				Object source = null;
				Object target = null;

				Object[] cells = graph.getSelectionCells();

				for (int i = 0; i < cells.length; i++) {
					if (model.isVertex(cells[i])) {
						if (source == null) {
							source = cells[i];
						} else if (target == null) {
							target = cells[i];
						}
					}

					if (source != null && target != null) {
						break;
					}
				}

				if (source != null && target != null) {
					int steps = graph.getChildEdges(graph.getDefaultParent()).length;
					Object[] path = mxGraphAnalysis.getInstance().getShortestPath(graph, source, target,
							new mxDistanceCostFunction(), steps, directed);
					graph.setSelectionCells(path);
				} else {
					JOptionPane.showMessageDialog(graphComponent, mxResources.get("noSourceAndTargetSelected"));
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SelectSpanningTreeAction extends AbstractAction {
		/**
		 * 
		 */
		protected boolean directed;

		/**
		 * 
		 */
		public SelectSpanningTreeAction(boolean directed) {
			this.directed = directed;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxIGraphModel model = graph.getModel();

				Object parent = graph.getDefaultParent();
				Object[] cells = graph.getSelectionCells();

				for (int i = 0; i < cells.length; i++) {
					if (model.getChildCount(cells[i]) > 0) {
						parent = cells[i];
						break;
					}
				}

				Object[] v = graph.getChildVertices(parent);
				Object[] mst = mxGraphAnalysis.getInstance().getMinimumSpanningTree(graph, v,
						new mxDistanceCostFunction(), directed);
				graph.setSelectionCells(mst);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleDirtyAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				graphComponent.showDirtyRectangle = !graphComponent.showDirtyRectangle;
			}
		}

	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleConnectModeAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				mxConnectionHandler handler = graphComponent.getConnectionHandler();
				handler.setHandleEnabled(!handler.isHandleEnabled());
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PromptPropertyAction extends AbstractAction {
		/**
		 * 
		 */
		protected Object target;

		/**
		 * 
		 */
		protected String fieldname, message;

		/**
		 * 
		 */
		public PromptPropertyAction(Object target, String message) {
			this(target, message, message);
		}

		/**
		 * 
		 */
		public PromptPropertyAction(Object target, String message, String fieldname) {
			this.target = target;
			this.message = message;
			this.fieldname = fieldname;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof Component) {
				try {
					Method getter = target.getClass().getMethod("get" + fieldname);
					Object current = getter.invoke(target);

					// TODO: Support other atomic types
					if (current instanceof Integer) {
						Method setter = target.getClass().getMethod("set" + fieldname, new Class[] { int.class });

						String value = (String) JOptionPane.showInputDialog((Component) e.getSource(), "Value", message,
								JOptionPane.PLAIN_MESSAGE, null, null, current);

						if (value != null) {
							setter.invoke(target, Integer.parseInt(value));
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			// Repaints the graph component
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class TogglePropertyItem extends JCheckBoxMenuItem {
		/**
		 * 
		 */
		public TogglePropertyItem(Object target, String name, String fieldname) {
			this(target, name, fieldname, false);
		}

		/**
		 * 
		 */
		public TogglePropertyItem(Object target, String name, String fieldname, boolean refresh) {
			this(target, name, fieldname, refresh, null);
		}

		/**
		 * 
		 */
		public TogglePropertyItem(final Object target, String name, final String fieldname, final boolean refresh,
				ActionListener listener) {
			super(name);

			// Since action listeners are processed last to first we add the given
			// listener here which means it will be processed after the one below
			if (listener != null) {
				addActionListener(listener);
			}

			addActionListener(new ActionListener() {
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e) {
					execute(target, fieldname, refresh);
				}
			});

			PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
				 * PropertyChangeEvent)
				 */
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equalsIgnoreCase(fieldname)) {
						update(target, fieldname);
					}
				}
			};

			if (target instanceof mxGraphComponent) {
				((mxGraphComponent) target).addPropertyChangeListener(propertyChangeListener);
			} else if (target instanceof mxGraph) {
				((mxGraph) target).addPropertyChangeListener(propertyChangeListener);
			}

			update(target, fieldname);
		}

		/**
		 * 
		 */
		public void update(Object target, String fieldname) {
			if (target != null && fieldname != null) {
				try {
					Method getter = target.getClass().getMethod("is" + fieldname);

					if (getter != null) {
						Object current = getter.invoke(target);

						if (current instanceof Boolean) {
							setSelected(((Boolean) current).booleanValue());
						}
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}

		/**
		 * 
		 */
		public void execute(Object target, String fieldname, boolean refresh) {
			if (target != null && fieldname != null) {
				try {
					Method getter = target.getClass().getMethod("is" + fieldname);
					Method setter = target.getClass().getMethod("set" + fieldname, new Class[] { boolean.class });

					Object current = getter.invoke(target);

					if (current instanceof Boolean) {
						boolean value = !((Boolean) current).booleanValue();
						setter.invoke(target, value);
						setSelected(value);
					}

					if (refresh) {
						mxGraph graph = null;

						if (target instanceof mxGraph) {
							graph = (mxGraph) target;
						} else if (target instanceof mxGraphComponent) {
							graph = ((mxGraphComponent) target).getGraph();
						}

						graph.refresh();
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class HistoryAction extends AbstractAction {
		/**
		 * 
		 */
		protected boolean undo;

		/**
		 * 
		 */
		public HistoryAction(boolean undo) {
			this.undo = undo;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			BEditor editor = Application.getInstance().getEditor().getCurrentEditor();

			if (editor != null) {
				if (undo) {
					editor.getManager().getUndo().undo();
				} else {
					editor.getManager().getUndo().redo();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class FontStyleAction extends AbstractAction {
		/**
		 * 
		 */
		protected boolean bold;

		/**
		 * 
		 */
		public FontStyleAction(boolean bold) {
			this.bold = bold;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				Component editorComponent = null;

				if (graphComponent.getCellEditor() instanceof mxCellEditor) {
					editorComponent = ((mxCellEditor) graphComponent.getCellEditor()).getEditor();
				}

				if (editorComponent instanceof JEditorPane) {
					JEditorPane editorPane = (JEditorPane) editorComponent;
					int start = editorPane.getSelectionStart();
					int ende = editorPane.getSelectionEnd();
					String text = editorPane.getSelectedText();

					if (text == null) {
						text = "";
					}

					try {
						HTMLEditorKit editorKit = new HTMLEditorKit();
						HTMLDocument document = (HTMLDocument) editorPane.getDocument();
						document.remove(start, (ende - start));
						editorKit.insertHTML(document, start,
								((bold) ? "<b>" : "<i>") + text + ((bold) ? "</b>" : "</i>"), 0, 0,
								(bold) ? HTML.Tag.B : HTML.Tag.I);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					editorPane.select(start, ende);
				} else {
					mxIGraphModel model = graphComponent.getGraph().getModel();
					model.beginUpdate();
					try {
						graphComponent.stopEditing(false);
						graphComponent.getGraph().toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE,
								(bold) ? mxConstants.FONT_BOLD : mxConstants.FONT_ITALIC);
					} finally {
						model.endUpdate();
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class WarningAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				Object[] cells = graphComponent.getGraph().getSelectionCells();

				if (cells != null && cells.length > 0) {
					String warning = JOptionPane.showInputDialog(mxResources.get("enterWarningMessage"));

					for (int i = 0; i < cells.length; i++) {
						graphComponent.setCellWarning(cells[i], warning);
					}
				} else {
					JOptionPane.showMessageDialog(graphComponent, mxResources.get("noCellSelected"));
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class NewAction extends AbstractAction {
		public static String TYPE_DETAIL = "detail";
		public static String TYPE_DATA = "data";

		private String type;

		public NewAction() {
			this.type = TYPE_DETAIL;
		}

		public NewAction(String type) {
			this.type = type;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {

			EditorWorkSpace editor = getEditor(e);

			if (editor != null) {
				if (type.equals(TYPE_DATA)) {
					JOptionPane.showMessageDialog((Component) editor.getCurrentEditor(), "do it later!");

				} else if (type.equals(TYPE_DETAIL)) {
					BeeActions.addNewSheet(Application.getInstance().getCurrentProject());
				}

			}

		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class KeyValueAction extends AbstractAction {
		/**
		 * 
		 */
		protected String key, value;

		/**
		 * 
		 * @param key
		 */
		public KeyValueAction(String key) {
			this(key, null);
		}

		/**
		 * 
		 * @param key
		 */
		public KeyValueAction(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty()) {
				graph.setCellStyles(key, value);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PageBackgroundAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent, mxResources.get("pageBackground"), null);

				if (newColor != null) {
					graphComponent.setPageBackgroundColor(newColor);
				}

				// Forces a repaint of the component
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class StyleAction extends AbstractAction {
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof mxGraphComponent) {
				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
				mxGraph graph = graphComponent.getGraph();
				String initial = graph.getModel().getStyle(graph.getSelectionCell());
				String value = (String) JOptionPane.showInputDialog(graphComponent, mxResources.get("style"),
						mxResources.get("style"), JOptionPane.PLAIN_MESSAGE, null, null, initial);

				if (value != null) {
					graph.setCellStyle(value);
				}
			}
		}
	}

	public static class ConfigAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7840557713991513891L;
		private boolean newProject = false;
		private boolean justClose = false;
		private BProject project;

		public ConfigAction(boolean newProject) {
			this.newProject = newProject;
		}

		public ConfigAction(BProject project) {
			this.project = project;
			this.newProject = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BeeProject project = null;

			String title = "プロジェクト設定";
			if (this.project == null) {

				if (newProject) {
					title = "新規プロジェクト";
					project = new BeeProject(BeeUIUtils.createID());
				} else {

					project = (BeeProject) Application.getInstance().getCurrentProject();

				}
				if (project == null) {
					return;
				}
			} else {
				project = (BeeProject) this.project;
			}
			BeeConfig config = Application.getInstance().getConfigSpective();
			if (newProject) {
				project = new BeeProject(BeeUIUtils.createID());
			}
			BeeProjectConfigView view = new BeeProjectConfigView(project, config);
			if (newProject) {
				view.setNew();
			}
			view.setListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object obj = e.getSource();

					boolean close = true;
					if (obj != null && obj.equals("ERROR")) {
						close = false;
					}
					if (close) {
						Container p = view.getParent();
						while (p != null) {
							if (p instanceof BeeDialog) {
								BeeDialog dialog = (BeeDialog) p;
								dialog.setVisible(false);
								break;
							} else {
								p = p.getParent();
							}
						}
					}

				}

			});

			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setLayout(new BorderLayout());
			int gap = BeeUIUtils.getDefaultFontSize();
			panel.setBorder(new EmptyBorder(gap, gap, gap, gap));

			Dimension size = view.getPreferredSize();
			view.setPreferredSize(new Dimension(size.width, size.height + gap * 3));

			panel.add(view, BorderLayout.CENTER);
			BeeDialog dialog = BeeDialog.showDialog(title, panel, this);
			dialog.setVisible(false);

		}

		@Override
		public void onclose() {

		}

	}

	public static class NewBookAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2849968538194965679L;

		public NewBookAction() {

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EditorBook book = (EditorBook) Application.getInstance().getDesignSpective().getWorkspace()
					.getCurrentEditor();
			if (book != null) {
				BeeActions.addNewBook(book.getProject());
			} else {
				BProject project = Application.getInstance().getCurrentProject();
				if (project != null) {
					BeeActions.addNewBook(project);
				}
			}

		}

		@Override
		public void onclose() {

		}

	}

	public static class NewGraphSheetAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2849968538194965679L;

		@Override
		public void actionPerformed(ActionEvent e) {
			BProject project = Application.getInstance().getCurrentProject();
			BeeActions.addNewBookWidthGraph(project);

		}

		@Override
		public void onclose() {

		}

	}

	public static class NewDataSheetAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2849968538194965679L;

		@Override
		public void actionPerformed(ActionEvent e) {
			BProject project = Application.getInstance().getCurrentProject();
			BeeActions.addNewBookWidthData(project);
		}

		@Override
		public void onclose() {

		}

	}

	public static class CodeConfigAction extends BasicAction implements BeeDialogCloseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2849968538194965679L;

		@Override
		public void actionPerformed(ActionEvent e) {
			BProject project = Application.getInstance().getCurrentProject();
			CodeConfig config = new CodeConfig(project);
			config.setVisible(true);
		}

		@Override
		public void onclose() {

		}

	}

}
