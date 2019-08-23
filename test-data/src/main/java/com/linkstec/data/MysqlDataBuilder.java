package com.linkstec.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.linkstec.data.vo.ColumnVo;
import com.linkstec.data.vo.RegexUtils;
import com.linkstec.data.vo.Rule;
import com.linkstec.data.vo.StepHolder;

@Component
public class MysqlDataBuilder extends DataBuilder{

	
	public void makePrimaryKey(Map<String, Object> master, Map<String, Object> record, ColumnVo v,
			TreeSet<Object> pkSet, StepHolder stepHolder, List<Rule> mine) {
		String fieldName = v.getName();
		Rule r = getRule(mine, fieldName);
		if(Rule.EMPTY_RULE == r) {
			String pkFirst = master.get(fieldName).toString();
			Long pk = Long.parseLong(pkFirst);
			pkSet.add(pk);
			if (pkSet.contains(pk)) {
				pk = Long.valueOf(pkSet.last().toString()) + 1;
			} else {
				pk = pk + 1;
			}
			Integer h = (int) (pk - Long.valueOf(pkFirst));
			
			stepHolder.setStep(h);
			pkSet.add(pk);
			// 生成pk
			record.put(fieldName, pk);
		}else {
			//rule='ABCD%05d'
			//rule='%05d'
			String pkFormat = (r.getRuleDescription());
			String pkFirst = master.get(fieldName).toString();
			Map<String, Object> splitted = new HashMap<>();
			RegexUtils.split2parts(pkFirst, splitted);
			Long pk = 0l;
			if(splitted.get(RegexUtils.SUFFIX) == null) {
			    pk = Long.parseLong(pkFirst);
			}else {
				pk = (Long) splitted.get(RegexUtils.SUFFIX);
			}
			pkSet.add(pk);
			if (pkSet.contains(pk)) {
				pk = Long.valueOf(pkSet.last().toString()) + 1;
			} else {
				pk = pk + 1;
			}
			Integer step = 0;
			if(splitted.get(RegexUtils.SUFFIX) == null) {
			     step = (int) (pk - Long.valueOf(pkFirst));
			}else {
				 step = (int) (pk - (Long)splitted.get(RegexUtils.SUFFIX));
			}
			stepHolder.setStep(step);
			pkSet.add(pk);
			// 生成pk
			String value = String.format(pkFormat, pk);
			record.put(fieldName, value);
		} 
	}
	
	
	
}
