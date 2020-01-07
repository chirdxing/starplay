package com.star.base.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null;

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {

		String value = super.getHeader(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 
	 * @param s
	 * @return
	 */
	  private static String xssEncode(String s) {
	    if (s == null || s.isEmpty()) {
	      return s;
	    }
		String html = "";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				buffer.append("&lt;");
				break;
			case '>':
				buffer.append("&gt;");
				break;
			case '&':
				buffer.append("&amp;");
				break;
			case '"':
				buffer.append("&quot;");
				break;
			case 10:
			case 13:
				break;
			default:
				buffer.append(c);
			}
		}
		html = buffer.toString();
		return html;
	  }

	/**
	 * 获取最原始的request
	 * 
	 * @return
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}

	public static void main(String[] args) {
		String xss = "<alert(\"hello\")>";
		String _xss = XssHttpServletRequestWrapper.xssEncode(xss);
		System.out.println(_xss);
	}
}
