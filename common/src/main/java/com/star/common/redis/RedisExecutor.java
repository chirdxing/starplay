package com.star.common.redis;

/**
 * Redis执行器
 */
public interface RedisExecutor {

	/**
	 * 使用jedis执行redis命令
	 * 
	 * @return
	 *         运行期异常，其中cause有可能是jedis运行期异常，也有可能是业务异常
	 */
	void doInRedis(JedisRunnable runnable);

	/**
	 * 使用jedis执行redis命令
	 *
	 * @return
	 *         运行期异常，其中cause有可能是jedis运行期异常，也有可能是业务异常
	 */
	<T> T doInRedis(JedisCallable<T> callable);

	/**
	 * 使用jedis pipeline机制批量执行redis命令
	 * <p>
	 * 请使用pipeline.sync()方法提交批量执行
	 *
	 * 例子：
	 *
	 * <pre>
	 * executor.doInPipeline(new JedisPipelined() {
	 *
	 *     public void runInPipeline(Pipeline pipeline) throws Exception {
	 *         Response<String> string = pipeline
	 *               .get("huaer_test_common_redis_pipelined_string");
	 *         pipeline.sync();
	 *     }
	 * }
	 * </pre>
	 *
	 * @param pipeline
	 *         运行期异常，其中cause有可能是jedis运行期异常，也有可能是业务异常
	 */
	void doInPipeline(JedisPipelined pipeline);
}
