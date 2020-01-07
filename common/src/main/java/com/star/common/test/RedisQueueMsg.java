package com.star.common.test;

import com.star.common.redis.JedisCallable;
import com.star.common.redis.JedisRunnable;
import com.star.common.redis.RedisExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

/**
 * 模拟redis消息队列
 * @date 2020年1月7日
 * @version 1.0
 */
@Component
public class RedisQueueMsg {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisQueueMsg.class);

	@Autowired
    @Qualifier("coreRedisExecutor")
    private RedisExecutor redisExecutor;
	
	private static final String MESSAGE_QUEUE_KEY = "message_queue_key";
	
	public void msgJoinQueue(final String message) {
		JedisRunnable runnable = new JedisRunnable() {
			@Override
			public void run(Jedis instance) throws Exception {
				try {
					instance.rpush(MESSAGE_QUEUE_KEY, message);
				} catch (Exception e) {
					LOGGER.error("message join in queue failed!", e);
				}
			}
		};
		redisExecutor.doInRedis(runnable);
	}
	
	public String msgLeftQueue() {
		JedisCallable<String> callable = new JedisCallable<String>() {
			@Override
			public String call(Jedis instance) throws Exception {
				try {
					return instance.lpop(MESSAGE_QUEUE_KEY);
				} catch (Exception e) {
					LOGGER.error("message left queue failed!", e);
				}
				return null;
			}
		};
		return redisExecutor.doInRedis(callable);
	}
	
}
