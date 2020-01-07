package com.star.common.test;

import com.star.common.context.SpringContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * 消费redis消息的线程
 * @date 2020年1月7日
 * @version 1.0
 */
public class ConsumeMsgThread implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeMsgThread.class); 

	private static RedisQueueMsg queue;
	static {
		queue = SpringContextManager.getBean(RedisQueueMsg.class);
	}
	
	@Override
	public void run() {
		System.out.println(this.getClass().getSimpleName() + " thread is start");
		int popCount = 0;
		while (true) {
			String msg = null;
			try {
				msg = queue.msgLeftQueue();
				if (Strings.isNullOrEmpty(msg)) {
					System.out.println("no msg, sleep 5 sends");
					Thread.sleep(5000);
				} else {
					popCount++;
					System.out.println("do something with msg: " + msg);
					if (popCount % 100 == 0) {
						Thread.sleep(1000);
					}
				}
				System.out.println("hhhhhhhhhhhhhhhhhhh");
			} catch (Exception e) {
				System.out.println("consume msg failed!");
				LOGGER.error("consume msg={} failed!", msg, e);
			}
		}
	}

}
