package com.linkstec.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.linkstec.data.vo.ColumnVo;

@Mapper
public interface TableInfoMapper {

//	@Select(" SELECT "   +
//			" c.column_id, " +
//			" c.name, " +
//			" type_name(c.system_type_id) as type, " +
//			" p.value, " +
//			" c.max_length, " +
//			" c.is_nullable, " +
//			" c.is_identity, " +
//			" c.precision, " +
//			" c.scale ," +
//			" case when exists " +
//			"   (SELECT 1 FROM sysobjects where " +
//			"    xtype='PK' " +
//			"    and parent_obj = c.object_id " +
//			"    and name in " +
//			"    (  " +
//			"      SELECT name FROM sysindexes WHERE indid in ( SELECT indid FROM sysindexkeys WHERE id = c.object_id AND colid=c.column_id  )\r\n" +
//			"     )) then 1 else 0 end as is_pk " +
//			" FROM sys.extended_properties p , " +
//			" sys.columns c " +
//			" WHERE p.major_id = OBJECT_ID(#{key}) " +
//			" and p.major_id = c.object_id " +
//			" and p.minor_id = c.column_id ")
//	public List<ColumnVo> getColumns(String key);
	
	
	
	@Select("select c.table_name, " + 
    "c.column_name name, " +
    "c.column_comment value, " +
    "c.column_type type, " +
    "c.character_maximum_length max_length, " +
    "c.is_nullable, " +
    "IF(c.column_key='PRI','1','0')  is_pk, " +
    "c.ordinal_position " +
    "from information_schema.columns c where table_name =#{table}  and table_schema=#{schema} order by c.ordinal_position") 
    public List<ColumnVo> getColumns(String schema, String table);
}
