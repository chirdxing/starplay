package com.star.base.servlet.fileupload;

import javax.servlet.annotation.WebServlet;


@WebServlet(name="CommonImgUploadServlet", urlPatterns="/admin/common/imgupload")
public class CommonImgUploadServlet extends AbstractImgUploadServlet {

	private static final long serialVersionUID = 263531620532833985L;

	@Override
	protected String getModuleName() {
		return "common";
	}

}
