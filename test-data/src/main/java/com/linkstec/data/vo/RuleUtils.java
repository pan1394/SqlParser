package com.linkstec.data.vo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

public class RuleUtils {

	public static Map<String, List<Rule>> rules = new HashMap<>();
	
	private static Properties prop = null;
//	static {
//		
//		List<Rule> demo = new ArrayList<>();
//		Rule r = new Rule("dep_id", "$departments(dep_id)");
//		//Rule r = new Rule("dep_id", "check(>2, <10)");
//		demo.add(r);
//		rules.put("users", demo);
//		
//		List<Rule> others = new ArrayList<>();
//		rules.put("demo", others);
//	}
	
	 
	static void init() {
		ClassPathResource resource = new ClassPathResource("rules.properties");
		prop = new Properties();
		try {
			prop.load(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		prop.forEach( (key, value) -> { 
			if(value == null) value="";
			String[] x = ((String)key).split("\\.");
			String table = x[0];
			String field = x[1];
			List<Rule> tk = rules.get(table);
			if(tk == null) {
				List<Rule> lst = new ArrayList<>();
				lst.add(new Rule(field, value.toString()));
				rules.put(table, lst);
			}else {
				tk.add(new Rule(field, value.toString()));
			}
		});
	}
	
	public static List<Rule> getRules(String table) {
		if(prop == null) {
			init();
		}
		return rules.get(table);
	}
}
