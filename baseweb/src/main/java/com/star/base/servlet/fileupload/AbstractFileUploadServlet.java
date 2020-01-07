package com.star.base.servlet.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.star.base.context.WebContext;
import com.star.base.fileupload.FileUploader;
import com.star.base.fileupload.FileUploaderFactory;
import com.star.base.servlet.BaseServlet;
import com.star.common.config.PlatformConfig;
import com.star.common.context.SpringContextManager;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 新的文件上传抽象类
 * 重构文件目录结构，默认上传到本地和阿里云OSS
 */
public abstract class AbstractFileUploadServlet extends BaseServlet {

	private static final long serialVersionUID = -838943736732960276L;
	private static Logger logger = LoggerFactory.getLogger(AbstractFileUploadServlet.class);

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 1024; // 1G
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 1034; // 1G多
	protected static PlatformConfig platformConfig;
	private static String localUploadTempDir;
	static{
		platformConfig = SpringContextManager.getBean(PlatformConfig.class);
		localUploadTempDir = platformConfig.UPLOAD_FILE_TEMP_PATH;
	}

	/**
	 * 上传文件保存的文件目录
	 * 
	 * @return
	 */
	protected abstract String getLocalUploadPath();

	/**
	 * 上传文件的临时保存目录，比如：/usr/temp/或c:\\user\xx\temp
	 * 
	 * @return
	 */
	protected String getLocalTempDir() {
		return localUploadTempDir;
	}

	/**
	 * 访问文件的URL前缀，以"/"结尾，比如：http://imgcloud.weijuju.com/
	 * <p>
	 * 文件的完整URL是通过这个前缀加上json中的fileName来拼
	 * 
	 * @return
	 */
	protected abstract String getURLPrefix();

	public void init(ServletConfig config) throws ServletException {
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 获取客户端回调函数名
		response.setContentType("text/html;charset=UTF-8");
		defaultProcessFileUpload(request, response);
		if ("onerror".equals(request.getParameter("testcase")))
			throw new IOException();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.error("上传怎么会是get请求呢");
	}

	/**
	 * 获取上传文件允许格式，以.开头的后缀名，比如：.jpg，.bmp
	 * <p>
	 * 例如：return new String[] {".jpg", ".bmp"};
	 * 
	 * @return
	 */
	public abstract String[] allowFileTypes();

	/**
	 * 文件类型判断
	 * 
	 * @param ext
	 *            文件后缀，最好以.开头，支持形式.jpg或jpg
	 * @return
	 */
	private boolean checkFileType(String ext) {
		assert ext != null;
		String fileExt = ext;
		if (!fileExt.startsWith(".")) {
			fileExt = "." + ext;
		}
		String[] allowTypes = allowFileTypes();
		if (allowTypes == null || allowTypes.length == 0) {
			return false;
		}
		for (String allowType : allowTypes) {
			if (fileExt.equalsIgnoreCase(allowType)) {
				return true;
			}
		}
		return false;
	}

	private void defaultProcessFileUpload(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!ServletFileUpload.isMultipartContent(request)) {
			logger.warn("请求不是multipart，但进入了资源上传处理逻辑");
			return;
		}
		// 超过MEMORY_THRESHOLD的内容写到暂时目录中
		DiskFileItemFactory factory = new DiskFileItemFactory(MEMORY_THRESHOLD,
				new File(getLocalTempDir()));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		// 最大图片大小
		upload.setFileSizeMax(MAX_FILE_SIZE);
		// 请求最大值（文件，加上表单内容的总大小）
		upload.setSizeMax(MAX_REQUEST_SIZE);

