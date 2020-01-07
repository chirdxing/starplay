package com.star.common.config;

import com.star.common.mq.rocketmq.consumer.MQConsumeMsgListenerProcessor;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午3:39:16
* 类说明 :
*/
@Configuration
public class RocketMQConsumerConfigure {
	
	@Value("${rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;
	
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;

    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;
    
    @Autowired
    private MQConsumeMsgListenerProcessor mqConsumeMsgListenerProcessor;

	@Bean("rocketMQConsumer")
	public DefaultMQPushConsumer getDefaultMQPushConsumer() throws MQClientException {
		
		if (StringUtils.isEmpty(groupName)){
            throw new MQClientException("groupName is null !!!", null);
        }
        if (StringUtils.isEmpty(namesrvAddr)){
            throw new MQClientException("namesrvAddr is null !!!", null);
        }
        
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        /**
         * 默认的是使用集群消费模式，这两者最大的区别在于同组中的消费，集群消费模式是同组公同消费一组消息，广播模式是同组各自都消费一组消息。
         */
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.registerMessageListener(mqConsumeMsgListenerProcessor);
        return consumer;
	}
}
