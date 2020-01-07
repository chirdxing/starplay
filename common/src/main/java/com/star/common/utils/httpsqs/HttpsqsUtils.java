package com.star.common.utils.httpsqs;


import com.star.common.config.PlatformConfig;
import com.star.common.context.SpringContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消息队列的帮助类，通过这个类可以获取Httpsqs客户端以及向服务器发送消息
 */
public class HttpsqsUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpsqsUtils.class);
	
	private static HttpsqsClient client = null;
	private static PlatformConfig platformConfig;
	static {
		try {
			platformConfig = SpringContextManager.getBean(PlatformConfig.class);
			try {
				//初始化Httpsqs客户端信息
				logger.info("初始化Httpsqs客户端,ip:{}, port:{}", platformConfig.HTTPSQS_SERVER_IP, 
						platformConfig.HTTPSQS_SERVER_PORT);
				Httpsqs4j.setConnectionInfo(platformConfig.HTTPSQS_SERVER_IP,
						Integer.parseInt(platformConfig.HTTPSQS_SERVER_PORT),
						platformConfig.HTTPSQS_CHARSET);
				client = Httpsqs4j.createNewClient();
				logger.info("初始化Httpsqs客户端信息成功");
			} catch (HttpsqsException e) {
				logger.error("连接消息队列服务器失败，具体原因:", e);
				throw new RuntimeException("连接消息队列服务器失败", e);
			}
		} catch (Throwable e) {
			logger.error("httpsqs init failed!", e);
		}
	}

	/**
	 * 获取Httpsqs客户端
	 * 
	 * @return
	 */
	public static HttpsqsClient getHttpsqsClient() {
		return client;
	}

	/**
	 * 向消息队列放入消息
	 * 
	 * @param message
	 */
	public static void putMessageToQueue(String message) {
		try {
			logger.info("向主线队列写入了消息:"+message);
			client.putString("main_queue", message);
		} catch (HttpsqsException e) {
			logger.error("消息写入主线队列发生错误:", e);
			throw new RuntimeException("消息放入主线队列发生错误", e);
		}
	}
	
	public static void main(String[] args){
		putMessageToQueue("[activity]::2::2013-01-27 12:08:00::delete");
	}

}
