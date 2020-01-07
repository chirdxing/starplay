package com.star.base.fileupload;

import java.io.File;

import com.google.common.base.Strings;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.star.common.config.PlatformConfig;
import com.star.common.context.SpringContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上传到七牛云存储
 */
public class QiniuUploadListener implements FileUploadListener {
	private static final Logger logger = LoggerFactory.getLogger(QiniuUploadListener.class);

	private static final String ACCESSKEY;
	private static final String SECRETKEY;
	private static final String BUCKET_NAME;
	private static PlatformConfig platformConfig;
	private static UploadManager uploadManager;

	static {
		platformConfig = SpringContextManager.getBean(PlatformConfig.class);
		if (Strings.isNullOrEmpty(platformConfig.QINIU_ACCESSKEY)) {
			logger.warn("没有找到七牛云配置" + platformConfig.QINIU_PROP
					+ "，如果要使用七牛云上传会出错");
			ACCESSKEY = null;
			SECRETKEY = null;
			BUCKET_NAME = null;
		} else {
			ACCESSKEY = platformConfig.QINIU_ACCESSKEY;
			SECRETKEY = platformConfig.QINIU_SECRETKEY;
			BUCKET_NAME = platformConfig.QINIU_BUCKET;
			uploadManager = new UploadManager();
		}
	}
	
	@Override
	public void fireFileUploaded(FileUploadEvent event) {
	    Auth mac = Auth.create(ACCESSKEY, SECRETKEY);
        String token = mac.uploadToken(BUCKET_NAME);
        
        File file = event.getFile();
        String key = generateKey(event.getFilePath(), event.isNewModule());
        logger.info("key" + key);
        try {
            Response response = uploadManager.put(file, key, token);
            logger.info(response.toString());
            logger.info(response.bodyString());
            if ( response.isOK() ) {
                // TODO: process is ok
                // 若文件上传成功，则将文件删除掉 
                file.delete();
            } else {
                // process error
            }
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时简单状态信息
            logger.error(r.toString());
            try {
                // 响应的文本信息
                logger.error(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
	}
	
	private String generateKey(String filePath, boolean isNewModule) {
		if (Strings.isNullOrEmpty(filePath)) {
			throw new IllegalArgumentException("文件名为空，无法上传到七牛云存储");
		}
		// 无论新旧模块
		if (filePath.startsWith("/")) {
			// key不能以/开头
			filePath = filePath.substring(1);
		}
		return filePath;
	}
}