		try {
			FileItemIterator itemIterator = upload.getItemIterator(request);
			Map<String, Object> jMap = new HashMap<String, Object>();
			int numOfFile = 0;
			String fileUrl = "";
			while (itemIterator.hasNext()) {
				FileItemStream item = itemIterator.next();
				// 处理非表单的元素，这里就是上传的文件
				if (!item.isFormField()) {
					InputStream stream = item.openStream();
					String fileName = item.getName();
					// 得到文件的扩展名(无扩展名时将得到全名，这样一般过不了类型检测)
					String fileExt = fileName.substring(fileName
							.lastIndexOf("."));
					if (!checkFileType(fileExt)) {
						// 文件类型不合法
						jMap.put("error", "filetype");
						jMap.put("allowtype", allowFileTypes());
						writerToClient(response, jMap);
						return;
					}

					// 文件的可阅读名字（就是不带后缀的名字）
					String originName = fileName.substring(0,
							fileName.lastIndexOf("."));
					// 生成文件上传后的相对文件路径
					String fileRelativePath = generateFileRelativePath(request,
							fileName);
					// 返回文件名保存到数据库
					jMap.put("fileName", fileRelativePath);
					jMap.put("originName", originName);
					// 本地保存上传文件的文件路径
					String localFilePath = getFullPath(getLocalUploadPath(),
							fileRelativePath);
					if (numOfFile > 0) {
						fileUrl += ",";
					}
					fileUrl += getFullPath(getURLPrefix(), fileRelativePath);

					//上传前对流进行操作（旋转、缩放、剪切等）
					stream=updateStream(request,stream);
					
					FileUploader defaultFileUploader = FileUploaderFactory
							.getDefaultFileUploader();
					defaultFileUploader.upload(fileRelativePath, stream,
							localFilePath, true);
					numOfFile++;
				}
			}
			jMap.put("fileUrl", fileUrl);
			Enumeration<?> pNames = request.getParameterNames();
			String pName;
			while (pNames.hasMoreElements()) {
				pName = (String) pNames.nextElement();
				jMap.put(pName, request.getParameter(pName));
			}

			writerToClient(response, jMap);
		} catch (Exception ex) {
			logger.error("上传文件出错", ex);
		}
	}

	private String getFullPath(String prefix, String fileRelativePath) {
		// 给prefix结尾加上/
		if (!prefix.endsWith("/")) {
			prefix = prefix.concat("/");
		}
		// 去掉file relative path前面的所有/，比如：///path变成path
		while (fileRelativePath.startsWith("/")) {
			fileRelativePath = fileRelativePath.substring(1);
		}
		return prefix + fileRelativePath;
	}

	/**
	 * 生成上传的文件的文件相对路径，不以"/"开头
	 * <p>
	 * 比如：a/b/c <strong>建议以日期/模块名/用户名的方式组织文件目录</strong>
	 * 
	 * @param request
	 *            请求，可以从中得到类似uid的参数
	 * @param fileName
	 *            使用上传控件传递的文件名称，用来区别多文件上传的不同文件
	 * @return
	 */
	protected String generateFileRelativePath(HttpServletRequest request,
			String fileName) {
		String uid = WebContext.getCurrentUid(request).toString();
		if (uid == null) {
			Object uidStr = request.getAttribute("uid");
			if(uidStr == null){
				throw new IllegalArgumentException("请求中没有带uid参数");
			}else{
				uid = ((Integer)uidStr).toString();
			}
		}
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		StringBuilder buf = new StringBuilder();
		buf.append(year);
		buf.append("/");
		buf.append(month);
		buf.append("/");
		buf.append(getModuleName());
		buf.append("/");
		buf.append(uid);
		buf.append("/");
		buf.append(UUID.randomUUID().toString());
		String fileExt = fileName.substring(fileName.lastIndexOf("."));
		buf.append(fileExt);
		return buf.toString();
	}

	/**
	 * 模块名称，用来拼文件路径，不要带/和中文等路径不友好的字符
	 * 
	 * @return
	 */
	protected abstract String getModuleName();
	
	/**
	 * 上传文件之前对流的处理，此方法会在上传操作前执行
	 * 
	 * @return
	 */
	protected InputStream updateStream(HttpServletRequest request,InputStream stream){
		return stream;
	}
	
}
