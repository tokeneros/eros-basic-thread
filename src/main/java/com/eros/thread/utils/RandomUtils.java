package com.eros.thread.utils;

import java.util.Random;

/**
 * @author xuwentao
 * @Date: 2021/4/9 10:33
 * @Description:
 *
 */
public class RandomUtils {

    private final static ThreadLocal<Random> randomThreadLocal = ThreadLocal.withInitial(() -> {
        return new Random();
    });

    public static Random getRandom(){
        return randomThreadLocal.get();
    }

}
