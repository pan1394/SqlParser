package com.linkstec.bee.UI.spective;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.accordion.BeeAccordion;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.EditorWorkSpace;
import com.linkstec.bee.UI.spective.detail.VerifyClass;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.logic.BuloonMenu;
import com.linkstec.bee.UI.spective.detail.logic.LogicMenu;
import com.linkstec.bee.UI.spective.detail.tree.ClassesTree;
import com.linkstec.bee.UI.spective.detail.tree.EditorExloprer;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BeeName;
import com.linkstec.bee.core.codec.util.BeeNamingUtil;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxResources;

public class BeeDetailSpective extends BeeSpective {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6948732008993898726L;

	protected mxGraphOutline graphOutline;
	protected BeeAccordion userActionPane;
	protected BeeAccordion resourcePane;
	protected JSplitPane left, middle, right, all, work, menu;

	public BeeDetailSpective(BeeConfig config) {
		super(config);
		userActionPane = new BeeAccordion();
		resourcePane = new BeeAccordion();
		graphOutline = new mxGraphOutline(null) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5230490330767474869L;

			@Override
			public void updateFinder(boolean repaint) {
				if (this.graphComponent != null) {
					super.updateFinder(repaint);
				}
			}

		};
		installOutLineMouseListener();
		this.layout();
	}

	public void installOutLineMouseListener() {
		// Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener() {
			/**
			 * 
			 */
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
					BEditor editor = workSpace.getCurrentEditor();

					if (editor instanceof EditorBook) {
						EditorBook book = (EditorBook) editor;
						BEditor b = book.getCurrentEditor();
						if (b instanceof BeeGraphSheet) {
							BeeGraphSheet sheet = (BeeGraphSheet) b;
							sheet.mouseWheelMoved(e);
						}
					}

				}
			}

		};
		graphOutline.addMouseWheelListener(wheelTracker);
		graphOutline.addMouseListener(new MouseAdapter() {

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e) {
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showOutlinePopupMenu(e);
				}
			}

		});
	}

	public void layout() {
		explorer = new EditorExloprer(config);

		menu = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.userActionPane, this.resourcePane);
		menu.setDividerSize(BeeConstants.DEVIDER_SIZE);
		menu.setBorder(null);
		menu.setContinuousLayout(true);

		left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, graphOutline);
		left.setDividerSize(BeeConstants.DEVIDER_SIZE);
		left.setBorder(null);
		left.setContinuousLayout(true);

		work = new JSplitPane(JSplitPane.VERTICAL_SPLIT, workSpace, tasks);
		work.setDividerSize(BeeConstants.DEVIDER_SIZE);
		work.setBorder(null);
		work.setContinuousLayout(true);

		middle = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, work);
		middle.setDividerSize(BeeConstants.DEVIDER_SIZE);
		middle.setBorder(null);
		middle.setContinuousLayout(true);

		all = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, middle, explorer);
		all.setDividerSize(BeeConstants.DEVIDER_SIZE);
		all.setBorder(null);
		all.setContinuousLayout(true);

		this.add(all, BorderLayout.CENTER);
		this.doLayout();

	}

	public void doLayout() {

		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		this.setPreferredSize(new Dimension((int) (s.getWidth()), (int) (s.getHeight())));
		int w = s.width;
		int h = s.height;
		all.setSize(this.getSize());
		all.setDividerLocation((int) (0.8 * w));
		middle.setDividerLocation((int) (w * 0.15));
		left.setDividerLocation((int) (0.65 * h));
		menu.setDividerLocation((int) (0.25 * h));
		work.setDividerLocation((int) (0.65 * h));
	}

	public BeeGraphSheet getGraphSheet() {
		BEditor editor = workSpace.getCurrentEditor();

		if (editor instanceof EditorBook) {
			EditorBook book = (EditorBook) editor;
			BEditor b = book.getCurrentEditor();
			if (b instanceof BeeGraphSheet) {
				BeeGraphSheet sheet = (BeeGraphSheet) b;
				return sheet;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	protected void showOutlinePopupMenu(MouseEvent e) {

		BeeGraphSheet sheet = this.getGraphSheet();
		if (sheet != null) {
			Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), sheet);
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(mxResources.get("magnifyPage"));
			item.setSelected(graphOutline.isFitPage());

			item.addActionListener(new ActionListener() {
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e) {
					graphOutline.setFitPage(!graphOutline.isFitPage());
					graphOutline.repaint();
				}
			});

			JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(mxResources.get("showLabels"));
			item2.setSelected(graphOutline.isDrawLabels());

			item2.addActionListener(new ActionListener() {
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e) {
					graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
					graphOutline.repaint();
				}
			});

			JPopupMenu menu = new JPopupMenu();
			menu.add(item);
			menu.add(item2);

			menu.show(sheet, pt.x, pt.y);

			e.consume();
		}
	}

	/**
	 * 
	 */
	public mxGraphOutline getGraphOutline() {
		return graphOutline;
	}

	/**
	 * 
	 */
	public BeeAccordion getUserActionPane() {
		return this.userActionPane;
	}

	public BeeAccordion getResourcePane() {
		return this.resourcePane;
	}

	public void installUserActionMenu(BProject project) {

		if (this.userActionPane.getProject() != null && this.userActionPane.getProject().equals(project)) {
			return;
		}

		userActionPane.setProject(project);

		int count = this.userActionPane.getTabCount();
		for (int i = count - 1; i >= 0; i--) {
			this.userActionPane.removeTab(i);
		}
		LogicMenu menu = new LogicMenu(project);
		this.userActionPane.addTab(mxResources.get("edit"), menu);
		this.userActionPane.revalidate();
		this.userActionPane.updateUI();
	}

	public void installResourceMenu(BProject project) {

		if (this.resourcePane.getProject() != null && this.resourcePane.getProject().equals(project)) {
			return;
		}

		resourcePane.setProject(project);

		int count = this.resourcePane.getTabCount();
		for (int i = count - 1; i >= 0; i--) {
			this.resourcePane.removeTab(i);
		}
		this.resourcePane.addTab("リソース", new ClassesTree(project));
		this.resourcePane.addTab("吹き出し", new BuloonMenu(project));
		this.resourcePane.revalidate();
		this.resourcePane.updateUI();
	}

	public void intializeOutline(BeeGraphSheet sheet) {
		this.graphOutline.setVisible(true);
		this.graphOutline.setPreferredSize(new Dimension(300, 300));
		this.graphOutline.setGraphComponent(sheet);
		this.graphOutline.updateFinder(true);
	}

	@Override
	public void openFile(File file, BProject project) {
		BeeActions.addDetailPane(file, project);
	}

	@Override
	public BEditorExplorer createExplorer() {
		return new EditorExloprer(config);
	}

	@Override
	public BWorkspace createWorkspace() {
		return new EditorWorkSpace(this);
	}

	@Override
	public BEditor createEditor(BProject project) {
		BeeGraphSheet c = new BeeGraphSheet(project);

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

		mxICell root = (mxICell) c.getGraph().getDefaultParent();
		root.insert((mxICell) header);

		LayoutUtils.RelayoutAll(c);
		return book;
	}

	@Override
	public void editorAdded(BEditor editor) {
		if (editor instanceof EditorBook) {
			EditorBook book = (EditorBook) editor;

			List<BEditor> list = book.getList();

			((BEditor) book.getSelectedComponent()).onSelected();

			for (BEditor sheet : list) {
				sheet.setFile(book.getFile());
				if (sheet instanceof BeeGraphSheet) {
					new VerifyClass((BeeGraphSheet) sheet);
				}
			}
			this.getOutline().setEditor(book);
			this.installUserActionMenu(book.getProject());
			this.installResourceMenu(book.getProject());
		}
	}

}
