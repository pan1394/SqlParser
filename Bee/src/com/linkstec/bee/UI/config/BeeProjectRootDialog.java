package com.linkstec.bee.UI.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;

public class BeeProjectRootDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6914958549547563852L;
	private JTextField text = new JTextField();

	private JLabel error = new JLabel();
	private String path;
	private Configuration config;

	public BeeProjectRootDialog() {
		this.setTitle("プロジェクト設定");
		config = new Configuration();
		int gap = BeeUIUtils.getDefaultFontSize();
		Container con = this.getContentPane();

		BorderLayout border = new BorderLayout();
		border.setHgap(gap);
		border.setVgap(gap);
		con.setLayout(border);

		error.setForeground(Color.RED);
		error.setPreferredSize(new Dimension(gap * 20, gap * 2));
		con.add(error, BorderLayout.NORTH);
		error.setBorder(new EmptyBorder(gap, gap, gap, gap));

		JPanel c = new JPanel();
		c.setBorder(new EmptyBorder(gap, gap, gap, gap));
		con.add(c, BorderLayout.CENTER);
		c.setLayout(new GridLayout(0, 1));

		JPanel pathrow = new JPanel();
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEADING);
		pathrow.setLayout(flow);
		c.add(pathrow);
		text.setPreferredSize(new Dimension(gap * 20, gap * 2));
		JLabel name = new JLabel("ワークスペース");
		name.setPreferredSize(new Dimension(gap * 7, gap * 2));
		JButton fold = new JButton();
		fold.setName("FOLD");
		fold.addActionListener(this);
		fold.setIcon(BeeConstants.FOLDER_ICON);
		pathrow.add(name);
		pathrow.add(text);
		pathrow.add(fold);

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(gap, gap, gap, gap));
		FlowLayout l = new FlowLayout();
		l.setAlignment(FlowLayout.RIGHT);
		JButton submit = new JButton("適用");
		submit.addActionListener(this);
		submit.setName("SUBMIT");
		submit.setPreferredSize(new Dimension(gap * 7, gap * 2));
		JButton cancel = new JButton("キャンセル");
		cancel.addActionListener(this);
		cancel.setName("CANCEL");
		cancel.setPreferredSize(new Dimension(gap * 7, gap * 2));
		buttons.add(submit);
		buttons.add(cancel);
		con.add(buttons, BorderLayout.SOUTH);

		this.pack();
		Dimension d = this.getContentPane().getSize();
		this.setIconImage(BeeConstants.CONFIG_ICON.getImage());

		Dimension size = BeeUIUtils.getScreenSize();
		this.setLocation((size.width - d.width) / 2, (size.height - d.height) / 2);

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

		});
		this.setModal(true);
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof JButton) {
			JButton b = (JButton) obj;
			if (b.getName().equals("FOLD")) {
				JFileChooser dialog = new JFileChooser();

				dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				dialog.showOpenDialog(this);
				File s = dialog.getSelectedFile();
				if (s != null) {
					text.setText(s.getAbsolutePath());
					error.setText("");
				}
			} else if (b.getName().equals("SUBMIT")) {

				String s = text.getText();
				File f = new File(s);
				if (!f.exists()) {
					error.setText("有効なディレクトリーではありません。");
					return;
				} else {
					if (!f.isDirectory()) {
						error.setText("有効なディレクトリーではありません。");
						return;
					} else {
						this.config.setWorkSpace(s);
					}
				}
				this.setVisible(false);
			} else if (b.getName().equals("CANCEL")) {
				System.exit(0);
			}

		}

	}

	public Configuration getConfig() {
		return this.config;
	}

}
