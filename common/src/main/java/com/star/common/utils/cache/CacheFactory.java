package com.star.common.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFactory {
	public static final int CACHE_TYPE_JAVA = 1;
	public static Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();
	

	/**
	 * Java序列化对象类型的Cache
	 * @param cacheName redis实例名称，关联某个库（注意cacheName必须为Redis.xml中配置的bean名称）
	 * @return
	 */
	public static Cache getCache(String cacheName) {
		return getCache(cacheName, CACHE_TYPE_JAVA);
	}
	
	/**
	 * 返回默认db实例coreRedisExecutor的cache
	 * @return
	 */
	public static Cache getDefaultCache() {
		return getCache("coreRedisExecutor", CACHE_TYPE_JAVA);
	}
	
	/**
	 * 返回指定缓存类型的Cache
	 * @param cacheName redis实例名称，关联某个库（注意cacheName必须为Redis.xml中配置的bean名称）
	 * @return
	 */
	public static Cache getCache(String cacheName, int cacheType) {
		Cache cache = cacheMap.get(cacheName);
		if (null == cache) {
			synchronized (CacheFactory.class) {
				cache = cacheMap.get(cacheName);
				if (null == cache) {
					if (cacheType == CACHE_TYPE_JAVA) {
						cache = new JavaSerializeCache(cacheName);
					}
					cacheMap.put(cacheName, cache);
				}
			}
		}
		return cache;
	}
	
	public static void main(String[] args) throws Exception{
		long start = System.nanoTime();
		Thread.sleep(1000);
		long end = System.nanoTime();
		System.out.println(end - start);
	}
	
}
