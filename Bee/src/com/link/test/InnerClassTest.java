package com.link.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;

import com.linkstec.bee.core.codec.util.CodecUtils;

public class InnerClassTest extends JPanel implements ActionListener {

	public InnerClassTest() {
		// TODO Auto-generated constructor stub
	}

	class inner {

	}

	public static void main(String[] args) {

		List<Class<?>> clss = new ArrayList<Class<?>>();
		clss.add(JPanel.class);
		clss.add(JTree.class);
		clss.add(JButton.class);
		clss.add(String.class);
		Class target = CodecUtils.getAllParent(clss);
		System.out.println(target.getName());

	}

	private static List<Class> getAllClass(Class cls, List<Class> list) {

		Class[] clss = cls.getInterfaces();
		for (Class c : clss) {
			if (!list.contains(c)) {
				list.add(c);
			}
			getAllClass(c, list);
		}
		Class superClass = cls.getSuperclass();
		if (superClass != null) {
			if (!list.contains(superClass)) {
				list.add(superClass);
			}
			getAllClass(superClass, list);
		}
		return list;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
