package com.star.common.lock;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.star.common.redis.JedisCallable;
import com.star.common.redis.JedisExecutor;
import com.star.common.redis.JedisRunnable;
import com.star.common.tools.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 基于redis实现的分布式公平阻塞队列排它锁
 * 使用须知：
 * 1、自定义线程需要给线程一个通用唯一标识符作为线程名Thread.currentThread().getName()，貌似不用
 * 2、当线程尝试获取锁时该线程进入优先级队列（redis有序集合实现），线程在队列中的唯一标记为线程名称，在队列中的位置由进入队列时分配到的id（redis的incr自增分配）决定
 * 3、当锁被释放时，队列头的线程优先获得锁并退出队列
 * 4、锁需要设置过期时间，防止一直被占用，造成大量线程等待；
 * 5、自检测机制会检测锁是否正确设置过期时间（ttl命令），防止因为网络异常导致没设置过期时间造成死锁
 * 6、当设置线程在指定时间内获取锁，获取失败则放弃同时退出队列
 * 7、当队列头线程长期处于等待状态，那么很有可能是线程获得锁后退出队列操作失败（网络异常等）引起的。所以自检测机制检测到超过默认等待时间则干掉队列头线程，防止造成死锁。
 * 8、获得锁的线程执行完业务代码要调用unLock释放锁，不然性能会大幅度下降
 * 【自检测机制主要是为了解决因为特殊情况产生的潜在死锁问题】
 * @date 2020年1月7日
 * @version 2.0
 */
