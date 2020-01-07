package com.star.common.redis;

import redis.clients.jedis.Jedis;

/**
 * jedis执行命令，不带返回值
 */
public interface JedisRunnable {

	/**
	 * 使用jedis连接执行redis命令
	 * 
	 * @param instance
	 * @throws Exception
	 *             业务异常可以自行处理
	 */
	void run(Jedis instance) throws Exception;
}
