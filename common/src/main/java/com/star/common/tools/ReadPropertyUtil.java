package com.star.common.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author 作者 : star
* @version 创建时间：2017年4月21日 上午11:06:47
* 类说明 :
*/
public class ReadPropertyUtil {
	private static Logger logger =LoggerFactory.getLogger(ReadPropertyUtil.class);
	public static Properties prop = null;
	
	/**
	 * 获取配置文件
	 * @return
	 */
	public static Properties getProperties() {
		InputStream in = null;
		try {
			if (prop == null) {
				prop = new Properties();
				//in = EsClientPool.class.getClassLoader().getResourceAsStream("DBConfig.properties");
				in = new BufferedInputStream (new FileInputStream(System.getProperty("ES_CONFIG_HOME") + File.separator + "DBConfig.properties"));
				prop.load(in); ///加载属性列表
			}
		} catch (Exception e) {
			logger.error("读取ES配置出错！", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("关闭读取文件流出错！", e);
				}
			}
		}
		return prop;
	}

}