@Component
public class RedisBlockQueueLock {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisBlockQueueLock.class);

	@Autowired
	@Qualifier("coreRedisExecutor")
	private JedisExecutor redisExecutor;
	
	/** 排它锁key  */
	private static final String REDIS_BLOCK_QUEUE_LOCK_KEY_PREFIX = "redis_block_queue_lock_key_";
	/** 优先级队列key */
	private static final String REDIS_PRIORITY_QUEUE_KEY_PREFIX = "redis_lock_priority_queue_key_";
	/** 优先级队列成员排序自增器key  */
	private static final String REDIS_PRIORITY_QUEUE_ITEM_INCREMENT_KEY_PREFIX = "redis_lock_priority_queue_item_increment_key_";
	/** 优先级队列头部元素防死锁的key */
	private static final String REDIS_PRIORITY_QUEUE_TOP_ITEM_KEY_PREFIX = "redis_priority_queue_top_item_key_";
	/** 排它锁默认过期时间 */
	private static final int DEFAULT_REDIS_BLOCK_QUEUE_LOCK_KEY_EXPIRE = 5;
	/** 优先级队列对头元素默认等待时间，超时则自动退出队列 */
	private static final int DEFUALT_PRIORITY_QUEUE_TOP_MEMBER_WAITING_TIME = 10;
	

	public boolean tryLock(String key, long timeout, TimeUnit unit) {
		return tryLock(key, DEFAULT_REDIS_BLOCK_QUEUE_LOCK_KEY_EXPIRE, timeout, unit);
	}
	
	public boolean tryLock(String key) {
		return tryLock(key, DEFAULT_REDIS_BLOCK_QUEUE_LOCK_KEY_EXPIRE);
	}
	
	
	public boolean tryLock(final String key, final int lockExpire, final long timeout, final TimeUnit unit) {
		final String lockKey = getLockKey(key);
		
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
//					Thread.currentThread().setName("");
					String threadFlg = Thread.currentThread().getName();
					joinQueue(key, threadFlg);
					
					long start = System.nanoTime();
					do {
						// 自检测机制：队列头线程超过默认等待时间直接退队，防止因为线程获得锁却退队失败导致的死锁问题
						autoLeftQueueIfNeed(key);
						
						if (isPriorityThread(key, threadFlg)) {
							long count = instance.setnx(lockKey, lockKey);
							if (count == 1) {
								instance.expire(lockKey, lockExpire);
								
								// 有三次的尝试机会，确保队列头元素获得锁后离开优先级队列
								boolean leftResult = leftQueue(key);
								if (!leftResult) {
									leftResult = leftQueue(key);
									if (!leftResult) {
										leftQueue(key);
									}
								}
								return Boolean.TRUE;
							} else {
								// 自检测机制：防止网络异常等情况造成锁的过期时间设置失败从而导致死锁问题
								if (-1 == instance.ttl(lockKey)) {
									instance.expire(lockKey, lockExpire);
								}
							}
						}
						Thread.sleep(100);
					} while ((System.nanoTime() - start) < unit.toNanos(timeout));
					
					// 线程超时放弃获取锁也要从队列退出
					leftQueue(key, threadFlg);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	public boolean tryLock(final String key, final int lockExpire) {
		final String lockKey = getLockKey(key);
		
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					String threadFlg = Thread.currentThread().getName();
					joinQueue(key, threadFlg);
					
					do {
						// 自检测机制：队列头线程超过默认等待时间直接退队，防止因为线程获得锁却退队失败导致的死锁问题
						autoLeftQueueIfNeed(key);
						
						if (isPriorityThread(key, threadFlg)) {
							long count = instance.setnx(lockKey, lockKey);
							if (count == 1) {
								instance.expire(lockKey, lockExpire);
								
								// 有三次的尝试机会，确保队列头元素获得锁后离开优先级队列
								boolean leftResult = leftQueue(key);
								if (!leftResult) {
									leftResult = leftQueue(key);
									if (!leftResult) {
										leftQueue(key);
									}
								}
								return Boolean.TRUE;
							} else {
								// 自检测机制：防止网络异常等情况造成锁的过期时间设置失败从而导致死锁问题
								if (-1 == instance.ttl(lockKey)) {
									instance.expire(lockKey, lockExpire);
								}
							}
						}
						Thread.sleep(100);
					} while (true);
				} catch (Exception e) {
					
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	public void unLock(final String key) {
		final String lockKey = getLockKey(key);
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.del(lockKey);
				} catch (Exception e) {
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	/**
	 * 判断一个线程是否在队列的头部（需要调用此方法的线程一定在队列中）
	 * @param key
	 * @param threadFlg 线程的标记
	 * @return
	 */
	private boolean isPriorityThread(final String key, final String threadFlg) {
		final String queueKey = getQueueKey(key);
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				// 取出有序集合第一个元素
				Set<String> set = instance.zrange(queueKey, 0, 0);
				if (ObjectUtils.isSetNotNull(set)) {
					// 理论上只要本方法被调用就说明还有线程在队列中尚未获得锁
					for (String item : set) {
						return threadFlg.equals(item);
					}
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	/**
	 * 线程进入优先级队列，已经进入优先级队列的则忽略操作
	 * @param key
	 * @param threadFlg 线程的标记
	 * @return
	 */
	private boolean joinQueue(final String key, final String threadFlg) {
		final String queueKey = getQueueKey(key);
		final String queueItemIncreKey = getQueueItemIncreKey(queueKey);
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					Long rank = instance.zrank(queueKey, threadFlg);
					if (null == rank) {
						// 线程尚未进入优先级队列
						Long score = instance.incr(queueItemIncreKey);
						// 线程进入优先级队列，分数值由自增id生成，越往后进入队列的线程score越高
						instance.zadd(queueKey, score, threadFlg);
					}
					return Boolean.TRUE;
				} catch (Exception e) {
					LOGGER.error("", e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	/**
	 * 指定线程从优先级队列离开
	 * @param key
	 * @return
	 */
	private boolean leftQueue(final String key, final String threadFlg) {
		final String queueKey = getQueueKey(key);
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					Long count = instance.zrem(queueKey, threadFlg);
					if (count == 1) {
						return Boolean.TRUE;
					} else {
						// 线程离开队列失败，有可能是已经离开了
					}
					return Boolean.FALSE;
				} catch (Exception e) {
					LOGGER.error("", e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	/**
	 * 队列头线程从优先级队列离开
	 * @param key
	 * @return
	 */
	private boolean leftQueue(final String key) {
		final String queueKey = getQueueKey(key);
		JedisCallable<Boolean> callable = new JedisCallable<Boolean>() {
			@Override
			public Boolean call(Jedis instance) throws Exception {
				try {
					Long count = instance.zremrangeByRank(queueKey, 0, 0);
					if (count == 1) {
						return Boolean.TRUE;
					} else {
						// 线程离开队列失败
					}
					return Boolean.FALSE;
				} catch (Exception e) {
					LOGGER.error("", e);
				}
				return Boolean.FALSE;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
	/**
	 * 队列头线程超过默认等待时间则做自动退队处理
	 * （两种情况：第一种是线程确实已经离开了，第二种没有离开但超时了，第一种情形此操作不影响，第二种情形会导致该线程一直等不到锁直到线程超时。
	 * 可以接受这样的结果，好处就是不会产生死锁问题）
	 * @param key
	 */
	private void autoLeftQueueIfNeed(final String key) {
		final String queueKey = getQueueKey(key);
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					Set<String> set = instance.zrange(queueKey, 0, 0);
					if (ObjectUtils.isSetNotNull(set)) {
						for (String item : set) {
							// 针对每一个头部线程设置的过期标记key（每一个头部线程独立的标记key不会对新的头部线程造成影响，过期时间的设定保证不会有数据的残留）
							String queueTopItemKey = getQueueTopItemKey(key + item);
							Long count = instance.setnx(queueTopItemKey, String.valueOf(System.nanoTime()));
							if (count == 1) {
								instance.expire(queueTopItemKey, DEFUALT_PRIORITY_QUEUE_TOP_MEMBER_WAITING_TIME 
										+ DEFAULT_REDIS_BLOCK_QUEUE_LOCK_KEY_EXPIRE);
							} else {
								String nanoTimeStr = instance.get(queueTopItemKey);
								if (!Strings.isNullOrEmpty(nanoTimeStr)) {
									long nanoTime = Long.parseLong(nanoTimeStr);
									if (System.nanoTime() - nanoTime > TimeUnit.SECONDS.toNanos(DEFUALT_PRIORITY_QUEUE_TOP_MEMBER_WAITING_TIME)) {
										// 头部线程等待时间过长，已经超过锁的自动释放时间，头部线程需要退出队列
										leftQueue(key);
										// 删除过期标记
										instance.del(queueTopItemKey);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	private String getQueueItemIncreKey(String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		return REDIS_PRIORITY_QUEUE_ITEM_INCREMENT_KEY_PREFIX + key;
	}
	
	private String getQueueKey(String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		return REDIS_PRIORITY_QUEUE_KEY_PREFIX + key;
	}
	
	private String getLockKey(String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can not be null!");
		return REDIS_BLOCK_QUEUE_LOCK_KEY_PREFIX + key;
	}
	
	private String getQueueTopItemKey(String key) {
		return REDIS_PRIORITY_QUEUE_TOP_ITEM_KEY_PREFIX + "";
	}
	
}
