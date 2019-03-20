package com.skies.utils.crudUtils.dao.impl;

import com.skies.utils.crudUtils.IgnoreColumn;
import com.skies.utils.crudUtils.MyTable;
import com.skies.utils.crudUtils.PageResult;
import com.skies.utils.crudUtils.dao.BaseDao;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sunpengyan on 2017/5/15.
 */
public class BaseDaoImpl<T> implements BaseDao<T> {

    /** 设置一些操作的常量 */
    public static final String SQL_INSERT = "insert";
    public static final String SQL_UPDATE = "update";
    public static final String SQL_DELETE = "delete";

    @Resource(name="primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<T>) type.getActualTypeArguments()[0];
        System.out.println("Dao实现类是：" + entityClass.getName());
    }

    @Override
    public void save(T entity) {
        String sql = this.makeSql(SQL_INSERT);
        Object[] args = this.setArgs(entity, SQL_INSERT ,null);
        int[] argTypes = this.setArgTypes(entity, SQL_INSERT,null);
        jdbcTemplate.update(sql.toString(), args, argTypes);
    }

    @Override
    public void save(T entity, List<String> excludes) {
        String sql = this.makeSql(SQL_INSERT, excludes);
        Object[] args = this.setArgs(entity, SQL_INSERT ,excludes);
        int[] argTypes = this.setArgTypes(entity, SQL_INSERT, excludes);
        jdbcTemplate.update(sql.toString(), args, argTypes);
    }

    @Override
    public <E> void save(E entity, List<String> excludes,Class<E> entityClass) {
        String sql = this.makeSql(SQL_INSERT, excludes, entityClass);
        Object[] args = this.setArgs(entity, SQL_INSERT ,excludes,entityClass);
        int[] argTypes = this.setArgTypes(entity, SQL_INSERT, excludes,entityClass);
        jdbcTemplate.update(sql.toString(), args, argTypes);
    }

