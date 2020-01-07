package com.star.common.mq.rocketmq.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午2:38:32
* 类说明 :
*/
@Component
public abstract class AbstractProduct implements IProducer {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractProduct.class);

	@Autowired
	private DefaultMQProducer rocketMQProducer;

	protected Map<String, String> analysisMsg(String msg) {
		Map<String, String> res = new HashMap<String, String>();
		JSONObject msgObj = JSONObject.parseObject(msg);
		
		try {
			String topic = msgObj.getJSONObject("params").getString("buzId");
			String tags = msgObj.getString("method");
			res.put("msgGroup", topic);
			res.put("topic", topic);
			res.put("tags", tags);
		} catch (Exception e) {
			logger.error("解析msg={}出现异常", msg);
			return null;
		}
		return res;
	}

	@Override
	public SendResult sendMsg(String topic, String tags, String msg) throws MQClientException{
		
        try {
            Message perMsg = new Message(topic,
            		tags, msg.getBytes(RemotingHelper.DEFAULT_CHARSET));
            return rocketMQProducer.send(perMsg);
        } catch (Throwable e) {
        	logger.error("消息发送失败, e={}", e);
        }
        return null;
	}

}
