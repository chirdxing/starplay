package com.star.common.redis;

import redis.clients.jedis.Jedis;

/**
 * jedis执行命令，有返回值
 */
public interface JedisCallable<T> {

	/**
	 * 使用jedis连接执行redis命令
	 * 
	 * @param instance
	 * @return
	 * @throws Exception
	 *             业务异常可以自行处理
	 */
	T call(Jedis instance) throws Exception;
}