    // 组装SQL
    private <E> String makeSql(String sqlFlag, List<String> excludes,Class<E> entityClass) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }
        StringBuffer sql = new StringBuffer();
        Field[] fields = entityClass.getDeclaredFields();
        if (sqlFlag.equals(SQL_INSERT)) {
            sql.append(" INSERT INTO " + tablename );
            sql.append("(");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();

                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(isColumn){
                    continue;
                }else  if(excludes==null || !excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段,（20171010可以改为注解或者特殊符号开头的名称，如t_）
                    sql.append(column).append(",");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");
            for (int i = 0; fields != null && i < fields.length; i++) {
                String column = fields[i].getName();
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(isColumn){
                    continue;
                }else  if(excludes==null || !excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                    sql.append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            sql.append(" UPDATE " + tablename + " SET ");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                if (column.equals("dzbm")) { // id 代表主键
                    continue;
                }
                if(excludes==null || !excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                    sql.append(column).append("=").append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE dzbm =?");
        } else if (sqlFlag.equals(SQL_DELETE)) {
            sql.append(" DELETE FROM " + tablename + " WHERE dzbm=?");
        }
        System.out.println("SQL=" + sql);
        return sql.toString();
    }


    @Override
    public void update(T entity) {
        String sql = this.makeSql(SQL_UPDATE);
        Object[] args = this.setArgs(entity, SQL_UPDATE ,null);
        int[] argTypes = this.setArgTypes(entity, SQL_UPDATE, null);
        jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public void update(T entity, List<String> excludes) {
        String sql = this.makeSql(SQL_UPDATE, excludes);
        Object[] args = this.setArgs(entity, SQL_UPDATE ,excludes);
        int[] argTypes = this.setArgTypes(entity, SQL_UPDATE,excludes);
        jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public <E> void update(E entity, List<String> excludes,Class<E> entityClass) {
        String sql = this.makeSql(SQL_UPDATE, excludes,entityClass);
        Object[] args = this.setArgs(entity, SQL_UPDATE ,excludes,entityClass);
        int[] argTypes = this.setArgTypes(entity, SQL_UPDATE,excludes,entityClass);
        jdbcTemplate.update(sql, args, argTypes);
    }


    @Override
    public void delete(T entity) {
        String sql = this.makeSql(SQL_DELETE);
        Object[] args = this.setArgs(entity, SQL_DELETE, null);
        int[] argTypes = this.setArgTypes(entity, SQL_DELETE, null);
        jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public void delete(Serializable id) {
        String tablename = entityClass.getSimpleName().toLowerCase();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        String sql = " DELETE FROM " + tablename + " WHERE dzbm=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteAll() {
        String tablename = entityClass.getSimpleName().toLowerCase();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        String sql = " TRUNCATE TABLE " + tablename;
        jdbcTemplate.execute(sql);
    }

    /*@Override
    public void batchSave(List<T> lt) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(entityClass.getSimpleName().toLowerCase()).append("( ");
        List<Object[]> params = new ArrayList<Object[]>();
        String val = "";

        for (T t : lt) {
            int index = 0;
            Field[] fields = t.getClass().getDeclaredFields();
            if (fields != null && fields.length > 0) {
                Object[] objVal = new Object[fields.length];
                for (Field field : fields) {
                    try {
                        field.setAccessible(true);
                        Object obj = field.get(t);
                        if (params.size() == 0) {
                            sb.append(field.getName()).append(" ,");
                            val += ", ? ";
                        }
                        if(obj==null) {
                            obj = "";
                        }
                        objVal[index++] = obj;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                params.add(objVal);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        val = val.substring(1);
        sb.append(") value (").append(val).append(")");
        String sql = sb.toString();
        System.out.println(sql);
        jdbcTemplate.batchUpdate(sql, params);
    }*/
    @Override
    public void batchSave(final List<T> list) {
        String sql = this.makeSql(SQL_INSERT);
        int[] ints = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return list.size();
            }
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                T t = list.get(i);
                Field[] fields = t.getClass().getDeclaredFields();
                try {
                    int index = 0;
                    for (int j = 0; j < fields.length; j++) {
                        boolean isColumn = fields[j].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解,如果有则忽略
                        if(isColumn){
                            continue;
                        }
                        fields[j].setAccessible(true); // 暴力反射
                        Object obj = fields[j].get(t);
                        if (obj!=null && obj.getClass().getName().equals("java.util.Date")){
                            Date parse = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(obj.toString());
                            ps.setTimestamp(index + 1,  new java.sql.Timestamp(parse.getTime()));
                        }else {
                            ps.setObject(index + 1, obj);
                        }
                        index++;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public <E> void batchSave(final List<E> list,final Class<E> entityClass) {
        String sql = this.makeSql(SQL_INSERT,entityClass);
        int[] ints = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return list.size();
            }
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                E e = list.get(i);
                Field[] fields = e.getClass().getDeclaredFields();
                try {
                    int k = 1;
                    for (int j = 0; j < fields.length; j++) {
                        fields[j].setAccessible(true); // 暴力反射
                        String column = fields[j].getName();
                        boolean isColumn = fields[j].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                        if(isColumn){
                            continue;
                        }

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

    /**
     * 未完成
     */
    @Override
    public void batchUpdate(List<T> list) {
        String tablename = entityClass.getSimpleName().toLowerCase();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tablename).append(" set ");
        List<Object[]> params = new ArrayList<Object[]>();
        Object primaryKey = "dzbm";
        for (T t : list) {
            int index = 0;
            Field[] fields = entityClass.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                Object id = null;
                Object[] objVal = new Object[fields.length];
                for (Field field : fields) {
                    try {
                        field.setAccessible(true);
                        Object obj = field.get(t);
                        if (field.getName().equalsIgnoreCase("dzbm")) {
                            //primaryKey = obj;
                            id = obj;
                        } else {
                            if (params.size() == 0) {
                                sb.append(field.getName()).append(" = ? ,");
                            }
                            objVal[index++] = obj;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    objVal[index] = id;
                }
                params.add(objVal);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" where ").append(primaryKey).append(" = ? ");
        String sql = sb.toString();
        jdbcTemplate.batchUpdate(sql, params);
    }

//    /**
//     * 未完成
//     */
//    @Override
//    public void batchDelete(List<T> list) {
//
//    }

    @Override
    public T findById(Serializable id) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }
        String sql = "SELECT * FROM " + tablename + " WHERE dzbm =?";
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        List<T> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.size()==0?null:list.get(0);
    }

    @Override
    public List<T> findAll() {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }
        String sql = "SELECT * FROM " + tablename;
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public PageResult<T> findByPage(int pageNo, int pageSize) {
        List<T> list = this.find(pageNo, pageSize, null, null);
        int totalRow = this.count(null);
        return new PageResult<T>(list, totalRow);
    }

    @Override
    public PageResult<T> findByPage(int pageNo, int pageSize, Map<String, String> where) {
        List<T> list = this.find(pageNo, pageSize, where, null);
        int totalRow = this.count(where);
        return new PageResult<T>(list, totalRow);
    }

    @Override
    public PageResult<T> findByPage(int pageNo, int pageSize, LinkedHashMap<String, String> orderby) {
        List<T> list = this.find(pageNo, pageSize, null, orderby);
        int totalRow = this.count(null);
        return new PageResult<T>(list, totalRow);
    }

    @Override
    public PageResult<T> findByPage(int pageNo, int pageSize, Map<String, String> where,
                                    LinkedHashMap<String, String> orderby) {
        List<T> list = this.find(pageNo, pageSize, where, orderby);
        int totalRow = this.count(where);
        return new PageResult<T>(list, totalRow);
    }

    @Override
    public List<T> findByConditons(Map<String, String> where, LinkedHashMap<String, String> orderby) {
        return this.find(where,orderby);
    }

    @Override
    public void execSQL(String sql) {
        jdbcTemplate.execute(sql);
    }

    @Override
    public List<T> executeSql(String sql) {
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Map<String,Object> queryForMap(String sql) {
        return jdbcTemplate.queryForMap(sql);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<String> queryForList2(String sql) {
        return jdbcTemplate.queryForList(sql, String.class);
    }

    // 组装SQL
    private String makeSql(String sqlFlag, List<String> excludes) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        StringBuffer sql = new StringBuffer();
        Field[] fields = entityClass.getDeclaredFields();
        if (sqlFlag.equals(SQL_INSERT)) {
            sql.append(" INSERT INTO " + tablename );
            sql.append("(");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                if(excludes==null || !excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段,（20171010可以改为注解或者特殊符号开头的名称，如t_）
                    sql.append(column).append(",");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");
            //sql.append(" VALUES (");
            for (int i = 0; fields != null && i < fields.length; i++) {
                String column = fields[i].getName();
                if(excludes==null || !excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                    sql.append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            sql.append(" UPDATE " + tablename + " SET ");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                if (column.equals("dzbm")) { // id 代表主键
                    continue;
                }

                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(isColumn){
                    continue;
                }
                if(excludes==null || !excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                    sql.append(column).append("=").append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE dzbm =?");
        } else if (sqlFlag.equals(SQL_DELETE)) {
            sql.append(" DELETE FROM " + tablename + " WHERE dzbm=?");
        }
        System.out.println("SQL=" + sql);
        return sql.toString();
    }

    // 组装SQL
    private String makeSql(String sqlFlag) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        StringBuffer sql = new StringBuffer();
        Field[] fields = entityClass.getDeclaredFields();
        if (sqlFlag.equals(SQL_INSERT)) {
            sql.append(" INSERT INTO " + tablename );
            sql.append("(");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                //sql.append(column).append(",");
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(!isColumn){
                    sql.append(column).append(",");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");
            //sql.append(" VALUES (");
            for (int i = 0; fields != null && i < fields.length; i++) {
                String column = fields[i].getName();
                //sql.append("?,");
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(!isColumn){
                    sql.append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            sql.append(" UPDATE " + tablename  + " SET ");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                if (column.equals("dzbm")) { // id 代表主键
                    continue;
                }
                //sql.append(column).append("=").append("?,");
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(!isColumn){
                    sql.append(column).append("=").append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE dzbm =?");
        } else if (sqlFlag.equals(SQL_DELETE)) {
            sql.append(" DELETE FROM " + tablename + " WHERE dzbm =?");
        }
        System.out.println("SQL=" + sql);
        return sql.toString();
    }

    // 组装SQL
    private <E> String makeSql(String sqlFlag,final Class<E> entityClass) {
        StringBuffer sql = new StringBuffer();
        Field[] fields = entityClass.getDeclaredFields();
        String tablename = entityClass.getSimpleName();
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
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(!isColumn){
                    sql.append(column).append(",");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");
            //sql.append(" VALUES (");
            for (int i = 0; fields != null && i < fields.length; i++) {
                String column = fields[i].getName();
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(!isColumn){
                    sql.append("?,");
                }
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
                boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                if(!isColumn){
                    sql.append(column).append("=").append("?,");
                }
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE dzbm =?");
        } else if (sqlFlag.equals(SQL_DELETE)) {
            sql.append(" DELETE FROM " + tablename + " WHERE dzbm =?");
        }
        System.out.println("SQL=" + sql);
        return sql.toString();
    }

    // 设置参数
    private Object[] setArgs(T entity, String sqlFlag, List<String> excludes) {
        Field[] fields = entityClass.getDeclaredFields();

        int ignoreColumnNum = 0;//排除字段个数
        for (int i = 0; fields != null && i < fields.length; i++) {
            fields[i].setAccessible(true); // 暴力反射
            String column = fields[i].getName();
            boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
            if(excludes==null) {
                if(isColumn) {
                    ignoreColumnNum ++;
                }
            } else {
                if(isColumn && !excludes.contains(column)) {//排除字段个数
                    ignoreColumnNum ++;
                }
            }
        }

        if (sqlFlag.equals(SQL_INSERT)) {
            //Object[] args = new Object[fields.length];;
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                ignoreColumnNum += excludes.size();
            }
            Object[] args = new Object[fields.length - ignoreColumnNum];;

            int n = 0;//tmepArr 和 bean的长度不一样
            for (int i = 0; fields != null && i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true); // 暴力反射
                    Object field = fields[i].get(entity);
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(field!=null) {
                        /*if (field.getClass().getName().equals("java.util.Date")) {
                            Date parse = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(field.toString());
                            field = new java.sql.Timestamp(parse.getTime());
                        }*/
                        args[n] = field;
                    }
                    n++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return args;
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            Object[] tempArr = new Object[fields.length];
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                tempArr = new Object[fields.length - excludes.size()];
            }
            int n = 0;//tmepArr 和 bean的长度不一样
            int m = 0;
            for (int i = 0; fields != null && i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true); // 暴力反射
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        m++;
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(fields[i].get(entity)!=null) {
                        tempArr[n] = fields[i].get(entity);
                    }
                    n++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Object[] args = new Object[tempArr.length-m];
            System.arraycopy(tempArr, 1, args, 0, tempArr.length - 1); // 数组拷贝
            args[args.length - 1] = tempArr[0];
            return args;
        } else if (sqlFlag.equals(SQL_DELETE)) {
            Object[] args = new Object[1]; // 长度是1
            fields[0].setAccessible(true); // 暴力反射
            try {
                args[0] = fields[0].get(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return args;
        }
        return null;
    }

    // 设置参数
    private <E> Object[] setArgs(E entity, String sqlFlag, List<String> excludes,Class<E> entityClass) {
        Field[] fields = entity.getClass().getDeclaredFields();

        int ignoreColumnNum = 0;//排除字段个数
        for (int i = 0; fields != null && i < fields.length; i++) {
            fields[i].setAccessible(true); // 暴力反射
            String column = fields[i].getName();
            boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
            if(excludes==null) {
                if(isColumn) {
                    ignoreColumnNum ++;
                }
            } else {
                if(isColumn && !excludes.contains(column)) {//排除字段个数
                    ignoreColumnNum ++;
                }
            }
        }

        if (sqlFlag.equals(SQL_INSERT)) {
            //Object[] args = new Object[fields.length];;
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                ignoreColumnNum += excludes.size();
            }
            Object[] args = new Object[fields.length - ignoreColumnNum];;

            int n = 0;//tmepArr 和 bean的长度不一样
            for (int i = 0; fields != null && i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true); // 暴力反射
                    Object field = fields[i].get(entity);
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(field!=null) {
                        /*if (field.getClass().getName().equals("java.util.Date")) {
                            Date parse = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(field.toString());
                            field = new java.sql.Timestamp(parse.getTime());
                        }*/
                        args[n] = field;
                    }
                    n++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return args;
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            Object[] tempArr = new Object[fields.length];
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                tempArr = new Object[fields.length - excludes.size()];
            }
            int n = 0;//tmepArr 和 bean的长度不一样
            for (int i = 0; fields != null && i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true); // 暴力反射
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(fields[i].get(entity)!=null) {
                        tempArr[n] = fields[i].get(entity);
                    }
                    n++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Object[] args = new Object[tempArr.length];
            System.arraycopy(tempArr, 1, args, 0, tempArr.length - 1); // 数组拷贝
            args[args.length - 1] = tempArr[0];
            return args;
        } else if (sqlFlag.equals(SQL_DELETE)) {
            Object[] args = new Object[1]; // 长度是1
            fields[0].setAccessible(true); // 暴力反射
            try {
                args[0] = fields[0].get(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return args;
        }
        return null;
    }

    // 设置参数类型（写的不全，只是一些常用的）
    private int[] setArgTypes(T entity, String sqlFlag, List<String> excludes) {
        Field[] fields = entityClass.getDeclaredFields();

        int ignoreColumnNum = 0;//排除字段个数
        for (int i = 0; fields != null && i < fields.length; i++) {
            fields[i].setAccessible(true); // 暴力反射
            String column = fields[i].getName();
            boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
            if(excludes==null) {
                if(isColumn) {
                    ignoreColumnNum ++;
                }
            } else {
                if(isColumn && !excludes.contains(column)) {//排除字段个数
                    ignoreColumnNum ++;
                }
            }
        }

        if (sqlFlag.equals(SQL_INSERT)) {
            //int[] argTypes = new int[fields.length];
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                ignoreColumnNum += excludes.size();
            }
            int[] argTypes = new int[fields.length - ignoreColumnNum];

            int n = 0;//argTypes 和 bean的长度不一样
            try {
                for (int i = 0; fields != null && i < fields.length; i++) {
                    fields[i].setAccessible(true); // 暴力反射
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(fields[i].get(entity)!=null) {
                        if (fields[i].get(entity).getClass().getName().equals("java.lang.String")) {
                            argTypes[n] = Types.VARCHAR;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Double")) {
                            argTypes[n] = Types.DECIMAL;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Integer")) {
                            argTypes[n] = Types.INTEGER;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.util.Date")) {
                            argTypes[n] = Types.TIMESTAMP;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.sql.Timestamp")) {
                            argTypes[n] = Types.TIMESTAMP;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Boolean")) {
                            argTypes[n] = Types.BOOLEAN;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.lang.Float")) {
                            argTypes[n] = Types.FLOAT;
                        }
                    }
                    n++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;
        } else if (sqlFlag.equals(SQL_UPDATE)) {

            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                ignoreColumnNum += excludes.size();
            }
            int[] tempArgTypes = new int[fields.length - ignoreColumnNum];
            int[] argTypes = new int[tempArgTypes.length];

           /* int[] tempArgTypes = new int[fields.length];
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                tempArgTypes = new int[fields.length - excludes.size()];
            }
            int[] argTypes = new int[tempArgTypes.length];*/

            int n = 0;//argTypes 和 bean的长度不一样
            try {
                for (int i = 0; tempArgTypes != null && n < tempArgTypes.length; i++) {
                    fields[i].setAccessible(true); // 暴力反射
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(fields[i].get(entity)!=null) {
                        if (fields[i].get(entity).getClass().getName().equals("java.lang.String")) {
                            tempArgTypes[n] = Types.VARCHAR;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Double")) {
                            tempArgTypes[n] = Types.DECIMAL;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Integer")) {
                            tempArgTypes[n] = Types.INTEGER;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.util.Date")) {
                            tempArgTypes[n] = Types.TIMESTAMP;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.sql.Timestamp")) {
                            tempArgTypes[n] = Types.TIMESTAMP;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.lang.Boolean")) {
                            tempArgTypes[n] = Types.BOOLEAN;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.lang.Float")) {
                            tempArgTypes[n] = Types.FLOAT;
                        }
                    }
                    n++;
                }
                System.arraycopy(tempArgTypes, 1, argTypes, 0, tempArgTypes.length - 1); // 数组拷贝
                argTypes[argTypes.length - 1] = tempArgTypes[0];

            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;

        } else if (sqlFlag.equals(SQL_DELETE)) {
            int[] argTypes = new int[1]; // 长度是1
            try {
                fields[0].setAccessible(true); // 暴力反射
                if (fields[0].get(entity).getClass().getName().equals("java.lang.String")) {
                    argTypes[0] = Types.VARCHAR;
                } else if (fields[0].get(entity).getClass().getName().equals("java.lang.Integer")) {
                    argTypes[0] = Types.INTEGER;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;
        }
        return null;
    }

    // 设置参数类型（写的不全，只是一些常用的）
    private <E> int[] setArgTypes(E entity, String sqlFlag, List<String> excludes, Class<E> entityClass) {
        Field[] fields = entity.getClass().getDeclaredFields();

        int ignoreColumnNum = 0;//排除字段个数
        for (int i = 0; fields != null && i < fields.length; i++) {
            fields[i].setAccessible(true); // 暴力反射
            String column = fields[i].getName();
            boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
            if(excludes==null) {
                if(isColumn) {
                    ignoreColumnNum ++;
                }
            } else {
                if(isColumn && !excludes.contains(column)) {//排除字段个数
                    ignoreColumnNum ++;
                }
            }
        }

        if (sqlFlag.equals(SQL_INSERT)) {
            //int[] argTypes = new int[fields.length];
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                ignoreColumnNum += excludes.size();
            }
            int[] argTypes = new int[fields.length - ignoreColumnNum];

            int n = 0;//argTypes 和 bean的长度不一样
            try {
                for (int i = 0; fields != null && i < fields.length; i++) {
                    fields[i].setAccessible(true); // 暴力反射
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(fields[i].get(entity)!=null) {
                        if (fields[i].get(entity).getClass().getName().equals("java.lang.String")) {
                            argTypes[n] = Types.VARCHAR;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Double")) {
                            argTypes[n] = Types.DECIMAL;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Integer")) {
                            argTypes[n] = Types.INTEGER;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.util.Date")) {
                            argTypes[n] = Types.TIMESTAMP;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.sql.Timestamp")) {
                            argTypes[n] = Types.TIMESTAMP;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Boolean")) {
                            argTypes[n] = Types.BOOLEAN;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.lang.Float")) {
                            argTypes[n] = Types.FLOAT;
                        }
                    }
                    n++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            int[] tempArgTypes = new int[fields.length];
            if(excludes!=null) { // 20170918 用于判断排除bean中的某些字段
                tempArgTypes = new int[fields.length - excludes.size()];
            }
            int[] argTypes = new int[tempArgTypes.length];
            int n = 0;//argTypes 和 bean的长度不一样
            try {
                for (int i = 0; tempArgTypes != null && n < tempArgTypes.length; i++) {
                    fields[i].setAccessible(true); // 暴力反射
                    String column = fields[i].getName();
                    boolean isColumn = fields[i].isAnnotationPresent(IgnoreColumn.class);//判断是否有自定义注解(排除bean中的某些字段),
                    if(isColumn){
                        continue;
                    }
                    if(excludes!=null && excludes.contains(column)) { // 20170918 用于判断排除bean中的某些字段
                        continue;
                    }
                    if(fields[i].get(entity)!=null) {
                        if (fields[i].get(entity).getClass().getName().equals("java.lang.String")) {
                            tempArgTypes[n] = Types.VARCHAR;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Double")) {
                            tempArgTypes[n] = Types.DECIMAL;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Integer")) {
                            tempArgTypes[n] = Types.INTEGER;
                        } else if (fields[i].get(entity).getClass().getName().equals("java.util.Date")) {
                            tempArgTypes[n] = Types.TIMESTAMP;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.sql.Timestamp")) {
                            tempArgTypes[n] = Types.TIMESTAMP;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.lang.Boolean")) {
                            tempArgTypes[n] = Types.BOOLEAN;
                        }else if (fields[i].get(entity).getClass().getName().equals("java.lang.Float")) {
                            tempArgTypes[n] = Types.FLOAT;
                        }
                    }
                    n++;
                }
                System.arraycopy(tempArgTypes, 1, argTypes, 0, tempArgTypes.length - 1); // 数组拷贝
                argTypes[argTypes.length - 1] = tempArgTypes[0];

            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;

        } else if (sqlFlag.equals(SQL_DELETE)) {
            int[] argTypes = new int[1]; // 长度是1
            try {
                fields[0].setAccessible(true); // 暴力反射
                if (fields[0].get(entity).getClass().getName().equals("java.lang.String")) {
                    argTypes[0] = Types.VARCHAR;
                } else if (fields[0].get(entity).getClass().getName().equals("java.lang.Integer")) {
                    argTypes[0] = Types.INTEGER;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;
        }
        return null;
    }

    private List<T> find(int pageNo, int pageSize, Map<String, String> where, LinkedHashMap<String, String> orderby) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        // where 与 order by 要写在select * from table 的后面，而不是where rownum<=? ) where rn>=?的后面
        StringBuffer sql = new StringBuffer(" select * from "
                + tablename);
        if (where != null && where.size() > 0) {
            sql.append(" where "); // 注意不是where
            for (Map.Entry<String, String> me : where.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                if(columnName.split(" ")[1].contains("in")) {
                    String replace = columnValue.replace(",", "','");
                    sql.append(columnName).append(" ('").append(replace).append("') and "); // 没有考虑or的情况
                } else if(columnName.split(" ")[1].contains("like")) {
                    sql.append(columnName).append(" '%").append(columnValue).append("%' and "); // 模糊查询
                }else { //=
                    sql.append(columnName).append(" '").append(columnValue).append("' and "); // 没有考虑or的情况
                }
            }
            int endIndex = sql.lastIndexOf("and");
            if (endIndex > 0) {
                sql = new StringBuffer(sql.substring(0, endIndex));
            }
        }
        if (orderby != null && orderby.size() > 0) {
            sql.append(" order by ");
            for (Map.Entry<String, String> me : orderby.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                sql.append(columnName).append(" ").append(columnValue).append(",");
            }
            sql = sql.deleteCharAt(sql.length() - 1);
        }
        int sIndex = (pageNo-1)*pageSize;
        sql.append(" limit "+pageSize+" offset "+sIndex);

        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        return jdbcTemplate.query(sql.toString(), rowMapper);
    }

    private List<T> find(Map<String, String> where, LinkedHashMap<String, String> orderby) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        StringBuffer sql = new StringBuffer(" SELECT * FROM " + tablename);
        if (where != null && where.size() > 0) {
            sql.append(" WHERE ");
            for (Map.Entry<String, String> me : where.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                if(columnName.split(" ")[1].contains("in")) {
                    String replace = columnValue.replace(",", "','");
                    sql.append(columnName).append(" ('").append(replace).append("') and "); // 没有考虑or的情况
                } else if(columnName.split(" ")[1].contains("like")) {
                    sql.append(columnName).append(" '%").append(columnValue).append("%' and "); // 模糊查询
                }else { //=
                    sql.append(columnName).append(" '").append(columnValue).append("' and "); // 没有考虑or的情况
                }
            }
            int endIndex = sql.lastIndexOf("and");
            if (endIndex > 0) {
                sql = new StringBuffer(sql.substring(0, endIndex));
            }
        }
        if (orderby != null && orderby.size() > 0) {
            sql.append(" ORDER BY ");
            for (Map.Entry<String, String> me : orderby.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                sql.append(columnName).append(" ").append(columnValue).append(",");
            }
            sql = sql.deleteCharAt(sql.length() - 1);
        }
        System.out.println("SQL=" + sql);
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        return jdbcTemplate.query(sql.toString(), rowMapper);
    }

    private int count(Map<String, String> where) {
        String tablename = entityClass.getSimpleName();
        boolean isTable = entityClass.isAnnotationPresent(MyTable.class);
        if(isTable) {
            //获取User类@Table注解的值value，该值我们定义为User表的表名称
            MyTable t = (MyTable) entityClass.getAnnotation(MyTable.class);
            tablename = t.value();
        }

        StringBuffer sql = new StringBuffer(" SELECT COUNT(*) FROM " + tablename);
        if (where != null && where.size() > 0) {
            sql.append(" WHERE ");
            for (Map.Entry<String, String> me : where.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                if(columnName.split(" ")[1].contains("in")) {
                    String replace = columnValue.replace(",", "','");
                    sql.append(columnName).append(" ('").append(replace).append("') and "); // 没有考虑or的情况
                } else if(columnName.split(" ")[1].contains("like")) {
                    sql.append(columnName).append(" '%").append(columnValue).append("%' and "); // 模糊查询
                }else { //=
                    sql.append(columnName).append(" '").append(columnValue).append("' and "); // 没有考虑or的情况
                }
            }
            int endIndex = sql.lastIndexOf("and");
            if (endIndex > 0) {
                sql = new StringBuffer(sql.substring(0, endIndex));
            }
        }
        System.out.println("SQL=" + sql);
        //return jdbcTemplate.queryForInt(sql.toString());
        return jdbcTemplate.queryForObject(sql.toString(), null, Integer.class);
    }
}
