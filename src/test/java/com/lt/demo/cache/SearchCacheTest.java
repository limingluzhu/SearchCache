package com.lt.demo.cache;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;


/**
 * Created by litao on 15/12/3.
 */
public class SearchCacheTest {

    private static Cache cache;

    @BeforeClass
    public static void init() throws Exception {
        CacheConfig cacheConfig=CacheConfig.DEFAULT_CONFIG;
        cacheConfig.setUseCache(true);
        cacheConfig.setSuggestCacheSize(20);
        cacheConfig.setSearchCacheSize(10);
        cache=new Cache(cacheConfig);
        cache.put("游戏","游戏玩家",CacheTypeEnum.SUGGEST);
        cache.put("游戏","QQ游戏",CacheTypeEnum.SEARCH);
        cache.put("游戏","QQ游戏1",CacheTypeEnum.SEARCH);
        cache.put("QQ","QQ音乐",CacheTypeEnum.SUGGEST);
    }


    @Test
    public void testMultiCache() throws Exception {

        CacheConfig cacheConfig=CacheConfig.DEFAULT_CONFIG;
        cacheConfig.setName("test1");
        cacheConfig.setUseCache(true);
        cacheConfig.setSuggestCacheSize(20);
        cacheConfig.setSearchCacheSize(10);
        Cache cache1=new Cache(cacheConfig);


        CacheConfig cacheConfig1=(CacheConfig) CacheConfig.DEFAULT_CONFIG.clone();
        cacheConfig.setName("test2");
        cacheConfig.setUseCache(true);
        cacheConfig.setSuggestCacheSize(20);
        cacheConfig.setSearchCacheSize(10);
        Cache cache2=new Cache(cacheConfig1);


        cache1.put("游戏","游戏玩家",CacheTypeEnum.SUGGEST);
        cache1.put("游戏","QQ游戏",CacheTypeEnum.SEARCH);
        cache1.put("游戏","QQ游戏1",CacheTypeEnum.SEARCH);
        cache1.put("QQ","QQ音乐",CacheTypeEnum.SUGGEST);

        cache2.put("游戏","QQ游戏1",CacheTypeEnum.SEARCH);
        cache2.put("游戏","QQ游戏",CacheTypeEnum.SEARCH);
        cache2.put("游戏","游戏玩家",CacheTypeEnum.SEARCH);
        cache2.put("QQ","QQ音乐",CacheTypeEnum.SUGGEST);



        Assert.assertEquals("QQ音乐",cache1.getSuggestCache("QQ"));

        Assert.assertEquals("游戏玩家",cache2.getSearchCache("游戏"));

    }

    @Test
    public void testGet() throws Exception {
        String value=cache.get("游戏",CacheTypeEnum.SUGGEST).toString();
        Assert.assertEquals("游戏玩家",value);
        value=cache.get("游戏",CacheTypeEnum.SEARCH).toString();
        Assert.assertEquals("QQ游戏1",value);
    }

    @Test
    public void testPut() throws Exception {
        cache.put("QQ","QQ音乐",CacheTypeEnum.SUGGEST);
        String value=cache.get("QQ",CacheTypeEnum.SUGGEST).toString();
        Assert.assertEquals("QQ音乐",value);
    }

    @Test
    public void concurrencyTest()
    {

        ExecutorService service = Executors.newFixedThreadPool(100);
        final Random random=new Random();
        final CyclicBarrier readAndWrite=new CyclicBarrier(100, new Runnable() {
            public void run() {
                System.out.println("读写线程都创建结束了,开始跑起来!");
            }
        });
        for (int i = 0; i < 50; i++) {
            System.out.println("创建写线程" + i);
            Runnable run = new Runnable() {
                public void run() {
                    try {
                        readAndWrite.await();
                        System.out.println("启动写线程");
                        if(random.nextBoolean()) {
                            cache.put("QQ", "QQ音乐", CacheTypeEnum.SUGGEST);
                        }
                        else
                        {
                            cache.put("QQ", "QQ音乐", CacheTypeEnum.SEARCH);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            service.execute(run);
        }

        for (int i = 0; i < 50; i++) {
            System.out.println("创建读线程" + i);
            Runnable run = new Runnable() {
                public void run() {
                    try {
                        readAndWrite.await();
                        System.out.println("启动读线程");
                        if(random.nextBoolean()) {
                            cache.get("QQ",CacheTypeEnum.SUGGEST);
                        }
                        else
                        {
                            cache.get("QQ",CacheTypeEnum.SEARCH);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            service.execute(run);
        }


        String value=cache.get("QQ",CacheTypeEnum.SUGGEST).toString();
        Assert.assertEquals("QQ音乐",value);

    }

}