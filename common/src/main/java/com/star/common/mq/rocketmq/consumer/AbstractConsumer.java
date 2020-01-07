package com.star.common.mq.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午3:39:16
* 类说明 :
*/
@Component
public abstract class AbstractConsumer implements IConsumer {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);
	
	@Autowired
	private DefaultMQPushConsumer rocketMQConsumer;

	@Override
	public void subscribeMsg(String topic, String tags) throws MQClientException {
		logger.info("开启订阅topic:{}, tags:{}", topic, tags);
		rocketMQConsumer.subscribe(topic, tags);
		if (!ServiceState.RUNNING.equals(rocketMQConsumer.getDefaultMQPushConsumerImpl().getServiceState())) {
			rocketMQConsumer.start();
		}
        logger.info("rocketmq cousumer启动成功-----------------------------");
	}
	
	@Override
	public void unsubscribeMsg(String topic) throws MQClientException {
		logger.info("取消订阅topic:{}", topic);
		rocketMQConsumer.unsubscribe(topic);
	}
}
