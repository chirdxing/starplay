package com.star.common.lock;

import java.util.concurrent.TimeUnit;

import com.star.common.redis.JedisCallable;
import com.star.common.redis.JedisExecutor;
import com.star.common.redis.JedisRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * redis实现的分布式阻塞锁 —— 主要用于解决分布式集群中多线程并发同步问题
 * FIXME 
 * 1、存在问题：多线程并发较高情况下有可能导致有的线程一直获取不到锁直至超时的情况
 * 2、较好的解决方法：基于优先级等待队列，由相关策略算法(等待时间长的先命中策略，高优先级先命中策略等等)保证命中。
 * @date 2020年1月7日
 * @version 1.0
 */
@Component
public class RedisBlockLock {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisBlockLock.class);

	@Autowired
	@Qualifier("coreRedisExecutor")
	private JedisExecutor redisExecutor;
	
	
	/** 分布式阻塞锁key前缀 */
	private static final String REDIS_BLOCK_LOCK_KEY_PREFIX = "redis_block_lock_key_";
	
	/** 分布式阻塞锁key默认过期时间为5秒（如果执行较慢的业务可以设置更长的锁过期时间） */
	private static final int DEFAULT_REDIS_BLOCK_LOCK_KEY_EXPIRE = 5;
	
	
	/**
	 * 在指定时间内尝试获取分布式阻塞锁，未超时情况下一直等待，超时则失败（锁默认的过期时间为5秒）
	 * @param key
	 * @param timeout 指定时间，可以为秒、毫秒等
	 * @param unit 时间单元，秒对应的为TimeUnit.SECONDS
	 * @return
	 */
	public boolean tryLock(String key, long timeout, TimeUnit unit) {
		return tryLock(key, DEFAULT_REDIS_BLOCK_LOCK_KEY_EXPIRE, timeout, unit);
	}
	
	
	/**
	 * 尝试获取分布式阻塞锁，如果获取不到，将一直等待（锁默认的过期时间为5秒）
	 * @param key
	 * @return
	 */
	public boolean tryLock(String key) {
		return tryLock(key, DEFAULT_REDIS_BLOCK_LOCK_KEY_EXPIRE);
	}
	
	
	/**
	 * 在指定时间内尝试获取分布式阻塞锁，未超时情况下一直等待，超时则失败
	 * @param key
	 * @param lockExpire 锁过期时间
	 * @param timeout 指定时间，可以为秒、毫秒等
	 * @param unit 时间单元，秒对应的为TimeUnit.SECONDS
	 * @return
	 */
	public boolean tryLock(final String key, final int lockExpire, final long timeout, final TimeUnit unit) {
		final String lockKey = getLockKey(key);
		
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					long start = System.nanoTime();
					do {
						long count = instance.setnx(lockKey, lockKey);
						if (count == 1) {
							LOGGER.info("[线程:{}]成功获取到分布式阻塞锁lockKey={}，该锁将在expire={}秒内过期", Thread.currentThread().getName(),
									lockKey, lockExpire);
							instance.expire(lockKey, lockExpire);
							return Boolean.TRUE;
						} else {
							LOGGER.info("尝试获取分布式阻塞锁失败，该锁已经被其他线程占用了，进入等待中...");
							// 此处防止网络异常等情况导致设置过期时间失败引起的潜在死锁问题
							if (-1 == instance.ttl(lockKey)) {
								LOGGER.info("因为未知异常[比如网络异常等]导致设置锁的过期时间操作失败，此处检测到后设置一个默认的过期时间，该锁lockKey={}将在expire={}秒内过期", 
										lockKey, lockExpire);
								instance.expire(lockKey, lockExpire);
							}
						}
						Thread.sleep(100);
					} while ((System.nanoTime() - start) < unit.toNanos(timeout));
				} catch (Exception e) {
					LOGGER.error("指定时间内尝试获取分布式阻塞锁lockKey={}失败！", lockKey, e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	
	/**
	 * 尝试获取分布式阻塞锁，如果获取不到，将一直等待
	 * @param key
	 * @param lockExpire 锁过期时间
	 * @return
	 */
	public boolean tryLock(final String key, final int lockExpire) {
		final String lockKey = getLockKey(key);
		
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					do {
						long count = instance.setnx(lockKey, lockKey);
						if (count == 1) {
							LOGGER.info("[线程:{}]成功获取到分布式阻塞锁lockKey={}，该锁将在expire={}秒内过期", Thread.currentThread().getName(), 
									lockKey, lockExpire);
							instance.expire(lockKey, lockExpire);
							return Boolean.TRUE;
						} else {
							LOGGER.info("尝试获取分布式阻塞锁失败，该锁已经被其他线程占用了，进入等待中...");
							// 此处防止网络异常等情况导致设置过期时间失败引起的潜在死锁问题
							if (-1 == instance.ttl(lockKey)) {
								LOGGER.info("因为未知异常[比如网络异常等]导致设置锁的过期时间操作失败，此处检测到后设置一个默认的过期时间，该锁lockKey={}将在expire={}秒内过期", 
										lockKey, lockExpire);
								instance.expire(lockKey, lockExpire);
							}
						}
						Thread.sleep(100);
					} while (true);
				} catch (Exception e) {
					LOGGER.error("尝试获取分布式阻塞锁lockKey={}失败！", lockKey, e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	/**
	 * 释放分布式阻塞锁
	 * @param key
	 */
	public void unLock(final String key) {
		final String lockKey = getLockKey(key);
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.del(lockKey);
					LOGGER.info("[线程:{}]已经成功释放分布式阻塞锁lockKey={}！", Thread.currentThread().getName(), lockKey);
				} catch (Exception e) {
					LOGGER.error("释放分布式阻塞锁lockKey={}失败！", lockKey, e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	private String getLockKey(String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		return REDIS_BLOCK_LOCK_KEY_PREFIX + key;
	}
	
}
