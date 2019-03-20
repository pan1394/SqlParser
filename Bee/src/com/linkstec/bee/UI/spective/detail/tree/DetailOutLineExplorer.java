package com.linkstec.bee.UI.spective.detail.tree;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAlertor;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxICell;

public class DetailOutLineExplorer extends BEditorOutlookExplorer implements TreeWillExpandListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6097226282166709998L;

	public static class RefreshAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3370505448387086578L;

		private DetailOutLineExplorer explorer;

		public RefreshAction(DetailOutLineExplorer explorer) {
			this.explorer = explorer;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			this.explorer.update();
		}

	}

	private ValueNode root;
	private EditorBook book;

	protected void installKeyboardActions() {

		InputMap inputmap = SwingUtilities.getUIInputMap(this, JComponent.WHEN_FOCUSED);
		inputmap.put(KeyStroke.getKeyStroke("F5"), "refresh");

		ActionMap map = SwingUtilities.getUIActionMap(this);
		map.put("refresh", new RefreshAction(this));
	}

	public DetailOutLineExplorer() {
		this(new ValueNode());
		setCellRenderer(new ValueRender());
	}

	private boolean scanNode(BasicNode target, ValueNode node) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			ValueNode n = (ValueNode) node.getChildAt(i);

			DefaultTreeModel model = (DefaultTreeModel) this.getModel();

			Object obj = n.getUserObject();
			if (obj instanceof BClass) {
				return this.scanNode(target, n);
			} else if (obj != null && obj instanceof BasicNode) {
				BasicNode b = (BasicNode) obj;
				if (b.getId().equals(target.getId())) {
					TreePath path = new TreePath(model.getPathToRoot(n));
					this.setSelectionPath(path);

					Rectangle rect = this.getPathBounds(path);
					rect.x = 0;
					this.scrollRectToVisible(rect);

					return true;
				}

			} else if (obj != null && isScopeChild(obj, target)) {
				TreePath path = new TreePath(model.getPathToRoot(n));
				if (!this.isExpanded(path)) {
					this.expandPath(path);
				}

				return this.scanNode(target, n);
			}
		}
		return false;
	}

	private boolean isScopeChild(Object scope, BasicNode target) {
		if (scope instanceof BeeModel) {
			BeeModel model = (BeeModel) scope;
			scope = model.getRoot();
			Object root = this.foundRoot((mxICell) scope);
			if (root != null) {
				scope = root;
			}
		}
		mxICell parent = target.getParent();
		while (parent != null) {
			if (parent.equals(scope)) {
				return true;
			} else {
				parent = parent.getParent();
			}
		}
		return false;

	}

	public DetailOutLineExplorer(ValueNode node) {
		super(node);
		this.root = node;

		this.setScrollsOnExpand(true);
		this.expandPath(new TreePath(root));
		this.installKeyboardActions();

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				TreePath selPath = DetailOutLineExplorer.this.getPathForLocation(e.getX(), e.getY());
				if (selPath != null) {

					Object obj = selPath.getLastPathComponent();

					if (obj instanceof ValueNode) {
						ValueNode node = (ValueNode) obj;
						node.setSelected(true);
						if (node.getUserObject() instanceof mxICell) {
							mxICell cell = (mxICell) node.getUserObject();
							Object scope = node.getValueScope();
							if (scope instanceof mxICell) {
								cell = (mxICell) scope;
							}
							selectNode(cell);

						} else if (node.getUserObject() instanceof BeeModel) {
							BeeModel n = (BeeModel) node.getUserObject();
							BeeGraphSheet sheet = BeeActions.getSheetByModel(n);
							int index = Application.getInstance().getDesignSpective().getWorkspace().indexOfComponent(sheet);
							if (index > -1) {
								Application.getInstance().getDesignSpective().getWorkspace().setSelectedIndex(index);
							}

						}
					}
					setSelectionPath(selPath);
					e.consume();
				}

			}

		});
		this.addTreeWillExpandListener(this);
	}

	private void selectNode(mxICell cell) {
		int count = this.book.getTabCount();

		for (int i = 0; i < count; i++) {
			Component comp = book.getComponentAt(i);
			if (comp instanceof BeeGraphSheet) {
				BeeGraphSheet sheet = (BeeGraphSheet) comp;

				mxICell target = sheet.getModel().getPossibleCellById(cell.getId());
				if (target != null) {

					if (target.getClass().equals(cell.getClass())) {
						EditorBook book = (EditorBook) Application.getInstance().getDesignSpective().getWorkspace().getCurrentEditor();
						if (book != null) {
							book.setSelectedComponent(comp);
							sheet.getGraph().setSelectionCell(target);
							sheet.scrollCellToVisible(target);
							break;
						}
					}

				}

			}

		}
	}

	public EditorBook getBook() {
		return this.book;
	}

	public void setBook(EditorBook book) {

		this.book = book;
		if (book == null) {
			this.root.removeAllChildren();
			this.updateUI();
			return;
		}

		this.makeAll(book, root);
		this.updateUI();
	}

	private void addComponent(BClass bclass, ValueNode root) {

		boolean contained = false;
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			ValueNode node = (ValueNode) root.getChildAt(i);
			if (node.getUserObject().equals(bclass)) {
				contained = true;
			}
		}
		if (!contained) {

			ValueNode c = new ValueNode() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 2437898802576517535L;

				@Override
				public boolean isError() {
					BAlertor alert = bclass.getAlertObject();
					return alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_ERROR);
				}

				@Override
				public boolean isAlert() {
					BAlertor alert = bclass.getAlertObject();
					return alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_WARNING);
				}

			};
			c.setUserObject(bclass);
			c.setImageIcon(BeeConstants.CLASSES_ICON);
			c.setInited(true);

			root.add(c);
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();
			this.expandPath(new TreePath(model.getPathToRoot(c)));

			this.makeModelNodes(bclass, c);
		}
	}

	private mxICell foundRoot(mxICell node) {
		int count = node.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell obj = node.getChildAt(i);
			if (obj instanceof BasicNode) {
				return node;
			} else {
				mxICell root = this.foundRoot(obj);
				if (root != null) {
					return root;
				}
			}
		}

		return null;
	}

	private static Thread RefreshThread;

	private void makeMethodNodes(BMethod s, ValueNode parent) {
		if (s == null)
			return;

		ValueNode c = new ValueNode();
		c.setUserObject(s);
		c.setImageIcon(BeeConstants.METHOD_ICON);
		parent.add(c);

		List<BParameter> paras = s.getParameter();
		if (paras != null && !paras.isEmpty()) {
			c.setLeaf(false);
		}

		if (!s.getLogicBody().getUnits().isEmpty()) {
			c.setLeaf(false);
		}

		if (s.getBClass() != null && !s.getBClass().isPrimitive()) {
			c.setLeaf(false);
		}

	}

	private void makeAssignNodes(BAssignment node, ValueNode parent) {
		if (node == null)
			return;

		ValueNode c = new ValueNode();
		c.setUserObject(node);
		c.setImageIcon(BeeConstants.VAR_PRIVATE_ICON);
		parent.add(c);
		c.setLeaf(false);

	}

	private void makeModelNodes(BClass bclass, ValueNode parent) {

		if (bclass == null)
			return;

		List<BAssignment> vars = bclass.getVariables();
		for (BAssignment assign : vars) {
			this.makeAssignNodes(assign, parent);
		}
		List<BConstructor> cs = bclass.getConstructors();
		for (BConstructor method : cs) {
			this.makeMethodNodes(method, parent);
		}
		List<BMethod> methods = bclass.getMethods();
		for (BMethod method : methods) {
			this.makeMethodNodes(method, parent);
		}

	}

	@Override
	public void update() {

		if (RefreshThread == null) {
			RefreshThread = new Thread(new Runnable() {

				@Override
				public synchronized void run() {
					ValueNode temp = (ValueNode) root.cloneAll();
					makeAll(book, root);
					DefaultTreeModel model = (DefaultTreeModel) getModel();
					model.nodeStructureChanged(root);
					temp.setExpanded(true);
					makeExpand(root, temp);
				}
			});
		}

		RefreshThread.run();

	}

	private void makeExpand(ValueNode node, ValueNode temp) {
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		int count = node.getChildCount();
		if (temp.isSelected()) {
			TreePath select = new TreePath(model.getPathToRoot(node));
			this.setSelectionPath(select);
		}
		for (int i = 0; i < count; i++) {
			ValueNode child = (ValueNode) node.getChildAt(i);
			String key = child.getUniqueKey();
			ValueNode old = this.findOldNode(key, temp);
			if (old != null) {
				if (old.isExpanded()) {

					TreePath path = new TreePath(model.getPathToRoot(child));
					this.expandPath(path);
					this.makeExpand(child, temp);
				}
			}
		}
	}

	private ValueNode findOldNode(String key, ValueNode temp) {
		int count = temp.getChildCount();
		for (int i = 0; i < count; i++) {
			ValueNode child = (ValueNode) temp.getChildAt(i);
			if (child.getUniqueKey().equals(key)) {
				return child;
			}

			ValueNode next = findOldNode(key, child);
			if (next != null) {
				return next;
			}
		}
		return null;
	}

	private void updateNode(ValueNode old, ValueNode temp) {

		Hashtable<ValueNode, ValueNode> sames = new Hashtable<ValueNode, ValueNode>();
		List<ValueNode> removes = new ArrayList<ValueNode>();
		List<ValueNode> adds = new ArrayList<ValueNode>();

		int count = temp.getChildCount();
		for (int i = 0; i < count; i++) {
			ValueNode node = (ValueNode) temp.getChildAt(i);
			ValueNode oldNode = (ValueNode) this.findSameNode(old, node);
			if (oldNode == null) {
				adds.add(node);
			} else {
				sames.put(oldNode, node);
			}
		}

		count = old.getChildCount();
		for (int i = 0; i < count; i++) {
			ValueNode node = (ValueNode) old.getChildAt(i);
			ValueNode newNode = (ValueNode) this.findSameNode(temp, node);
			if (newNode == null) {
				removes.add(node);
			}
		}

		for (ValueNode node : adds) {
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();

			int index = model.getChildCount(old);
			model.insertNodeInto(node, old, index);
		}

		for (ValueNode node : removes) {
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();
			if (node.getParent() != null) {
				model.removeNodeFromParent(node);
			}
		}

		Enumeration<ValueNode> keys = sames.keys();
		while (keys.hasMoreElements()) {

			DefaultTreeModel model = (DefaultTreeModel) this.getModel();

			ValueNode oldNode = keys.nextElement();
			ValueNode node = sames.get(oldNode);

			int index = model.getIndexOfChild(old, oldNode);
			model.removeNodeFromParent(oldNode);
			model.insertNodeInto(node, old, index);

			this.makeSubExpended(oldNode, node);
		}

	}

	private void makeSubExpended(ValueNode old, ValueNode node) {
		boolean expaned = old.isExpanded();
		if (expaned) {
			if (!node.isExpanded()) {
				this.expandPath(new TreePath(node));
				// DefaultTreeModel model = (DefaultTreeModel) this.getModel();

				int count = node.getChildCount();

				for (int i = 0; i < count; i++) {
					ValueNode newNode = (ValueNode) node.getChildAt(i);

					// node.remove(newNode);
					// model.insertNodeInto(newNode, node, i);

					ValueNode oldNode = (ValueNode) this.findSameNode(old, newNode);
					if (oldNode != null) {
						makeSubExpended(oldNode, newNode);
					}
				}
			}
		}
	}

	private void makeAll(EditorBook book, ValueNode root) {
		// clearSelection();
		if (book == null) {
			root.removeAllChildren();
			root.setUserObject("");
			return;
		}

		root.setUserObject(book);
		if (root.getChildCount() > 0) {
			root.removeAllChildren();
		}
		root.setImageIcon(BeeConstants.EXPLORE_FILE_TILE_ICON);
		Application.getInstance().getDesignSpective().getExplore().setOutLineSelected();

		List<BClass> list = book.getBookModel().getClassList();

		for (BClass logic : list) {
			BValuable s = logic.getSuperClass();
			if (s != null) {
				BClass sc = s.getBClass();
				Class<?> cls = CodecUtils.getClassByName(sc.getQualifiedName(), book.getProject());
				this.makeSuperClass(cls, root, (DefaultTreeModel) this.getModel());
			} else {
				this.makeSuperClass(Object.class, root, (DefaultTreeModel) this.getModel());
			}
			addComponent(logic, root);
		}
		expandPath(new TreePath(root));
	}

	private void makeSuperClass(Class<?> cls, ValueNode parent, DefaultTreeModel model) {

		ValueNode item = new ValueNode();
		item.setUserObject(cls);
		item.setImageIcon(BeeConstants.CLASSES_ICON);
		item.setInited(true);
		item.setSuper(true);

		model.insertNodeInto(item, parent, 0);

		Field[] fs = cls.getDeclaredFields();

		for (Field f : fs) {

			if (Modifier.isProtected(f.getModifiers())) {
				ValueLogicHelper.createFieldNode(f, item, model);
			}
		}
		fs = cls.getFields();
		for (Field f : fs) {
			String name = f.getName();
			if (!Modifier.isPrivate(f.getModifiers())) {
				if (!name.equals("serialVersionUID")) {
					ValueLogicHelper.createFieldNode(f, item, model);
				}
			}
		}

		Method[] methods = cls.getDeclaredMethods();
		for (Method m : methods) {
			if (Modifier.isProtected(m.getModifiers())) {
				ValueLogicHelper.createMethodNode(m, item, model);
			}
		}
		methods = cls.getMethods();
		for (Method m : methods) {
			if (!Modifier.isAbstract(m.getModifiers())) {
				ValueLogicHelper.createMethodNode(m, item, model);
			}
		}

	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		ValueNode node = (ValueNode) event.getPath().getLastPathComponent();
		ValueLogicHelper.expand(node, (DefaultTreeModel) this.getModel(), book.getProject());
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

	@Override
	public void setEditor(BEditor editor) {
		this.setBook((EditorBook) editor);

	}

	@Override
	public void setSelected(Object node) {

		if (node != null) {
			if (!this.scanNode((BasicNode) node, this.root)) {
				this.clearSelection();
			}
		}
	}
}
