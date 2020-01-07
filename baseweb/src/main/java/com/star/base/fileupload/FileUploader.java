package com.star.base.fileupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件上传器
 * 处理文件上传，主要会涉及云存储
 */
public class FileUploader {
	private Logger logger = LoggerFactory.getLogger(FileUploader.class);
	private List<FileUploadListener> listeners = new ArrayList<FileUploadListener>();

	protected FileUploader() {
	}

	/**
	 * 上传文件
	 * 
	 * @param fileRelativePath
	 *            文件的相对路径，会保存到数据库中，要求能唯一定位上传的文件
	 * @param stream
	 *            上传的文件流
	 * @param localFilePath
	 *            要保存文件到本地的路径
	 * @param isNewModule
	 *            历史原因，新的模块中资源的路径与原有的路径命名规则不同
	 *            URL使用PlatformConfig.UPLOAD_PIC_PATH2和PlatformConfig.
	 *            API_RESOURCE_DOMAIN2的这里要填true
	 */
	public void upload(String fileRelativePath, InputStream stream,
			String localFilePath, boolean isNewModule) {
		assert localFilePath != null;
		File file = new File(localFilePath);
		byte[] buf = new byte[1024];
		FileOutputStream bos = null;
		try {
			if (file.getParentFile() != null && !file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bos = new FileOutputStream(file);
			int length = -1;
			// 把文件内容写到本地
			while ((length = stream.read(buf)) != -1) {
				bos.write(buf, 0, length);
			}
		} catch (Exception ex) {
			logger.error("上传文件存储到本地时出错", ex);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		// 把文件上传通知出去
		for (FileUploadListener l : listeners) {
			l.fireFileUploaded(new FileUploadEvent(fileRelativePath, file,
					isNewModule));
		}
	}
	/**
	 * 直接触发监听器
	 * @param fileRelativePath
	 * @param file
	 * @param isNewModule
	 */
	public void triggerListener(String fileRelativePath , File file , boolean isNewModule){
		// 把文件上传通知出去
		for (FileUploadListener l : listeners) {
			l.fireFileUploaded(new FileUploadEvent(fileRelativePath, file,
					isNewModule));
		}
	}

	/**
	 * 添加文件上传监听器
	 * 
	 * @param listener
	 */
	public void addFileUploadListener(FileUploadListener listener) {
		listeners.add(listener);
	}
}
