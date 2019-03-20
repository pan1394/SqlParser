package com.linkstec.bee.core.codec.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.editor.BEditorModel;

public class BeeNamingUtil {

	private static List<BeeName> names = new ArrayList<BeeName>();

	public static String getBooleanGetter(String name) {
		return "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static String getGetter(String name) {
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static String getSetter(String name) {
		return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static BeeName makeBookName() {
		int count = 0;
		String type = BeeName.TYPE_BOOK;
		BeeName bname = new BeeName();
		bname.setModel((BEditorModel) CodecUtils.BString());
		for (BeeName name : names) {
			if (name.getType().equals(BeeName.TYPE_BOOK)) {
				for (int i = 0; i < 10000; i++) {
					String logicName = name.getLogicName();
					if (logicName.startsWith(BeeName.TYPE_BOOK)) {
						logicName = logicName.substring(BeeName.TYPE_BOOK.length());
						if (!logicName.equals("")) {
							try {
								int logicNameInt = Integer.parseInt(logicName);
								count = logicNameInt;
							} catch (Exception e) {

							}
						}
					}
				}
			}
		}
		count++;
		String name = getName(type) + count;
		String logicName = type + count;
		while (inOutLine(name, logicName)) {
			count++;
			name = getName(type) + count;
			logicName = type + count;
		}

		bname.setLogicName(logicName);
		bname.setName(name);
		bname.setType(type);
		names.add(bname);
		return bname;
	}

	public static BeeName makeName(BEditorModel model, String type) {
		int count = 0;
		BeeName bname = new BeeName();
		bname.setModel(model);
		if (type.equals(BeeName.TYPE_CLASS)) {
			Hashtable<BEditorModel, String> hash = new Hashtable<BEditorModel, String>();
			for (BeeName name : names) {
				hash.put(name.getModel(), "");
			}
			count = hash.size() - 1;
		} else {
			for (BeeName name : names) {
				if (name.getModel().equals(model)) {
					if (name.getType().equals(type)) {
						for (int i = 0; i < 10000; i++) {
							String logicName = name.getLogicName();
							if (logicName.startsWith(type)) {
								logicName = logicName.substring(type.length());
								if (!logicName.equals("")) {
									try {
										int logicNameInt = Integer.parseInt(logicName);
										count = logicNameInt;
									} catch (Exception e) {

									}
								}
							}
						}
					}
				}
			}
		}
		count++;
		String name = getName(type) + count;
		String logicName = type + count;
		while (inOutLine(name, logicName)) {
			count++;
			name = getName(type) + count;
			logicName = type + count;
		}

		bname.setLogicName(logicName);
		bname.setName(name);
		bname.setType(type);
		names.add(bname);
		return bname;
	}

	private static boolean inOutLine(String name, String logicName) {

		BeeTreeNode root = Application.getInstance().getDesignSpective().getOutline().getRoot();
		if (root.getChildCount() == 0) {
			return false;
		}
		BeeTreeNode child = (BeeTreeNode) root.getFirstChild();
		while (child != null) {
			Object obj = child.getUserObject();
			if (obj instanceof ILogic) {
				ILogic logic = (ILogic) obj;
				if (name.equals(logic.getName()) || logicName.equals(logic.getLogicName())) {
					return true;
				}
			}
			child = (BeeTreeNode) child.getNextNode();
		}
		return false;
	}

	private static String getName(String type) {
		switch (type) {
		case BeeName.TYPE_CLASS:
			return "設計";
		case BeeName.TYPE_VAR:
			return "変数";
		case BeeName.TYPE_METHOD:
			return "処理";
		case BeeName.TYPE_BOOK:
			return "設計";
		}
		return null;
	}
}
