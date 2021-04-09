package com.eros.thread.business.spike;

import com.eros.thread.exception.NullInventoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuwentao
 * @Date: 2021/4/9 10:17
 * @Description:
 */
public interface ResolveInventoryService {

    /**
     * 扣除库存
     * @param goodNum
     * @return
     */
    boolean deductionInventory(Integer goodNum);

    /**
     * 取消订单，回滚库存
     * @param rollback
     * @return
     */
    boolean rollbackInventory(Integer rollback);

}
