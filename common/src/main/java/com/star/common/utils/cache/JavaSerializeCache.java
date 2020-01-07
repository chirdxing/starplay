package com.star.common.utils.cache;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.star.common.context.SpringContextManager;
import com.star.common.redis.JedisCallable;
import com.star.common.redis.JedisRunnable;
import com.star.common.redis.RedisExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class JavaSerializeCache implements Cache{
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaSerializeCache.class);
	private RedisExecutor redisExecutor;
	private String cacheName;
	
	/**
	 * @param cacheName redis实例名称，关联某个库
	 */
	public JavaSerializeCache(String cacheName) {
		this.cacheName = cacheName;
		redisExecutor = SpringContextManager.getBean(this.cacheName, RedisExecutor.class);
	}
	
	@Override
	public boolean isExists(final String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					return instance.exists(key.getBytes());
				} catch (Exception e) {
					LOGGER.error("exists key={} failed!", key, e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
 	
	@Override
	public <T extends Serializable> void put(final String key, final T value) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		Preconditions.checkNotNull(value, "value can not be null!");
		
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.set(key.getBytes(), JavaSerializeUtil.serialize(value));
				} catch (Exception e) {
					LOGGER.error("put to cache[key={}] failed!", key, e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	@Override
	public <T extends Serializable> void put(final String key, final T value, final int seconds) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		Preconditions.checkNotNull(value, "value can not be null!");
		
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.set(key.getBytes(), JavaSerializeUtil.serialize(value));
					instance.expire(key.getBytes(), seconds);
				} catch (Exception e) {
					LOGGER.error("put to cache[key={}] failed!", key, e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T get(final String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		
		JedisCallable<T> callable = new JedisCallable<T>() {
			@Override
			public T call(Jedis instance) throws Exception {
				try {
					if (!instance.exists(key.getBytes())) {
						return null;
					}
					return (T) JavaSerializeUtil.unSerialize(instance.get(key.getBytes()));
				} catch (Exception e) {
					LOGGER.error("get object by key={} failed!", key, e);
				}
				return null;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	@Override
	public void remove(final String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.del(key.getBytes());
				} catch (Exception e) {
					LOGGER.error("del key={} failed!", key, e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}

	@Override
	public String getCacheName() {
		return this.cacheName;
	}

}
