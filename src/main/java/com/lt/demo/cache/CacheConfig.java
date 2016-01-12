package com.lt.demo.cache;

/**
 * Created by litao on 15/12/3.
 */
public class CacheConfig implements Cloneable {

    public static final CacheConfig DEFAULT_CONFIG =
            new CacheConfig("default-cache", 500, 300, 1200L, true);
    private String name;

    private Integer searchCacheSize;

    private Integer suggestCacheSize;

    private Long ttl;

    private Boolean useCache;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSuggestCacheSize() {
        return suggestCacheSize;
    }

    public void setSuggestCacheSize(Integer suggestCacheSize) {
        this.suggestCacheSize = suggestCacheSize;
    }

    public Integer getSearchCacheSize() {
        return searchCacheSize;
    }

    public void setSearchCacheSize(Integer searchCacheSize) {
        this.searchCacheSize = searchCacheSize;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public Boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(Boolean useCache) {
        this.useCache = useCache;
    }


    public CacheConfig() {
    }

    public CacheConfig(String name, Integer searchCacheSize, Integer suggestCacheSize, Long ttl, Boolean useCache) {
        this.name = name;
        this.searchCacheSize = searchCacheSize;
        this.suggestCacheSize = suggestCacheSize;
        this.ttl = ttl;
        this.useCache = useCache;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
