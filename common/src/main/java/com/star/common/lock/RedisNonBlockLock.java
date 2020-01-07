package com.star.common.lock;

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
 * 基于Redis实现的非阻塞分布式锁 —— 主要用于解决多线程并发下重复生成数据的问题
 * @date 2020年1月7日
 * @version 1.0
 */
@Component
public class RedisNonBlockLock {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisNonBlockLock.class);

	/** 防止重复就生成的分布式锁key */
	private static final String REDIS_NONBLOCK_LOCK_KEY_PREFIX = "redis_nonblock_lock_key_";
	
	/** 防止重复就生成的分布式锁key过期时间，默认为5秒 */
	private static final int DEFAULT_REDIS_NONBLOCK_LOCK_KEY_EXPIRE = 5;
	
	
	@Autowired
	@Qualifier("coreRedisExecutor")
	private JedisExecutor redisExecutor;
	
	
	/**
	 * 尝试获得防止重复生成记录操作分布式锁，获取成功后才锁过期之前其他生成操作都是非法不可执行的
	 * @param key 根据记录数据计算出来的MD5值
	 * @return
	 */
	public boolean tryLock(final String key) {
		return tryLock(key, DEFAULT_REDIS_NONBLOCK_LOCK_KEY_EXPIRE);
	}
	
	/**
	 * 尝试获得防止重复生成记录操作分布式锁，获取成功后才锁过期之前其他生成操作都是非法不可执行的
	 * @param key 根据记录数据计算出来的MD5值
	 * @param lockExpire 过期时间
	 * @return
	 */
	public boolean tryLock(final String key, final int lockExpire) {
		final String lockKey = getLockKey(key);
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {

			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					Long count = instance.setnx(lockKey, lockKey);
					
					if (count == 1) {
						LOGGER.info("[线程:{}]成功获得防止重复生成记录操作分布式锁，为合法操作，该锁lockKey={}将在lockExpire={}秒内过期！", 
								Thread.currentThread().getName(), lockKey, lockExpire);
						instance.expire(lockKey, lockExpire);
						return Boolean.TRUE;
					} else {
						LOGGER.info("防止重复生成记录操作锁lockKey={}已经被占用，本次操作将放弃，防止重复生成记录", lockKey);
						if (-1 == instance.ttl(lockKey)) {
							LOGGER.info("因为未知异常[比如网络异常等]导致设置锁的过期时间操作失败，此处检测到后设置一个默认的过期时间，该锁lockKey={}将在expire={}秒内过期", 
									lockKey, lockExpire);
							instance.expire(lockKey, lockExpire);
						}
						return Boolean.FALSE;
					}
				} catch (Exception e) {
					LOGGER.error("尝试加分布式锁lockKey={}防止重复数据生成操作失败！", lockKey, e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	/**
	 * 释放防止重复数据生成锁
	 * @param key
	 */
	public void unLock(final String key) {
		final String lockKey = getLockKey(key);
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.del(lockKey);
					LOGGER.info("[线程:{}]已经成功释放防止重复数据锁lockKey={}！", Thread.currentThread().getName(), lockKey);
				} catch (Exception e) {
					LOGGER.error("释放防止重复数据生成锁lockKey={}失败！", lockKey, e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	
	private String getLockKey(String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		return REDIS_NONBLOCK_LOCK_KEY_PREFIX + key;
	}
	
}
