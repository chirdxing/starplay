package com.star.base.servlet.fileupload;

import javax.servlet.annotation.WebServlet;


@WebServlet(name="CommonDocUploadServlet", urlPatterns="/admin/common/docupload")
public class CommonDocUploadServlet extends AbstractDocUploadServlet {

	private static final long serialVersionUID = 263531620532833985L;

	@Override
	protected String getModuleName() {
		return "common";
	}

}
