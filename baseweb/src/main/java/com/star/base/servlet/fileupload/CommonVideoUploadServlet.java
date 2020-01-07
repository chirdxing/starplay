package com.star.base.servlet.fileupload;

import javax.servlet.annotation.WebServlet;


@WebServlet(name="CommonVideoUploadServlet", urlPatterns="/admin/common/videoupload")
public class CommonVideoUploadServlet extends AbstractVideoUploadServlet {

	private static final long serialVersionUID = 263531620532833985L;

	@Override
	protected String getModuleName() {
		return "common";
	}

}
