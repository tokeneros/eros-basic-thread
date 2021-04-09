package com.eros.thread.config.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuwentao
 * @Date: 2021/4/9 13:46
 * @Description:
 */
@Component
public class RedisTools implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(RedisTools.class);

    private final static Long EXPIRE_TIME = new Long("3");

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 值 运算操作
     */
    private ValueOperations<String, Object> valueOperations;

    private ReentrantLock lock;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.valueOperations = redisTemplate.opsForValue();
        this.lock = new ReentrantLock();
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param val
     * @param expireTime 过期时间
     * @return
     */
    public boolean set(String key, Long val, Long expireTime) {
        boolean flag = false;
        try {
            this.valueOperations.set(key, val, expireTime, TimeUnit.SECONDS);
            flag = true;
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return flag;
    }

    /****************  库存操作  ****************/
    /**
     * 初始化库存
     *
     * @param key 一类库存的key
     * @param val 一类库存的val
     * @return
     */
    public boolean initStock(String key, Long val) {
        boolean flag = false;
        try {
            this.valueOperations.set(key, val);
            flag = true;
        } catch (Exception ex) {
            logger.info("RedisTools[initStock] error: {}", ex.getLocalizedMessage());
        }
        return flag;
    }

    /**
     * 扣除库存
     *
     * @param key 一类库存的key
     * @param num 扣除数量
     * @return
     */
    public Long deductStock(String key, Long num) {
        Long decrement = 0L;
        try {
            decrement = this.valueOperations.decrement(key, num);
        } catch (Exception ex) {
            logger.info("RedisTools[deductStock] error: {}", ex.getLocalizedMessage());
        }
        return decrement;
    }

    /**
     * 回滚库存
     *
     * @param key 一类库存的key
     * @param num 回滚数量
     * @return
     */
    public Long rollbackStock(String key, Long num) {
        Long increment = 0L;
        try {
            increment = this.valueOperations.increment(key, num);
        } catch (Exception ex) {
            logger.info("RedisTools[rollbackStock] error: {}", ex.getLocalizedMessage());
        }
        return increment;
    }



}
