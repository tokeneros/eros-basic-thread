package com.eros.thread.business.spike.impl;

import com.eros.thread.business.spike.ResolveInventoryService;
import com.eros.thread.config.tool.RedisConstant;
import com.eros.thread.config.tool.RedisTools;
import com.eros.thread.exception.NullInventoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xuwentao
 * @Date: 2021/4/9 16:08
 * @Description:
 */
@Service("foreseeResolveInventoryService")
public class ForeseeResolveInventoryServiceImpl implements ResolveInventoryService {

    private final static Logger logger = LoggerFactory.getLogger(ForeseeResolveInventoryServiceImpl.class);

    @Autowired
    private RedisTools redisTools;

    /**
     * todo 只是生成订单，没有支付
     * @param goodNum
     * @return
     */
    @Override
    public boolean deductionInventory(Integer goodNum) {
        // 扣除
        Long stock = redisTools.deductStock(RedisConstant.COMMODITY, goodNum.longValue());
        if(stock > 0) {
            logger.info("RedisResolveInventoryServiceImpl[deductionInventory] Good Stock: {}, Good Num: {}", stock, goodNum);
            return true;
        } else {
            // 回滚
            redisTools.rollbackStock(RedisConstant.COMMODITY, goodNum.longValue());
            throw new NullInventoryException();
        }
    }

    @Override
    public boolean rollbackInventory(Integer rollback) {
        redisTools.rollbackStock(RedisConstant.COMMODITY, rollback.longValue());
        return true;
    }

}
