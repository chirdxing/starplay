package com.star.common.utils.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http请求工具类，可提供基于http和https通道的get、post请求
 * @date 2020年1月7日
 * @version 1.0
 */
public class HttpUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
     * 发送get请求，如果返回的HttpResult中的response为空表示请求失败
     * @param url 请求链接
     * @param params 请求参数键值对
     * @param charset 字符集
     * @return
     */
    public static HttpResult get(String url, Map<String, String> params, String charset) {
    	return get(false, url, null, params, charset);
    }
    
    /**
     * 发送post请求，如果返回的HttpResult中的response为空表示请求失败
     * 参数形式为：UrlEncodedFormEntity
     * @param url 请求链接
     * @param params 请求参数键值对
     * @param charset 字符集
     * @return
     */
    public static HttpResult post(String url, Map<String, String> params, String charset) {
    	return post(false, url, null, params, charset);
    }
    
    /**
     * 发送post请求，如果返回的HttpResult中的response为空表示请求失败
     * 参数形式为：JSON字符串
     * @param url 请求链接
     * @param params 请求参数键值对
     * @param charset 字符集
     * @return
     */
    public static HttpResult postWithJSON(String url, Map<String, String> params, String charset) {
    	return postWithJSON(false, url, null, params, charset);
    }
    
    /**
     * 发送post请求，如果返回的HttpResult中的response为空表示请求失败
     * @param url 请求链接
     * @param paramsStr 请求参数字符串
     * @param charset 字符集
     * @return
     */
    public static HttpResult postWithString(String url, String paramsStr, String charset) {
    	return postWithString(false, url, null, paramsStr, charset);
    }
    
    /**
     * 发送带文件的post请求，如果返回的HttpResult中的response为空表示请求失败
     * @param url 请求链接
     * @param params 请求参数Map
     * @param fileLists 文件列表
     * @param charset 字符集
     * @return
     */
    public static HttpResult postWithFiles(String url, Map<String, String> params, List<File> fileLists, 
    		String charset) {
    	return postWithFiles(false, url, null, params, fileLists, charset);
    }

    /**
     * 发送get请求，如果返回的HttpResult中的response为空表示请求失败
     * @param isHttps 是否开启https通道
     * @param url 请求链接
     * @param params 请求参数键值对
     * @param charset 字符集
     * @return
     */
    public static HttpResult get(boolean isHttps, String url, Map<String, String> headers, 
    		Map<String, String> params, String charset) {
    	try {
			HttpClient client = getHttpClient(isHttps);
			HttpGet httpGet = new HttpGet(buildGetUrl(url, params));
			if (null != headers) {
				httpGet.setHeaders(assemblyHeader(headers));
			}
			HttpResponse response = client.execute(httpGet);
			
			HttpResult result = new HttpResult();
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setHeaders(response.getAllHeaders());
			result.setResponse(EntityUtils.toString(response.getEntity(), charset));
			return result;
		} catch (Exception e) {
			LOGGER.error("发送http请求失败！", e);
			return new HttpResult();
		}
    }
    
    /**
     * 发送post请求，如果返回的HttpResult中的response为空表示请求失败
     * 参数形式为：UrlEncodedFormEntity
     * @param isHttps 是否开启https通道
     * @param url 请求链接
     * @param params 请求参数键值对
     * @param charset 字符集
     * @return
     */
    public static HttpResult post(boolean isHttps, String url, Map<String, String> headers,
    		Map<String, String> params, String charset) {
    	try {
			HttpClient client = getHttpClient(isHttps);
			
			HttpPost httpPost = new HttpPost(url);
			if (null != headers) {
				httpPost.setHeaders(assemblyHeader(headers));
			}
			setEntity(httpPost, params);
			HttpResponse response = client.execute(httpPost);
					
			HttpResult result = new HttpResult();
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setHeaders(response.getAllHeaders());
			result.setResponse(EntityUtils.toString(response.getEntity(), charset));
			return result;
		} catch (Exception e) {
			LOGGER.error("发送http请求失败！", e);
			return new HttpResult();
		}
    }
    
    /**
     * 发送post请求，如果返回的HttpResult中的response为空表示请求失败
     * 参数形式为：JSON字符串
     * @param isHttps 是否开启https通道
     * @param url 请求链接
     * @param params 请求参数键值对
     * @param charset 字符集
     * @return
     */
    public static HttpResult postWithJSON(boolean isHttps, String url, Map<String, String> headers,
    		Map<String, String> params, String charset) {
    	try {
			HttpClient client = getHttpClient(isHttps);
			
			HttpPost httpPost = new HttpPost(url);
			if (null != headers) {
				httpPost.setHeaders(assemblyHeader(headers));
			}
			StringEntity str = new StringEntity(mapToJSON(params));
			str.setContentEncoding(charset);
			str.setContentType("application/json");
			httpPost.setEntity(str);
			
			HttpResponse response = client.execute(httpPost);
					
			HttpResult result = new HttpResult();
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setHeaders(response.getAllHeaders());
			result.setResponse(EntityUtils.toString(response.getEntity(), charset));
			return result;
		} catch (Exception e) {
			LOGGER.error("发送http请求失败！", e);
			return new HttpResult();
		}
    }
    
    
    /**
     * 发送post请求，如果返回的HttpResult中的response为空表示请求失败
     * 参数形式为：字符串，是这个StringEntity
     * @param isHttps 是否开启https通道
     * @param url 请求链接
     * @param headers 请求头
     * @param paramStr 请求参数字符串
     * @param charset 字符集
     * @return
     */
    public static HttpResult postWithString(boolean isHttps, String url, Map<String, String> headers,
    		String paramStr, String charset) {
    	try {
			HttpClient client = getHttpClient(isHttps);
			
			HttpPost httpPost = new HttpPost(url);
			if (null != headers) {
				httpPost.setHeaders(assemblyHeader(headers));
			}
			StringEntity str = new StringEntity(paramStr, charset);
			httpPost.setEntity(str);
			
			HttpResponse response = client.execute(httpPost);
					
			HttpResult result = new HttpResult();
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setHeaders(response.getAllHeaders());
			result.setResponse(EntityUtils.toString(response.getEntity(), charset));
			return result;
		} catch (Exception e) {
			LOGGER.error("发送http请求失败！", e);
			return new HttpResult();
		}
    }
    
    /**
     * 发送带文件的post请求
     * @param isHttps true启动https
     * @param url
     * @param params 请求参数列表
     * @param fileLists 文件列表
     * @param charset 字符集
     * @return
     */
	public static HttpResult postWithFiles(boolean isHttps, String url, Map<String, String> headers,
			Map<String, String> params, List<File> fileLists, String charset) {
		try {
			HttpClient client = getHttpClient(isHttps);
			
			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
			if (null == params) {
				params = new HashMap<String, String>();
			}
			for (String key : params.keySet()) {
				meBuilder.addPart(key, new StringBody(params.get(key), ContentType.TEXT_PLAIN));
			}
			for (File file : fileLists) {
				FileBody fileBody = new FileBody(file);
				meBuilder.addPart("files", fileBody);
			}
			HttpEntity reqEntity = meBuilder.build();
			httpPost.setEntity(reqEntity);
			
			HttpResponse response = client.execute(httpPost);
			
			HttpResult result = new HttpResult();
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setHeaders(response.getAllHeaders());
			result.setResponse(EntityUtils.toString(response.getEntity(), charset));
			return result;
		} catch (Exception e) {
			LOGGER.error("发送http请求失败！", e);
			return new HttpResult();
		}
	}
    
    /**
     * 按照参数key字典顺序排序参数，拼成key=value&key2=value2的形式，需要“?”的自己加
     * @param params 参数集
     * @return
     */
    public static String orderByAscii(Map<String, String> params) {
        Set<String> paramKeys = new TreeSet<String>();
        for (Entry<String, String> param : params.entrySet()) {
            String paramValue = param.getValue();
            if (paramValue != null && !"".equals(paramValue)) {
                paramKeys.add(param.getKey());
            }
        }
        StringBuilder stringA = new StringBuilder(1024);
        boolean first = true;
        for (String paramKey : paramKeys) {
            if (!first) {
                stringA.append("&");
            }
            stringA.append(paramKey);
            stringA.append("=");
            stringA.append(params.get(paramKey));
            first = false;
        }
        return stringA.toString();
    }
    
    /*
     * 将Map转化为JSON字符串（仅支持一级JSON）
     * @param params
     * @return
     */
    private static String mapToJSON(Map<String, String> params) {
    	JSONObject paramJson = new JSONObject();
		for (String param : params.keySet()) {
			paramJson.put(param, params.get(param));
		}
		return paramJson.toString();
    }
    
    /*
     * 请求带参数的话则拼接成请求链接，否则不处理
     * @param url
     * @param params
     * @return
     */
    private static String buildGetUrl(String url, Map<String, String> params) {
    	if (null != params) {
    		StringBuilder sb = new StringBuilder(256);
    		sb.append(url);
    		sb.append("?");
    		sb.append(orderByAscii(params));
    		return sb.toString();
    	}
    	return url;
    }
    
    /*
     * 封装请求参数到HttpEntity中
     * @param post
     * @param pair
     */
    private static void setEntity(HttpPost post , Map<String,String> pair) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<Entry<String,String>> entry = pair.entrySet();
		for(Iterator<Entry<String,String>> it=entry.iterator(); it.hasNext(); ){
			Entry<String,String> next = it.next();
			nvps.add(new BasicNameValuePair(next.getKey(), next.getValue())); 
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			//编码一般是支持的，所以不处理此异常
		}
	}
    
    /*
     * 组装http头部
     * @param headers
     * @return
     */
 	private static Header[] assemblyHeader(Map<String, String> headers) {
 		Header[] allHeader= new BasicHeader[headers.size()];
 		int i = 0;
 		for (String str : headers.keySet()) {
 			allHeader[i] = new BasicHeader(str,headers.get(str));
 			i++;
 		}
 		return allHeader;
 	}
    
    
    /*
     * 获取httpClient实例，根据isHttps标记判断是否返回https通道实例
     * 如果需要证书和双向秘钥加密的话需要额外开发接口
     * @param isHttps
     * @return
     * @throws Exception
     */
	private static CloseableHttpClient getHttpClient(boolean isHttps) throws Exception {
		if (isHttps) {
			TrustManager trustManager = new X509TrustManager() {
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] x509Certificates,
						String s) throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] x509Certificates,
						String s) throws java.security.cert.CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[0];
				}
	        };
	        SSLContext sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(null, new TrustManager[] { trustManager }, null);
	        SSLConnectionSocketFactory sslsf =  new SSLConnectionSocketFactory(sslContext, 
	        		new String[] { "TLSv1" }, null, new HostnameVerifier() {
				@Override
				public boolean verify(String paramString, SSLSession paramSSLSession) {
					// 不校验，其实可以通过SSLSession得到对方的ip和端口，可以校验下，起到白名单的作用
					// paramSSLSession.getPeerHost()? 获取主机名然后解析出ip？
					return true;
				}
			});
	        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} else {
			return HttpClients.createDefault();
		}
    }
	
}
