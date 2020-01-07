package com.star.base.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.star.common.constant.ErrorCode;
import com.star.common.tools.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

public abstract class BaseController {
	
	public Integer getStart(Integer start) {
		return ObjectUtils.isNull(start) ? 0 : start;
	}
	
	public Integer getLimit(Integer limit) {
		return ObjectUtils.isNull(limit) ? 10 : limit;
	}
	
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
		        .getRequestAttributes()).getRequest();
		return request;
	}

	public HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
		        .getRequestAttributes()).getResponse();
		return response;
	}

	public Result successResponse() {
		return successResponse(null);
	}

	public Result successResponse(Map<String, Object> model) {
		Result result = new Result();
		result.setRet(ErrorCode.SUCCESS.getCode());
		result.setMsg("success");
		result.setModel(model);
		return result;
	}
	
	public Result successResponse(Map<String, Object> model, String msg) {
		Result result = new Result();
		result.setRet(ErrorCode.SUCCESS.getCode());
		result.setMsg(msg);
		result.setModel(model);
		return result;
	}

	public Result failResponse(ErrorCode errorCode) {
		return failResponse(errorCode, errorCode.getMessage());
	}

	public Result failResponse(ErrorCode errorCode, String message) {
		Result result = new Result();
		result.setRet(errorCode.getCode());
		result.setMsg(message);
		return result;
	}

	/**
	 * 返回参数并跳转链接
	 * @param viewName
	 * @param model
	 * @return
	 */
	public ModelAndView createMav(String viewName, Map<String, ?> model) {
		return new ModelAndView(viewName, model);
	}

	public static class Result {
		private int ret;

		private String msg;
		
		private Map<String, Object> model;

		public int getRet() {
			return ret;
		}

		public void setRet(int ret) {
			this.ret = ret;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public Map<String, Object> getModel() {
			return model;
		}

		public void setModel(Map<String, Object> model) {
			this.model = model;
		}

		public void addModel(String key, Object object) {
			if (ObjectUtils.isNull(model)) {
				model = new HashMap<>();
			}
			model.put(key, object);
		}
	}
}
