//package com.yuan.redis;
//
//import com.yuan.util.BaseException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by lynn on 2018/5/15.
// */
//@Component
//public class RedisDistributionLockUtil {
//    protected static final Logger LOGGER = LoggerFactory.getLogger(RedisDistributionLockUtil.class);
//    @Resource
//    private  RedisTemplateCache redisTemplateCache;
//
//    private static volatile RedisTemplateCache redisTemplateCacheV;
//    private final static int TEN_MILLISECONDS = 10;
//
//    @PostConstruct
//    public void init(){
//        RedisDistributionLockUtil.redisTemplateCacheV = redisTemplateCache;
//    }
//
//    /**
//     * 加锁
//     * @param lockKey
//     * @return
//     */
//    public static boolean lock(String lockKey) throws Exception {
//        int count = 0;
//        while (count < 10) {
//            Assert.notNull(redisTemplateCacheV, "jimClientV is null, doesn't connect to Redis Cluster");
//            String lockValue = lockKey + "_" + System.currentTimeMillis();
//            LOGGER.info("RedisDistributionLockUtil.lock, lockKey:" + lockKey);
//            Boolean j = redisTemplateCacheV.setNX(lockKey, lockValue);
//            LOGGER.info("RedisDistributionLockUtil.lock, Redis分布式锁设置是否成功:" + j);
//            String value = redisTemplateCacheV.get(lockKey);
//            LOGGER.info("RedisDistributionLockUtil.lock, Redis分布式设置的锁的值是：" + value);
//            if (j != null && j) {
//                redisTemplateCacheV.expire(lockKey, TEN_MILLISECONDS, TimeUnit.MILLISECONDS);
//                return true;
//            }
//            Thread.sleep(TEN_MILLISECONDS);
//            count++;
//        }
//        throw new Exception("获取锁失败");
//    }
//
//    /**
//     * 加锁
//     * @param lockKey
//     * @param expire  锁失效时间  单位：毫秒
//     * @return
//     */
//    public static boolean lock(String lockKey, int expire) {
//        Assert.notNull(redisTemplateCacheV, "jimClientV is null, doesn't connect to Redis Cluster");
//        String lockValue = lockKey + "_" + System.currentTimeMillis();
//        LOGGER.info("RedisDistributionLockUtil.lock expire, lockKey:" + lockKey);
//        /*redis 2.6.12以后版本，redis 锁使用set*/
//        Boolean j = redisTemplateCacheV.set(lockKey, lockValue, expire);
//        LOGGER.info("RedisDistributionLockUtil.lock expire, Redis分布式锁设置是否成功:" + j );
//        if (j != null && j) {
//            return true;
//        }
//        throw new Exception("获取锁失败");
//    }
//
//    /**
//     * 异步定时解锁
//     * @param key
//     * @param expire 倒计时  单位：毫秒
//     */
//    public static void unLockAsyn(final String key, int expire) {
//        try {
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    unLock(key);
//                }
//            }, expire);
//        } catch (Throwable t) {
//            LOGGER.error("RedisDistributionLockUtil.unLockAsyn ", t);
//        }
//    }
//
//    /**
//     * 解锁
//     * @param key
//     */
//    public static void unLock(String key) {
//        Assert.notNull(redisTemplateCacheV, "jimClientV is null, doesn't connect to Redis Cluster");
//        try {
//            redisTemplateCacheV.del(key);
//        } catch (Throwable t) {
//            LOGGER.error("RedisDistributionLockUtil.unLock ", t);
//        }
//    }
//}
