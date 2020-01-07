package com.star.base.servlet.fileupload;


/**
 * 文档上传，支持office、pdf以及压缩文件
 * 视频上传，
 * @date 2020年1月7日
 * @version 1.0
 */
public abstract class AbstractDocUploadServlet extends
		AbstractFileUploadServlet {

	private static final long serialVersionUID = 3698750452699638989L;

	@Override
	protected String getLocalUploadPath() {
		return platformConfig.UPLOAD_PIC_PATH;
	}

	@Override
	protected String getURLPrefix() {
		return platformConfig.UPLOAD_RESOURCE_PREFIX;
	}

	@Override
	public String[] allowFileTypes() {
		return new String[] { ".xls", ".xlsx", ".doc", ".docx", ".ppt", ".pptx", ".pdf", ".txt", 
				".zip", ".rar", ".rars" };
	}

	@Override
	protected abstract String getModuleName();

}
