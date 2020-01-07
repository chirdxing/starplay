package com.star.base.servlet.fileupload;

public abstract class AbstractVideoUploadServlet extends
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
		return new String[] { ".mp4" };
	}

	@Override
	protected abstract String getModuleName();

}
