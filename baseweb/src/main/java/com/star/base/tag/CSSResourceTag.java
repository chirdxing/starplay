package com.star.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.star.common.config.PlatformConfig;
import com.star.common.context.SpringContextManager;

public class CSSResourceTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static PlatformConfig platformConfig;
	static{
		platformConfig = SpringContextManager.getBean(PlatformConfig.class);
	}
	
	private String src;
	private boolean suffix;

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			StringBuilder sb = new StringBuilder(512);
			sb.append("<link rel=\"stylesheet\" ");
			sb.append("href=\"");
			sb.append(platformConfig.STATIC_RESOURCE_PREFIX);
			sb.append(this.src);
			sb.append("?v=");
			sb.append(platformConfig.BUILD_VERSION);
			sb.append("\" />");
			sb.append("");
			out.print(sb.toString());
		} catch (java.io.IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public boolean isSuffix() {
		return suffix;
	}

	public void setSuffix(boolean suffix) {
		this.suffix = suffix;
	}

	
}
