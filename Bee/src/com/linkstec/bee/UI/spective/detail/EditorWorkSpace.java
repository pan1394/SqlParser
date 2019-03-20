package com.linkstec.bee.UI.spective.detail;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.popup.BeePopupMenuItem;
import com.linkstec.bee.UI.spective.BeeDetailSpective;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.fw.editor.BEditor;

public class EditorWorkSpace extends BWorkspace {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9064326636144822404L;
	private String ADD_BOOK = "add_book";

	private BEditor currentEditor;
	private BeeDetailSpective spective;

	public EditorWorkSpace(BeeDetailSpective spective) {
		super(spective);
		this.spective = spective;
	}

	@Override
	protected void editorChanged(BEditor editor) {
		if (editor != null && editor instanceof EditorBook) {
			currentEditor = (BEditor) ((EditorBook) editor).getSelectedComponent();
			if (this.currentEditor != null) {
				this.currentEditor.onSelected();
			}
		}
		hideAllHints();
	}

	public List<EditorBook> getAllBooks() {
		List<EditorBook> list = new ArrayList<EditorBook>();
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponentAt(i);
			if (comp instanceof EditorBook) {
				EditorBook book = (EditorBook) comp;
				list.add(book);
			}
		}
		return list;
	}

	public void hideAllHints() {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponentAt(i);

			if (comp instanceof EditorBook) {
				EditorBook book = (EditorBook) comp;
				int c = book.getTabCount();
				for (int j = 0; j < c; j++) {
					Component tab = book.getComponentAt(j);
					if (tab instanceof BEditor) {
						BEditor sheet = (BEditor) tab;
						sheet.windowDeactived();

					}
				}
				book.hidePopup();
			}
		}

	}

	@Override
	protected void beforeMenuShow() {

		EditorBook book = (EditorBook) this.getCurrentEditor();
		if (book != null) {

			BeePopupMenuItem addBook = new BeePopupMenuItem();
			addBook.setText("Book新規");
			addBook.setValue(ADD_BOOK);
			addBook.setIcon(BeeConstants.BOOK_ICON);
			this.actionMenu.addItem(addBook);
		}

		super.beforeMenuShow();
	}

	@Override
	public void menuSelected(Object menu) {
		super.menuSelected(menu);
		new Thread(new Runnable() {
			public void run() {
				BeePopupMenuItem item = (BeePopupMenuItem) menu;

				if (item.getValue().equals(ADD_BOOK)) {
					EditorBook book = (EditorBook) getCurrentEditor();
					if (book != null) {
						BeeActions.addNewBook(book.getProject());
					}
				}
			}

		}).start();
	}

	protected void editorAdded(BEditor editor) {
		this.spective.editorAdded(editor);
	}

}
