package com.star.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 平台配置常量
 * @since 2020年1月7日
 */
@Component
public class PlatformConfig {
	@Value("${debug}")
	public boolean DEBUG;

	@Value("${redis_config_file}")
	public String REDIS_CONFIG_FILE;

	@Value("${config_file}")
	public String CONFIG_FILE;

	/**版本，每次构建时都会变化**/
	@Value("${build_version}")
	public String BUILD_VERSION;


	/**------七牛文件上传--------*/
	@Value("${qiniu_prop}")
	public String QINIU_PROP;

	@Value("${qiniu.accessKey}")
	public String QINIU_ACCESSKEY;

	@Value("${qiniu.secretKey}")
	public String QINIU_SECRETKEY;

	@Value("${qiniu.bucket}")
	public String QINIU_BUCKET;

	@Value("${qiniu.soTimeout}")
	public Integer QINIU_SO_TIMEOUT;

	@Value("${qiniu.upHost}")
	public String QINIU_UP_HOST;


	/**--------资源路径 ---------**/
	/**文件上传时的临时保存路径**/
	@Value("${upload_file_temp_path}")
	public String UPLOAD_FILE_TEMP_PATH;

	/**文件上传本地保留的路径**/
	@Value("${upload_pic_path}")
	public String UPLOAD_PIC_PATH;

	/**文件上传后显示时的路径前缀**/
	@Value("${upload_resource_prefix}")
	public String UPLOAD_RESOURCE_PREFIX;

	/**静态资源的路径前缀**/
	@Value("${static_resource_prefix}")
	public String STATIC_RESOURCE_PREFIX;


	/**------阿里服务--------*/
	/**阿里大鱼key*/
	@Value("${alidayu.Key}")
	public String ALIDAYU_KEY;

	/**阿里大鱼secret*/
	@Value("${alidayu.Secret}")
	public String ALIDAY_SECRET;

	/** 阿里云key */
	@Value("${alimail.Access.Key}")
	public String ALIMAIL_ACCESS_KEY;

	/** 阿里云secret */
	@Value("${alimail.Access.Secret}")
	public String ALIMAIL_ACCESS_SECRET;

	/** 阿里云邮件发出地址 */
	@Value("${alimail.fromaccount}")
	public String ALIMAIL_FROMACCOUNT;

	/** 阿里云邮件tag */
	@Value("${alimail.tag.name}")
	public String ALIMAIL_TAG_NAME;

	/** httpsqs ip地址 */
	@Value("${httpsqs_server_ip}")
	public String HTTPSQS_SERVER_IP;

	/** httpsqs端口号 */
	@Value("${httpsqs_server_port}")
	public String HTTPSQS_SERVER_PORT;

	/** httpsqs字符集 */
	@Value("${httpsqs_charset}")
	public String HTTPSQS_CHARSET;

	/** 是否跳过微信授权 */
	@Value("${weixin_oauth_skip}")
	public boolean WEIXIN_OAUTH_SKIP;
}