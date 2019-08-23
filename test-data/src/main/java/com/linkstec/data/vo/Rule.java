package com.linkstec.data.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;

import com.linkstec.data.mapper.MasterMapper;

/**
 *   暂不检测逻辑互斥的情况
 * @author panyl
 *
 */
public class Rule {

	/**
	 * 规则格式：
	 * $departments(dep_id) -> 表名(字段), 关联关系， 表字段随机取值
	 * check('a', 'b', 'c') -> 枚举随机取值， check约束
	 * check( >3, <10 )      -> 范围随机取值
	 * 
	 */
	
	public final static Rule EMPTY_RULE = new Rule();
	
	public Rule() {
		
	}
	
	public Rule(String field, String ruleDescription) {
		this.field = field;
		this.ruleDescription = ruleDescription;
	}

	private String field;
	
	private String ruleDescription;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}
	
	public Object execute(MasterMapper dao) {
		final List<Object> options = new ArrayList<>();
		if(StringUtils.isNotBlank(ruleDescription)) {
			if(ruleDescription.startsWith("check")) {
				Pattern p = Pattern.compile("check\\((.+)\\)");
				Matcher m = p.matcher(ruleDescription);
				String content = "";
				if(m.find()) {
					content = m.group(1);
				}
				String[] x = content.split(",");
				List<Object> values = Arrays.asList(x);
				values.forEach( o -> {
					if(o instanceof String) {
						String s = (String)o;
						s = StringUtils.trim(s.replaceAll("\"|\'", ""));
						options.add(s);
					}else {
						options.add(o);
					}
				});
			} 
			if(ruleDescription.startsWith("$")) {
				Pattern p = Pattern.compile("(\\w+)\\((\\w+)\\)");
				Matcher m = p.matcher(ruleDescription.substring(1));
				String table = "";
				String field = "";
				if(m.find()) {
					table = m.group(1);
					field = m.group(2);
				}
				String sql = String.format("SELECT %s FROM %s", field, table);
				options.addAll(dao.getListFields(sql));
			}
		}
		
		Collections.shuffle(options);
		Random r = new Random();
		Object o = options.get(r.nextInt(options.size()));
		return o;
	}
}
