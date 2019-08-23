package com.linkstec.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DataSetGenerator {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(DataSetGenerator.class, args);
		MysqlDataBuilder builder = ctx.getBean(MysqlDataBuilder.class);
		
		builder.doStart("test", "t_dept", 10, true);
		builder.doStart("test", "t_project", 5, true);
		builder.doStart("test", "t_property", 11, true);
		builder.doStart("test", "t_personner", 2, " ORDER BY personner_no+0 DESC LIMIT 0,1", true);
	}
}