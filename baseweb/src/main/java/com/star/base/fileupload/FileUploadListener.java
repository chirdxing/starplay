package com.star.base.fileupload;

/**
 * 文件上传完成的监听器
 */
public interface FileUploadListener {

	/**
	 * 发出文件上传完成事件
	 * @param event 文件上传事件
	 */
	void fireFileUploaded(FileUploadEvent event);
}
