package com.star.common.mq.rocketmq.consumer;

import org.apache.rocketmq.client.exception.MQClientException;

/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午3:36:29
* 类说明 :
*/
public interface IConsumer {
	
	/**
	 * 订阅消息
	 * @param topic 主题
	 * @param tags 标签（*号表示所有标签）
	 * @throws MQClientException
	 */
	public void subscribeMsg(String topic, String tags)throws MQClientException;
	
	/**
	 * 取消订阅
	 * @param topic
	 * @throws MQClientException
	 */
	public void unsubscribeMsg(String topic)throws MQClientException;

}
