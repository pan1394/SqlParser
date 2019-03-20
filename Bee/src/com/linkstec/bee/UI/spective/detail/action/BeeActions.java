package com.linkstec.bee.UI.spective.detail.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeEditor;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.DefaultFileFilter;
import com.linkstec.bee.UI.look.filechooser.BeeFileChooser;
import com.linkstec.bee.UI.look.menu.BeeMemuDisableListener;
import com.linkstec.bee.UI.look.menu.BeeMenuItem;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.MethodNode;
import com.linkstec.bee.UI.spective.code.tree.BeeSourceTransferable;
import com.linkstec.bee.UI.spective.detail.BeeDataSheet;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.EditorWorkSpace;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BeeName;
import com.linkstec.bee.core.codec.util.BeeNamingUtil;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;

public class BeeActions {

	public static Action bind(String name, final Action action) {
		return bind(name, action, (String) null);
	}

	public static Action bind(String name, final Action action, String iconUrl) {
		return bind(name, action, iconUrl, Application.getInstance().getEditor());
	}

	public static Action bind(String name, final Action action, ImageIcon icon, BeeMemuDisableListener listener) {

		AbstractAction newAction = new AbstractAction(name, icon) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 8196768062499302415L;

			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(
						new ActionEvent(Application.getInstance().getEditor(), e.getID(), e.getActionCommand()));
			}
		};
		if (action != null) {

			newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		}
		if (listener != null) {
			newAction.putValue(BeeMenuItem.DISABLE_LISTENER, listener);
		}
		return newAction;
	}

	public static Action bind(String name, final Action action, String iconUrl, JComponent comp) {
		ImageIcon icon = null;
		if (iconUrl != null) {
			icon = new ImageIcon(BeeEditor.class.getResource(iconUrl)) {

				/**
				 * 
				 */
				private static final long serialVersionUID = -884104641326358975L;
				private int imageHeight = (int) (BeeUIUtils.getDefaultFontSize() * 1.2);

				@Override
				public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
					g.drawImage(this.getImage(), x, y, imageHeight, imageHeight, c);
				}

				@Override
				public int getIconWidth() {
					return imageHeight;
				}

				@Override
				public int getIconHeight() {

					return imageHeight;
				}

			};

		}
		AbstractAction newAction = new AbstractAction(name, icon) {

			/**
					 * 
					 */
			private static final long serialVersionUID = 8196768062499302415L;

			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(comp, e.getID(), e.getActionCommand()));
			}

		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

		return newAction;
	}

	public static int getClostCellIndex(Object[] objs, mxCell parent) {
		if (objs != null && objs.length > 0) {

			double closedY = 100000;
			for (Object o : objs) {
				if (o instanceof BasicNode) {
					BasicNode bo = (BasicNode) o;
					if (bo.getGeometry().isRelative()) {
						if (bo.getGeometry().getOffset() != null) {
							closedY = Math.min(closedY, bo.getGeometry().getOffset().getY());
						}
					} else {
						closedY = Math.min(closedY, bo.getGeometry().getY());
					}
				}
			}

			int count = parent.getChildCount();
			for (int i = 0; i < count; i++) {
				mxICell tc = parent.getChildAt(i);
				if (tc instanceof BasicNode) {
					BasicNode bo = (BasicNode) tc;
					if (parent instanceof BasicNode) {
						if (bo.getGeometry().getOffset() != null) {
							if (bo.getGeometry().getOffset().getY() == closedY) {
								return i;
							}
						}
					} else {
						// if it is dropped into root
						if (bo.getGeometry().getY() == closedY) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	public static BeeTabbedPane getContainer(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof BeeTabbedPane) {
			return (BeeTabbedPane) obj;
		}
		if (obj instanceof JComponent) {
			JComponent cop = (JComponent) obj;
			Container parent = cop.getParent();
			if (parent != null) {
				return getContainer(parent);
			}

		}
		return null;
	}

	public static File saveModelWidoutThead(String filename, BookModel model, BProject project) {

		model.setModified(false);

		String ppath = null;
		List<BEditorModel> list = model.getList();
		for (BEditorModel m : list) {

			BClass bee = (BClass) m;
			String pack = bee.getPackage();
			if (pack != null) {
				ppath = pack.replace('.', File.separatorChar);
				ppath = project.getDesignPath() + File.separator + ppath;
				File dir = new File(ppath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(filename);
				filename = ppath + File.separator + file.getName();

				break;
			} else {
				File file = new File(filename);
				filename = project.getDesignPath() + File.separator + file.getName();
			}
		}

		if (!filename.endsWith(".bee")) {
			filename = filename + ".bee";
		}

		File f = new File(filename);
		boolean exist = f.exists();
		if (exist) {
			f.delete();
		}

		try {

			ObjectFileUtils.writeObject(f, model);

			if (!exist) {
				Application.getInstance().getDesignSpective().getFileExplore().updateProject(project);
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, f.getAbsolutePath() + "グラフの保存が失敗しました。", "変換エラー", JOptionPane.OK_OPTION);
			e1.printStackTrace();
		}

		return f;

	}

	public static File saveModel(String filename, BookModel model, BProject project) {

		model.setModified(false);

		String ppath = null;
		List<BEditorModel> list = model.getList();
		for (BEditorModel m : list) {

			BClass bee = (BClass) m;
			String pack = bee.getPackage();
			if (pack != null) {
				ppath = pack.replace('.', File.separatorChar);
				ppath = project.getDesignPath() + File.separator + ppath;
				File dir = new File(ppath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(filename);
				filename = ppath + File.separator + file.getName();

				break;
			} else {
				File file = new File(filename);
				filename = project.getDesignPath() + File.separator + file.getName();
			}
		}

		if (!filename.endsWith(".bee")) {
			filename = filename + ".bee";
		}

		File f = new File(filename);
		boolean exist = f.exists();
		if (exist) {
			f.delete();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ObjectFileUtils.writeObject(f, model);

					if (!exist) {
						Application.getInstance().getDesignSpective().getFileExplore().updateProject(project);
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, f.getAbsolutePath() + "グラフの保存が失敗しました。", "変換エラー",
							JOptionPane.OK_OPTION);
					e1.printStackTrace();
				}

			}

		}).start();

		return f;

	}

	public static File export(BEditor editor) {
		HashSet<String> formats = new HashSet<String>();
		JFileChooser fc = new JFileChooser();
		Object[] imageFormats = ImageIO.getReaderFormatNames();

		String ext = "jpg";
		formats.add(ext);

		imageFormats = formats.toArray();

		fc.addChoosableFileFilter(new DefaultFileFilter("." + ext,
				ext.toUpperCase() + " " + mxResources.get("file") + " (." + ext + ")"));

		// Adds filter that accepts all supported image formats
		fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(mxResources.get("allImages")));

		Color bg = null;

		EditorBook set = (EditorBook) editor;
		int count = set.getTabCount();
		fc.showDialog(null, mxResources.get("save"));

		String filename = fc.getSelectedFile().getAbsolutePath();
		if (count != 0) {

			File file = new File(filename);
			File dir = file.getParentFile();
			dir.mkdir();

			for (int i = 0; i < count; i++) {
				Component comp = set.getComponentAt(i);

				if (comp instanceof BeeGraphSheet) {
					BeeGraphSheet beeGraph = (BeeGraphSheet) comp;
					String realName = dir.getAbsolutePath() + File.separator + beeGraph.getTitleLabel() + "." + ext;
					BufferedImage image = null;
					if (comp.getClass().equals(BeeGraphSheet.class)) {
						image = mxCellRenderer.createBufferedImage(beeGraph.getGraph(), null, 1, bg,
								beeGraph.isAntiAlias(), null, beeGraph.getCanvas());

						if (image != null) {
							try {
								ImageIO.write(image, ext, new File(realName));
							} catch (IOException e) {

								e.printStackTrace();
							}
						} else {
							JOptionPane.showMessageDialog((Component) editor, mxResources.get("noImageData"));
						}

					} else if (comp.getClass().equals(BeeDataSheet.class)) {
						BeeDataSheet data = (BeeDataSheet) comp;

						realName = dir.getAbsolutePath() + File.separator + data.getTitleLabel() + "[DATA]." + ext;
						try {
							ImageIO.write(data.getExportImage(), ext, new File(realName));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}

		return null;
	}

	public static File SaveAs(BEditor editor) {
		if (editor != null && editor.getFile() != null) {

			FileFilter selectedFilter = null;
			String filename = null;

			String lastDir = editor.getFile().getParentFile().getAbsolutePath();

			BeeFileChooser fc = new BeeFileChooser(lastDir);
			fc.addChoosableFileFilter(new DefaultFileFilter(".bee", "Bee " + mxResources.get("file") + " (.bee)"));

			// Adds filter that accepts all supported image formats
			fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(mxResources.get("allImages")));
			// fc.setFileFilter(defaultFilter);
			File defaultFile = new File(lastDir + File.separator + editor.getLogicName() + ".bee");
			fc.setSelectedFile(defaultFile);
			int r = fc.showDialog(null, mxResources.get("save"));
			if (r != BeeFileChooser.APPROVE_OPTION) {
				return editor.getFile();
			}

			filename = fc.getSelectedFile().getAbsolutePath();
			selectedFilter = fc.getFileFilter();

			if (selectedFilter instanceof DefaultFileFilter) {
				String ext = ((DefaultFileFilter) selectedFilter).getExtension();

				if (!filename.toLowerCase().endsWith(ext)) {
					filename += ext;
				}
			}

			if (new File(filename).exists() && JOptionPane.showConfirmDialog((Component) editor,
					mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
				return null;
			}

			try {
				String ext = filename.substring(filename.lastIndexOf('.') + 1);

				if (ext.equalsIgnoreCase("bee")) {
					EditorBook book = null;
					if (editor instanceof EditorBook) {
						book = (EditorBook) editor;
					} else {
						book = BeeActions.findBook(editor.getContents());
					}
					if (book != null) {
						book.setFile(new File(filename));
						Component[] comps = book.getComponents();
						int i = 0;
						for (Component comp : comps) {
							BClass bclass = null;
							if (comp instanceof BeeDataSheet) {
								BeeDataSheet data = (BeeDataSheet) comp;
								bclass = data.getModel();
							} else if (comp instanceof BeeGraphSheet) {
								BeeGraphSheet graph = (BeeGraphSheet) comp;

								bclass = graph.getModel();
							}
							if (bclass != null) {

								String pack = filename.substring(editor.getProject().getDesignPath().length() + 1);
								pack = pack.substring(0, pack.lastIndexOf('.'));
								pack = pack.replace(File.separatorChar, '.');
								bclass.setPackage(pack);

								if (!bclass.isInnerClass() && i == 0) {
									String className = filename.substring(filename.lastIndexOf(File.separatorChar) + 1);
									className = className.substring(0, className.lastIndexOf("."));
									bclass.setLogicName(className);
									// bclass.setName(className);
									i++;
								}
								editor.updateView();
							}
						}
						File f = BeeActions.saveModel(filename, BeeActions.getBookModel(book), editor.getProject());
						editor.setModified(false);
						return f;
					}

				} else if (ext.equalsIgnoreCase("java")) {

				}
			} catch (Throwable ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog((Component) editor, ex.toString(), mxResources.get("error"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}

	public static BeeGraphSheet getSheetByModel(BeeModel model) {
		EditorWorkSpace space = (EditorWorkSpace) Application.getInstance().getDesignSpective().getWorkspace();
		int count = space.getTabCount();
		for (int i = 0; i < count; i++) {
			Component comp = space.getComponentAt(i);
			if (comp instanceof EditorBook) {
				EditorBook book = (EditorBook) comp;
				int cou = book.getTabCount();
				for (int j = 0; j < cou; j++) {
					Component c = book.getComponentAt(j);
					if (c instanceof BeeGraphSheet) {
						BeeGraphSheet sheet = (BeeGraphSheet) c;
						if (sheet.getGraph().getModel().equals(model)) {
							return sheet;
						}
					}
				}
			}
		}
		return null;
	}

	public static MethodNode getSegmentParent(mxICell cell) {
		if (cell instanceof MethodNode) {
			return (MethodNode) cell;
		}
		if (cell.getParent() == null) {
			return null;
		}
		return getSegmentParent(cell.getParent());
	}

	public static double getXfromChild(mxICell parent, mxICell child, double x) {
		if (child.getGeometry().getOffset() == null) {
			x = child.getGeometry().getX();
		} else {
			x = child.getGeometry().getOffset().getX();
		}
		mxICell cell = child.getParent();
		if (cell == null) {
			return x;
		}
		if (cell.equals(parent)) {
			return x;
		} else {
			return x + getXfromChild(parent, cell, x);
		}
	}

	public static mxPoint getTranslateToRoot(mxICell child) {

		if (child.getGeometry() == null) {
			return new mxPoint(0, 0);
		}
		mxPoint offset = child.getGeometry().getOffset();
		mxICell parent = child.getParent();
		if (parent == null) {
			if (offset != null) {
				if (child.getGeometry().isRelative()) {
					return offset;
				}
			}
			return new mxPoint(child.getGeometry().getX(), child.getGeometry().getY());
		} else {
			mxPoint p = getTranslateToRoot(child.getParent());
			if (offset != null) {
				if (child.getGeometry().isRelative()) {
					return new mxPoint(p.getX() + offset.getX(), p.getY() + offset.getY());
				}
			}
			return new mxPoint(p.getX() + child.getGeometry().getX(), p.getY() + child.getGeometry().getY());
		}
	}

	public static double getYfromChild(mxICell parent, mxICell child, double y) {
		if (child.getGeometry().getOffset() == null) {
			y = child.getGeometry().getY();
		} else {
			y = child.getGeometry().getOffset().getY();
		}
		mxICell cell = child.getParent();
		if (cell == null) {
			return y;
		}
		if (cell.equals(parent)) {
			return y;
		} else {
			return y + getYfromChild(parent, cell, y);
		}
	}

	public static IUnit getUnit(mxICell cell) {

		if (cell instanceof IUnit) {
			return (IUnit) cell;
		}

		if (cell.getParent() != null) {
			return getUnit(cell.getParent());
		}

		return null;
	}

	public static void addNewSheet(BProject project) {
		EditorBook book = (EditorBook) Application.getInstance().getDesignSpective().getWorkspace().getCurrentEditor();
		if (book != null) {
			BeeGraphSheet c = new BeeGraphSheet(project);
			BeeName name = BeeNamingUtil.makeName(c.getModel(), BeeName.TYPE_CLASS);
			c.getModel().setName(name.getName());
			c.getModel().setLogicName(name.getLogicName());

			BookModel bm = book.getBookModel();
			if (bm.getList().size() > 0) {
				BEditorModel m = bm.getList().get(0);
				BClass b = (BClass) m;
				c.getModel().setPackage(b.getPackage());
			}

			book.addEditor(c);
		}
	}

	public static void addNewDataSheet(BProject project) {
		EditorBook book = (EditorBook) Application.getInstance().getDesignSpective().getWorkspace().getCurrentEditor();
		if (book != null) {
			BeeDataSheet c = new BeeDataSheet(project);
			BeeName name = BeeNamingUtil.makeName(c.getModel(), BeeName.TYPE_CLASS);
			c.getModel().setName(name.getName());
			c.getModel().setLogicName(name.getLogicName());
			book.addEditor(c);

			BookModel bm = book.getBookModel();
			if (bm.getList().size() > 0) {
				BClass model = (BClass) bm.getList().get(0);
				c.getModel().setPackage(model.getPackage());
			}
		}

	}

	public static void addNewBookWidthGraph(BProject project) {
		// BeeGraphSheet c = new BeeGraphSheet(project);

		BEditor editor = Application.getInstance().getDesignSpective().createEditor(project);
		Application.getInstance().getDesignSpective().getWorkspace().addEditor(editor);
	}

	public static void addNewBookWidthData(BProject project) {
		BeeDataSheet c = new BeeDataSheet(project);

		BeeName name = BeeNamingUtil.makeName(c.getModel(), BeeName.TYPE_CLASS);
		c.getModel().setName(name.getName());
		c.getModel().setLogicName(name.getLogicName());

		BookModel bm = new BookModel();

		BeeName bookName = BeeNamingUtil.makeBookName();
		bm.setName(bookName.getName());
		bm.setLogicName(bookName.getLogicName());
		bm.getList().add(c.getModel());

		EditorBook book = new EditorBook(bm, project, Application.getInstance().getDesignSpective().getWorkspace());
		book.insertTab(name.getLogicName(), c.getImageIcon(), c, name.getLogicName(), 0);

		BClassHeader header = PatternCreatorFactory.createView().craeteClassHeader();
		header.setBClass(c.getModel());

		Application.getInstance().getDesignSpective().getWorkspace().addEditor(book);

	}

	public static void addNewBook(BProject project) {

		BEditor editor = Application.getInstance().getDesignSpective().createEditor(project);
		Application.getInstance().getDesignSpective().getWorkspace().addEditor(editor);

	}

	public static void showDetailPane(Component comp) {
		int index = Application.getInstance().getDesignSpective().getWorkspace().indexOfComponent(comp);
		if (index != -1) {
			Application.getInstance().getDesignSpective().getWorkspace().setCurrentComponent(index);
		}
	}

	public static void addDetailPane(File f, BProject project) {
		if (f != null && f.exists() && !f.isDirectory()) {
			Application.getInstance().getEditor().getStatusBar().startProgress("ファイルを開いています...");

			try {

				Object obj = ObjectFileUtils.readObject(f);

				if (obj instanceof BookModel) {
					BookModel b = (BookModel) obj;

					BEditor editor = b.getBook(b, project, f,
							Application.getInstance().getDesignSpective().getWorkspace());
					Application.getInstance().getDesignSpective().getWorkspace().addEditor(editor);

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Application.getInstance().getEditor().getStatusBar().endProgress();
			}
		}
	}

	public static void addDetailPane(BeeTreeFileNode node, BProject project) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				addDetailPane(new File(node.getFilePath()), project);
			}

		}).start();

	}

	public static void setTransfer(JComponent comp) {
		DragGestureListener dragGestureListener = new DragGestureListener() {
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e) {
				Component comp = e.getComponent();
				Object obj = null;
				mxCell target = null;
				if (comp instanceof JTree) {
					JTree tree = (JTree) comp;
					TreePath path = tree.getSelectionPath();
					if (path != null) {
						BeeTreeNode dragTreeNode = (BeeTreeNode) path.getLastPathComponent();
						if (dragTreeNode != null) {

							target = dragTreeNode.getTransferNode();
							if (target == null) {
								obj = dragTreeNode.getUserObject();

							}
						}
					}
				}
				if (target != null) {
					mxRectangle bounds = (mxGeometry) target.getGeometry().clone();
					BeeTransferable t = new BeeTransferable(new Object[] { target }, bounds);
					e.startDrag(DragSource.DefaultMoveDrop, mxSwingConstants.EMPTY_IMAGE, new Point(), t,
							new DragAndDropDragSourceListener());

				}
				if (obj != null) {
					if (obj instanceof File) {
						File f = (File) obj;
						BeeSourceTransferable t = new BeeSourceTransferable(f.getAbsolutePath());
						e.startDrag(DragSource.DefaultMoveDrop, mxSwingConstants.EMPTY_IMAGE, new Point(), t,
								new DragAndDropDragSourceListener());
					}
				}

			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_COPY, dragGestureListener);
	}

	public static class DragAndDropDragSourceListener implements DragSourceListener {

		@Override
		public void dragDropEnd(DragSourceDropEvent e) {
			if (!e.getDropSuccess() || e.getDropAction() != DnDConstants.ACTION_MOVE) {
				return;
			}

			DragSourceContext context = e.getDragSourceContext();
			Object comp = context.getComponent();
			if (comp == null || !(comp instanceof JTree)) {
				return;
			}

		}

		@Override
		public void dragEnter(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			int dropAction = e.getDropAction();
			if ((dropAction & DnDConstants.ACTION_COPY) != 0) {
				context.setCursor(DragSource.DefaultCopyDrop);
			} else if ((dropAction & DnDConstants.ACTION_MOVE) != 0) {
				context.setCursor(DragSource.DefaultMoveDrop);
			} else {
				context.setCursor(DragSource.DefaultCopyNoDrop);
			}

		}

		@Override
		public void dragExit(DragSourceEvent arg0) {

		}

		@Override
		public void dragOver(DragSourceDragEvent arg0) {

		}

		@Override
		public void dropActionChanged(DragSourceDragEvent arg0) {

		}

	}

	public static boolean isValidDropTarget(Object cell, Object[] cells) {
		if (cells == null) {
			return false;
		}
		if (cell instanceof BasicNode) {
			BasicNode b = (BasicNode) cell;
			if (cells.length == 1) {
				if (cells[0] instanceof BasicNode)
					return b.isDropTarget((BasicNode) cells[0]);
			} else {
				return false;
			}

		}
		return false;
	}

	public static BookModel getBookModel(EditorBook book) {
		BookModel model = new BookModel();

		List<BEditor> list = book.getList();
		boolean set = false;
		for (BEditor comp : list) {
			model.getList().add(comp.getEditorModel());
			comp.setFile(book.getFile());

			if (!set) {
				model.setName(comp.getEditorModel().getName());
				model.setLogicName(comp.getEditorModel().getLogicName());
				set = true;
			}
		}
		return model;
	}

	public static BeeTabCloseButton findPaneButton(Container comp, JComponent target) {
		if (comp instanceof BeeTabbedPane) {
			BeeTabbedPane pane = (BeeTabbedPane) comp;
			int count = pane.getTabCount();
			for (int i = 0; i < count; i++) {
				Component sub = pane.getComponentAt(i);
				if (sub.equals(target)) {
					BeeTabCloseButton button = (BeeTabCloseButton) pane.getTabComponentAt(i);
					return button;
				}
			}
		}
		if (comp == null) {
			return null;
		}
		Container parent = comp.getParent();
		if (parent != null) {
			return findPaneButton(parent, (JComponent) comp);
		}
		return null;
	}

	public static EditorBook findBook(JComponent compent) {
		Container parent = compent.getParent();
		while (parent != null && !(parent instanceof EditorBook)) {
			parent = parent.getParent();
		}
		return (EditorBook) parent;
	}

	public static EditorBook getBookByModel(BookModel model, BProject project, BWorkSpace space) {
		EditorBook book = new EditorBook(model, project, space);
		List<BEditorModel> list = model.getList();
		int i = 0;

		for (BEditorModel m : list) {
			BEditor isheet = m.getSheet(project);
			book.insertTab(isheet.getLogicName(), isheet.getImageIcon(), (Component) isheet, isheet.getLogicName(), i);
			i++;

		}
		book.setTitleLabel(model.getName());
		return book;
	}

	public static mxICell foundRoot(mxICell node) {
		int count = node.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell obj = node.getChildAt(i);
			if (obj instanceof BasicNode) {
				return node;
			} else {
				mxICell root = foundRoot(obj);
				if (root != null) {
					return root;
				}
			}
		}

		return null;
	}

}
