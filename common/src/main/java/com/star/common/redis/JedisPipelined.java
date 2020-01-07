package com.star.common.redis;

import redis.clients.jedis.Pipeline;

/**
 * 使用jedis的pipleline执行redis命令
 * <p>
 * 例子：
 * <pre>
 * executor.doInPipeline(new JedisPipelined() {
 *
 *     public void runInPipeline(Pipeline pipeline) throws Exception {
 *         Response<String> string = pipeline
 *               .get("huaer_test_common_redis_pipelined_string");
 *         pipeline.sync();
 *     }
 * }
 */
public interface JedisPipelined {

	/**
	 * 使用jedis pipeline批量执行一组redis命令
	 * <p>
	 * 注意：需要自己调用pipeline.sync()方法提交命令
	 * 
	 * @param pipeline
	 * @throws Exception
	 *             业务异常可以自行处理
	 */
	void runInPipeline(Pipeline pipeline) throws Exception;
}
