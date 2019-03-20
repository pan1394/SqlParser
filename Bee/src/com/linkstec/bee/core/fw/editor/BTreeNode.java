package com.linkstec.bee.core.fw.editor;

import javax.swing.tree.TreeNode;

public interface BTreeNode {

	int getChildCount();

	TreeNode getChildAt(int i);

	boolean isLeaf();

	String getFilePath();

	BProject getProject();

}
