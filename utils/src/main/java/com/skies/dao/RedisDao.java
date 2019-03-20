package com.skies.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2017-10-30.
 */
public interface RedisDao {
    /**
     * 保存Object数据
     * @param key
     * @param value
     */
    void saveObjectData(String key, Object value);

    /**
     * 保存Map数据
     * @param key
     * @param value
     */
    void saveMapData(String key, Map<String, Object> value);

    /**
     * 获取Map数据
     * @param key
     * @return Map<String,Object>
     */
    Map<String,Object> getMapData(String key);

    /**
     * 获取Object数据
     * @param key
     * @return Object
     */
    Object getObjectData(String key);

    /**
     * 获取map中的值
     * @param key
     * @param name
     * @return
     */
    Object getMapInfoData(String key, String name);

    /**
     * 保存List数据
     * @param key
     * @param value
     */
    void saveListData(String key, List<?> value);

    /**
     * 获取List中的值
     * @param key
     * @param index 开始下标
     * @param count 个数
     * @return List<Object>
     */
    List<?> getListData(String key, long index, long count);

    /**
     * 保存map数据
     * @param key
     * @param name
     * @param value
     */
    void saveMapInfoData(String key, String name, Object value);

    /**
     * 获取map数据量
     * @param key
     * @return long
     */
    long getMapDataCount(String key);

    /**
     * 删除Object
     * @param key
     */
    void delelteObject(String key);

    /**
     * 保存Object数据并设置过期时间
     * @param key
     * @param value
     * @param time 有效时间
     * @param unit 单位
     */
    void saveObjectData(String key, Object value, long time, TimeUnit unit);
}
