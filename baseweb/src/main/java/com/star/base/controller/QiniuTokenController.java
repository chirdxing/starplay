package com.star.base.controller;

import com.qiniu.util.Auth;
import com.star.common.config.PlatformConfig;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.qiniu.util.Auth.*;


@Controller
@RequestMapping("/qiniu/upload")
public class QiniuTokenController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(QiniuTokenController.class);
	
	@Autowired
	private PlatformConfig platformConfig;
	
	
	@RequestMapping(value = "uptoken.do", method = RequestMethod.GET,
			produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String uptoken() {
		try {
			Auth mac = create(platformConfig.QINIU_ACCESSKEY, platformConfig.QINIU_SECRETKEY);
		    String uptoken = mac.uploadToken(platformConfig.QINIU_BUCKET);

		    JSONObject uptokenJO = new JSONObject();
		    uptokenJO.put("uptoken", uptoken);
		    
		    return uptokenJO.toString();
		} catch (Exception e) {
			logger.error("get qiniu uptoken failed!", e);
			return "{}";
		}
	}
	
}
