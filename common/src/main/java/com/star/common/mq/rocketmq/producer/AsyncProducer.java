package com.star.common.mq.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
* @author star
* @version 1.0
  @date 2018年11月5日 下午3:17:17
* 类说明 :异步发送消息
*/
@Component
public class AsyncProducer extends AbstractProduct {
	
	private static Logger logger = LoggerFactory.getLogger(AsyncProducer.class);
	
	/**
	 * 网吧请求类型的业务消息
	 * {
		"method": "getMenuLists",   //每个业务请需要该接口
		"bsId": "",  //bs全局唯一id
		"bcId": "",  //bc全局唯一id
		"buzId": "buzIdxxxxx",   //业务id
		"params": { //业务参数
			    	   "parentId": "0"
		    },
		"id": ""
		}
	 * @param msg
	 */
	public SendResult sendBusinessMsg(String msg) {
		try {
			JSONObject msgObj = JSONObject.parseObject(msg);
			String topic = msgObj.getString("buzId");
			String tags = msgObj.getString("method");
			return super.sendMsg(topic, tags, msg);
		} catch (Throwable e) {
			logger.error("发送网吧请求类型的业务消息msg={}出现异常", msg);
		}
		return null;
	}
}
