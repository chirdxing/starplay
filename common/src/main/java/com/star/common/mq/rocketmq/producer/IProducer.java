package com.star.common.mq.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;

/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午2:24:43
* 类说明 :
*/
public interface IProducer {
	
	/**
	 * @param msg 消息主题
	 */
	public SendResult sendMsg(String topic, String tags, String msg) throws MQClientException;

}
