package com.star.base.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;
import com.star.base.context.WebContext;
import com.star.common.constant.ErrorCode;
import com.star.common.entity.User;
import com.star.common.service.IUserService;
import com.star.common.tools.ObjectUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/login")
public class LoginController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private IUserService userSvc;
	
	
	@RequestMapping(value = "login.do", method = RequestMethod.POST,
			produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Result login(@RequestParam("account_name") String accountName, 
			@RequestParam("password") String password) {
		try {
			Map<String, Object> model = Maps.newHashMap();

			// MD5加密
			String passwordMd5 = DigestUtils.md5Hex(password);
			User user = userSvc.findByLoginInfo(accountName, passwordMd5);
			if (ObjectUtils.isNull(user)) {
				// 找不到对应的用户
				return failResponse(ErrorCode.USER_OR_PASSWORD_ERROR);
			}
			
			// 执行登录
			Integer uid = user.getUid();
			HttpServletRequest request = getRequest();
			WebContext.login(request, uid);
			
			// 写入登录日志
			try {
				// String ip = WebUtil.getClientIp(request);
			} catch (Exception e) {
				logger.error("查询不到登录用户" + accountName + "的ip", e);
			}
			
			model.put("toUrl", "admin/manage/homepage.jsp");
			return successResponse(model);
		} catch (Exception e) {
			logger.error("user:[{}] login fail!", accountName, e);
			return failResponse(ErrorCode.SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "logout.do", method = RequestMethod.GET)
	public String testEntity() {
		// 不用ajax的方式
		WebContext.logout(getRequest());
		return "redirect:/";
	}
	
	public static void main(String[] args) {
		System.out.println(DigestUtils.md5Hex("123"));
	}
	
}
