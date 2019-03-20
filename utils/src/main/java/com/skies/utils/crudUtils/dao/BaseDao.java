package com.skies.utils.crudUtils.dao;



import com.skies.utils.crudUtils.PageResult;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * jdbc基础操作
 * @param
 */
public interface BaseDao<T> {

    public void save(T entity);
    public void save(T entity, List<String> excludes);

    <E> void save(E entity, List<String> excludes, Class<E> entityClass);

    public void update(T entity);
    public void update(T entity, List<String> excludes);
    <E> void update(E entity, List<String> excludes, Class<E> entityClass);



    public void delete(T entity);


    public void delete(Serializable id);


    public void deleteAll();

    /**
     * 批量保存
     */
    void batchSave(List<T> list);

    /**
     * 批量保存
     */
    <E> void batchSave(final List<E> list, final Class<E> entityClass);

    /**
     * 未完成
     */
    public void batchUpdate(List<T> list);

//    /**
//     * 未完成
//     */
//    public void batchDelete(List<T> list);


    public T findById(Serializable id);


    public List<T> findAll();

    public PageResult<T> findByPage(int pageNo, int pageSize);


    //例如：name='张三'，map.put("name =","张三"); in 则为map.put("name in","张三,李四")
    public PageResult<T> findByPage(int pageNo, int pageSize, Map<String, String> where);


    public PageResult<T> findByPage(int pageNo, int pageSize, LinkedHashMap<String, String> orderby);


    public PageResult<T> findByPage(int pageNo, int pageSize, Map<String, String> where, LinkedHashMap<String, String> orderby);

    /**
     *
     * @param where Map   例如：name='张三'，map.put("name =","张三"); in 则为map.put("name in","张三,李四")
     * @param orderby
     * @return
     */
    public List<T> findByConditons(Map<String, String> where, LinkedHashMap<String, String> orderby);

    void execSQL(String sql);

    List<T> executeSql(String sql);

    Map<String,Object> queryForMap(String sql);

    List<Map<String, Object>> queryForList(String sql);

    List<String> queryForList2(String sql);
}
