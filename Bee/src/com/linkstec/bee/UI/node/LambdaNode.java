package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.ParametersNode;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BLambda;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.impl.BAnyBClass;
import com.mxgraph.model.mxCell;

public class LambdaNode extends BasicNode implements Serializable, BLambda {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1441151577999865150L;

	private String titleRowBID, headerBID, EditorBID, parameterBID, numberBID, titleBarBID;

	public LambdaNode() {

		VerticalLayout layout = new VerticalLayout();
		layout.setMaxWidth(BeeConstants.SEGMENT_MAX_WIDTH);
		this.setLayout(layout);

		Header header = new Header();
		layout.addNode(header);
		this.headerBID = header.getId();

		// body
		BLockNode mxEditor = new BLockNode();
		mxEditor.getGeometry().setWidth(BeeConstants.SEGMENT_MAX_WIDTH);
		layout.addNode(mxEditor);
		this.EditorBID = mxEditor.getId();
	}

	@Override
	public BClass getBClass() {
		return new BAnyBClass();
	}

	@Override
	public void setCast(BValuable cast) {
	}

	@Override
	public BType getParameterizedTypeValue() {
		return null;
	}

	@Override
	public BValuable getCast() {
		return null;
	}

	@Override
	public BValuable getArrayIndex() {
		return null;
	}

	@Override
	public void setArrayIndex(BValuable index) {

	}

	@Override
	public void setArrayObject(BValuable object) {

	}

	@Override
	public BValuable getArrayObject() {
		return null;
	}

	@Override
	public void addParameter(BParameter parameter) {
		makeParameterArea();
		ParametersNode node = (ParametersNode) getCellByBID(parameterBID);
		node.addParameter(parameter);
	}

	private Header getHeader() {
		return (Header) this.getCellByBID(headerBID);
	}

	private void makeParameterArea() {
		if (this.parameterBID == null) {
			ParametersNode paratemeters = new ParametersNode();

			this.getHeader().getLayout().addNode(paratemeters);

			this.parameterBID = paratemeters.getId();
		}
	}

	@Override
	public List<BParameter> getParameter() {
		ParametersNode node = (ParametersNode) this.getCellByBID(this.parameterBID);
		List<BParameter> vars = new ArrayList<BParameter>();
		if (node != null) {
			vars.addAll(node.getParameters());
		}
		return vars;
	}

	@Override
	public void setLogicBody(BLogicBody body) {
		if (body == null) {
			mxCell b = this.getCellByBID(EditorBID);
			if (b != null) {
				b.removeFromParent();
			}
		}
	}

	public BLockNode getEditor() {
		return (BLockNode) this.getCellByBID(this.EditorBID);
	}

	@Override
	public BLogicBody getLogicBody() {
		return this.getEditor();
	}

	public class Header extends BasicNode implements Serializable {// , NodeActions.Add {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7575586980109253848L;

		public Header() {
			this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
			this.getGeometry().setWidth(BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH);
			VerticalLayout headerLayout = new VerticalLayout();
			headerLayout.setSpacing(0);
			headerLayout.setBetweenSpacing(0);
			this.setLayout(headerLayout);
		}
	}

}
