package com.star.base.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.star.common.tools.ObjectUtils;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;


public class DateJsonValueProcessor implements JsonValueProcessor {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";    
	private DateFormat dateFormat;  
	
	 public DateJsonValueProcessor(String datePattern) {    
		 try {    
			 dateFormat = new SimpleDateFormat(datePattern);    
		 } catch (Exception ex) {    
			 dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);    
		 }    
	}   
	
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		 return process(value); 
	}

	public Object processObjectValue(String key, Object value,JsonConfig jsonConfig) {
		return process(value);
	}
	
	private Object process(Object value) {    
		return ObjectUtils.isNull(value) ? null : dateFormat.format((Date) value);
	} 
}


