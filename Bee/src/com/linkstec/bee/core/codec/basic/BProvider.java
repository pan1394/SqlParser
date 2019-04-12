package com.linkstec.bee.core.codec.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BProperties;
import com.linkstec.bee.core.fw.basic.BPropertiesUtil;
import com.linkstec.bee.core.fw.basic.BSQLSet;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.basic.ITableSqlInfo;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.excel.testcase.ExcelExport;

public class BProvider extends BLogicProvider {

	private static final long serialVersionUID = -8545331004254136405L;

	private IPatternCreator creator;
	private transient static Hashtable<String, BClass> templates = new Hashtable<String, BClass>();
	private transient BPropertiesUtil util;

	public BProvider(IPatternCreator creator, BProperties properties) {
		super(creator, properties);
		this.creator = creator;
	}

	/*
	 * public BClass getTemplate(Class<?> cls) { return
	 * this.getTemplate(cls.getName()); }
	 * 
	 * public BClass getTemplate(String name) { if (!this.properties.isReload()) {
	 * BClass bclass = templates.get(name); if (bclass != null) { return (BClass)
	 * properties.copy(bclass); } } BClass bclass = properties.getTemplate(name); if
	 * (bclass != null) { templates.put(bclass.getQualifiedName(), bclass); return
	 * (BClass) properties.copy(bclass); } return null; }
	 * 
	 */
	@Override
	public File doDetailExport(BProject project, BModule module, List<BSQLSet> sqlSet) {
		ExcelExport export = new ExcelExport();

		ITableSql sql = new ITableSql() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private List<BInvoker> invokers = new ArrayList<BInvoker>();
			private List<BInvoker> befores = new ArrayList<BInvoker>();
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

				@Override
				public boolean hasEqualsExceptedExpression() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setEqualsExceptedExpression() {
					// TODO Auto-generated method stub

				}

			};

			@Override
			public List<BEditorModel> getEditors() {
				return module.getList();
			}

			@Override
			public void setEditors(List<BEditorModel> editors) {

			}

			@Override
			public List<BInvoker> getInvokers() {
				return invokers;
			}

			@Override
			public void setFormat(boolean format) {
			}

			@Override
			public boolean isFormat() {
				return true;
			}

			@Override
			public ITableSqlInfo getInfo() {
				return info;
			}

			@Override
			public List<BInvoker> getBeforeSqlInvokers() {
				return befores;
			}

			@Override
			public BLogicProvider getProvider() {
				return BProvider.this;
			}

			@Override
			public List<BInvoker> getSelectInfos() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		String template = "D:/beny/export/template5.xlsx";
		try {
			// SQLUtils.doSQL(project, module, sqlSet, sql);
			return export.doExport(module, project, template, false, false, sqlSet, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
