package com.lvmama.phpsrv.Repository.cache;

import com.lvmama.phpsrv.utils.Common;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Callable;

/**
 * redis缓存工具类
 * Created by libiying on 2016/10/14.
 */

public class RedisCache implements Cache{

    private RedisTemplate<String, Object> redisTemplate;

    private String name;

    private long lifeTime;

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public Object getNativeCache() {
        return null;
    }

    public ValueWrapper get(Object key) {
        final String keyf = (String) key;
        Object object = null;
        object = redisTemplate.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                byte[] key = keyf.getBytes();
                byte[] value = redisConnection.get(key);
                if(value == null){
                    return null;
                }

                return Common.byteArrToObject(value);
            }
        });

        return (object != null ? new SimpleValueWrapper(object) : null);
    }

    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    public void put(Object key, Object value) {
        final String keyf = (String) key;
        final Object valuef = value;

        redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {

                byte[] keyb = keyf.getBytes();
                byte[] valueb = Common.objectToByteArr(valuef);
                redisConnection.set(keyb, valueb);
                if(lifeTime > 0){
                    redisConnection.expire(keyb, lifeTime);
                }

                return 1L;
            }
        });
    }

    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    public void evict(Object key) {
        final String keyf = (String) key;
        redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.del(keyf.getBytes());
            }
        });
    }

    public void clear() {
        redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.flushDb();
                return "ok";
            }
        });
    }
}
