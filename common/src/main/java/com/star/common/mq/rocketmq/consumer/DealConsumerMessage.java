package com.star.common.mq.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shunwang.bs.access.webSocket.Constant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
* @author star
* @version 1.0
  @date 2018年11月7日 下午6:49:48
* 类说明 :
*/
@Service
public class DealConsumerMessage extends MQConsumeMsgListenerProcessor {

	@Override
	protected ConsumeConcurrentlyStatus messageHandle(MessageExt messageExt) {
		//TODO 处理对应的业务逻辑
        /**
         * {
			"code": 0,   // 0：成功，1：失败
			    "result": {  // code为1时，result为空
			        "menus": [{"menuId":123 , "menuName":"menuName"}]
			    },
			"id": "",
			"bsId": "000001",  //bs全局唯一id
			" bcId ": "客户机唯一标识"
			}
         */
        String bodyStr = new String(messageExt.getBody());
        JSONObject jsonObject = JSONObject.parseObject(bodyStr);
        String bsId = jsonObject.getString("bsId");
        // TODO 向netty通道发信息
        ChannelHandlerContext ctx = Constant.getBsCtxMap().get(bsId);
        if(ctx != null){
			TextWebSocketFrame tws = new TextWebSocketFrame(bodyStr);
			ctx.channel().writeAndFlush(tws);
        }
        
        // 如果没有return success ，consumer会重新消费该消息，直到return success
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

	}

}
