package com.linkstec.bee.UI.spective.detail;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.TransferHandler;

import org.w3c.dom.Document;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorManager;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BGeneratable;
import com.linkstec.bee.UI.BLayoutable;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeEditor;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeScrollUI;
import com.linkstec.bee.UI.look.scroll.BeeScrollPaneErrorActionListener;
import com.linkstec.bee.UI.look.scroll.BeeScrollPaneErrorPoint;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.IClassMember;
import com.linkstec.bee.UI.node.view.ILink;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.action.BeeGraphHandler;
import com.linkstec.bee.UI.spective.detail.action.BeeHandlers;
import com.linkstec.bee.UI.spective.detail.action.BeeHandlers.CellMoveHandler;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferHandler;
import com.linkstec.bee.UI.spective.detail.logic.BeeCanvas;
import com.linkstec.bee.UI.spective.detail.logic.BeeCellEditor;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraph;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraphBorder;
import com.linkstec.bee.UI.spective.detail.logic.BeeLoigcEditor;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.spective.detail.tip.DetailEditToolTip;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxGraphModel.mxChildChange;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.mxgraph.view.mxTemporaryCellStates;

public class BeeGraphSheet extends mxGraphComponent
		implements IBeeTitleUI, BeeScrollPaneErrorActionListener, BEditor, BLayoutable, BGeneratable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6833603133512882012L;

	// private File file;
	private BeeScrollUI UI;
	private String id;

	private BeeGraphBorder graphBorder;
	private BProject project;
	private DetailEditToolTip tooltip;
	private BeeLoigcEditor editor;

	public DetailEditToolTip getTooltip() {
		return tooltip;
	}

	public String getTitleLabel() {

		return ((BeeModel) this.getGraph().getModel()).getTitleLabel();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		this.editor.paintHandler(g);
	}

	@Override
	protected void paintBorder(Graphics g) {

	}

	public void setTitleLabel(String title) {
		((BeeModel) this.getGraph().getModel()).setTitleLabel(title);
	}

	public List<BasicNode> doSearch(String keyword) {
		List<BasicNode> list = this.getModel().doSearch(keyword);
		this.getGraph().setSelectionCells(list.toArray());
		return list;
	}

	/**
	 * 
	 */
	protected BEditorManager manager;

	/**
	 * 
	 */
	protected transient mxIEventListener undoHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {

			mxUndoableEdit edit = (mxUndoableEdit) evt.getProperty("edit");
			manager.getUndo().undoableEditHappened(edit);

			if (evt.getName().equals("undo")) {

				boolean doNumber = false;
				List<mxUndoableChange> changes = edit.getChanges();
				for (mxUndoableChange change : changes) {
					if (change instanceof mxChildChange) {
						mxChildChange c = (mxChildChange) change;
						Object obj = c.getChild();
						if (obj instanceof IUnit) {
							doNumber = true;
						}
					}
				}

				new VerifyClass(BeeGraphSheet.this);
				if (doNumber) {
					LayoutUtils.makeNumber((mxICell) getGraph().getDefaultParent(), null);
				}

				LayoutUtils.RelayoutAll(BeeGraphSheet.this);

			}
			setModified(true);
		}
	};

	public File getFile() {
		return this.findBook().getFile();
	}

	/**
	 * 
	 * @param graph
	 */
	public BeeGraphSheet(BProject project) {
		super(new BeeGraph());
		this.manager = new BEditorManager(this, new mxUndoManager());
		((BeeGraph) this.getGraph()).installHandlers(this);
		this.getModel().installHandlers(this);
		this.project = project;
		this.setBorder(null);
		setPageVisible(true);
		setGridVisible(true);
		setToolTips(false);
		setEnterStopsCellEditing(true);
		this.setPageVisible(false);
		this.getVerticalScrollBar().setUnitIncrement(50);

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		mxConnectionHandler connectionHandler = getConnectionHandler();
		connectionHandler.setCreateTarget(true);
		connectionHandler.addListener(mxEvent.CONNECT, new BeeHandlers.ConnectionListener(this));

		graph.addListener(mxEvent.REMOVE_CELLS, new BeeHandlers.CellRemoveHandler(this));
		graph.addListener(mxEvent.CELLS_FOLDED, new BeeHandlers.CellFoldHandler(this));
		addListener(mxEvent.LABEL_CHANGED, new BeeHandlers.LabelChangeHandler());

		// Loads the defalt stylesheet from an external file
		mxCodec codec = new mxCodec();
		Document doc = mxUtils
				.loadDocument(BeeEditor.class.getResource("/com/linkstec/bee/UI/resources/basic-style.xml").toString());
		codec.decode(doc.getDocumentElement(), graph.getStylesheet());

		Map<String, Object> styles = graph.getStylesheet().getDefaultVertexStyle();
		// styles.put("fontSize", BeeUIUtils.getDefaultFontSize());
		styles.put("fontSize", 11);
		styles.put("fontFamily", BeeUIUtils.getDefaultFontFamily());

		// Sets the background to white
		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);

		// Keeps the selection in sync with the command history
		mxIEventListener undo = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
				LayoutUtils.RelayoutAll(BeeGraphSheet.this);
			}
		};

		((mxEventSource) manager.getUndo()).addListener(mxEvent.UNDO, undo);
		((mxEventSource) manager.getUndo()).addListener(mxEvent.REDO, undo);

		graph.getView().addListener(mxEvent.UNDO, undoHandler);
		// Adds the command history to the model and view
		getGraph().getModel().addListener(mxEvent.UNDO, undoHandler);

		new mxRubberband(this);
		// new EditorKeyboardHandler(this);

		installRepaintListener();
		installListeners();

		// this.hint = new HintEditor((BeeCellEditor) this.getCellEditor(), this);
		UI = new BeeScrollUI();
		this.setUI(UI);
		this.id = BeeUIUtils.createID();

		tooltip = new DetailEditToolTip(this);

		this.editor = new BeeLoigcEditor(this);

		// this.setPageBackgroundColor(BeeConstants.BACKGROUND_COLOR);
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	public void removeErrorLine(BasicNode node) {
		if (this.isVisible()) {
			UI.removeErrorLine(Integer.toString(node.hashCode()));

			fireError();
			this.graphBorder.removeError(node);
		}
	}

	public void clearErrorLine() {
		if (this.isVisible()) {
			UI.clearError();
			fireError();
			this.graphBorder.clearErrors();
		}
	}

	private void fireError() {
		BAlert error = this.getModel().getAlertObject();

		for (TitleChangeListener listener : this.getModel().getTitleChangeListeners()) {
			if (error == null) {
				listener.setError(false);
			} else if (error.getType().equals(BAlert.TYPE_ERROR)) {
				listener.setError(true);
			} else {
				listener.setAlert(true);
			}

		}
	}

	public void addErrorLine(BasicNode node) {
		if (this.isVisible()) {
			mxPoint point = BeeActions.getTranslateToRoot(node);
			int y = (int) (point.getY() * this.getGraph().getView().getScale());
			UI.addErrorLine(Integer.toString(node.hashCode()), y, node.getAlert(), node);
			fireError();
			this.graphBorder.addError(node);
		}

	}

	@Override
	public mxRectangle getLayoutAreaSize() {
		mxRectangle rect = super.getLayoutAreaSize();
		return rect;

	}

	@Override
	public mxInteractiveCanvas createCanvas() {
		return new BeeCanvas(this);
	}

	@Override
	protected mxICellEditor createCellEditor() {
		return new BeeCellEditor(this);
	}

	public void Deactivated() {
		// this.hint.Deactivated();
	}

	// Installs mouse wheel listener for zooming
	MouseWheelListener wheelTracker = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
				BeeGraphSheet.this.mouseWheelMoved(e);
			}
			tooltip.hideTip();
		}

	};

	// public HintEditor getHindEditor() {
	// return this.hint;
	// }

	public mxIEventListener getUndoHandler() {
		return undoHandler;
	}

	public MouseWheelListener getWheelTracker() {
		return wheelTracker;
	}

	protected void installListeners() {

		// Handles mouse wheel events in the outline and graph component

		this.addMouseWheelListener(wheelTracker);

		// Installs the popup menu in the graph component
		this.getGraphControl().addMouseListener(new MouseAdapter() {

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e) {
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
				tooltip.hideTip();
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showGraphPopupMenu(e);
				}

				getGraphControl().repaint();
			}

		});

		// Installs a mouse motion listener to display the mouse location
		this.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

			private ILink moveTarget = null;

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
			 */
			public void mouseDragged(MouseEvent e) {
				mouseLocationChanged(e.getX(), e.getY());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
			 */
			public void mouseMoved(MouseEvent e) {
				mouseLocationChanged(e.getX(), e.getY());
				if (!DetailEditToolTip.isSticking()) {
					int x = e.getX();
					int y = e.getY();
					Object cell = getCellAt(x, y);

					if (cell != null) {
						if (cell instanceof BasicNode) {
							BasicNode node = (BasicNode) cell;

							if (node instanceof ILink) {

								ILink link = (ILink) node;
								if (this.moveTarget != null) {
									if (!this.moveTarget.equals(link)) {
										link.onMouseOver();
										mxCellState state = graph.getView().getState(node);
										getCanvas().drawCell(state);
										graph.repaint(state.getBoundingBox());
									}
								} else {
									link.onMouseOver();
									mxCellState state = graph.getView().getState(node);
									getCanvas().drawCell(state);

								}
								moveTarget = link;
							} else {
								if (this.moveTarget != null) {
									moveTarget.onMouseOut();
									getCanvas().drawCell(graph.getView().getState(node));
									moveTarget = null;
								}
							}
							if (!isDraggingOver()) {
								// tooltip.showTip(node, x, y, getGraphControl());
							}
						} else {
							tooltip.hideTip();
						}
					}
				}
			}

		});
	}

	protected TransferHandler createTransferHandler() {
		return new BeeTransferHandler();
	}

	// public void hideEditor() {
	// hint.getCellEditor().getPopup().setVisible(false);
	// }

	/**
	 * 
	 */
	protected void showGraphPopupMenu(MouseEvent e) {

		Object obj = this.getGraph().getSelectionCell();
		if (obj == null) {
			tooltip.showImmediatelyWithLocation(null, e.getX(), e.getY(), this);
		} else {
			if (obj instanceof BasicNode) {
				BasicNode b = (BasicNode) obj;
				tooltip.showImmediatelyWithLocation(b, e.getX(), e.getY(), this);
			}
		}
	}

	public void hideTip() {
		this.tooltip.hideTip();
	}

	// private void showHintEditor(MouseEvent e) {
	// hint.onMouseClick(this, e);
	//
	// }

	@Override
	protected mxGraphHandler createGraphHandler() {
		return new BeeGraphHandler(this);
	}

	protected mxGraphControl createGraphControl() {

		mxGraphControl control = new mxGraphControl() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8775400366621921164L;

			@Override
			public void paint(Graphics g) {
				g.setColor(Color.decode("#EEEEEE"));
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				super.paint(g);
				// Rectangle editorRect = hint.getEditorRect();
				g.setColor(Color.LIGHT_GRAY);

				BeeGraphHandler handler = (BeeGraphHandler) getGraphHandler();
				handler.paintTargetMarker(g, this);
				editor.afterConctrolPainted(g);
			}

			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.setSize(size.width, size.height + 400 * getPageScale());
				return size;
			}

			@Override
			public void drawCell(mxICanvas canvas, Object cell) {
				super.drawCell(canvas, cell);
				mxCellState state = graph.getView().getState(cell);
				editor.paint(state, (BeeCanvas) canvas);
			}

		};

		graphBorder = new BeeGraphBorder(this);
		control.setBorder(this.graphBorder);

		return control;

	}

	/**
	 * 
	 */
	public void mouseLocationChanged(int x, int y) {
		// hint.onMouseMove(this, x, y);
	}

	public BeeGraphBorder getGraphBorder() {
		return graphBorder;
	}

	/**
	 * 
	 */
	protected void installRepaintListener() {
		getGraph().addListener(mxEvent.REPAINT, new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {

			}
		});

	}

	/**
	 * 
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			zoomIn();
		} else {
			zoomOut();
		}

	}

	/**
	 * Overrides drop behaviour to set the cell style if the target is not a valid
	 * drop target and the cells are of the same type (eg. both vertices or both
	 * edges).
	 */
	public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {

		getGraphControl().repaint();
		if (cells.length == 1) {

			if (target == null && location != null) {
				Object t = getCellAt(location.x, location.y);
				if (t == null) {
					if (cells[0] instanceof BasicNode) {
						BasicNode node = (BasicNode) cells[0];
						if (node instanceof BVariable) {

						} else if (node instanceof BInvoker) {
							BInvoker invoker = (BInvoker) node;
							if (invoker.getBClass() == null) {
								return null;
							}
							if (invoker.getBClass().getQualifiedName().equals(BClass.VOID)) {
								return null;
							}
						} else if (node instanceof BValuable) {

						} else if (node instanceof BNote) {
							node.getGeometry().setRelative(false);
						} else if (!(node instanceof IClassMember)) {
							return null;
						} else {
							if (!node.layoutInited()) {
								if (node.getLayout() != null) {
									node.getLayout().layout();
									node.setLayoutInited(true);
								}
							}
						}
					}
				}

			}

		}

		Object[] result = this.doImport(cells, dx, dy, target, location);
		if (result == null) {
			return super.importCells(cells, dx, dy, target, location);
		} else {
			return cells;
		}

	}

	public Object[] doImport(Object[] cells, double dx, double dy, Object target, Point location) {
		if (target == null) {
			if (location == null) {
				Object obj = this.getGraph().getSelectionCell();
				if (obj instanceof BMethod) {
					BMethod method = (BMethod) obj;
					BLogicBody body = method.getLogicBody();
					target = body;
					location = this.getGraph().getBoundingBox(body).getPoint();
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		BeeModel model = this.getModel();
		mxGraph graph = this.getGraph();

		if (cells != null && (dx != 0 || dy != 0 || target != null)) {
			model.beginUpdate();
			try {

				cells = graph.cloneCells(cells, graph.isCloneInvalidEdges());

				CellMoveHandler handler = new CellMoveHandler(this);
				handler.process(cells, dx, dy, target, location);

			} finally {
				model.endUpdate();
			}
		}

		return cells;
	}

	public String toString() {
		return ((BeeModel) this.getGraph().getModel()).getTitleLabel();
	}

	public void setModified(boolean modified) {
		BeeTabCloseButton button = BeeActions.findPaneButton((JComponent) this.getParent(), this);
		if (button != null) {
			button.setModified(modified);
			if (modified) {
				BeeTabCloseButton b = BeeActions.findPaneButton(this.getParent().getParent(),
						(JComponent) this.getParent());
				if (b != null) {
					b.setModified(true);
				}
			}
		}
	}

	public BeeModel getModel() {
		return (BeeModel) this.getGraph().getModel();
	}

	@Override
	public void setTitleWithOutListenerAction(String title) {
		((BeeModel) this.getGraph().getModel()).setTitleLabel(title);
	}

	@Override
	public void addTitleChangeListener(TitleChangeListener listener) {
		((BeeModel) this.getGraph().getModel()).addTitleChangeListener(listener);
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.SHEET_LOGIC_ICON;
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
		// this.hideEditor();
		tooltip.hideTip();
	}

	@Override
	public void beforeSave() {

	}

	public void scrollToVisible(mxICell cell) {
		mxPoint point = BeeActions.getTranslateToRoot(cell);
		double scale = this.getGraph().getView().getScale();
		if (cell.getGeometry() != null) {
			this.graphControl.scrollRectToVisible(new Rectangle((int) (point.getX() * scale),
					(int) (point.getY() * scale), (int) (cell.getGeometry().getWidth() * scale),
					(int) (cell.getGeometry().getHeight() * scale)));
		}
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public void errorClicked(BeeScrollPaneErrorPoint point) {
		Object obj = point.getUserObject();
		if (obj instanceof BasicNode) {
			BasicNode b = (BasicNode) obj;
			this.getGraph().setSelectionCell(b);
		}
	}

	public boolean isDraggingOver() {
		BeeGraphHandler handler = (BeeGraphHandler) this.graphHandler;
		return handler.isDragging();
	}

	@Override
	public String getDisplayPath() {

		EditorBook book = this.findBook();
		if (book != null) {
			return book.getDisplayPath();
		}
		return null;
	}

	@Override
	public BEditorFileExplorer getFileExplore() {
		return Application.getInstance().getDesignSpective().getFileExplore();
	}

	@Override
	public BEditorManager getManager() {
		return this.manager;
	}

	@Override
	public JComponent getContents() {
		return this;
	}

	@Override
	public BEditorOutlookExplorer getOutlookExplore() {
		return Application.getInstance().getDesignSpective().getOutline();
	}

	@Override
	public void makeTabPopupItems(BManager manager) {
		manager.addPopupItem("ソースへ変換", BeeConstants.GENERATE_CODE_ICON,
				new CodecAction.GenerateSourceSingle(this.getFile(), this.project));
	}

	@Override
	public List<TitleChangeListener> getTitleChangeListeners() {
		return this.getModel().getTitleChangeListeners();
	}

	@Override
	public void generate() {
		CodecAction.GenerateSource g = new CodecAction.GenerateSource();
		g.actionPerformed(null);
	}

	@Override
	public File save() {
		EditorBook book = this.findBook();
		return book.save();
	}

	@Override
	public void saveAs(ActionEvent e) {
		EditorBook book = this.findBook();
		book.saveAs(e);
	}

	@Override
	public void deleteSelect(ActionEvent e) {

		mxGraphActions.getDeleteAction().actionPerformed(e);
	}

	@Override
	public void selectAll(ActionEvent e) {
		this.getGraph().selectAll();
	}

	public EditorBook findBook() {
		Container parent = this.getParent();
		while (parent != null && !(parent instanceof EditorBook)) {
			parent = parent.getParent();
		}
		return (EditorBook) parent;
	}

	@Override
	public void layoutEditor() {
		LayoutUtils.RelayoutAll(this);
	}

	@Override
	public String getLogicName() {
		return this.getModel().getLogicName();
	}

	@Override
	public void onSelected() {
		double width = Application.getInstance().getDesignSpective().getWorkspace().getWidth();
		if (this.getBorder() != null) {
			Insets insets = this.getBorder().getBorderInsets(this);

			width = width - insets.left - insets.right;
			insets = this.getInsets();
			width = width - insets.left - insets.right;
		}
		if (width <= 0) {
			width = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.7;
		}
		double scale = width / (BeeConstants.PAGE_SPACING_TOP + BeeConstants.SEGMENT_MAX_WIDTH
				+ BeeConstants.NODE_SPACING * 2 + +BeeConstants.PAGE_SPACING_LEFT * 1.3);

		this.zoomTo(scale, false);
		Application.getInstance().getEditor().getToolbar().getScaleTracker().invoke(this, null);
		Application.getInstance().getDesignSpective().intializeOutline(this);
		Application.getInstance().setCurrentEditor(this);

	}

	@Override
	public void updateView() {
		this.refresh();
	}

	@Override
	public void setFile(File file) {

	}

	public int print(Graphics g, PageFormat f, int page) {

		int result = NO_SUCH_PAGE;

		// Disables double-buffering before printing
		RepaintManager currentManager = RepaintManager.currentManager(this);
		currentManager.setDoubleBufferingEnabled(false);

		// Gets the current state of the view
		mxGraphView view = graph.getView();

		// Stores the old state of the view
		boolean eventsEnabled = view.isEventsEnabled();
		mxPoint translate = view.getTranslate();

		// Disables firing of scale events so that there is no
		// repaint or update of the original graph while pages
		// are being printed
		view.setEventsEnabled(false);

		double ratio = 1000 / f.getWidth();

		// Uses the view to create temporary cell states for each cell
		mxTemporaryCellStates tempStates = new mxTemporaryCellStates(view, 1 / ratio);

		try {
			view.setTranslate(new mxPoint(0, 0));

			mxGraphics2DCanvas canvas = createCanvas();
			canvas.setGraphics((Graphics2D) g);
			canvas.setScale(1 / ratio);

			view.revalidate();

			mxRectangle graphBounds = graph.getGraphBounds();
			Dimension pSize = new Dimension((int) Math.ceil(graphBounds.getX() + graphBounds.getWidth()) + 1,
					(int) Math.ceil(graphBounds.getY() + graphBounds.getHeight()) + 1);

			int w = (int) (f.getImageableWidth());
			int h = (int) (f.getImageableHeight());
			int cols = (int) Math.max(Math.ceil((double) (pSize.width - 5) / (double) w), 1);
			int rows = (int) Math.max(Math.ceil((double) (pSize.height - 5) / (double) h), 1);

			if (page < cols * rows) {
				String info = (page + 1) + "/" + (cols * rows);
				Application.getInstance().getEditor().getStatusBar().setMessag(info + "ページ目を印刷しています…");
				int dx = (int) ((page % cols) * f.getImageableWidth());
				int dy = (int) (Math.floor(page / cols) * f.getImageableHeight());

				g.translate(-dx + (int) f.getImageableX(), -dy + (int) f.getImageableY());
				g.setClip(dx, dy, (int) (dx + f.getWidth()), (int) (dy + f.getHeight()));

				graph.drawGraph(canvas);

				FontMetrics metrics = g.getFontMetrics();

				g.drawString(info, (int) g.getClipBounds().getWidth() - metrics.stringWidth(info) - 10,
						(int) g.getClipBounds().getHeight() - 20);
				result = PAGE_EXISTS;
			} else {
				Application.getInstance().getEditor().getStatusBar().setMessag("印刷完了");
			}
		} finally {
			view.setTranslate(translate);

			tempStates.destroy();
			view.setEventsEnabled(eventsEnabled);

			// Enables double-buffering after printing
			currentManager.setDoubleBufferingEnabled(true);
		}

		return result;

	}

	@Override
	public BEditorModel getEditorModel() {
		return (BEditorModel) this.getGraph().getModel();
	}

	@Override
	public void removeErrorLine(Object cell) {
		this.removeErrorLine((BasicNode) cell);

	}
}
