package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.basic.ITableSqlInfo;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.logic.BInvoker;

public class BSqlModel implements ITableSql {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1988842649342592433L;
	private List<BEditorModel> editors;
	private List<BInvoker> invokers = new ArrayList<BInvoker>();
	private List<BInvoker> inputValues = new ArrayList<BInvoker>();
	private boolean format;
	private BLogicProvider provider;

	private ITableSqlInfo info = new ITableSqlInfo() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1422504824274638447L;
		private boolean group = false;
		private boolean sort = false;
		private boolean union = false;
		private boolean nesslect = false;
		private boolean fixed = false;
		private boolean equalsExceptedExpression = false;

		@Override
		public void setGroupBy() {
			this.group = true;
		}

		@Override
		public void setSortBy() {
			this.sort = true;
		}

		@Override
		public void setFixedValue() {
			this.fixed = true;
		}

		@Override
		public void setUnion() {
			this.union = true;
		}

		@Override
		public void setNesSelect() {
			this.nesslect = true;
		}

		@Override
		public boolean hasGroupBy() {
			return this.group;
		}

		@Override
		public boolean hasSortBy() {
			return this.sort;
		}

		@Override
		public boolean hasFixedValue() {
			return this.fixed;
		}

		@Override
		public boolean hasNesSelect() {
			return this.nesslect;
		}

		@Override
		public boolean hasUnion() {
			return this.union;
		}

		public void setEqualsExceptedExpression() {
			equalsExceptedExpression = true;
		}

		@Override
		public boolean hasEqualsExceptedExpression() {
			return equalsExceptedExpression;
		}

	};

	public BSqlModel(List<BEditorModel> editors, boolean format, BLogicProvider provider) {
		this.editors = editors;
		this.format = format;
		this.provider = provider;
	}

	@Override
	public List<BEditorModel> getEditors() {
		return this.editors;
	}

	@Override
	public void setEditors(List<BEditorModel> editors) {
		this.editors = editors;
	}

	@Override
	public List<BInvoker> getInvokers() {
		return invokers;
	}

	@Override
	public void setFormat(boolean format) {
		this.format = format;
	}

	@Override
	public boolean isFormat() {
		return format;
	}

	@Override
	public ITableSqlInfo getInfo() {
		return info;
	}

	@Override
	public List<BInvoker> getBeforeSqlInvokers() {
		return inputValues;
	}

	@Override
	public BLogicProvider getProvider() {
		return this.provider;
	}

}
