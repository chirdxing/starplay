package com.star.base.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.star.base.context.WebContext;
import com.star.base.entity.CommonFile;
import com.star.base.service.ICommonFileService;
import com.star.base.servlet.BaseServlet;
import com.star.base.utils.DateJsonValueProcessor;
import com.star.common.context.SpringContextManager;
import net.sf.json.JsonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name="CommonFileServlet", urlPatterns="/admin/commonfile")  
public class CommonFileServlet extends BaseServlet {
	private static final long serialVersionUID = 157277751010366376L;
	private Logger logger = LoggerFactory.getLogger(CommonFileServlet.class);
	
	private static ICommonFileService commonFileSvc;
	static{
		commonFileSvc = SpringContextManager.getBean(ICommonFileService.class);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (Strings.isNullOrEmpty(action)) {
			return;
		}
		if ("save".equals(action)) {
			save(request, response);
			return;
		} else if ("list".equals(action)) {
			list(request, response);
			return;
		} else if ("del".equals(action)) {
			del(request, response);
			return;
		} else if ("delAll".equals(action)) {
			delAll(request, response);
			return;
		}
	}

	protected void save(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> jMap = new HashMap<String, Object>();
		try {
			int uid = WebContext.getCurrentUid(request);
			String url = request.getParameter("url");
			String filename = request.getParameter("filename");
			CommonFile commonFile = new CommonFile();
			commonFile.setUid(uid);
			commonFile.setFilename(filename);
			commonFile.setUrl(url);
			commonFileSvc.save(commonFile);
			jMap.put("ret", 0);
		} catch (Exception e) {
			logger.error("CommonFileServlet save error:", e);
			jMap.put("success", false);
		} finally {
			writerToClient(response, jMap);
		}
	}

	private void list(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String page_str = request.getParameter("page");
		String limit_str = request.getParameter("limit");
		int page = 0;
		int limit = 10;
		try {
			page = Integer.parseInt(page_str);
			limit = Integer.parseInt(limit_str);
		} catch (NumberFormatException e) {
			// ignore
		}
		int uid = WebContext.getCurrentUid(request);
		List<CommonFile> commonFiles = commonFileSvc.findByUid(uid, page, limit);
		CommonFile commonFile = new CommonFile();
		commonFile.setUid(uid);
		int total = commonFileSvc.findTotalCount(commonFile);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("ret", 0);
		data.put("msg", "success");
		data.put("file_list", commonFiles);
		data.put("total_count", total);

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class,
				new DateJsonValueProcessor("yyyy-MM-dd HH:mm"));
		writerToClient(response, data, jsonConfig);
	}

	protected void del(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> jMap = new HashMap<String, Object>();
		try {
			String filename = request.getParameter("filename");
			if(!Strings.isNullOrEmpty(filename)){
				commonFileSvc.del(filename);
				jMap.put("ret", 0);
			}
		} catch (Exception e) {
			logger.error("CommonFileServlet del error:", e);
			jMap.put("success", false);
		} finally {
			writerToClient(response, jMap);
		}
	}
	
	protected void delAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
				commonFileSvc.delAll(WebContext.getCurrentUid(request));
		} catch (Exception e) {
			logger.error("CommonFileServlet delAll error:", e);
		} finally {
		}
	}
}
