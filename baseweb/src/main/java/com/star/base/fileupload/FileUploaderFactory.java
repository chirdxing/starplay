package com.star.base.fileupload;

/**
 * 文件上传器工厂
 * 带上abstract是不要云实例化它的意思
 */
public abstract class FileUploaderFactory {

	/**
	 * 获取默认的文件上传器，如果不符合要求，可以继承FileUploader生成自己的文件上传器
	 * <p>
	 * 默认会上传资源到七牛云存储
	 * 
	 * @return
	 */
	public static FileUploader getDefaultFileUploader() {
		FileUploader uploader = new FileUploader();
		// 默认加上七牛云存储
		uploader.addFileUploadListener(new QiniuUploadListener());
		return uploader;
	}
}
