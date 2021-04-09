package com.eros.thread.business;

import com.eros.thread.business.spike.ResolveInventoryService;
import com.eros.thread.business.worker.StockWorker;
import com.eros.thread.config.tool.RedisConstant;
import com.eros.thread.config.tool.RedisTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xuwentao
 * @Date: 2021/4/9 10:18
 * @Description:
 */
@Component
public class BusinessRunner implements CommandLineRunner {

    @Resource(name = "localResolveInventoryService")
    private ResolveInventoryService localResolveInventoryService;

    @Resource(name = "redisResolveInventoryService")
    private ResolveInventoryService redisResolveInventoryService;

    @Resource(name = "foreseeResolveInventoryService")
    private ResolveInventoryService foreseeResolveInventoryService;

    @Value("${commodity.stock:10000}")
    private Long commodityStock;

    @Autowired
    private RedisTools redisTools;

    @Override
    public void run(String... args) throws Exception {
        testForesee();
    }

    private void testLocal() {
        for (int i = 0; i < 8; i++) {
            new StockWorker(localResolveInventoryService, String.format("StockWorker-%d", i)).start();
        }
    }

    private void testRedis() {
        redisTools.initStock(RedisConstant.COMMODITY, commodityStock);
        for (int i = 0; i < 1000; i++) {
            new StockWorker(redisResolveInventoryService, String.format("StockWorker-%d", i)).start();
        }
    }

    private void testForesee() {
        redisTools.initStock(RedisConstant.COMMODITY, commodityStock);
        for (int i = 0; i < 1000; i++) {
            new StockWorker(foreseeResolveInventoryService, String.format("StockWorker-%d", i)).start();
        }
    }

}
