package com.star.common.mq.rocketmq.consumer;

import java.util.List;

import com.star.common.redis.JedisCallable;
import com.star.common.redis.JedisExecutor;
import com.star.common.redis.JedisRunnable;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

@Component
public abstract class MQConsumeMsgListenerProcessor implements MessageListenerConcurrently{
    private static final Logger logger = LoggerFactory.getLogger(MQConsumeMsgListenerProcessor.class);

    @Autowired
    @Qualifier("coreRedisExecutor")
    private JedisExecutor redisExecutor;
    /**
     *  默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息<br/>
     *  不要抛异常，如果没有return CONSUME_SUCCESS ，consumer会重新消费该消息，直到return CONSUME_SUCCESS
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
    	if(CollectionUtils.isEmpty(msgs)) {
            logger.info("接受到的消息为空，不处理，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = msgs.get(0);
        logger.info("接受到的消息为：" + messageExt.toString());
        // 判断该消息是否重复消费（RocketMQ不保证消息不重复，如果你的业务需要保证严格的不重复消息，需要你自己在业务端去重）
        Boolean result = redisExecutor.doInRedis(new JedisCallable<Boolean>(){
            @Override
            public Boolean call(Jedis instance) throws Exception {
                return instance.exists(messageExt.getMsgId());
            }
        });

        if (result) {
        	return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} else {
            redisExecutor.doInRedis(new JedisRunnable() {
                @Override
                public void run(Jedis instance) throws Exception {
                    instance.set(messageExt.getMsgId(), messageExt.getMsgId());
                    instance.expire(messageExt.getMsgId(), 1 * 60 * 60);
                }
            });
		}
        //TODO 获取该消息重试次数
        int reconsume = messageExt.getReconsumeTimes();
        if(reconsume == 3){ //消息已经重试了3次，如果不需要再次消费，则返回成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        if (messageExt.getBody() == null || messageExt.getBody().length == 0){
        	return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
        return messageHandle(messageExt);
    }
    
    protected abstract ConsumeConcurrentlyStatus messageHandle(MessageExt messageExt);
}