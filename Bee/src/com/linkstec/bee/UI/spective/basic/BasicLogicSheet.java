package com.linkstec.bee.UI.spective.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.w3c.dom.Document;

import com.linkstec.bee.UI.BEditorClipboardReceiver;
import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorManager;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeEditor;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tab.BeeCloseable;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.BasicCanvas;
import com.linkstec.bee.UI.spective.basic.logic.BasicGraph;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BGraphHandler;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLoigcEditor;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.LogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.data.PluralDataLogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.provider.ProviderLogics;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedValueLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.DataCopyLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.JudgeLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.VarLogicList;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BComponentNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDataCopyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BLabelNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BProcessTypeNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferHandler;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IActionModel;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxConnectPreview;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class BasicLogicSheet extends mxGraphComponent implements BEditor, BeeCloseable, BEditorClipboardReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1058480937370881737L;

	private File file;
	private BEditorManager manager;
	private BProject project;
	private BLoigcEditor editor;

	/**
	 * 
	 */
	protected transient mxIEventListener undoHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {

			undoOcurred();

			mxUndoableEdit edit = (mxUndoableEdit) evt.getProperty("edit");
			manager.getUndo().undoableEditHappened(edit);

			List<mxUndoableChange> changes = edit.getChanges();
			graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
			BLogicLayout.layoutNodes(BasicLogicSheet.this);

			setModified(true);

		}
	};

	public void undoOcurred() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				BasicModel model = (BasicModel) getEditorModel();
				List<BNode> nodes = model.getBNodes();
				for (BNode node : nodes) {

					if (node instanceof BActionPropertyNode) {
						BActionPropertyNode a = (BActionPropertyNode) node;
						validateModelIOs(a, BasicLogicSheet.this);
					} else if (node instanceof BActionNode) {
						BActionNode action = (BActionNode) node;
						BActionPropertyNode a = action.getProperty();
						if (a != null) {
							validateModelIOs(a, BasicLogicSheet.this);
							makeRelaction(a);
						}
					}
					makeRelaction(node);
				}
				BasicBook book = findBook();
				if (book != null) {
					book.validateModels();
				}
			}

		}).start();

	}

	public void validateModelIOs(BActionPropertyNode node, BasicLogicSheet sheet) {

		mxICell parent = node.getParent();

		BActionModel model = (BActionModel) node.getLogic().getPath().getAction();
		if (model == null) {
			return;
		}

		model.getInputModels().clear();
		model.getOutputModels().clear();

		mxICell cell = parent.getTerminal(true);
		if (cell instanceof BComponentNode) {
			BComponentNode data = (BComponentNode) cell;
			BasicComponentModel m = data.getModel();
			model.setInput(m);
			model.getInputModels().add(m);

			this.makeRelaction(data);
		}
		cell = parent.getTerminal(false);
		if (cell instanceof BComponentNode) {
			BComponentNode data = (BComponentNode) cell;
			BasicComponentModel m = data.getModel();
			model.setOutput(m);
			model.getOutputModels().add(m);
		}
		this.addModels(node, sheet, model.getInputModels(), model.getOutputModels());

	}

	private void addModels(BActionPropertyNode node, BasicLogicSheet sheet, List<BasicComponentModel> inputList,
			List<BasicComponentModel> outputList) {
		Object[] edges = sheet.getGraph().getEdges(node);
		int count = edges.length;
		for (int i = 0; i < count; i++) {
			mxICell line = (mxICell) edges[i];
			if (line.getTerminal(true).equals(node)) {
				mxICell output = line.getTerminal(false);
				addList(outputList, output);
			} else if (line.getTerminal(false).equals(node)) {
				mxICell input = line.getTerminal(true);
				addList(inputList, input);
			}
		}
	}

	private void addList(List<BasicComponentModel> list, mxICell cell) {
		if (cell instanceof BComponentNode) {
			BComponentNode data = (BComponentNode) cell;
			BasicComponentModel m = data.getModel();
			list.add(m);
		} else if (cell instanceof BGroupNode) {
			int count = cell.getChildCount();
			for (int i = 0; i < count; i++) {
				mxICell child = cell.getChildAt(i);
				addList(list, child);
			}
		}
	}

	// for logic sheet
	private void makeRelaction(BNode node) {
		boolean hasPrevious = false;
		if (node instanceof ILogicCell) {

			ILogicCell nlogic = (ILogicCell) node;
			BLogic l = nlogic.getLogic();
			if (l == null) {
				return;
			}
			if (l instanceof JudgeLogic) {
				System.out.println(nlogic.hashCode() + ":" + l.getClass().getName() + ":" + l.hashCode() + ":"
						+ nlogic.toString());
			}
			BPath npath = nlogic.getLogic().getPath();
			if (!npath.getCell().equals(nlogic)) {
				npath.setCell(nlogic);
			}
			int count = node.getEdgeCount();
			for (int i = 0; i < count; i++) {
				mxICell edge = node.getEdgeAt(i);
				mxICell source = edge.getTerminal(true);
				mxICell target = edge.getTerminal(false);
				if (source != null && target != null) {
					if (target.equals(node)) {
						if (source instanceof ILogicCell) {
							ILogicCell logic = (ILogicCell) source;
							BPath path = logic.getLogic().getPath();
							npath.setParent(path);
							hasPrevious = true;

						}
					}
				}
			}
			if (!hasPrevious) {
				mxICell parent = node.getParent();
				if (parent instanceof ILogicCell) {
					ILogicCell logic = (ILogicCell) parent;
					BPath path = logic.getLogic().getPath();
					if (!path.equals(npath)) {
						npath.setParent(path);
					}
				}
			}
			int c = node.getChildCount();
			for (int i = 0; i < c; i++) {
				mxICell child = node.getChildAt(i);
				if (child instanceof BNode) {
					BNode b = (BNode) child;
					this.makeRelaction(b);
				}
			}
		}

	}

	// Installs mouse wheel listener for zooming
	MouseWheelListener wheelTracker = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
				BasicLogicSheet.this.mouseWheelMoved(e);
			}
		}

	};

	public BasicLogicSheet(BProject project, SubSystem sub) {
		super(new BasicGraph(sub));
		BasicGraph graph = (BasicGraph) this.getGraph();
		graph.setSheet(this);
		this.project = project;
		manager = new BEditorManager(this, new mxUndoManager());

		mxCodec codec = new mxCodec();
		Document doc = mxUtils
				.loadDocument(BeeEditor.class.getResource("/com/linkstec/bee/UI/resources/basic-style.xml").toString());
		codec.decode(doc.getDocumentElement(), graph.getStylesheet());

		this.setBorder(null);
		setPageVisible(true);
		setGridVisible(true);
		setToolTips(false);
		setEnterStopsCellEditing(true);
		this.setPageVisible(false);
		this.getVerticalScrollBar().setUnitIncrement(50);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		new mxRubberband(this);

		graph.setKeepEdgesInBackground(true);
		// Sets the background to white
		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);

		// Keeps the selection in sync with the command history
		// mxIEventListener undo = new mxIEventListener() {
		// public void invoke(Object source, mxEventObject evt) {
		// List<mxUndoableChange> changes = ((mxUndoableEdit)
		// evt.getProperty("edit")).getChanges();
		// graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
		//
		// }
		// };

		mxIEventListener connect = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {

				Object obj = evt.getProperty("cell");
				if (obj instanceof mxICell) {
					mxICell node = (mxICell) obj;
					cellConnected(node);
				}

			}
		};

		mxIEventListener added = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {

				Object obj = evt.getProperty("cells");
				if (obj instanceof Object[]) {
					Object[] cells = (Object[]) obj;
					if (cellsAdded(cells)) {
						return;
					}
					if (cells != null && cells.length == 1) {
						Object parent = evt.getProperty("parent");
						if (parent instanceof BNode) {
							BNode p = (BNode) parent;
							if (cells[0] instanceof BNode) {
								BNode node = (BNode) cells[0];
								node.added(BasicLogicSheet.this);
								p.childAdded(node, BasicLogicSheet.this);
							} else if (cells[0] instanceof BasicNode) {
								BasicNode node = (BasicNode) cells[0];
								if (BasicLogicSheet.this instanceof BPatternSheet) {
									BPatternSheet sheet = (BPatternSheet) BasicLogicSheet.this;
									BPatternModel model = (BPatternModel) sheet.getEditorModel();
									BDetailNodeWrapper wrapper = new BDetailNodeWrapper(model.getActionPath(), node);
									node.removeFromParent();

									p.insert(wrapper);
									p.childAdded(wrapper, sheet);

								} else {
									node.removeFromParent();
								}

							} else if (cells[0] instanceof mxICell) {
								mxICell cell = (mxICell) cells[0];
								p.cellAdded(cell);
							}
						} else {
							if (cells[0] instanceof BasicNode) {
								BasicNode node = (BasicNode) cells[0];
								if (BasicLogicSheet.this instanceof BPatternSheet) {
									BPatternSheet sheet = (BPatternSheet) BasicLogicSheet.this;
									BPatternModel model = (BPatternModel) sheet.getEditorModel();
									BDetailNodeWrapper wrapper = new BDetailNodeWrapper(model.getActionPath(), node);
									node.removeFromParent();

									mxGeometry geo = wrapper.getGeometry();

									if (geo.isRelative()) {
										geo.setRelative(false);
										geo.setX(geo.getX() + geo.getOffset().getX());
										geo.setY(geo.getY() + geo.getOffset().getY());

										geo.setOffset(new mxPoint(0, 0));

									}

									getRoot().insert(wrapper);
								} else {
									node.removeFromParent();
								}

							} else if (cells[0] instanceof BNode) {
								BNode node = (BNode) cells[0];
								node.added(BasicLogicSheet.this);
							}
						}
					}
				}
			}
		};

		this.getConnectionHandler().addListener(mxEvent.CONNECT, connect);

		this.graph.addListener(mxEvent.CELLS_ADDED, added);

		mxIEventListener resized = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {

				Object obj = evt.getProperty("cells");
				if (obj instanceof Object[]) {
					cellsResized((Object[]) obj);
				}

			}
		};
		this.graph.addListener(mxEvent.CELLS_RESIZED, resized);

		mxIEventListener selected = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {

				Object obj = evt.getProperty("cells");
				if (obj instanceof Object[]) {
					cellSelected((Object[]) obj);
				}

			}
		};
		this.graph.addListener(mxEvent.CHANGE, selected);

		// ((mxEventSource) manager.getUndo()).addListener(mxEvent.UNDO, undo);
		// ((mxEventSource) manager.getUndo()).addListener(mxEvent.REDO, undo);

		graph.getView().addListener(mxEvent.UNDO, undoHandler);
		getGraph().getModel().addListener(mxEvent.UNDO, undoHandler);

		this.installListeners();

		Map<String, Object> styles = graph.getStylesheet().getDefaultVertexStyle();
		styles.put("fontSize", 11);
		styles.put("fontFamily", BeeUIUtils.getDefaultFontFamily());

		this.editor = new BLoigcEditor(this);
	}

	protected void labelChanged() {

	}

	protected void cellSelected(Object[] cells) {
		if (cells.length == 1) {
			Object cell = cells[0];
			if (cell instanceof BNode) {
				BNode node = (BNode) cell;
				node.cellSelected(this);
			}
		}
	}

	protected void cellConnected(mxICell connector) {
		mxICell cell = connector.getTerminal(true);
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			node.cellConnected(this, connector, true);
		}
		cell = connector.getTerminal(false);
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			node.cellConnected(this, connector, false);
		}
	}

	public double layoutNode() {
		return this.getLayoutAreaSize().getHeight();
	}

	public BPath getPath() {
		return null;
	}

	public void makeDataSelectionWhenSelected(BasicDataSelectionNode parent, BNode logic) {

	}

	public void makeLogic(BasicEditNode root, BasicEditDataSelection selection, DefaultTreeModel model) {

		makeLogicModel(root, this.getProject(), model);

		List<BasicDataSelectionNode> selecteds = new ArrayList<BasicDataSelectionNode>();

		scanNode(root, (BasicDataSelectionNode) selection.getRoot(), selection, model, selecteds);

		List<BValuable> values = new ArrayList<BValuable>();
		for (BasicDataSelectionNode node : selecteds) {
			Object obj = node.getUserObject();
			TreeNode p = node.getParent();
			if (p instanceof BasicDataSelectionNode) {
				BasicDataSelectionNode prent = (BasicDataSelectionNode) p;
				Object prentObj = prent.getUserObject();
				if (obj instanceof BAssignment) {
					BAssignment assign = (BAssignment) obj;
					if (prentObj instanceof BParameter) {

						BParameter data = (BParameter) prentObj;

						IPatternCreator view = PatternCreatorFactory.createView();
						BInvoker invoker = view.createMethodInvoker();

						invoker.setInvokeParent(data);
						BParameter left = assign.getLeft();
						invoker.setInvokeChild((BValuable) left.cloneAll());

						values.add(invoker);//

					} else {
						BParameter left = assign.getLeft();
						values.add((BValuable) left.cloneAll());
					}
				} else if (obj instanceof BFixedValueLogic) {

					IPatternCreator view = PatternCreatorFactory.createView();
					BVariable fixedVar = view.createVariable();
					fixedVar.addUserAttribute("FIXED", "FIXED");
					fixedVar.setBClass(CodecUtils.BString());
					fixedVar.setName("固定値");
					fixedVar.setLogicName("@TODO");
					values.add(fixedVar);
				}
			}
			if (obj instanceof BValuable) {
				BValuable bv = (BValuable) obj;
				values.add(bv);
			}
		}

		if (values.size() > 0) {
			// expression
			BasicEditNode node = new BasicEditNode(this.getProject());
			node.setUserObject(values);
			node.setImageIcon(BeeConstants.P_EXPRESSION_ICON);
			String ex = null;
			for (BValuable param : values) {

				String name = BValueUtils.createValuable(param, false);
				if (ex == null) {
					ex = name;
				} else {
					ex = ex + "+" + name;
				}

			}
			node.setDisplay(ex);
			model.insertNodeInto(node, root, 0);

			if (values.size() == 2) {

				BValuable source = values.get(0);
				BValuable target = values.get(1);

				if (source != null && target != null) {
					if (source.getBClass().isData() && target.getBClass().isData()) {
						BasicEditNode copy = new BasicEditNode(this.getProject());
						copy.setImageIcon(BeeConstants.REFERENCE_ICON);
						BDataCopyNode bcl = new BDataCopyNode();
						DataCopyLogic logic = new DataCopyLogic(null, bcl);
						bcl.setLogic(logic);
						logic.setSource(source);
						logic.setTarget(target);
						copy.setUserObject(logic);
						copy.setDisplay(logic.getName());

						model.insertNodeInto(copy, root, 0);
					}
				}

			}

		}

		List<BLogic> list = ProviderLogics.getList(selection.getActionPath(), selection.isProviderReload());
		if (list != null) {
			for (BLogic logic : list) {
				makeLogic(logic, root, model);
			}
		}
		IActionModel action = selection.getActionPath().getAction();
		if (action instanceof BActionModel) {
			// BActionModel am = (BActionModel) action;
			List<BParameter> outputs = selection.getActionPath().getLogic().getOutputs();
			for (BParameter param : outputs) {
				BasicEditNode node = new BasicEditNode(project);
				node.setUserObject(param);
				node.setImageIcon(BeeConstants.DATA_ICON);
				node.setDisplay(param.getName() + "編集");
				root.add(node);
			}
		}

	}

	public void makeLogicModel(BasicEditNode parent, BProject project, DefaultTreeModel model) {

	}

	public void makeActionLogic(BPath path, BasicEditNode parent) {

		BActionModel action = (BActionModel) path.getAction();
		int depth = action.getActionDepth() + 1;

		ActionModel am = action.getProcessModel();
		List<LayerModel> layers = am.getLayers();
		if (layers.size() > depth) {
			BasicEditNode typeNode = new BasicEditNode(project);
			NewLayerClassLogic logic = new NewLayerClassLogic(path, null, null);
			BProcessTypeNode node = new BProcessTypeNode(logic);
			logic.getPath().setCell(node);

			typeNode.setUserObject(logic);
			typeNode.setProject(project);
			typeNode.setDisplay(logic.getDesc());
			typeNode.setImageIcon(logic.getIcon());

			parent.add(typeNode);
		}
	}

	public void scanNode(BasicEditNode parent, BasicDataSelectionNode node, BasicEditDataSelection selection,
			DefaultTreeModel model, List<BasicDataSelectionNode> list) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			BasicDataSelectionNode child = (BasicDataSelectionNode) node.getChildAt(i);
			if (child.isChecked()) {
				if (list != null) {
					list.add(child);
				}
				// Object obj = child.getUserObject();
				// Object parentObj = node.getUserObject();
				this.makeLogics(child, parent, selection.getActionPath(), model);
			}
			this.scanNode(parent, child, selection, model, list);
		}
	}

	public void makeLogicsback(Object obj, Object prentObj, BasicEditNode parent, BPath parentPath,
			DefaultTreeModel model) {
		LogicList logicList = null;
		if (obj instanceof BAssignment) {
			BAssignment assign = (BAssignment) obj;
			if (prentObj instanceof BeeDataModel) {
				BeeDataModel data = (BeeDataModel) prentObj;
				logicList = new VarLogicList(data, assign.getLeft());
			} else if (prentObj instanceof BVariable) {
				BVariable var = (BVariable) prentObj;
				logicList = new VarLogicList(var, assign.getLeft());
			} else if (prentObj == null) {
				// defined variable
				BVariable var = assign.getLeft();
				logicList = new VarLogicList(var, var);
			}
		} else if (obj instanceof BVariable) {
			BVariable var = (BVariable) obj;
			BClass bclass = var.getBClass();
			if (bclass.getQualifiedName().equals(List.class.getName())) {
				logicList = new PluralDataLogicList(parentPath, var);
			} else {
				logicList = new VarLogicList(var, var);
			}

			if (var.getBClass().isData()) {
				BasicEditNode node = new BasicEditNode(project);
				node.setUserObject(var);
				node.setImageIcon(BeeConstants.DATA_ICON);
				node.setDisplay(var.getName() + "編集");
				parent.add(node);
			}

		}
		if (logicList != null) {
			List<BLogic> list = logicList.getList(parentPath);
			if (list != null) {
				for (BLogic logic : list) {
					this.makeLogic(logic, parent, model);
				}
			}
		}
	}

	public void makeLogics(BasicDataSelectionNode select, BasicEditNode parent, BPath parentPath,
			DefaultTreeModel model) {
		mxCell cell = select.getTransferNode();

		if (cell instanceof BTansferHolderNode) {
			BTansferHolderNode t = (BTansferHolderNode) cell;
			List<BNode> nodes = t.getNodes();
			for (BNode node : nodes) {
				this.makeCell(node, parent, parentPath, model);
			}
		}

		this.makeCell(cell, parent, parentPath, model);
	}

	public void makeCell(mxCell cell, BasicEditNode parent, BPath parentPath, DefaultTreeModel model) {
		LogicList logicList = null;
		if (cell instanceof BInvoker) {
			BInvoker invoker = (BInvoker) cell;
			if (invoker.getInvokeChild() instanceof BVariable) {
				logicList = new VarLogicList(invoker);
			}
		} else if (cell instanceof BVariable) {
			BVariable var = (BVariable) cell;
			// BClass bclass = var.getBClass();
			// if (bclass.getQualifiedName().equals(List.class.getName())) {
			// logicList = new PluralDataLogicList(parentPath, var);
			// } else {
			logicList = new VarLogicList(var, var);
			// }
			if (var.getBClass().isData()) {
				BasicEditNode node = new BasicEditNode(project);
				node.setUserObject(var);
				node.setImageIcon(BeeConstants.DATA_ICON);
				node.setDisplay(var.getName() + "編集");
				parent.add(node);
			}

		} else if (cell instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) cell;
			BasicNode node = wrapper.getNode();

			this.makeCell(node, parent, parentPath, model);
		} else if (cell instanceof BAssignment) {
			BAssignment a = (BAssignment) cell;
			BParameter left = a.getLeft();
			BVariable var = BasicGenUtils.toView(left);
			var = (BVariable) var.cloneAll();
			var.setClass(false);
			var.setCaller(true);
			this.makeCell((mxCell) var, parent, parentPath, model);
		}
		if (logicList != null) {
			List<BLogic> list = logicList.getList(parentPath);
			if (list != null) {
				for (BLogic logic : list) {
					this.makeLogic(logic, parent, model);
				}
			}
		}
	}

	public void makeLogic(BLogic logic, BasicEditNode parent, DefaultTreeModel model) {
		BasicEditNode node = new BasicEditNode(parent.getProject());
		node.setUserObject(logic);
		node.setDisplay(logic.getName());
		node.setImageIcon(logic.getIcon());
		model.insertNodeInto(node, parent, 0);

	}

	protected boolean cellsAdded(Object[] cells) {
		return false;
	}

	protected void cellsResized(Object[] cells) {
		if (cells != null && cells.length == 1) {
			if (cells[0] instanceof BNode) {
				BNode node = (BNode) cells[0];
				node.resized(this);
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		this.editor.paintHandler(g);
	}

	public SubSystem getSub() {
		return ((BasicModel) this.getGraph().getModel()).getSubSystem();
	}

	public mxIEventListener getUndoHandler() {
		return undoHandler;
	}

	@Override
	protected mxConnectionHandler createConnectionHandler() {
		return new mxConnectionHandler(this) {

			@Override
			protected mxConnectPreview createConnectPreview() {
				return new mxConnectPreview(graphComponent) {

					@Override
					protected Object createCell(mxCellState startState, String style) {

						mxICell source = startState != null ? (mxICell) startState.getCell() : null;
						mxCell connector = getConnector(source);
						if (connector == null) {
							connector = new BActionNode();
						}

						connector.setSource(source);
						((mxICell) startState.getCell()).insertEdge(connector, true);
						return connector;
					}

				};
			}

		};
	}

	protected mxCell getConnector(mxICell cell) {
		return null;
	}

	@Override
	protected mxICellEditor createCellEditor() {
		return new BasicCellEditor(this);
	}

	@Override
	public mxInteractiveCanvas createCanvas() {
		return new BasicCanvas();
	}

	@Override
	protected mxGraphControl createGraphControl() {

		mxGraphControl control = new mxGraphControl() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8775400366621921164L;

			@Override
			public void paint(Graphics g) {

				super.paint(g);
				BGraphHandler handler = (BGraphHandler) getGraphHandler();
				// handler.paintTargetMarker(g, this);
				// editor.afterConctrolPainted(g);
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
				// editor.paint(state, (BasicCanvas) canvas);
			}

		};

		return control;

	}

	@Override
	protected mxGraphHandler createGraphHandler() {
		return new BGraphHandler(this);
	}

	public void setModel(BasicModel model) {
		this.getGraph().setModel(model);
		// this.getGraph().setDefaultParent(model.getRoot());
		model.addListener(mxEvent.UNDO, this.getUndoHandler());
	}

	protected TransferHandler createTransferHandler() {
		return new BeeTransferHandler();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			zoomIn();
		} else {
			zoomOut();
		}

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

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Object cell = getCellAt(e.getX(), e.getY());
					if (cell != null && cell instanceof BNode) {
						BNode node = (BNode) cell;
						node.doubleClicked(BasicLogicSheet.this);
					}
				} else if (e.getClickCount() == 1) {
					Object cell = getCellAt(e.getX(), e.getY());
					if (cell != null && cell instanceof BNode) {
						BNode node = (BNode) cell;
						node.clicked(BasicLogicSheet.this);
						if (node instanceof BLabelNode || node.getClass().getName().equals(BNode.class.getName())) {
							Debug.a();
						} else {
							Application.getInstance().getBasicSpective().setLogic(node);
						}
					}
				}
			}

		});

	}

	protected void showGraphPopupMenu(MouseEvent e) {
		Object[] selects = this.getGraph().getSelectionCells();
		if (selects != null && selects.length > 1) {

			JPopupMenu menu = new JPopupMenu();
			JMenuItem item = new JMenuItem();
			item.setText("グループ");
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getGraph().groupCells();
				}

			});
			menu.add(item);
			menu.show(this.getGraphControl(), e.getX(), e.getY());
		} else if (selects != null && selects.length == 1) {
			Object obj = selects[0];
			if (obj instanceof BGroupNode) {
				JPopupMenu menu = new JPopupMenu();
				JMenuItem item = new JMenuItem();
				item.setText("グループを解除する");
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						getGraph().ungroupCells();
					}

				});
				menu.add(item);
				menu.show(this.getGraphControl(), e.getX(), e.getY());

			}
		}
	}

	public BeeTabbedPane findTopPane() {
		Container parent = this.getParent();
		while (parent != null && !(parent instanceof BeeTabbedPane)) {
			parent = parent.getParent();
		}
		return (BeeTabbedPane) parent;
	}

	public void setTabName(String name) {
		BeeTabbedPane pane = this.findTopPane();
		if (pane != null) {
			int index = pane.indexOfComponent(this);
			if (index != -1) {
				BeeTabCloseButton b = (BeeTabCloseButton) pane.getTabComponentAt(index);
				b.setTitle(name);
			}
		}
	}

	@Override
	public String getDisplayPath() {
		BasicBook book = this.findBook();
		if (book != null) {
			return book.getDisplayPath();
		}
		return null;
	}

	@Override
	public BEditorFileExplorer getFileExplore() {
		return Application.getInstance().getBasicSpective().getFileExplore();
	}

	@Override
	public BEditorOutlookExplorer getOutlookExplore() {
		return Application.getInstance().getBasicSpective().getOutline();
	}

	@Override
	public BProject getProject() {
		return this.project;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public File save() {
		this.file = this.findBook().save();
		return this.file;
	}

	@Override
	public BEditorManager getManager() {
		return this.manager;
	}

	@Override
	public void makeTabPopupItems(BManager manager) {

		manager.addPopupItem("詳細設計・CD生成", BeeConstants.GENERATE_CODE_ICON,
				new CodecAction.BasicGenerate(this.findBook(), project));

	}

	@Override
	public JComponent getContents() {
		return this;
	}

	@Override
	public String getLogicName() {
		BasicModel model = (BasicModel) this.getGraph().getModel();
		return model.getLogicName();
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.BASIC_DESIGN_ICON;
	}

	@Override
	public void saveAs(ActionEvent e) {
		BasicBook book = this.findBook();
		book.saveAs(e);
	}

	public BasicBook findBook() {
		Container parent = this.getParent();
		while (parent != null && !(parent instanceof BasicBook)) {
			parent = parent.getParent();
		}
		return (BasicBook) parent;
	}

	@Override
	public void deleteSelect(ActionEvent e) {
		mxGraphActions.getDeleteAction().actionPerformed(e);
	}

	@Override
	public void selectAll(ActionEvent e) {
		this.getGraph().selectAll();
	}

	@Override
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
	public void onSelected() {
		double width = Application.getInstance().getBasicSpective().getWorkspace().getWidth();
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
		Application.getInstance().setCurrentEditor(this);
		Application.getInstance().getBasicSpective().getOutline().setEditor(this);
	}

	@Override
	public void updateView() {
		this.refresh();
	}

	@Override
	public void setFile(File file) {

	}

	@Override
	public BEditorModel getEditorModel() {
		return (BasicModel) this.getGraph().getModel();
	}

	public mxICell getRoot() {
		return ((mxICell) this.getGraph().getModel().getRoot()).getChildAt(0);
	}

	@Override
	public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
		Object[] obj = super.importCells(cells, dx, dy, target, location);
		if (cells != null && cells.length == 1) {
			if (cells[0] instanceof BNode) {
				BNode node = (BNode) cells[0];
				node.imported(this);
			}
		}
		return obj;
	}

	private mxICell findRoot(mxICell node) {
		int count = node.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell obj = node.getChildAt(i);
			if (obj instanceof BNode) {
				return node;
			} else {
				mxICell root = findRoot(obj);
				if (root != null) {
					return root;
				}
			}
		}

		return node.getChildAt(0);
	}

	@Override
	public void windowDeactived() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProject(BProject project) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeErrorLine(Object cell) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tabCloseable() {
		return true;
	}

	@Override
	public void receiveClipboard(String value) {

	}

}
