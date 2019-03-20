package com.linkstec.bee.UI.spective.detail;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.editor.task.problem.BeeDetailError;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class VerifyClass {

	public VerifyClass(BeeGraphSheet sheet) {
		new RuleStarter(sheet);
	}

	public static class RuleStarter implements Runnable {
		private BeeGraphSheet sheet;
		private List<BeeDetailError> errors = new ArrayList<BeeDetailError>();

		public RuleStarter(BeeGraphSheet sheet) {
			this.sheet = sheet;
			Thread t = new Thread(this);
			t.start();
		}

		@Override
		public void run() {
			this.sheet.clearErrorLine();
			sheet.getModel().setAlert(null);

			BeeModel model = ((BeeModel) sheet.getGraph().getModel());

			Application.getInstance().getDesignSpective().getTask().getProblems().clear();

			Object obj = model.getRoot();
			if (obj instanceof mxCell) {
				mxCell cell = (mxCell) obj;
				this.scanVerify(cell, model);
			}
			if (errors.size() != 0) {
				Application.getInstance().getDesignSpective().getTask().getProblems().addErrors(errors);
			}
		}

		private void verify(BasicNode node) {
			node.setAlert(null);
			node.Verify(sheet, sheet.getProject());
			if (node.getAlert() != null) {
				BeeDetailError error = new BeeDetailError();
				error.setContents(node.getAlert());
				if (sheet.getFile() != null) {
					error.setFilePath(sheet.getFile().getAbsolutePath());
				}
				error.setSheet(sheet);
				error.setProject(sheet.getProject());
				error.setTargetPath(node.toString());
				error.setUserObject(node);
				errors.add(error);
				if (node.getAlertObject().getType().equals(BAlert.TYPE_ERROR)) {
					sheet.getModel().setAlert("エラー").setType(BAlert.TYPE_ERROR);
				} else if (node.getAlertObject().getType().equals(BAlert.TYPE_WARNING)) {
					if (sheet.getModel().getAlert() == null) {
						sheet.getModel().setAlert("ワーニング").setType(BAlert.TYPE_WARNING);
					}
				}
			}
		}

		private void scanVerify(mxCell cell, BeeModel model) {
			if (cell == null) {
				return;
			}
			int count = cell.getChildCount();

			for (int i = 0; i < count; i++) {
				mxICell child = cell.getChildAt(i);
				if (child instanceof BasicNode) {
					BasicNode node = (BasicNode) child;
					this.verify(node);
				}
				this.scanVerify((mxCell) child, model);
			}
		}
	}

}
