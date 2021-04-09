package com.eros.thread.business.worker;

import com.eros.thread.business.spike.ResolveInventoryService;
import com.eros.thread.exception.NullInventoryException;
import com.eros.thread.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author xuwentao
 * @Date: 2021/4/9 10:27
 * @Description:
 */
public class StockWorker extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(StockWorker.class);

    private static Integer count = new Integer(0);

    private ResolveInventoryService resolveInventoryService;

    private Integer goods;

    public StockWorker(ResolveInventoryService resolveInventoryService, String workerName) {
        this.resolveInventoryService = resolveInventoryService;
        this.setName(workerName);
        this.goods = new Integer(0);
    }

    @Override
    public void run() {
        // 扣库存
        while (true) {
            try {
                int goodNum = RandomUtils.getRandom().nextInt(10);
                goodNum = goodNum > 0 ? goodNum : 1;
                boolean flag = resolveInventoryService.deductionInventory(goodNum);
                if(flag) {
                    this.goods += goodNum;
                }
                logger.info("StockWorker[run] worker: {} goodNum: {} status: {}", this.getName(), goodNum, flag);
                Thread.sleep(goodNum * 100);
            } catch (NullInventoryException e) {
                // 模拟回退
                int rollback = RandomUtils.getRandom().nextInt(this.goods);
                resolveInventoryService.rollbackInventory(rollback);
                // 统计总数
                synchronized (count) {
                    this.goods -= rollback;
                    StockWorker.count += this.goods;
                    logger.info("Activity is over, worker {} owns {} goods, count {}", this.getName(), this.goods, StockWorker.count);
                }
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
