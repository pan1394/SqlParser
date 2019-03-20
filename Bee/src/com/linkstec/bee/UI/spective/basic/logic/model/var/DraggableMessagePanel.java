package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;

import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.basic.logic.LogicMenuMessage;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferable;
import com.linkstec.bee.UI.thread.BeeThread;
import com.mxgraph.swing.util.mxGraphTransferable;

public class DraggableMessagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3919087976973008460L;

	private LogicMenuMessage message;
	private ValueChangeListener listener;
	private MessageDropListener dropListener;
	private LogMessagePanel LogPanel;

	public DraggableMessagePanel(MessageDropListener dropListener) {
		this.setOpaque(false);
		this.dropListener = dropListener;
		this.setLayout(new BorderLayout());
		this.setTransferHandler(new MessageTransferHandler());
		this.setBorder(new TitledBorder("以下エリアへプロパティ値をドラッグしてください"));

		this.setPreferredSize(new Dimension(30, 20));
	}

	public ValueChangeListener getListener() {
		return listener;
	}

	public void setListener(ValueChangeListener listener) {
		this.listener = listener;
		if (this.LogPanel != null) {
			this.LogPanel.setListener(listener);
		}
	}

	public LogicMenuMessage getMessage() {
		return message;
	}

	public LogMessagePanel getLogMessagePanel() {
		return this.LogPanel;
	}

	public void setLogMessage(LogicMenuMessage message) {
		this.message = message;
		this.removeAll();
		LogPanel = new LogMessagePanel(message, listener);

		if (this.dropListener != null) {
			dropListener.dropped(LogPanel);
		}
		this.add(LogPanel, BorderLayout.CENTER);
		this.updateUI();
	}

	public class MessageTransferHandler extends TransferHandler {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5520076116990708115L;

		public MessageTransferHandler() {

		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			new BeeThread(new Runnable() {

				@Override
				public void run() {
					doImport(comp, t);
				}

			}).start();
			return true;
		}

		private void doImport(JComponent comp, Transferable t) {
			if (comp instanceof DraggableMessagePanel) {
				DraggableMessagePanel panel = (DraggableMessagePanel) comp;
				try {

					Object obj = t.getTransferData(mxGraphTransferable.dataFlavor);
					if (obj instanceof BeeTransferable) {

						BeeTransferable bt = (BeeTransferable) obj;
						Object[] cells = bt.getCells();

						if (cells != null && cells.length == 1) {
							Object cell = cells[0];

							if (cell instanceof ObjectNode) {

								ObjectNode node = (ObjectNode) cell;

								Object value = node.getValue();
								if (value instanceof LogicMenuMessage) {

									LogicMenuMessage message = (LogicMenuMessage) value;
									panel.setLogMessage(message);
								}
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			super.exportAsDrag(comp, e, action);
		}

		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			super.exportDone(source, data, action);

		}

		@Override
		public boolean canImport(TransferSupport support) {
			return true;
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			return true;
		}
	}
}
