package com.star.common.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

/**
 * 泛型方法类用来拷贝转化对象
 * @date 2020年1月7日
 * @version 1.0
 */
public class EntityUtils {

	/**
	 * 实体转换
	 * @param source 源对象
	 * @param targetClass 目标对象的Class类型
	 * @return
	 */
	public static <S, T> T transform(S source, Class<? extends T> targetClass) {
		if (!ObjectUtils.isNull(source)) {
			T target = null;
			try {
				target = targetClass.newInstance();
				BeanUtils.copyProperties(source, target);
			} catch (Exception e) {
				return null;
			}
			return target;
		} else {
			return null;
		}
	}
	
	
	/**
	 * 实体列表转换
	 * @param sourceList
	 * @param targetClass
	 * @return
	 */
	public static <S,T> List<T> transform(List<S> sourceList, Class<? extends T> targetClass) {
		List<T> result = new ArrayList<T>();
		if (!ObjectUtils.isNull(sourceList)) {
			for (S source : sourceList) {
				result.add(transform(source, targetClass));
			}
		}
		return result;
	}
	
	/**
	 * 实体列表转换，并返回第一个元素，数组为空则返回null
	 * @param sourceList
	 * @param targetClass
	 * @return
	 */
	public static <S, T> T transformAndReturnFirst(List<S> sourceList, Class<? extends T> targetClass) {
		if (ObjectUtils.isNotNull(sourceList)) {
			return transform(sourceList.get(0), targetClass);
		}
		return null;
	}

}
