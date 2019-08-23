package com.linkstec.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MasterMapper {

	@Select("${customSql}")
	public List<Map<String, Object>> execute(@Param("customSql") String customSql);
 
	@Select("${customSql}")
	public List<Object> getListFields(@Param("customSql") String customSql);
	
	@Select("${customSql}")
	public void insert(@Param("customSql") String customSql);

}
