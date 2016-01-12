# SearchCache

使用ehcache实现的本地搜索缓存

spring配置:
<!-- 设置cache配置项 -->
<bean name="cacheConfig" class="com.meizu.galaxy2.cache.CacheConfig">
		<property name="name" value="app4"/> <!-- cache的名称 -->
		<property name="searchCacheSize" value="${search.cache.size}"/> <!-- 搜索cache的名称 -->
		<property name="suggestCacheSize" value="${suggest.cache.size}"/> <!-- 联想cache的名称 -->
		<property name="ttl" value="${cache.ttl}"/> <!-- cache的过期时间, 当 当前时间>ttl+放入时间 ,key对应的数据就会过期-->
		<property name="useCache" value="${cache.on}"/> <!-- cache是否生效 -->
	</bean>
<!-- 定义cache -->
<bean id="cache" name="cache" class="com.meizu.galaxy2.cache.Cache">
		<constructor-arg name="cacheConfig" ref="cacheConfig"/>
</bean>