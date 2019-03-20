package com.skies.utils;

import com.skies.utils.crudUtils.MyTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SKIES on 2018/11/12.
 */
@Component
public class SqlUtil {

	@Qualifier("primaryDataSource")
	private JdbcTemplate jdbcTemplate;
	public <E> void batchSave(final List<E> list, final Class<E> entityClass) {
		String sql = this.makeSql(SQL_INSERT,entityClass);
		int[] ints = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return list.size();
			}
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				E e = list.get(i);
				Field[] fields = e.getClass().getDeclaredFields();
				try {
					int k = 1;
					for (int j = 0; j < fields.length; j++) {
						fields[j].setAccessible(true); // 暴力反射
						String column = fields[j].getName();
//						boolean isColumn = fields[j].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
//						if(isColumn){
//							continue;
//						}

						Object obj = fields[j].get(e);
						if (obj!=null && obj.getClass().getName().equals("java.util.Date")){
							Date parse = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(obj.toString());
							ps.setTimestamp(j,  new java.sql.Timestamp(parse.getTime()));
						}else if (obj!=null && obj.getClass().getName().equals("java.util.HashSet")){//去掉hibernate的关系映射
							continue;
						}else {
							ps.setObject(k, obj);
							k++;
						}
					}
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private <E> String makeSql(String sqlFlag, final Class<E> entityClass) {
		StringBuffer sql = new StringBuffer();
		Field[] fields = entityClass.getDeclaredFields();

		String tablename = camelToUnderline(entityClass.getSimpleName());
		boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
		if(isTable) {
			//获取User类@Table注解的值value，该值我们定义为User表的表名称
			MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
			tablename = t.value();
		}

		if (sqlFlag.equals(SQL_INSERT)) {
			sql.append(" INSERT INTO " + tablename);
			sql.append("(");
			for (int i = 0; fields != null && i < fields.length; i++) {
				fields[i].setAccessible(true); // 暴力反射
				String column = fields[i].getName();
//				boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
//				if(!isColumn){
					sql.append(camelToUnderline(column)).append(",");
//				}
			}
			sql = sql.deleteCharAt(sql.length() - 1);
			sql.append(") VALUES (");
			//sql.append(" VALUES (");
			for (int i = 0; fields != null && i < fields.length; i++) {
				String column = fields[i].getName();
//				boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
//				if(!isColumn){
					sql.append("?,");
//				}
			}
			sql = sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		} else if (sqlFlag.equals(SQL_UPDATE)) {
			sql.append(" UPDATE " + tablename + " SET ");
			for (int i = 0; fields != null && i < fields.length; i++) {
				fields[i].setAccessible(true); // 暴力反射
				String column = fields[i].getName();
				if (column.equals("id")) { // id 代表主键
					continue;
				}
//				boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
//				if(!isColumn){
					sql.append(camelToUnderline(column)).append("=").append("?,");
//				}
			}
			sql = sql.deleteCharAt(sql.length() - 1);
			sql.append(" WHERE id=?");
		} else if (sqlFlag.equals(SQL_DELETE)) {
			sql.append(" DELETE FROM " + tablename + " WHERE id=?");
		}
		System.out.println("SQL=" + sql);
		return sql.toString();
	}

	private static String camelToUnderline(String param){
		if (param==null||"".equals(param.trim())){
			return "";
		}
		int len=param.length();
		StringBuilder sb=new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c=param.charAt(i);
			if (Character.isUpperCase(c)){
				if(i > 0){
					sb.append(UNDERLINE);
					sb.append(Character.toLowerCase(c));
				}else{
					sb.append(Character.toLowerCase(c));
				}
			}else{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static final char UNDERLINE='_';

	public static final String SQL_INSERT = "insert";
	public static final String SQL_UPDATE = "update";
	public static final String SQL_DELETE = "delete";
}
