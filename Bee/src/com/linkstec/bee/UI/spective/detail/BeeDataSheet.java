package com.linkstec.bee.UI.spective.detail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.JTableHeader;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorManager;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.scroll.BeeScrollPane;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.table.BeeTable;
import com.linkstec.bee.UI.look.table.BeeTable.PopListener;
import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.look.table.BeeTablePopupMenu;
import com.linkstec.bee.UI.look.table.BeeTableUndo;
import com.linkstec.bee.UI.look.table.BeeTableUndo.UndoListener;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.action.BeeDataTransferHandler;
import com.linkstec.bee.UI.spective.detail.action.EditorActions.SaveAction;
import com.linkstec.bee.UI.spective.detail.data.BeeDataHeader;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.UI.thread.BeeListedThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeDataSheet extends BeeScrollPane implements Printable, IBeeTitleUI, BEditor, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3735009807909352289L;

	private BeeDataModel model;
	private BeeTable table;
	private File file;

	private boolean headerShown = true;
	private Rectangle headerButton = null;
	private BeeDataHeader header;

	private BProject project;
	private List<TitleChangeListener> titleChangeListeners = new ArrayList<TitleChangeListener>();
	private BEditorManager manager;
	private String id;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		if (file == null) {
			return;
		}
		String name = file.getName();
		if (name.indexOf('.') > 0) {
			name = name.substring(0, name.indexOf('.'));
			this.setTitleLabel(name);
		}
	}

	public BeeDataSheet(BProject project) {
		this(new BeeDataModel(), project);
		this.manager = new BEditorManager(this, this.getModel().getUndo());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		JScrollBar bar = this.getVerticalScrollBar();
		JTableHeader header = this.table.getTableHeader();

		Point point = bar.getLocation();
		g.setColor(BeeTable.backgroundColor);
		g.fillRect(point.x, point.y - header.getHeight(), bar.getWidth(), header.getHeight());
		headerButton = new Rectangle(point.x, point.y - header.getHeight(), bar.getWidth(), header.getHeight());

		Image img = BeeConstants.PROPERTY_ICON.getImage();

		int h = img.getHeight(this);
		int w = img.getWidth(this);
		int x = point.x + (bar.getWidth() - w) / 2;
		int y = point.y - header.getHeight() + (header.getHeight() - h) / 2;
		g.drawImage(img, x, y, this);

	}

	public BeeDataSheet(BeeDataModel model, BProject project) {
		this(new BeeTable(model));
		header = new BeeDataHeader(model, this);
		this.add(header);
		this.setBorder(header);
		this.project = project;
		model.setErrorListener(this);
		this.model = model;
		this.manager = new BEditorManager(this, this.getModel().getUndo());

		model.getUndo().setListener(new UndoListener() {

			@Override
			public void undoOcurred() {
				setModified(true);
			}

		});

		model.addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				setModified(true);

			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
				setModified(true);

			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
				setModified(true);

			}

			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				setModified(true);

			}

		});

	}

	private BeeDataSheet(Component view) {
		super(view);

		table = (BeeTable) view;
		this.addMouseListener(this);

		PopListener lisetener = new PopListener() {

			@Override
			public void beforeShowup(BeeTablePopupMenu menu, int[] column, int[] rows) {
				if (rows.length == 1) {
					int row = rows[0];
					Object obj = table.getModel().getValueAt(row, 3);
					if (obj instanceof BClass) {
						BClass bclass = (BClass) obj;
						List<BType> types = bclass.getParameterizedTypes();
						if (types.size() > 0) {
							BeeTableNode node = (BeeTableNode) table.getValueAt(row, 0);
							int gap = types.size() - node.getChildCount();
							if (gap > 0) {
								menu.addSeparator();
								menu.addPopMenu("タイプ行を追加する", new AbstractAction() {
									/**
									 * 
									 */
									private static final long serialVersionUID = 4331620115224626093L;

									@Override
									public void actionPerformed(ActionEvent e) {

										for (int i = 0; i < gap; i++) {
											BeeTableNode n = new BeeTableNode(node, node.getColumnCount());
											n.setValueAt("Parameter" + (i + 1), 1);
											n.setValueAt("Parameter" + (i + 1), 2);
											n.setValueAt(CodecUtils.BObject().cloneAll(), 3);
											node.addChild(n);
											table.repaint();
										}
									}
								}, BeeConstants.ADD_ICON);
							}
						}
					}
				}
			}

		};
		// ,BeeConstants.ADD_ICON);

		table.setListener(lisetener);

		KeyStroke save = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK, false);
		table.registerKeyboardAction(new SaveAction(false), "save", save, JComponent.WHEN_FOCUSED);
		this.id = BeeUIUtils.createID();
		new BeeDataTransferHandler(table);
	}

	public BeeDataModel getModel() {
		return this.model;
	}

	public void setModel(BeeDataModel model) {
		model.initialize(model.getRoot(), BeeDataModel.names, BeeDataModel.types);
		table = new BeeTable(model);
		BeeTableUndo undo = new BeeTableUndo(table);
		model.setUndo(undo);
		this.getViewport().removeAll();
		this.getViewport().add(table);
	}

	@Override
	public int print(Graphics g, PageFormat f, int pageIndex) throws PrinterException {
		// Disables double-buffering before printing
		// RepaintManager currentManager = RepaintManager.currentManager(this.table);
		// currentManager.setDoubleBufferingEnabled(false);

		return this.manager.print(g, f, pageIndex, this.table);
	}

	@Override
	public String getTitleLabel() {
		return this.model.getName();
	}

	@Override
	public void setTitleLabel(String title) {
		this.model.setName(title);
		for (TitleChangeListener listener : titleChangeListeners) {
			listener.change(this);
		}
	}

	@Override
	public void setTitleWithOutListenerAction(String title) {
		this.model.setName(title);
	}

	@Override
	public void addTitleChangeListener(TitleChangeListener listener) {
		this.titleChangeListeners.add(listener);

	}

	public BufferedImage getExportImage() {
		BufferedImage tag = new BufferedImage(700, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics g = tag.getGraphics();
		this.setOpaque(true);

		JPanel container = new JPanel();
		container.setSize(700, 1000);

		container.setLayout(new BorderLayout());
		container.add(table.getTableHeader(), BorderLayout.PAGE_START);
		container.add(table, BorderLayout.CENTER);
		container.doLayout();
		container.setOpaque(true);

		container.setBackground(Color.GREEN);
		this.doLayout();

		container.paint(g);

		return tag;
	}

	public void setModified(boolean modified) {
		BeeTabCloseButton button = BeeActions.findPaneButton((JComponent) this.getParent(), this);
		if (button != null) {
			button.setModified(modified);
			if (modified) {
				BeeTabCloseButton b = BeeActions.findPaneButton(this.getParent().getParent(),
						(JComponent) this.getParent());
				b.setModified(true);
			}
		}
		new BeeListedThread(new Runnable() {

			@Override
			public void run() {
				getOutlookExplore().update();
			}
		});

	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.SHEET_DATA_ICON;
	}

	@Override
	public boolean isModified() {
		BeeTabCloseButton button = BeeActions.findPaneButton((JComponent) this.getParent(), this);
		if (button != null) {
			return button.isModified();
		} else {
			return false;
		}

	}

	@Override
	public void windowDeactived() {

	}

	@Override
	public void zoom(double scale) {

	}

	@Override
	public void beforeSave() {

	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public BProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(BProject project) {
		this.project = project;
	}

	@Override
	public String getDisplayPath() {
		EditorBook book = this.findBook();
		if (book != null) {
			return book.getDisplayPath();
		}
		if (this.file != null) {
			String root = this.project.getDesignPath();
			String path = file.getAbsolutePath().substring(root.length() + 1);
			return project.getName() + "/" + path.replace(File.separatorChar, '/');
		}
		return null;
	}

	@Override
	public BEditorFileExplorer getFileExplore() {
		return Application.getInstance().getDesignSpective().getFileExplore();
	}

	@Override
	public BEditorOutlookExplorer getOutlookExplore() {
		return Application.getInstance().getDesignSpective().getOutline();
	}

	@Override
	public void refresh() {
		File f = this.getFile();
		if (f != null) {
			Application.getInstance().getDesignSpective().openFile(f, this.project);
		}
	}

	@Override
	public File save() {
		EditorBook book = this.findBook();
		this.file = book.save();
		return this.file;
	}

	public EditorBook findBook() {
		Container parent = this.getParent();
		while (parent != null && !(parent instanceof EditorBook)) {
			parent = parent.getParent();
		}
		return (EditorBook) parent;
	}

	@Override
	public BEditorManager getManager() {
		return this.manager;
	}

	@Override
	public JComponent getContents() {
		return this.table;
	}

	@Override
	public void removeErrorLine(Object cell) {

	}

	@Override
	public void makeTabPopupItems(BManager manager) {
		manager.addPopupItem("ソースへ変換dasdfafd", BeeConstants.GENERATE_CODE_ICON,
				new CodecAction.GenerateSourceSingle(this.file, this.project));
	}

	@Override
	public List<TitleChangeListener> getTitleChangeListeners() {
		return this.titleChangeListeners;
	}

	@Override
	public void saveAs(ActionEvent e) {
		EditorBook book = this.findBook();
		book.saveAs(e);
	}

	@Override
	public void deleteSelect(ActionEvent e) {

	}

	@Override
	public void selectAll(ActionEvent e) {
		table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
		table.setRowSelectionInterval(0, table.getRowCount() - 1);
	}

	@Override
	public String getLogicName() {
		return this.model.getLogicName();
	}

	@Override
	public void onSelected() {
		Application.getInstance().setCurrentEditor(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (this.headerButton != null && this.headerButton.contains(e.getPoint())) {
			if (this.headerShown) {
				headerShown = false;
				this.header.setVisible(false);
			} else {
				headerShown = true;
				this.header.setVisible(true);
			}
			this.updateUI();
			e.consume();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void updateView() {
		this.table.updateUI();

	}

	protected PageFormat pageFormat = new PageFormat();

	@Override
	public PageFormat getPageFormat() {

		return pageFormat;
	}

	@Override
	public void setPageFormat(PageFormat format) {
		this.pageFormat = format;

	}

	@Override
	public BEditorModel getEditorModel() {
		return this.model;
	}
}
