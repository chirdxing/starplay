package com.star.common.config;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class RocketProducerMQConfigure {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RocketProducerMQConfigure.class);
	 /**
     * 发送同一类消息的设置为同一个group，保证唯一
     */
    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    
    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;
    /**
     * 消息最大大小，默认4M
     */
    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize ;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;
    /**
     * 消息发送失败重试次数，默认2次
     */
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;

    /**
     * 创建普通消息发送者实例
     * 
     * @return
     * @throws MQClientException
     */
    @Bean(name="rocketMQProducer")
    public DefaultMQProducer getRocketMQProducer() throws MQClientException {
        if (StringUtils.isEmpty(this.groupName)) {
            throw new MQClientException("groupName is blank", null);
        }
        if (StringUtils.isEmpty(this.namesrvAddr)) {
        	throw new MQClientException("nameServerAddr is blank", null);
        }
        DefaultMQProducer producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);
        // 如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        // producer.setInstanceName(instanceName);
        producer.setCreateTopicKey("TBW102");
        if(this.maxMessageSize != null){
            producer.setMaxMessageSize(this.maxMessageSize);
        }
        if(this.sendMsgTimeout != null){
            producer.setSendMsgTimeout(this.sendMsgTimeout);
        }
        //如果发送消息失败，设置重试次数，默认为5次
        if(this.retryTimesWhenSendFailed != null){
            producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        }
        
        try {
            producer.start();
            LOGGER.info(String.format("producer is start ! groupName:[%s],namesrvAddr:[%s]"
                    , this.groupName, this.namesrvAddr));
        } catch (MQClientException e) {
            LOGGER.error(String.format("producer is error {}"
                    , e.getMessage(),e));
            throw e;
        }
        return producer;
    }
}