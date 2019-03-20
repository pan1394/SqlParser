package com.linkstec.bee.UI.spective.basic.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.tab.BeeCloseable;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.core.fw.editor.BProject;

public class CodeConfig extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -109117433860560087L;
	private BProject project;
	private JButton apply, cancel, applyAndClose;
	private ConfigModel model;
	private BeeTabbedPane pane;

	public CodeConfig(BProject project) {
		this.setTitle(project.getName() + "コード化設定");
		this.project = project;
		this.setOpacity(1);
		this.setModal(true);

		model = ConfigModel.load(project);

		this.setLayout(new BorderLayout());
		this.add(this.makeContents(), BorderLayout.CENTER);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(size.width * 3 / 4, size.height * 3 / 4);
		this.setLocation(size.width / 8, size.height / 8);
	}

	private JPanel makeContents() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		panel.setLayout(layout);
		panel.add(this.makePane(), BorderLayout.CENTER);
		panel.setBackground(Color.WHITE);

		panel.add(this.makeButtons(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel makeButtons() {

		int s = BeeUIUtils.getDefaultFontSize();

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(s, s, s, s));

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.RIGHT);
		panel.setLayout(layout);

		this.applyAndClose = new JButton("適用後クローズ");
		this.applyAndClose.setUI(new BeeButtonUI());
		this.applyAndClose.setPreferredSize(new Dimension(s * 15, s * 2));
		this.applyAndClose.addActionListener(this);

		this.apply = new JButton("適用");
		this.apply.setUI(new BeeButtonUI());
		this.apply.setPreferredSize(new Dimension(s * 5, s * 2));
		this.apply.addActionListener(this);
		this.cancel = new JButton("キャンセル");
		this.cancel.setPreferredSize(new Dimension(s * 10, s * 2));
		this.cancel.addActionListener(this);
		this.cancel.setUI(new BeeButtonUI());

		panel.add(cancel);
		panel.add(apply);

		panel.add(applyAndClose);
		return panel;
	}

	private JTabbedPane makePane() {
		pane = new BeeTabbedPane();
		// pane.setEditable(false);
		pane.setBarEdiable(false);
		pane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JScrollPane p = (JScrollPane) pane.getSelectedComponent();
				Component comp = p.getViewport().getView();
				if (comp != null && comp instanceof Config) {
					Config config = (Config) comp;
					config.onSelected();
				}

			}

		});
		String path = this.getClass().getPackage().getName();
		path = path.replace('.', '/');

		try {

			Enumeration<URL> urls = this.getClass().getClassLoader().getResources(path);
			int index = 0;
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String name = url.getPath().substring(1);
				String root = name.substring(0, name.indexOf(path));
				File file = new File(name);
				File[] files = file.listFiles();
				for (File f : files) {
					try {
						String className = f.getAbsolutePath().substring(root.length());
						if (className.lastIndexOf('.') < 0) {
							continue;
						}
						className = className.substring(0, className.lastIndexOf('.'));
						className = className.replace(File.separatorChar, '.');
						Class<?> cls = this.getClass().getClassLoader().loadClass(className);
						if (Config.class.isAssignableFrom(cls) && !cls.equals(Config.class) && !cls.equals(this.getClass())) {
							Constructor<?> c = cls.getConstructor(BProject.class, ConfigModel.class);
							Config config = (Config) c.newInstance(this.project, this.model);
							if (!config.Debug()) {
								pane.insertTab(config.getTitle(), config.getIcon(), makeTab(config), config.getTitle(), index);
								index++;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pane;
	}

	private JScrollPane makeTab(JPanel panel) {
		ConfigScroll pane = new ConfigScroll(panel);
		pane.getVerticalScrollBar().setUnitIncrement(50);
		pane.setBorder(null);
		return pane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.cancel)) {
			this.setVisible(false);
		} else if (e.getSource().equals(this.apply)) {
			apply();
			model.save(project);
		} else if (e.getSource().equals(this.applyAndClose)) {
			apply();
			this.setVisible(false);
		}

	}

	private void apply() {
		int count = this.pane.getTabCount();
		for (int i = 0; i < count; i++) {
			JScrollPane p = (JScrollPane) this.pane.getComponentAt(i);

			Component c = p.getViewport().getView();
			if (c instanceof Config) {
				Config config = (Config) c;
				config.beforeSave();
			}
		}
		model.save(project);
	}

	public static class ConfigScroll extends JScrollPane implements BeeCloseable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7937520979935644173L;

		public ConfigScroll() {
			super();

		}

		public ConfigScroll(Component view, int vsbPolicy, int hsbPolicy) {
			super(view, vsbPolicy, hsbPolicy);

		}

		public ConfigScroll(Component view) {
			super(view);

		}

		public ConfigScroll(int vsbPolicy, int hsbPolicy) {
			super(vsbPolicy, hsbPolicy);
		}

		@Override
		public boolean tabCloseable() {
			return false;
		}

	}
}
