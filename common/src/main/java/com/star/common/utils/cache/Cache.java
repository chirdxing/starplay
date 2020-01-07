package com.star.common.utils.cache;

import java.io.Serializable;

public interface Cache {
	
	/**
	 * 获取cache名称，主要是区分不同cache库实例
	 * @return
	 */
	String getCacheName();

	/**
	 * 判断key是否存在
	 * @param key
	 * @return
	 */
	boolean isExists(String key);
	
	/**
	 * 删除指定的key如果存在的话
	 * @param key
	 */
	void remove(String key);
	
	/**
	 * 根据指定key关联的对象
	 * @param key
	 * @return
	 */
	<T extends Serializable> T get(String key);
	
	/**
	 * 将对象存在指定的key中
	 * @param key
	 * @param value
	 */
	<T extends Serializable> void put(String key, T value);
	
	/**
	 * 将对象存在指定的key中并设置过期时间
	 * @param key
	 * @param value
	 * @param expire
	 */
	<T extends Serializable> void put(String key, T value, int expire);
	
}
