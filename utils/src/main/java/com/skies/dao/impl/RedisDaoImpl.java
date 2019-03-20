package com.skies.dao.impl;

import com.skies.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2017-10-30.
 */

@Repository
public class RedisDaoImpl implements RedisDao {
    @Autowired
    protected RedisTemplate redisTemplate ;

    @Override
    public void saveObjectData(String key, Object value) {
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public void saveMapData(String key, Map<String, Object> value) {
        redisTemplate.opsForHash().putAll(key,value);
    }

    @Override
    public Map<String, Object> getMapData(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Object getObjectData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Object getMapInfoData(String key, String name) {
        return redisTemplate.opsForHash().get(key,name);
    }

    @Override
    public void saveListData(String key, List<?> value) {
        redisTemplate.opsForList().rightPushAll(key,value);
    }

    @Override
    public List<Object> getListData(String key, long index, long count) {
        return redisTemplate.opsForList().range(key,index,count);
    }

    @Override
    public void saveMapInfoData(String key, String name, Object value) {
        redisTemplate.opsForHash().put(key,name,value);
    }

    @Override
    public long getMapDataCount(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    @Override
    public void delelteObject(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    @Override
    public void saveObjectData(String key, Object value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key,value,time, unit);
    }

}
