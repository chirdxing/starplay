package com.star.common.elasticsearch;

import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.star.common.tools.ReadPropertyUtil;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* @author 作者 : star
* @version 创建时间：2017年3月28日 上午9:45:02
* 类说明 : 连接ES服务器
*/
public class EsClientPool {
	private static Logger logger =LoggerFactory.getLogger(EsClientPool.class);
	public static Timer currTimer = null;
	private static EsClientPool esClient = null;// 连接池
    private static String esClusterName = null;//"swmv-es"; // 集群名elasticsearch
    private static String esServerIps = null;//"10.137.2.15"; // "10.128.16.237, 192.168.42.10, 10.137.2.15"; // 集群服务IP集合
    private static Integer esServerPort = null;//9300; // ES集群端口
    private static Integer totalClient = null;//100;
    // 空闲连接  
    private static List<TransportClient> freeClient = new Vector<TransportClient>();
    // 活动连接
    private static List<TransportClient> activeClient = new Vector<TransportClient>();
    
    private EsClientPool(){
    	// 先建立200个连接放入连接池中
    	init();
    	// 定期检查连接池
    	cheackPool();
    }
    
    private void init() {
    	Properties prop = ReadPropertyUtil.getProperties();
		esClusterName = prop.getProperty("es.cluster.name");
		esServerIps = prop.getProperty("es.server.ip");
		esServerPort = Integer.parseInt(prop.getProperty("es.server.port"));
		totalClient = Integer.parseInt(prop.getProperty("es.client.num"));
    	
    	for(int i = 0; i < totalClient; i++) {
    		TransportClient client = newTransPortClient();
    		if (client != null) {
    			freeClient.add(client);
			}
    	}
    }
    
    public static EsClientPool getInstance() {
    	if (esClient == null) {
    		synchronized(EsClientPool.class){
    			 if (esClient == null) {
    				 esClient = new EsClientPool(); 
    			 }
    		}
		}
    	return esClient;
    }
    
    /**
     * 与ES服务器建立连接
     * @return
     */
    private TransportClient newTransPortClient() {
    	TransportClient transPort = null;
    	try {
			Settings settings = Settings.builder()
			        .put("cluster.name", esClusterName) // 集群名
			        .put("client.transport.sniff", true)  // 自动把集群下的机器添加到列表中
			        .build();
			transPort = new PreBuiltTransportClient(settings);
			if(esServerIps == null || "".equals(esServerIps.trim())) {
			    return  null;
			}
			
			String esIps[] = esServerIps.split(",");
			// 添加集群IP列表
			TransportAddress[] transportAddresses = new TransportAddress[esIps.length];
			for (int i = 0; i< esIps.length; i++) {
                transportAddresses[i] = new InetSocketTransportAddress(InetAddresses.forString(esIps[i]), esServerPort);
            }
		    transPort.addTransportAddresses(transportAddresses);
			
		} catch (Exception e) {
			logger.error("与ES服务器建立连接失败！", e);
		}
    	return transPort;
    }
    
    /**
     * 获取ES服务器建立连接
     * @return
     */
    public synchronized TransportClient getTransPortClient() {
    	TransportClient client = null;
    	int freeClientSize = freeClient.size();
    	if (freeClientSize == 0) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				logger.error("休眠线程出问题了！", e);
			}
		} else {
			client = freeClient.get(0);
			try {
				boolean addFlag = activeClient.add(client);
				if (!addFlag) {
					logger.error("将连接加入活动连接池失败");
					return null;
				}
			} catch (Exception e) {
				logger.error("将连接加入活动连接池失败！", e);
				return null;
			}
			try {
				boolean removeFlag = freeClient.remove(client);
				if (!removeFlag) {
					logger.error("将连接从空闲连接池删除失败");
					activeClient.remove(client);
				}
			} catch (Exception e) {
				logger.error("将连接从空闲连接池删除失败!", e);
				activeClient.remove(client);
			}
		}
    	return client;
    }
    
    /**
     * 释放连接
     * 将该连接从活动连接池中删除，并同时添加到空闲连接池
     * @param client
     */
    public synchronized void releaseClient(TransportClient client) {
    	
    	try {
    		boolean addFlag = freeClient.add(client);
    		if (!addFlag) {
    			logger.error("添加连接到空闲连接池出错！");
    			return;
			}
		} catch (Exception e) {
			logger.error("添加连接到空闲连接池出错！", e);
			return;
		}
    	
    	try {
			boolean removeFlag = activeClient.remove(client);
			if (!removeFlag) {
				logger.error("将该连接从活跃连接池中删除出错！");
				freeClient.remove(client);
			}
		} catch (Exception e) {
			logger.error("将该连接从活跃连接池中删除出错！");
			freeClient.remove(client);
		}
    }
    
    /**
     * 销毁连接池
     */
    public void destoryClients() {
    	for (TransportClient client : activeClient) {
			if (client != null) {
				try {
					client.close();
				} catch (Exception e) {
					logger.error("销毁当前活动连接失败！client={}", client);
				}
			}
		}
    	for (TransportClient client : freeClient) {
    		if (client != null) {
				try {
					client.close();
				} catch (Exception e) {
					logger.error("销毁当前空闲连接失败！client={}", client);
				}
			}
		}
    }
    
    /**
     * 定时检查连接池情况
     */
    private void cheackPool() {
    	currTimer = new Timer();
    	currTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				logger.info("空闲池连接数：freeClient={}, 活动池连接数：activeClient={}"
						, freeClient.size(), activeClient.size());
			}
		}, 0, 5*60*1000);
    }  
}
