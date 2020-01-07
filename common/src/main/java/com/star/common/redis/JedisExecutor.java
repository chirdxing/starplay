package com.star.common.redis;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Jedis执行器，用来执行与Redis相关的业务逻辑
 * <p>
 * 业务逻辑由业务模块来写，这里只负责执行，包括异常的处理，Redis连接的分配与回收
 * <p>
 * 注：业务逻辑也可以处理异常，如下所示：
 * 
 * <pre>
 * RedisExecutor executor = ...;
 * JedisRunnable runnable = ...;
 * try {
 *     executor.submit(runnable);
 * } catch (Exception ex) {
 *     logger.error("XXXXXX", ex);
 * }
 */
public class JedisExecutor implements RedisExecutor {

	private Logger logger = LoggerFactory.getLogger(JedisExecutor.class.getName());

	private JedisPool jedisPool;

	public JedisExecutor(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	private Jedis getJedis(JedisPool jedisPool) {
		Jedis resource = null;
		try {
			resource = jedisPool.getResource();
		} catch (JedisException e) {
			logger.error("从连接池中拿Jedis连接时发生异常", e);
			throw e;
		}
		if (resource == null) {
			logger.error("从连接池中拿Jedis连接为空");
			throw new IllegalStateException("从连接池中拿Jedis连接为空");
		}
		return resource;
	}

	private void finishResource(JedisPool jedisPool, Jedis resource,
			boolean broken) {
		if (jedisPool == null) {
			return;
		}
		resource.close();
	}

	private void logResourceInfo(Jedis resource) {
		StringBuilder message = new StringBuilder();
		message.append("Redis连接属性，host ip:");
		message.append(resource.getClient().getHost());
		message.append(",port:");
		message.append(resource.getClient().getPort());
		message.append(",DB:");
		message.append(resource.getDB());
		message.append(",ConnectionTimeout:");
        message.append(resource.getClient().getConnectionTimeout());
		message.append(",SoTimeout:");
		message.append(resource.getClient().getSoTimeout());
		logger.debug(message.toString());
	}

	@Override
	public void doInRedis(JedisRunnable runnable) {
		JedisException jedisEx = null;
		Jedis jedis = getJedis(jedisPool);
		try {
			logResourceInfo(jedis);
			runnable.run(jedis);
		} catch (JedisException e) {
			// 处理Jedis异常
			jedisEx = e;
			throw new RedisRuntimeException(e);
		} catch (Exception e) {
			// 处理业务异常
			throw new RedisRuntimeException(e);
		} finally {
			// 只有jedis异常才算jedis连接坏了，其它的业务异常不影响jedis连接的继续使用
			boolean broken = jedisEx != null;
			finishResource(jedisPool, jedis, broken);
		}
	}

	@Override
	public <T> T doInRedis(JedisCallable<T> callable) {
		JedisException jedisEx = null;
		Jedis jedis = getJedis(jedisPool);
		try {
			logResourceInfo(jedis);
			return callable.call(jedis);
		} catch (JedisException e) {
			// 处理Jedis异常
			jedisEx = e;
			throw new RedisRuntimeException(e);
		} catch (Exception e) {
			// 处理业务异常
			throw new RedisRuntimeException(e);
		} finally {
			// 只有jedis异常才算jedis连接坏了，其它的业务异常不影响jedis连接的继续使用
			boolean broken = jedisEx != null;
			finishResource(jedisPool, jedis, broken);
		}
	}

	@Override
	public void doInPipeline(JedisPipelined pipelined) {
		JedisException jedisEx = null;
		Jedis jedis = getJedis(jedisPool);
		try {
			logResourceInfo(jedis);
			Pipeline pipeline = jedis.pipelined();
			pipelined.runInPipeline(pipeline);
		} catch (JedisException e) {
			// 处理Jedis异常
			jedisEx = e;
			throw new RedisRuntimeException(e);
		} catch (Exception e) {
			// 处理业务异常
			throw new RedisRuntimeException(e);
		} finally {
			// 只有jedis异常才算jedis连接坏了，其它的业务异常不影响jedis连接的继续使用
			boolean broken = jedisEx != null;
			finishResource(jedisPool, jedis, broken);
		}
	}
}
