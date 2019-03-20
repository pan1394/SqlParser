package com.linkstec.bee.UI.spective.code.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BMod;
import com.linkstec.bee.core.impl.BObjectImpl;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;

public class SourceOutLineExplorer extends BEditorOutlookExplorer implements TreeExpansionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6097226282166709998L;

	private BeeTreeNode root;
	private File file;
	private BProject project;

	protected void installKeyboardActions() {

		InputMap inputmap = SwingUtilities.getUIInputMap(this, JComponent.WHEN_FOCUSED);
		inputmap.put(KeyStroke.getKeyStroke("F5"), "refresh");

		ActionMap map = SwingUtilities.getUIActionMap(this);
		map.put("refresh", new RefreshAction(this));
	}

	public SourceOutLineExplorer() {
		this(new BeeTreeNode(""));
	}

	private SourceOutLineExplorer(BeeTreeNode node) {
		super(node);
		this.root = node;
		this.setScrollsOnExpand(true);
		this.expandPath(new TreePath(root));
		this.installKeyboardActions();

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				TreePath selPath = SourceOutLineExplorer.this.getPathForLocation(e.getX(), e.getY());
				if (selPath != null) {
					setSelectionPath(selPath);
				}
				// e.consume();
			}

			@Override
			public void mouseClicked(MouseEvent e) {

				TreePath selPath = SourceOutLineExplorer.this.getPathForLocation(e.getX(), e.getY());
				if (selPath != null) {
					Object obj = selPath.getLastPathComponent();
					if (obj instanceof BeeTreeNode) {
						BeeTreeNode node = (BeeTreeNode) obj;
						Object value = node.getUserObject();
						if (value instanceof BObjectImpl) {
							BObjectImpl impl = (BObjectImpl) value;
							long line = impl.getLine();
							String word = node.getValue().toString();
							doNodeClicked(line, word);
						}
					}
				}

			}

		});

		this.addTreeExpansionListener(this);
	}

	private void doNodeClicked(long line, String word) {
		sheet.setSelected(line, word);
	}

	private BeeSourceSheet sheet;

	private void setSource(BeeSourceSheet sheet, File file, BProject project) throws IOException {
		Application.getInstance().getJavaSourceSpective().setOutlineSelected();
		this.sheet = sheet;
		this.file = file;
		this.project = project;
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		if (file == null) {
			return;
		}

		if (project == null) {
			return;
		}

		JavacTask javacTask = BeeCompiler.scan(project, file.getAbsolutePath());
		DocTrees trees = DocTrees.instance(javacTask);
		Iterable<? extends CompilationUnitTree> result = javacTask.parse();
		Iterator<? extends CompilationUnitTree> ite = result.iterator();
		while (ite.hasNext()) {
			CompilationUnitTree unit = ite.next();

			LineMap map = unit.getLineMap();
			SourceScanner scanner = new SourceScanner(unit, map, trees);
			scanner.scan(unit, null);
			if (root.getChildCount() != 0) {
				this.root.removeAllChildren();
			}

			BClass bclass = scanner.getBClass();
			BeeTreeNode node = this.makeClassNode(bclass, this.root);

			List<BAssignment> vars = bclass.getVariables();
			for (BAssignment a : vars) {
				this.makeVarNodes(a, node);
			}

			List<BMethod> methods = bclass.getMethods();
			for (BMethod m : methods) {
				this.makeMethodNodes(m, node);

			}

			this.expandPath(new TreePath(model.getPathToRoot(node)));
		}
		model.reload();
		// this.update();
	}

	private static Thread RefreshThread;

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		BeeTreeNode node = (BeeTreeNode) event.getPath().getLastPathComponent();
		node.setExpanded(true);
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {

	}

	@Override
	public void update() {
		this.updateUI();
	}

	private BeeTreeNode makeClassNode(BClass cell, BeeTreeNode parent) {
		if (cell == null)
			return null;
		BeeTreeNode c = new BeeTreeNode(cell.getLogicName());
		c.setUserObject(cell);
		c.setLeaf(false);
		parent.add(c);
		c.setImageIcon(BeeConstants.CLASSES_ICON);

		return c;
	}

	private BeeTreeNode makeMethodNodes(BMethod cell, BeeTreeNode parent) {
		if (cell == null)
			return null;
		BeeTreeNode c = new BeeTreeNode(cell.getLogicName());
		c.setUserObject(cell);
		c.setLeaf(true);
		parent.add(c);

		if (Modifier.isStatic(cell.getModifier())) {
			c.setImageIcon(BeeConstants.METHOD_STATIC_ICON);
		} else if (Modifier.isPrivate(cell.getModifier())) {
			c.setImageIcon(BeeConstants.METHOD_PRIVATE_ICON);
		} else if (Modifier.isProtected(cell.getModifier())) {
			c.setImageIcon(BeeConstants.METHOD_PROTECED_ICON);
		} else {
			c.setImageIcon(BeeConstants.METHOD_ICON);
		}

		return c;
	}

	private BeeTreeNode makeVarNodes(BAssignment assign, BeeTreeNode parent) {
		if (assign == null)
			return null;

		BParameter cell = assign.getLeft();
		BeeTreeNode c = new BeeTreeNode(cell.getLogicName());
		c.setUserObject(cell);
		c.setLeaf(true);
		parent.add(c);

		if (Modifier.isStatic(cell.getModifier())) {
			c.setImageIcon(BeeConstants.VAR_STATIC_ICON);
		} else if (Modifier.isPrivate(cell.getModifier())) {
			c.setImageIcon(BeeConstants.VAR_PRIVATE_ICON);
		} else if (Modifier.isProtected(cell.getModifier())) {
			c.setImageIcon(BeeConstants.VAR_PROTECED_ICON);
		} else {
			c.setImageIcon(BeeConstants.VAR_ICON);
		}

		return c;
	}

	public static class SourceScanner extends TreeScanner<Object, Object> {

		private IPatternCreator temp = PatternCreatorFactory.createTempPattern();
		private BClass bclass = temp.createClass();

		protected CompilationUnitTree unitTree;
		protected LineMap map;
		protected DocTrees trees;

		public SourceScanner(CompilationUnitTree unitTree, LineMap map, DocTrees trees) {
			this.unitTree = unitTree;
			this.map = map;
			this.trees = trees;
		}

		public BClass getBClass() {
			return this.bclass;
		}

		@Override
		public Object visitClass(ClassTree node, Object p) {
			String name = node.getSimpleName().toString();
			bclass.setLogicName(name);
			int mobj = (int) node.getModifiers().accept(this, null);
			bclass.setModifier(mobj);
			this.setLineNumber(node, (BObjectImpl) bclass);
			return super.visitClass(node, p);
		}

		@Override
		public Object visitMethod(MethodTree node, Object object) {
			BMethod method;

			if (node.getName().toString().equals("<init>")) {
				method = temp.createConstructor();
				method.setLogicName(this.bclass.getLogicName());
				this.bclass.getConstructors().add((BConstructor) method);
			} else {
				method = temp.createMethod();
				method.setLogicName(node.getName().toString());
				String name = node.getReturnType().toString();
				BVariable var = temp.createVariable();
				var.setLogicName(name);
				method.setReturn(var);

				int mobj = (int) node.getModifiers().accept(this, null);
				method.setModifier(mobj);
				this.bclass.getMethods().add(method);
			}

			List<? extends VariableTree> parameters = node.getParameters();
			for (VariableTree v : parameters) {
				BParameter p = this.getParameter(v);
				method.addParameter(p);
			}
			Tree rtree = node.getReturnType();
			if (rtree != null) {
				method.setReturn(this.getParameter(rtree));
			}
			this.setLineNumber(node, (BObjectImpl) method);

			return null;
		}

		@Override
		public Object visitModifiers(ModifiersTree node, Object p) {
			Set<javax.lang.model.element.Modifier> mods = node.getFlags();
			Iterator<javax.lang.model.element.Modifier> ite = mods.iterator();
			int m = 0;
			while (ite.hasNext()) {
				javax.lang.model.element.Modifier mod = ite.next();
				String name = mod.name();
				int type = BMod.getType(name.toLowerCase());
				m = m | type;

			}
			return m;
		}

		@Override
		public Object visitVariable(VariableTree node, Object object) {
			BParameter p = this.getParameter(node);

			BAssignment assign = temp.createAssignment();
			assign.setLeft(p);

			int mobj = (int) node.getModifiers().accept(this, null);
			p.setModifier(mobj);

			this.bclass.getVariables().add(assign);
			this.setLineNumber(node, (BObjectImpl) p);

			return null;
		}

		private BParameter getParameter(VariableTree node) {
			BParameter p = temp.createParameter();
			p.setLogicName(node.getName().toString());
			p.setName(p.getLogicName());
			BClass type = temp.createClass();
			type.setLogicName(node.getType().toString());
			p.setBClass(type);

			return p;
		}

		private BParameter getParameter(Tree node) {
			BParameter p = temp.createParameter();
			p.setLogicName(node.toString());
			p.setName(p.getLogicName());

			BClass type = temp.createClass();
			type.setLogicName(node.toString());
			p.setBClass(type);

			return p;
		}

		private void setLineNumber(Tree tree, BObjectImpl impl) {
			SourcePositions sourcePositions = trees.getSourcePositions();
			long pos = sourcePositions.getStartPosition(this.unitTree, tree);
			impl.setLine(map.getLineNumber(pos));

		}

	}

	@Override
	public void setEditor(BEditor editor) {
		if (editor instanceof BeeSourceSheet) {
			BeeSourceSheet sheet = (BeeSourceSheet) editor;
			try {
				this.setSource(sheet, editor.getFile(), editor.getProject());
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	@Override
	public void setSelected(Object node) {

	}

}
