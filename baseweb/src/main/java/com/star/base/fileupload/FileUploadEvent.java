package com.star.base.fileupload;

import java.io.File;

/**
 * 文件上传事件
 * @version 1.0
 */
public class FileUploadEvent {

	private String filePath;
	private File file;
	private boolean isNewModule;	// 新模块的url命名规则不同于以前的模块

	public FileUploadEvent(String filePath, File file, boolean isNewModule) {
		this.filePath = filePath;
		this.file = file;
		this.isNewModule = isNewModule;
	}

	/**
	 * 文件相对路径，数据库存储的路径
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 本地文件
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 * 是否是新模块，新模块url命名规则不同于以前的模块
	 * @return
	 */
	public boolean isNewModule() {
		return isNewModule;
	}
}
