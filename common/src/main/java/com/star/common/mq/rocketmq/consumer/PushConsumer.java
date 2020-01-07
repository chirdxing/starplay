package com.star.common.mq.rocketmq.consumer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午3:40:32
* 类说明 :
*/
@Component
public class PushConsumer extends AbstractConsumer {
	private static final Logger logger = LoggerFactory.getLogger(PushConsumer.class);
	
	@Override
	public void subscribeMsg(String topic, String tags) {
		try {
			super.subscribeMsg(topic, tags);
		} catch (MQClientException e) {
			logger.error("订阅消息服务启动失败, topic:{}, tags:{}, e:{}", topic, tags, e);
		}
	}
	
	@Override
	public void unsubscribeMsg(String topic) {
		try {
			super.unsubscribeMsg(topic);
		} catch (MQClientException e) {
			logger.error("取消订阅服务失败，topic:{}, e:{}", topic, e);
		}
	}
}
