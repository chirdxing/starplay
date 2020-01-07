package com.star.base.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.star.common.tools.CommonUtils;
import org.apache.commons.lang.StringUtils;

public class WebUtil {
	private static final int COOKIE_DEFAULT_EXPIRE = 7 * 24 * 3600;
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * 获取客户端的IP
	 * 
	 * @param request
	 * @return 获取的IP, 如果获取不到返回 null
	 */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
     * 获取访问者IP
     *
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     *
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @param request
     * @return
     */
//    public static String getIpAddr(HttpServletRequest request){
//        try {
//            String ip = request.getHeader("X-Real-IP");
//            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
//                return ip;
//            }
//            ip = request.getHeader("X-Forwarded-For");
//            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
//                // 多次反向代理后会有多个IP值，第一个为真实IP。
//                int index = ip.indexOf(',');
//                if (index != -1) {
//                    return ip.substring(0, index);
//                } else {
//                    return ip;
//                }
//            } else {
//                return request.getRemoteAddr();
//            }
//        }catch (Exception e){
//            throw new RuntimeException("请求ip信息异常");
//        }
//    }

	/**
	 * 构造请求参数，不包含“?”
	 * @param params
	 * @return
	 */
	public static String buildParams(Map<String, String> params) {
		Set<String> keys = params.keySet();
		StringBuffer params_result = new StringBuffer();
		int i = 0;
		for (String key : keys) {
			if (i != 0) {
				params_result.append("&");
			}
			i++;
			params_result.append(key).append("=").append(params.get(key));
		}
		return params_result.toString();
	}

	public static String buildUrlParams(String url, Map<String, String> params) {
		StringBuffer url_result = new StringBuffer();
		if (url.indexOf("?") != -1) {
			if (url.indexOf("&") != -1) {
				if (url.indexOf("#") != -1) {
					String[] url_arr = url.split("#");
					for (int i = 0; i < url_arr.length; i++) {
						url_result.append(url_arr[i]);
						if (i == 0) {
							url_result.append("&").append(buildParams(params));
						}
						if (i != url_arr.length - 1) {
							url_result.append("#");
						}
					}
				} else {
					url_result.append(url).append("&")
							.append(buildParams(params));
				}
			} else {
				if (url.indexOf("#") != -1) {
					String[] url_arr = url.split("#");
					for (int i = 0; i < url_arr.length; i++) {
						url_result.append(url_arr[i]);
						if (i == 0) {
							url_result.append(buildParams(params));
						}
						if (i != url_arr.length - 1) {
							url_result.append("#");
						}
					}
				} else {
					url_result.append(url).append("&")
							.append(buildParams(params));
				}
			}
		} else {
			url_result.append(url).append("?").append(buildParams(params));
		}
		return url_result.toString();
	}

	/**
	 * 防止页面提交非法字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean sqlValidate(String str) {
		str = str.toLowerCase();// 统一转为小写
		String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|"
				+ "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|"
				+ "table|from|grant|use|group_concat|column_name|"
				+ "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|"
				+ "chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#";// 过滤掉的sql关键字，可以手动添加
		String[] badStrs = badStr.split("\\|");
		for (int i = 0; i < badStrs.length; i++) {
			if (str.indexOf(badStrs[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 当前请求是不是微信浏览器发起的
	 * 
	 * @param req
	 * @return
	 */
	public static boolean inWeixinBrowser(HttpServletRequest req) {
		String userAgent = req.getHeader("user-agent");
		if (userAgent != null
				&& userAgent.toLowerCase().indexOf("micromessenger") > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 返回请求的全路径
	 * <p>
	 * 例如：http://120.24.163.85:8989/abc.jsp?wuid=818
	 * 
	 * @param request
	 * @param 支持过滤某些参数
	 * @return
	 */
	public static String getHttpRequestFullURL(HttpServletRequest request , String[] excludeParamName) {
		List<String> params = new ArrayList<String>(); 
		Enumeration<?> parameterNames = request.getParameterNames();
		if(excludeParamName == null){
			excludeParamName = new String[]{};
		}
		 while(parameterNames.hasMoreElements()){
			 String name = (String)parameterNames.nextElement();
			 if(CommonUtils.contains(name , excludeParamName)){
				 continue;
			 }
			 try {
				params.add(name+"="+URLEncoder.encode(request.getParameterValues(name)[0], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}   
		 }
		 String path = StringUtils.trimToEmpty(request.getPathInfo());
		 path = path.isEmpty()? "" : "#"+path;
		 return request.getRequestURL() + "?" + StringUtils.join(params, "&") + path;
	}
	
	/**
	 * 添加cookie，默认值用utf-8编码，默认存储7天
	 * @param name
	 * @param value
	 * @param response
	 */
	public static void addCookie(String name, String value, HttpServletResponse response) {
		try {
			Cookie cookie = new Cookie(name, URLEncoder.encode(value, DEFAULT_CHARSET));
			cookie.setPath("/");
			//cookie.setDomain(""); // 域名默认网页所在的域，不能设置跨域
			cookie.setMaxAge(COOKIE_DEFAULT_EXPIRE); // 默认七天过期（单位：秒）
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
		}
	}
	
	/**
	 * 获取 cookie值
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request , String cookieName) {
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					value = cookie.getValue();
					break;
				}
			}
		}
		try {
			if (null != value) {
				return URLDecoder.decode(value, DEFAULT_CHARSET);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 获取 cookie
	 * @param request
	 * @param name
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		if (cookies == null || name == null || name.length() == 0) {
			return null;
		}
		Cookie cookie = null;
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name)) {
				cookie = cookies[i];
				break;
			}
		}
		return cookie;
	}
	
	/**
	 * 使某个cookie失效
	 * @param request
	 * @param response
	 * @param cookieName
	 */
	public static void invalidateCookie(HttpServletRequest request, 
			HttpServletResponse response, String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		if (null != cookie) {
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setValue("");
			response.addCookie(cookie);
		}
	}
	
}
