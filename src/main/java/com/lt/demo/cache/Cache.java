package com.lt.demo.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.MemoryUnit;
import org.apache.log4j.Logger;


/**
 * Created by litao on 15/12/3.
 */
public class Cache {

    private static Logger logger = Logger.getLogger(Cache.class);
    private static final String SUGGEST = "-suggest";
    private static final String SEARCH = "-search";

    private net.sf.ehcache.Cache searchCache;

    private net.sf.ehcache.Cache suggestCache;

    private String searchCacheName;
    private String suggestCacheName;

    private CacheConfig cacheConfig;

    private CacheConfig checkAndInitConfig(CacheConfig cacheConfig) {

        if (cacheConfig == null) {
            return CacheConfig.DEFAULT_CONFIG;
        } else {
            if (cacheConfig.getSearchCacheSize() == null) {
                cacheConfig.setSearchCacheSize(CacheConfig.DEFAULT_CONFIG.getSearchCacheSize());
            }

            if (cacheConfig.getSuggestCacheSize() == null) {
                cacheConfig.setSuggestCacheSize(CacheConfig.DEFAULT_CONFIG.getSuggestCacheSize());
            }

            if (cacheConfig.getName() == null) {
                cacheConfig.setName(CacheConfig.DEFAULT_CONFIG.getName());
            }
            if (cacheConfig.getTtl() == null) {
                cacheConfig.setTtl(CacheConfig.DEFAULT_CONFIG.getTtl());
            }
            if (cacheConfig.isUseCache() == null) {
                cacheConfig.setUseCache(CacheConfig.DEFAULT_CONFIG.isUseCache());
            }
        }
        return cacheConfig;
    }

    public Cache() {
        this(CacheConfig.DEFAULT_CONFIG);
    }

    public Cache(CacheConfig cacheConfig) {


        this.cacheConfig = checkAndInitConfig(cacheConfig);

        if (!this.cacheConfig.isUseCache()) {

            this.searchCache = null;
            this.suggestCache = null;
        } else {

            this.searchCacheName = this.cacheConfig.getName() + SEARCH;
            this.suggestCacheName = this.cacheConfig.getName() + SUGGEST;
            Configuration config = new Configuration();
            CacheConfiguration suggestCacheConfiguration = new CacheConfiguration()
                    .name(this.suggestCacheName) //设置缓存名称
                    .overflowToOffHeap(false) //是否使用非堆栈结构存储:不使用减小序列化时间
                    .eternal(false) //缓存是否永久有效:false
                    .timeToLiveSeconds(this.cacheConfig.getTtl()) //设置缓存过期时间:单位秒
                    .maxBytesLocalHeap(this.cacheConfig.getSuggestCacheSize(), MemoryUnit.MEGABYTES);//设置缓存大小

            CacheConfiguration searchCacheConfiguration = new CacheConfiguration()
                    .name(this.searchCacheName) //设置缓存名称
                    .overflowToOffHeap(false) //是否使用非堆栈结构存储:不使用减小序列化时间
                    .eternal(false) //缓存是否永久有效:false
                    .timeToLiveSeconds(this.cacheConfig.getTtl()) //设置缓存过期时间:单位秒
                    .maxBytesLocalHeap(this.cacheConfig.getSearchCacheSize(), MemoryUnit.MEGABYTES);//设置缓存大小

            config.addCache(suggestCacheConfiguration);
            config.addCache(searchCacheConfiguration);
            config.setName(cacheConfig.getName());

            CacheManager manager = CacheManager.newInstance(config);

            this.searchCache = manager.getCache(this.searchCacheName);
            this.suggestCache = manager.getCache(this.suggestCacheName);

            if (this.searchCache == null) {
                logger.error("build search cache occurs an exception");
            }

            if (this.suggestCache == null) {
                logger.error("build suggest cache occurs an exception");
            }
        }

    }


    public String getSearchCache(String key) {
        return get(key, CacheTypeEnum.SEARCH);
    }

    public String getSuggestCache(String key) {
        return get(key, CacheTypeEnum.SUGGEST);
    }

    public String get(String key, CacheTypeEnum type) {
        try {
            if (cacheConfig.isUseCache()) {
                Element element = null;
                if (type.getValue() == CacheTypeEnum.SEARCH.getValue()) {
                    searchCache.acquireReadLockOnKey(key);
                    try {
                        element = searchCache.get(key);
                    } finally {
                        searchCache.releaseReadLockOnKey(key);
                    }
                    if (element == null) {
                        return null;
                    } else {
                        logger.debug("get search cache,key:" + key + ",type:" + type.toString());
                        return element.getObjectValue().toString();
                    }
                } else {
                    suggestCache.acquireReadLockOnKey(key);
                    try {
                        element = suggestCache.get(key);
                    } finally {
                        suggestCache.releaseReadLockOnKey(key);
                    }
                    if (element == null) {
                        return null;
                    } else {
                        logger.debug("get suggest cache,key:" + key + ",type:" + type.toString());
                        return element.getObjectValue().toString();
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("get cache occurs an error,key:" + key, e);
        }
        return null;

    }

    public void putSearchCache(String key, String value) {
        put(key, value, CacheTypeEnum.SEARCH);
    }

    public void putSuggestCache(String key, String value) {
        put(key, value, CacheTypeEnum.SUGGEST);
    }

    public void put(String key, String value, CacheTypeEnum type) {
        try {
            if (cacheConfig.isUseCache()) {
                logger.debug("put cache,key:" + key + ",type:" + type.toString());
                if (type.getValue() == CacheTypeEnum.SEARCH.getValue()) {
                    searchCache.acquireWriteLockOnKey(key);
                    try {
                        Element element = new Element(key, value);
                        searchCache.put(element);
                    } finally {
                        searchCache.releaseWriteLockOnKey(key);
                    }
                } else {
                    suggestCache.acquireWriteLockOnKey(key);
                    try {
                        Element element = new Element(key, value);
                        suggestCache.put(element);
                    } finally {
                        suggestCache.releaseWriteLockOnKey(key);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("put cache occurs an error,key:" + key, e);
        }
    }


    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public void setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }


}
