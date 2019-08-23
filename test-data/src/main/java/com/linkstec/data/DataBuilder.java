package com.linkstec.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linkstec.data.mapper.MasterMapper;
import com.linkstec.data.mapper.TableInfoMapper;
import com.linkstec.data.vo.ColumnVo;
import com.linkstec.data.vo.RegexUtils;
import com.linkstec.data.vo.Rule;
import com.linkstec.data.vo.RuleUtils;
import com.linkstec.data.vo.StepHolder;

@Component
public class DataBuilder {

	@Autowired
	protected TableInfoMapper tableInfoMapper;

	@Autowired
	protected MasterMapper masterMapper;

	private List<String> getColumnString(String schema, String tableName) {
		List<ColumnVo> list = tableInfoMapper.getColumns(schema, tableName);
		return list.stream().map(ColumnVo::getName).collect(Collectors.toList());
	}

	protected Rule getRule(List<Rule> rules, String field) {
		return rules.stream().filter( r ->field.equals(r.getField())).findAny().orElse(Rule.EMPTY_RULE);
	}
	
	private String getPrimaryField(List<ColumnVo> list) {
		for(ColumnVo v : list) {
			if(v.isPrimary())
				return v.getName();
		}
		return "";
	}
	
	protected String makeCustomSql(String schema, String table, String whereClause) {
		StringBuffer customSql = new StringBuffer();
		String pk = getPrimaryField(tableInfoMapper.getColumns(schema, table));
		if (StringUtils.isBlank(whereClause)) {
			whereClause = String.format(" where %s = (select max(%s) from %s)", pk, pk, table);
		}

		List<String> columns = getColumnString(schema, table);
		customSql.append("SELECT ");
		columns.forEach(c -> customSql.append(c + ","));
		customSql.deleteCharAt(customSql.length() - 1);
		customSql.append(" FROM " + table);
		customSql.append(whereClause);
		return customSql.toString();
	}

	private Map<String, Object> getMasterData(String sql) {
		List<Map<String, Object>> list = masterMapper.execute(sql);
		Map<String, Object> map = null;
		if (CollectionUtils.isNotEmpty(list)) {
			map = list.get(0);
		}else {
			throw new RuntimeException("无法取得准备数据。");
		}
		return map;
	}

	public void doStart(String schema, String table, int num, boolean executed) {
		List<Map<String, Object>> records = make(schema, table, num, "");
		insert(schema, table, records, executed);
	}
	
	public void doStart(String schema, String table, int num, String whereClause, boolean executed) {
		List<Map<String, Object>> records = make(schema, table, num, whereClause);
		insert(schema, table, records, executed);
	}
	
	public List<Map<String, Object>> make(String schema, String table, int n, String whereClause) {
		List<ColumnVo> list = tableInfoMapper.getColumns(schema, table);
		List<Map<String, Object>> generated = new ArrayList<Map<String, Object>>();
		Map<String, Object> master = getMasterData(makeCustomSql(schema, table, whereClause));
		TreeSet<Object> pkSet = new TreeSet<>();
		StepHolder stepHolder = new StepHolder();
		List<Rule> allRules = RuleUtils.getRules(table);
		for (int i = 0; i < n; i++) {
			Map<String, Object> record = new HashMap<>();
			for (ColumnVo c : list) {
				// get field rule
				String fieldName = c.getName();
				List<Rule> mine = allRules.stream().filter(r -> r.getField().equals(fieldName))
						.collect(Collectors.toList());
				if (c.isPrimary()) {
					makePrimaryKey(master, record, c, pkSet, stepHolder, mine);
				} else {
					if (CollectionUtils.isNotEmpty(mine)) {
						makeFieldDataWithRules(record, c, mine);
					} else {
						makeFieldData(master, record, c, stepHolder.getStep());
					}
				}
			}
			generated.add(record);
		}
		return generated;
	}

