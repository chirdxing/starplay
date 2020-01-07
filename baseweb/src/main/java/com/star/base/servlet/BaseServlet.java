package com.star.base.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.io.IOUtils;

public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public BaseServlet() {
		super();
	}

	/**
	 * 把json字符串写到输出流
	 *            返回到前台的json字符串
	 */
	public void writerToClient(HttpServletResponse response,
			Map<String, Object> map) {
		String resStr = JSONObject.fromObject(map).toString();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(resStr);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("IO工作流出现异常!");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * 把json字符串写到输出流
	 * 
	 * @param response
	 * @param jsonString
	 *            json格式的字符串
	 */
	public void writerToClient(HttpServletResponse response, String jsonString) {
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(jsonString);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("IO工作流出现异常!");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public void writerToClient(HttpServletResponse response,
			Map<String, Object> map, JsonConfig jsonConfig) {
		String resStr = JSONObject.fromObject(map, jsonConfig).toString();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(resStr);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("IO工作流出现异常!");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
