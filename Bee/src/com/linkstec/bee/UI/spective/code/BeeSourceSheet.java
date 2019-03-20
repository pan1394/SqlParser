package com.linkstec.bee.UI.spective.code;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorManager;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.task.problem.BeeSourceError;
import com.linkstec.bee.UI.look.scroll.BeeScrollPane;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeSourceSheet extends BeeScrollPane implements BEditor, UndoableEditListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -66377652770879651L;
	protected StyleContext m_context;
	private File file;
	private int trimmedGap = 0;

	public void setFile(File file) {
		this.file = file;
		Application.getInstance().setCurrentProject(project);
		this.file = file;
		this.lastModified = this.file.lastModified();
		this.doSetSource();
	}

	private long lastModified = -1;

	protected DefaultStyledDocument m_doc;
	private MutableAttributeSet keyAttr, normalAttr, searchAttr, nosearchAttr, commentAttr, stringAttr, selectedAttr,
			errorAttr;
	private MutableAttributeSet inputAttributes = new RTFEditorKit().getInputAttributes();
	private String keyword;
	private List<Integer> found = new ArrayList<Integer>();

	private final static String[] _keys = new String[] { "private", "public", "class", "int", "double", "float",
			"static", "protected", "new", "long", "final", "char", "super", "this", "null", "if", "else", "do", "while",
			"try", "catch", "package", "import", "void", "return", "true", "false", "boolean", "continue", "break",
			"for", "switch", "case", "throw", "throws", "implements", "extends", "synchronized", "native", "finally" };

	private final static char[] _character = new char[] { '(', ')', ',', ';', ':', '\t', '\n', '+', '-', '*', '/', '{',
			'}', '.', '[', ']' };

	public static float FONT_SIZE = (float) (BeeUIUtils.getDefaultFontSize() * 1.2);
	public static String font = "Consolas";
	public static String commnetfont = "HGｺﾞｼｯｸM";
	// int indent = BeeUIUtils.getDefaultFontSize() * 5;
	public boolean inited = false;
	private BeeSourceComplier compiler;
	private BeeSourceSheetBorder border;
	private List<BeeSourceError> errors = new ArrayList<BeeSourceError>();
	private Font editorFont = new Font(font, Font.PLAIN, (int) (FONT_SIZE * 1.1));
	private BEditorManager manager;
	private String lastText = null;
	private BProject project;
	private int[] selected = null;

	private JTextPane textPane = new JTextPane() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5732604902181782290L;

		@Override
		public void paint(Graphics g) {

			super.paint(g);

			paintTextPane(g);
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		@Override
		public void setSize(Dimension d) {
			if (d.width < getParent().getSize().width) {
				d.width = getParent().getSize().width;
			}
			d.width += 100;
			super.setSize(d);
		}

		public void paintTextPane(Graphics g) {

			g.setColor(Color.GRAY);
			int h = g.getFontMetrics(editorFont).getHeight();

			for (BeeSourceError error : errors) {
				long line = error.getLine() - 1;
				// if (trimmedGap > 0) {
				// line--;
				// }
				Image img = BeeConstants.ERROR_ICON.getImage();
				int errorHeight = img.getHeight(this);
				int y = (int) (h * (line) + (h - errorHeight) / 2);
				g.drawImage(img, 0, y, this);
				error.setBound(new Rectangle(0, y, this.getWidth(), img.getHeight(this)));

				// draw error
				g.setColor(Color.RED);
				long errorStart = error.getStart() - trimmedGap;
				long errorEnd = error.getEnd() - trimmedGap;
				Rectangle startPoint = null, endPoint = null;

				// Element lineString = m_doc.getDefaultRootElement().getElement((int) line);
				// int lineStart = lineString.getStartOffset();

				try {
					startPoint = this.modelToView((int) errorStart);
					endPoint = this.modelToView((int) errorEnd);
				} catch (BadLocationException e) {
					System.out.println(error.getContents());
					continue;
				}
				if (startPoint != null && endPoint != null) {
					int tabWidth = 0;
					startPoint.x = (int) (startPoint.x + tabWidth);
					endPoint.x = (int) (endPoint.x + tabWidth);
					int stringWidth = endPoint.x - startPoint.x;
					int startX = startPoint.x;// + this.getBorder().getBorderInsets(this).left + this.getInsets().left;

					error.setBound(new Rectangle(startPoint.x, startPoint.y, stringWidth, startPoint.height));

					Font f = new Font(font, Font.BOLD, (int) (BeeUIUtils.getDefaultFontSize() * 0.5));
					g.setFont(f);
					FontMetrics mtrics = g.getFontMetrics();
					int cw = mtrics.stringWidth("^");
					int num = stringWidth / cw;
					for (int j = 0; j < num; j++) {
						g.drawString("^", startX + cw * j, startPoint.y + startPoint.height + mtrics.getAscent());
					}
				}
			}
		}

		@Override
		public String getToolTipText(MouseEvent e) {

			for (BeeSourceError error : errors) {
				Rectangle rect = error.getBound();
				if (rect != null) {
					if (rect.contains(e.getPoint())) {
						return error.getContents();
					}
				}
			}
			return null;
		}

		@Override
		public void setEditable(boolean b) {

			super.setEditable(b);
		}

	};

	public BeeSourceSheet(BProject project) {
		this.project = project;
		this.manager = new BEditorManager(this, new BeeSourceUndoManager());
		this.setViewportView(textPane);
		textPane.setOpaque(false);

		Color color = Color.decode("#E8F2FE");
		Color selectd = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);

		textPane.setFont(editorFont);

		m_context = new StyleContext();

		m_doc = new DefaultStyledDocument(m_context);

		textPane.setDocument(m_doc);

		keyAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(keyAttr, Color.decode("#990033"));
		StyleConstants.setBold(keyAttr, true);
		StyleConstants.setFontFamily(keyAttr, font);
		StyleConstants.setFontSize(keyAttr, (int) FONT_SIZE);
		// StyleConstants.setLeftIndent(keyAttr, indent);

		commentAttr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(commentAttr, commnetfont);
		StyleConstants.setFontSize(commentAttr, (int) (FONT_SIZE));
		StyleConstants.setForeground(commentAttr, Color.GREEN.darker().darker());
		StyleConstants.setBold(commentAttr, false);

		stringAttr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(stringAttr, font);
		StyleConstants.setFontSize(stringAttr, (int) FONT_SIZE);
		StyleConstants.setForeground(stringAttr, Color.BLUE.darker());
		StyleConstants.setBold(stringAttr, false);

		selectedAttr = new SimpleAttributeSet();
		StyleConstants.setBackground(selectedAttr, Color.decode("#0078D7"));
		StyleConstants.setForeground(selectedAttr, Color.WHITE);

		searchAttr = new SimpleAttributeSet();
		StyleConstants.setBackground(searchAttr, BeeConstants.BACKGROUND_COLOR);

		nosearchAttr = new SimpleAttributeSet();
		StyleConstants.setBackground(nosearchAttr, Color.WHITE);

		normalAttr = new SimpleAttributeSet();
		// normalAttr.addAttribute("Underline-Color", Color.red);
		StyleConstants.setBold(normalAttr, false);
		StyleConstants.setForeground(normalAttr, Color.black);
		StyleConstants.setFontFamily(normalAttr, font);
		StyleConstants.setFontSize(normalAttr, (int) FONT_SIZE);
		// StyleConstants.setLeftIndent(normalAttr, indent);

		// errorAttr = new SimpleAttributeSet();
		// StyleConstants.setComponent(errorAttr, new ErrorComponent());

		textPane.setOpaque(true);
		textPane.setBackground(Color.WHITE);

		textPane.setEditable(true);
		border = new BeeSourceSheetBorder();
		textPane.setBorder(border);
		textPane.getDocument().addUndoableEditListener(this);
		// textPane.getDocument().addDocumentListener(this);

		compiler = new BeeSourceComplier(this);
		ToolTipManager.sharedInstance().registerComponent(textPane);

		textPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					found.clear();
					clearSelected();
					repaint();
				}
			}

		});
		textPane.addCaretListener(new CaretListener() {
			private Rectangle lastRect = null;

			@Override
			public void caretUpdate(CaretEvent e) {
				int dot = e.getDot();
				int mark = e.getMark();

				if (mark < dot) {

					new Thread(new Runnable() {

						@Override
						public void run() {
							try {

								String text = m_doc.getText(mark, dot - mark);
								setKeyword(text);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							Graphics g = textPane.getGraphics();

							if (lastRect != null) {
								textPane.repaint(lastRect);
							}
							int h = g.getFontMetrics(editorFont).getHeight();
							Point p = textPane.getCaret().getMagicCaretPosition();
							if (p != null) {
								g.setColor(selectd);
								Rectangle rect = new Rectangle(textPane.getBorder().getBorderInsets(textPane).left, p.y,
										textPane.getWidth(), h);
								g.fillRect(rect.x, rect.y, rect.width, rect.height);

								lastRect = rect;
							}
						}

					}).start();

				}

			}

		});
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		found.clear();
		this.keyword = keyword;
		if (this.keyword != null) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					syntaxParse();
					repaint();
				}

			});

		}
	}

	public File getFile() {
		return this.file;
	}

	public void refresh() {
		setFile(this.file);
	}

	public BProject getProject() {
		return this.project;
	}

	public List<BeeSourceError> getErrors() {
		return this.errors;
	}

	public void addError(BeeSourceError error) {

		this.errors.add(error);

		// boolean test = false;
		// if (test) {
		// int start = (int) error.getStart();
		// int end = (int) error.getEnd();
		//
		// String s;
		// try {
		// s = m_doc.getText(start, end - start + 1);
		// SimpleAttributeSet errorAttr = new SimpleAttributeSet();
		// StyleConstants.setComponent(errorAttr, new ErrorComponent(s));
		// m_doc.setCharacterAttributes(start, end - start + 1, errorAttr, false);
		// } catch (BadLocationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		this.repaint();
		this.error(error.getLine() + "error", (int) (error.getLine() * this.getFontMetrics(editorFont).getHeight()),
				error.getContents(), error);
		this.repaint();
		this.textPane.repaint();
		Container parent = this.getParent();
		BeeTabbedPane pane = (BeeTabbedPane) parent;
		int index = pane.indexOfComponent(this);
		BeeTabCloseButton button = (BeeTabCloseButton) pane.getTabComponentAt(index);
		button.setModified(this.getManager().isModified());
		button.setError(true);
		button.repaint();

	}

	private void doSetSource() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[1024];
				String source = "";
				try {
					if (!file.exists() || file.isDirectory()) {
						return;
					}
					FileInputStream in = new FileInputStream(file);
					int len = 0;

					while ((len = in.read(buffer)) != -1) {
						source = source + new String(buffer, 0, len, "UTF-8");
					}
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					String trimmed = source;// .trim();
					// int gap = source.indexOf(trimmed);
					// if (gap > 0) {
					// trimmedGap = gap;
					// }
					textPane.setText(trimmed);
					lastText = trimmed;
					syntaxParse();

				} catch (Exception e) {
					e.printStackTrace();
				}
				inited = true;
				scrollRectToVisible(new Rectangle(0, 0, 10, 10));

				// lastText = textPane.getText();
				compiler.compile();

			}

		}).start();

	}

	public File save() {

		byte[] buffer = textPane.getText().getBytes();
		try {
			FileOutputStream out = new FileOutputStream(file);

			out.write(buffer);
			out.flush();

			out.close();
			this.clearError();
			BeeCompiler.compile(project, file.getAbsolutePath(), null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		buffer = null;
		this.getManager().setModified(false);

		return file;
	}

	private boolean isCharacter(char _ch) {
		for (int i = 0; i < _character.length; i++) {
			if (_ch == _character[i]) {
				return true;
			}
		}
		return false;
	}

	public void scrollToPoint(int start) {
		Rectangle startPoint;
		try {

			startPoint = textPane.modelToView(start);
			if (startPoint == null) {
				return;
			}
			int height = startPoint.y;
			Dimension rect = this.getSize();
			this.textPane.scrollRectToVisible(new Rectangle(0, height - rect.height / 2, this.getWidth(), rect.height));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void scrollToWord(int start, int end, int line) {

		if (start < 0) {
			return;
		}

		if (end < 0) {
			return;
		}
		if (start > end) {
			return;
		}

		String word = this.getText().substring(start, end);
		this.setSelected(line--, word);
		// scrollToPoint(start);
		// this.clearSelected();
		// this.selected = new int[] { start, end - start, (int) line };
		// m_doc.setCharacterAttributes(start, end - start, selectedAttr, false);
	}

	private int setKeyColor(String _key, int _start, int _length) {
		for (int i = 0; i < _keys.length; i++) {
			int li_index = _key.indexOf(_keys[i]);
			if (li_index < 0) {
				continue;
			}
			int li_legnth = li_index + _keys[i].length();
			if (li_legnth == _key.length()) {
				if (li_index == 0) {
					m_doc.setCharacterAttributes(_start, _keys[i].length(), keyAttr, false);
				} else {
					char ch_temp = _key.charAt(li_index - 1);
					if (isCharacter(ch_temp)) {
						m_doc.setCharacterAttributes(_start + li_index, _keys[i].length(), keyAttr, false);
					}
				}
			} else {
				if (li_index == 0) {
					char ch_temp = _key.charAt(_keys[i].length());
					if (isCharacter(ch_temp)) {
						m_doc.setCharacterAttributes(_start, _keys[i].length(), keyAttr, false);
					}
				} else {
					char ch_temp = _key.charAt(li_index - 1);
					char ch_temp_2 = _key.charAt(li_legnth);
					if (isCharacter(ch_temp) && isCharacter(ch_temp_2)) {
						m_doc.setCharacterAttributes(_start + li_index, _keys[i].length(), keyAttr, false);
					}
				}
			}
		}
		return _length + 1;
	}

	/**
	 * 
	 * @param _start
	 * @param _end
	 */
	private void dealText(int _start, int _end, int lineNumber) {
		String text = null;
		try {
			text = m_doc.getText(_start, _end - _start).toUpperCase();
		} catch (BadLocationException e) {
			System.out.println("start:" + _start);
			System.out.println("end:" + _end);
			e.printStackTrace();
		}

		if (text == null) {
			return;
		}
		int xStart = 0;

		m_doc.setCharacterAttributes(_start, text.length(), normalAttr, false);
		BeeSourceStringTokenizer st = new BeeSourceStringTokenizer(text);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s == null)
				return;
			xStart = st.getCurrPosition();
			setKeyColor(s.toLowerCase(), _start + xStart, s.length());
		}
		inputAttributes.addAttributes(normalAttr);

		m_doc.setCharacterAttributes(_start, text.length(), nosearchAttr, false);
		if (this.keyword != null) {

			if (text.contains(this.keyword.toUpperCase())) {
				int index = text.indexOf(this.keyword.toUpperCase());
				m_doc.setCharacterAttributes(_start + index, keyword.length(), searchAttr, false);

				int h = this.getFontMetrics(this.getFont()).getHeight();
				found.add(lineNumber * h);
			}
		}
		String s = text.trim();
		if (s.startsWith("*/") || s.startsWith("*") || s.startsWith("/*") || s.startsWith("//")) {
			m_doc.setCharacterAttributes(_start, text.length(), this.commentAttr, false);
		} else {
			int commentIndex = s.indexOf("//");
			boolean isjavaString = false;

			int stringStart = text.indexOf("\"");
			while (stringStart > -1) {
				text = text.substring(stringStart + 1);
				int length = text.indexOf("\"");
				if (length > 0) {
					if (_start + stringStart < commentIndex && _start + stringStart + length + 2 > commentIndex) {
						isjavaString = true;
					}
					m_doc.setCharacterAttributes(_start + stringStart, length + 2, this.stringAttr, false);
					text = text.substring(length + 1);
					_start = _start + length + stringStart + 2;
				}
				stringStart = text.indexOf("\"");

			}
			if (commentIndex > 0 && !isjavaString) {
				m_doc.setCharacterAttributes(_start + commentIndex + 1, text.length() - commentIndex, this.commentAttr,
						false);
			}
		}

	}

	public List<Integer> getSearchResult() {
		return found;
	}

	// private void dealSingleRow() {
	// Element root = m_doc.getDefaultRootElement();
	//
	// int cursorPos = textPane.getCaretPosition();
	// int line = root.getElementIndex(cursorPos);
	// Element para = root.getElement(line);
	// int start = para.getStartOffset();
	// int end = para.getEndOffset() - 1;
	// dealText(start, end, line);
	// }

	public void syntaxParse() {
		Element root = m_doc.getDefaultRootElement();
		int li_count = root.getElementCount();
		for (int i = 0; i < li_count; i++) {
			Element para = root.getElement(i);
			int start = para.getStartOffset();
			int end = para.getEndOffset() - 1;
			dealText(start, end, i);

		}
		// for (BeeSourceError error : errors) {
		// int start = (int) error.getStart();
		// int end = (int) error.getEnd();
		// m_doc.setCharacterAttributes(start, end - start, errorAttr, false);
		// }
	}

	public void setSelected(long line, String word) {
		Element root = m_doc.getDefaultRootElement();
		Element para = root.getElement((int) line - 1);

		int start = para.getStartOffset();
		int end = para.getEndOffset();
		try {

			String lineString = m_doc.getText(start, end - start);

			int index = lineString.indexOf(word);

			if (index == -1) {

				int look = 0;
				while (index < 0 && look < 10) {
					line++;
					look++;
					para = root.getElement((int) line - 1);
					if (para != null) {
						lineString = m_doc.getText(start, para.getEndOffset() - start);
						index = lineString.indexOf(word);
					}
				}

			}
			int fh = this.getFontMetrics(this.editorFont).getHeight();
			int height = (int) (fh * (line + 1));

			start = start + index;

			this.textPane.scrollRectToVisible(new Rectangle(0, height - this.getHeight() / 2, 0, this.getHeight()));

			this.clearSelected();
			this.selected = new int[] { start, word.length(), (int) line };
			m_doc.setCharacterAttributes(start, word.length(), selectedAttr, false);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void clearSelected() {
		if (this.selected != null) {
			this.dealText(this.selected[0], this.selected[0] + this.selected[1], this.selected[2]);
		}
	}

	public void clearError() {
		this.errors.clear();
		super.clearError();
		Container parent = this.getParent();
		BeeTabbedPane pane = (BeeTabbedPane) parent;
		int index = pane.indexOfComponent(this);
		BeeTabCloseButton button = (BeeTabCloseButton) pane.getTabComponentAt(index);
		button.setModified(this.manager.isModified());
		button.setError(false);
		button.repaint();
	}

	private void documentChanged() {
		if (this.inited) {
			String text = textPane.getText();
			if (!text.equals(this.lastText)) {

				this.clearError();
				this.setModified(true);
				compiler.compile();
				this.lastText = text;

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						syntaxParse();

					}

				});
			}
		}
	}

	private void refreshLine(int p) {
		// new BeeListedThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Element ele = m_doc.getParagraphElement(p);
		// dealText(ele.getStartOffset(), ele.getEndOffset(),
		// m_doc.getDefaultRootElement().getElementIndex(p));
		//
		// }
		// });

	}

	public String getText() {
		return this.textPane.getText();
	}

	public void setText(String text) {
		this.textPane.setText(text);
	}

	public ImageIcon getIcon() {
		return BeeConstants.JAVA_SOURCE_ICON;
	}

	@Override
	public String getDisplayPath() {
		if (this.file != null) {
			String root = this.project.getSourcePath();
			String path = file.getAbsolutePath().substring(root.length() + 1);
			return project.getName() + "/src/" + path.replace(File.separatorChar, '/');
		}
		return null;
	}

	@Override
	public BEditorFileExplorer getFileExplore() {
		return Application.getInstance().getJavaSourceSpective().getFileExplore();
	}

	@Override
	public BEditorManager getManager() {
		return this.manager;
	}

	@Override
	public JComponent getContents() {
		return this.textPane;
	}

	@Override
	public BEditorOutlookExplorer getOutlookExplore() {
		return Application.getInstance().getJavaSourceSpective().getOutline();
	}

	@Override
	public void makeTabPopupItems(BManager manager) {
		manager.addPopupItem("デザインへ変換", BeeConstants.GENERATE_GRAPH_ICON,
				new CodecAction.GenerateViewSingle(this.getFile(), this.project));
	}

	@Override
	public void saveAs(ActionEvent e) {

	}

	@Override
	public void deleteSelect(ActionEvent e) {

	}

	@Override
	public void selectAll(ActionEvent e) {
		textPane.selectAll();
	}

	@Override
	public String getLogicName() {
		File f = this.getFile();
		if (f != null) {
			String s = f.getName();
			s = s.substring(0, s.lastIndexOf('.'));
			return s;
		}
		return null;
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.JAVA_SOURCE_ICON;
	}

	@Override
	public void setModified(boolean modified) {
		BeeTabCloseButton button = BeeActions.findPaneButton((JComponent) this.getParent(), this);
		if (button != null) {
			button.setModified(modified);
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
		Application.getInstance().setCurrentEditor(this);
		if (this.file != null) {
			long m = this.file.lastModified();

			File newFile = new File(file.getAbsolutePath());
			long m1 = newFile.lastModified();

			// SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			// String d = format.format(new Date(m1));

			if (file.exists()) {

				if (m != lastModified) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// setSource(newFile);
							// setModified(false);
						}

					}).start();

				}
			} else {
				// TODO delete it
			}
		}

	}

	public String getSource() {
		return this.textPane.getText();
	}

	@Override
	public void updateView() {
		this.textPane.updateUI();
	}

	public static class ErrorComponent extends JTextField {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ErrorComponent(String text) {

			super(text);
			this.setFont(new Font(font, Font.PLAIN, (int) FONT_SIZE));
			FontMetrics metrics = this.getFontMetrics(this.getFont());
			int width = metrics.stringWidth(text);
			this.setSize(width, metrics.getHeight());
			this.setPreferredSize(this.getSize());
			this.setMargin(new Insets(0, 0, 0, 0));
			// this.setBorder(null);
		}

		@Override
		public void paint(Graphics g) {

			super.paint(g);
			this.setSize(0, 0);
			g.setColor(Color.RED);
			g.fillRect(0, 0, this.getWidth(), 20);

		}

	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		if (this.inited) {
			this.manager.getUndo().undoableEditHappened(e);
		}

		documentChanged();
	}

	@Override
	public int print(Graphics g, PageFormat f, int pageIndex) throws PrinterException {

		return this.manager.print(g, f, pageIndex, this.textPane);

	}

	protected PageFormat pageFormat = new PageFormat();

	@Override
	public PageFormat getPageFormat() {

		return pageFormat;
	}

	@Override
	public void setPageFormat(PageFormat format) {
		this.pageFormat = format;

	}

	@Override
	public BEditorModel getEditorModel() {
		return null;
	}

	@Override
	public void windowDeactived() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoom(double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getID() {
		return null;
	}

	@Override
	public void setProject(BProject project) {
		this.project = project;
	}

	@Override
	public void removeErrorLine(Object cell) {

	}

}