	protected void insert(String schema, String table, List<Map<String, Object>> data, boolean executed) {
		String sql = "";
		List<ColumnVo> list = tableInfoMapper.getColumns(schema, table);
		for(Map<String, Object> m : data) {
			StringBuffer param = new StringBuffer();
			StringBuffer value = new StringBuffer();
			for(ColumnVo c : list) {
				String fieldName = c.getName();
				Object v = m.get(fieldName);
				if(v != null) {
					param.append(fieldName + ",");
					if( v instanceof String) {
						value.append( "\'"+v.toString()+"\',");
					}else if( v instanceof Integer) {
						value.append(Integer.parseInt(v.toString()) + ",");
					}else if( v instanceof Long) {
						value.append(Long.parseLong(v.toString()) + ",");
					}
				}
			}
			param.deleteCharAt(param.length() - 1);
			value.deleteCharAt(value.length() - 1);
			sql = String.format("INSERT INTO %s(%s) VALUE(%s)", table, param, value);
			if(executed) {
				masterMapper.insert(sql);
			}
			System.out.println(sql);
		}
	}
	
	private void makeFieldDataWithRules(Map<String, Object> record, ColumnVo c, List<Rule> mine) {
		Object value = null;
		for (Rule r : mine) {
			value = r.execute(masterMapper);
		}
		record.put(c.getName(), value);
	}

	/**
	 * 制作主键
	 * 
	 * @param master 原始数据
	 * @param record 生成数据
	 * @param v      字段meta信息
	 * @param pkSet  主键集合
	 */
	protected void makePrimaryKey(Map<String, Object> master, Map<String, Object> record, ColumnVo v,
			TreeSet<Object> pkSet, StepHolder stepHolder, List<Rule> mine) {
		String fieldName = v.getName();
		// TODO 假定为整数类型
		Long pk = Long.valueOf(master.get(fieldName).toString());
		pkSet.add(pk);
		if (pkSet.contains(pk)) {
			pk = Long.valueOf(pkSet.last().toString()) + 1;
		} else {
			pk = pk + 1;
		}
		Integer h = (int) (pk - Long.valueOf(master.get(fieldName).toString()));
		stepHolder.setStep(h);
		pkSet.add(pk);
		// 生成pk
		record.put(fieldName, pk);
	}

	/**
	 * 无规则下生成普通字段
	 * 
	 * @param master
	 * @param record
	 * @param c
	 */
	private void makeFieldData(Map<String, Object> master, Map<String, Object> record, ColumnVo c, int step) {
		String fieldName = c.getName();
		String type = c.getType();
		Object value = master.get(fieldName);
		int length = Integer.valueOf(c.getMaxLength());
		Object generated = doMakeFields(type, value, length, step);
		record.put(fieldName, generated);
	}

	/**
	 * 根据数据类型和准备数据生成字段, 生成逻辑应可被子类覆盖.
	 * 
	 * @param type
	 * @param value
	 * @param maxLength
	 * @param step
	 * @return
	 */
	protected Object doMakeFields(String type, Object value, int maxLength, int step) {
		if (value == null) {
			return value;
		}
		if ("int".equals(type)) { 
			return add(value, step);
		} else if (type.contains("char")) {
			try {
				return add(value, step);
			}catch(Exception e) {
				String tmp = value.toString();
				Map<String, Object> content = new HashMap<>();
				RegexUtils.split2parts(tmp, content);
				String tmp1 = tmp + step;
				if(content.get(RegexUtils.SUFFIX) != null) {
					int suf = (int) ((Long)content.get(RegexUtils.SUFFIX) + step);
					tmp1 = (String)content.get(RegexUtils.PREFIX) + suf;
				}
				int stepLength = (step + "").length();
				if (tmp1.length() < maxLength) {
					value = tmp1;
				} else {
					value = tmp.substring(0, maxLength - stepLength) + step;
				}
				return value;
			}
		}
		return value;
	}
	
	private Object add(Object value, int step) {
		Integer res = Integer.valueOf(value.toString());
		value = res + step;
		return value;
	}
}
