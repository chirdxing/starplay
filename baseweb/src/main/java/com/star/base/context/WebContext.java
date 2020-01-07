package com.star.base.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.star.common.context.SpringContextManager;
import com.star.common.entity.User;
import com.star.common.service.IUserService;
import com.star.common.tools.ObjectUtils;


/**
 * 获取Web上下文中的数据
 * @date 2020年1月7日
 * @version 1.0
 */
public class WebContext {
	private static final String CURRENT_UID = "uid";
	
	private static IUserService userSvc;
	static {
		userSvc = SpringContextManager.getBean(IUserService.class);
	}

	/**
	 * 获取存储在上下文中的当前登录账号的Uid
	 * @param request
	 * @return
	 */
	public static Integer getCurrentUid(HttpServletRequest request) {
		Object uidObj = getSession(request).getAttribute(CURRENT_UID);
		return ObjectUtils.isNull(uidObj) ? null : (Integer) uidObj;
	}
	
	/**
	 * 获取当前登录账号的实体
	 * @param request
	 * @return
	 */
	public static User getCurrentUser(HttpServletRequest request) {
		Integer uid = getCurrentUid(request);
		if (!ObjectUtils.isNull(uid)) {
			return userSvc.findByUid(uid);
		}
		return null;
	}
	
	/**
	 * 执行登录操作 --> 将登录账号Uid放入session中
	 * @param request
	 * @param uid
	 */
	public static void login(HttpServletRequest request, Integer uid) {
		getSession(request).setAttribute(CURRENT_UID, uid);
	}
	
	/**
	 * 执行登出操作
	 * @param request
	 */
	public static void logout(HttpServletRequest request) {
		getSession(request).removeAttribute(CURRENT_UID);
	}
	
	
	private static HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}
	
}
