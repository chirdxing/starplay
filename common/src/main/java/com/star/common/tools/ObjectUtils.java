package com.star.common.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对象判空工具类
 * @date 2020年1月7日
 * @version 1.0
 */
public class ObjectUtils {

	/**
	 * 判断对象是否为空
	 * @param obj 对象
	 * @return
	 */
	public static boolean isNull(Object obj) {
		return (null == obj);
	}
	
	public static boolean isNotNull(Object obj) {
		return (null != obj);
	}
	
	/**
	 * 判断数组是否为空
	 * @param objs
	 * @return
	 */
	public static <T> boolean isNull(List<T> list) {
		return (null == list || list.size() == 0);
	}
	
	public static <T> boolean isNotNull(List<T> list) {
		return (null != list && list.size() > 0);
	}
	
	/**
	 * 判断map是否为空
	 * @param map
	 * @return
	 */
	public static <K, V> boolean isMapNull(Map<K, V> map) {
		return (null == map || map.size() == 0);
	}
	
	public static <K, V> boolean isMapNotNull(Map<K, V> map) {
		return (null != map && map.size() > 0);
	}
	
	/**
	 * 判断set是否为空
	 * @param set
	 * @return
	 */
	public static <T> boolean isSetNull(Set<T> set) {
		return (null == set || set.size() == 0);
	}
	
	public static <T> boolean isSetNotNull(Set<T> set) {
		return (null != set && set.size() > 0);
	}
	
}
