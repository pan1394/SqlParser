package com.linkstec.bee.UI.spective.detail.tree;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenu;
import com.linkstec.bee.UI.look.tip.BeeToolTip;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.ClassInfos;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.javadoc.DocClass;
import com.linkstec.bee.core.javadoc.DocMethod;
import com.linkstec.bee.core.javadoc.DocReader;

public class ClassesTree extends BasicMenu implements TreeWillExpandListener {
	private ValueNode root;
	private BeeTree tree;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1976147303364723903L;

	public ClassesTree(BProject project) {
		super(project);

	}

	protected void init() {
		root = new ValueNode();
		tree = new BeeTree(root) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4275686737568389114L;

			@Override
			public String getToolTipText() {
				return getTootip(tree);
			}

		};
		BeeToolTip.getInstance().register(tree);
		tree.setCellRenderer(new ValueRender());
		this.contents.add(tree);
		tree.addTreeWillExpandListener(this);
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_TITLE_VAR_ICON;
	}

	@Override
	protected void addAllItems(String text) {
		this.root.removeAllChildren();
		if (text == null || text.trim().equals("")) {
			BClass[] bs = CodecUtils.getAllBClasses();
			if (bs != null) {
				for (BClass name : bs) {

					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					ValueLogicHelper.createClassNode(name.getQualifiedName(), root, model);

				}
			}
		} else {
			List<String> names;
			try {
				names = ClassInfos.lookupClass(text);
				for (String name : names) {
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					ValueLogicHelper.createClassNode(name, root, model);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		tree.updateUI();
		tree.expandPath(new TreePath(root));
	}

	private String getTootip(BeeTree tree) {
		Point p = tree.getMousePosition();
		if (p != null) {
			TreePath path = tree.getPathForLocation(p.x, p.y);
			if (path == null) {
				return null;
			}
			ValueNode node = (ValueNode) path.getLastPathComponent();
			Object obj = node.getUserObject();
			if (obj instanceof String) {
				String className = (String) obj;
				DocClass doc = this.readClassJavaDoc(className);
				if (doc == null) {
					return "";
				}

				if (node.getLogicType() == ValueNode.LOGIC_ARRAY) {
					className = className + "[]";
				}
				return "<div><span style='color:green'>・</span>" + className + "</div><hr/>" + doc.getComment();

			} else if (obj instanceof Method) {
				Method method = (Method) obj;
				Class<?> cls = method.getDeclaringClass();
				DocClass doc = this.readClassJavaDoc(cls.getName());
				if (doc == null) {
					return "";
				}
				String s = doc.getName();

				List<DocMethod> list = doc.getMethodList();
				for (DocMethod m : list) {
					if (m.getName().equals(method.getName())) {
						s = "<div><span style='color:green'>・</span>" + s + "." + m.getName() + "()</div><hr/>"
								+ m.getComment();
						break;
					}
				}
				return s;
			}
			// return obj.toString();
		}
		return null;
	}

	private DocClass readClassJavaDoc(String className) {

		String path = "/" + className.replace('.', '/') + ".d";

		InputStream is = DocReader.class.getResourceAsStream(path);
		if (is != null) {
			try {

				BufferedInputStream bis = new BufferedInputStream(is);

				ObjectInputStream in = new ObjectInputStream(bis);
				DocClass d = (DocClass) in.readObject();
				in.close();

				return d;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		ValueNode item = (ValueNode) event.getPath().getLastPathComponent();
		ValueLogicHelper.expand(item, (DefaultTreeModel) tree.getModel(),
				Application.getInstance().getCurrentProject());
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}
}
