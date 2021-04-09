package com.eros.thread.business.spike.impl;

import com.eros.thread.business.spike.ResolveInventoryService;
import com.eros.thread.exception.NullInventoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuwentao
 * @Date: 2021/4/9 14:54
 * @Description: 本地扣除库存
 */
@Service("localResolveInventoryService")
public class LocalResolveInventoryServiceImpl implements ResolveInventoryService {

    private final static Logger logger = LoggerFactory.getLogger(ResolveInventoryService.class);

    @Value(value = "${good.stock:1000}")
    private Integer goodStock;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 计时
     */
    private Long start;

    /**
     * 锁
     */
    private ReentrantLock lock;

    @PostConstruct
    public void init() {
        this.lock = new ReentrantLock();
        this.start = System.currentTimeMillis();
    }

    /**
     * todo 测试环境达不到那么大的测试
     * 扣减库存
     * 需要通过锁，保证库存不会被脏读
     * 80张票 - 1000张票 - 10000张票 - 100000张票
     * synchronized - 单位时间只有一个线程可以去使用
     *      a. 方法级别:
     *          4个工作者 - (124 ~ 180) - (131 ~ 238) - (217 ～ 439) - (3532 ～ 4071)
     * volatile
     *      a. 属性级别: (114 ~ 130) - () - () - (3586 ~ 3743)
     *
     * lock
     *
     * redis
     */
    @Override
    public boolean deductionInventory(Integer goodNum) {
        logger.info("Good Stock: {}, Good Num: {}", goodStock, goodNum);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(checkInventory()) {
                logger.info("This activity timing: {}", System.currentTimeMillis() - this.start);
                throw new NullInventoryException();
            }
            if(goodStock.compareTo(goodNum) >= 0) {
                goodStock -= goodNum;
                logger.info("New Good Stock: {}", goodStock);
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public boolean rollbackInventory(Integer rollback) {
        return false;
    }

    /**
     * 检查库存
     * @return
     */
    public boolean checkInventory(){
        return this.goodStock == 0;
    }

}
