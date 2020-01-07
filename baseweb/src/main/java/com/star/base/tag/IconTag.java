package com.star.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.star.common.config.PlatformConfig;
import com.star.common.context.SpringContextManager;

public class IconTag extends TagSupport{
	private static final long serialVersionUID = 1L;
	private static PlatformConfig platformConfig;
	static{
		platformConfig = SpringContextManager.getBean(PlatformConfig.class);
	}
	
	// 图标链接
	private String url;
	
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			StringBuilder sb = new StringBuilder(512);
			sb.append("<link rel=\"icon\" href=\"");
			sb.append(platformConfig.STATIC_RESOURCE_PREFIX);
			sb.append("/image/favicon.ico");
			sb.append("?v=");
			sb.append(platformConfig.BUILD_VERSION);
			sb.append("\" type=\"image/x-icon\" />");
            out.print(sb.toString());
		} catch (java.io.IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
