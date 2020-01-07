package com.star.base.servlet.fileupload;

import javax.servlet.annotation.WebServlet;


@WebServlet(name="CommonMusicUploadServlet", urlPatterns="/admin/common/musicupload")
public class CommonMusicUploadServlet extends AbstractMusicUploadServlet {

	private static final long serialVersionUID = 263531620532833985L;

	@Override
	protected String getModuleName() {
		return "common";
	}

}
