package com.star.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.google.common.base.Strings;
import com.star.common.config.PlatformConfig;
import com.star.common.context.SpringContextManager;

public class ImageTag extends TagSupport{
	private static final long serialVersionUID = 1L;
	private static PlatformConfig platformConfig;
	static{
		platformConfig = SpringContextManager.getBean(PlatformConfig.class);
	}
	
	// 图片链接属性
	private String src;
	// 图片DOM的id属性
	private String id;
	// 图片DOM的class属性
	private String clz;
	// 图片DOM的提示属性
	private String alt;
	
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			StringBuilder sb = new StringBuilder(512);
        	sb.append("<img ");
        	if (!Strings.isNullOrEmpty(getSrc())) {
        		sb.append(" src=\"");
        		sb.append(platformConfig.STATIC_RESOURCE_PREFIX);
        		sb.append(this.src);
        		sb.append("?v=");
        		sb.append(platformConfig.BUILD_VERSION);
        		sb.append("\" ");
        	}
        	if (!Strings.isNullOrEmpty(getId())) {
        		sb.append(" id=\"");
        		sb.append(this.id);
        		sb.append("\" ");
        	}
        	if (!Strings.isNullOrEmpty(getClz())) {
        		sb.append(" class=\"");
        		sb.append(this.clz);
        		sb.append("\" ");
        	}
        	if (!Strings.isNullOrEmpty(getAlt())) {
        		sb.append(" alt=\"");
        		sb.append(this.alt);
        		sb.append("\" ");
        	}
        	sb.append(" />");
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClz() {
		return clz;
	}
	public void setClz(String clz) {
		this.clz = clz;
	}
	public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	
}
