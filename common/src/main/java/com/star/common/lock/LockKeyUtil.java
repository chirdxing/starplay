package com.star.common.lock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Maps;
import com.star.common.tools.CommonUtils;
import com.star.common.utils.security.DigestUtil;


/**
 * Redis锁生成key工具类
 * @date 2020年1月7日
 * @version 1.0
 */
public class LockKeyUtil {
	
    /**
     * 根据请求参数键值对Map生成MD5值作为redis分布式锁的key
     * params不要放空字段
     * @param params
     * @return
     */
	public static String buildLockKeyByMap(Map<String, String> params) {
		return DigestUtil.md5ToHex(orderByAscii(params));
	}
	
	/**
	 * 根据请求对象的【非空】实体数据域生成MD5值作为Redis分布式锁的key
	 * static、transient修饰的字段会被过滤掉
	 * Date等非基本数据类型的包装类对象也会被过滤掉
	 * @param entity 泛型对象
	 * @return
	 * @throws Exception
	 */
	public static <T> String buildLockKeyByEntity(T entity) {
		return buildLockKeyByMap(entityToMap(entity));
	}
	
	/**
	 * 将对象的【非空】实体数据域转化为Map
	 * @param entity 泛型对象
	 * @return
	 * @throws Exception
	 */
	private static <T> Map<String, String> entityToMap(T entity) {
		Map<String, String> resultMap = Maps.newHashMap();
		
		try {
			Class<?> clz = entity.getClass();
			Field[] fields = clz.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())
						|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				
				String type = field.getGenericType().toString();
				String fieldName = field.getName();
				Method m = clz.getMethod("get" + CommonUtils.getUpperName(fieldName));
				
				if ("class java.lang.String".equals(type)) {
					String value = (String) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value);
					}
				} else if ("class java.lang.Boolean".equals(type)) {
					Boolean value = (Boolean) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value.toString());
					}
				} else if ("class java.lang.Double".equals(type)
						|| "double".equals(type)) {
					Double value = (Double) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value.toString());
					}
				} else if ("class java.lang.Integer".equals(type)
						|| "int".equals(type)) {
					Integer value = (Integer) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value.toString());
					}
				} else if ("class java.lang.Long".equals(type)
						|| "long".equals(type)) {
					Long value = (Long) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value.toString());
					}
				} else if ("class java.lang.Float".equals(type)
						|| "float".equals(type)) {
					Float value = (Float) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value.toString());
					}
				} else if ("class java.lang.Short".equals(type)
						|| "short".equals(type)) {
					Short value = (Short) m.invoke(entity);
					if (value != null) {
						resultMap.put(fieldName, value.toString());
					}
				}
			}
		} catch (Exception e) {
		}
		return resultMap;
	}
	
	/**
     * 按照参数key字典顺序排序参数，拼成key=value&key2=value2的形式，需要“?”的自己加
     * @param params 参数集
     * @return
     */
    private static String orderByAscii(Map<String, String> params) {
        Set<String> paramKeys = new TreeSet<String>();
        for (Map.Entry<String, String> param : params.entrySet()) {
            String paramValue = param.getValue();
            if (paramValue != null && !"".equals(paramValue)) {
                paramKeys.add(param.getKey());
            }
        }
        StringBuilder stringA = new StringBuilder(1024);
        boolean first = true;
        for (String paramKey : paramKeys) {
            if (!first) {
                stringA.append("&");
            }
            stringA.append(paramKey);
            stringA.append("=");
            stringA.append(params.get(paramKey));
            first = false;
        }
        return stringA.toString();
    }
    
    
//    public static void main(String[] args) {
//    	TestParam testParam = new TestParam();
//		testParam.setId(1);
//		testParam.setName("edward");
//		testParam.setPassword("24355adv");
//		testParam.setStartDate("2016-08-18 14:36:10");
//		testParam.setType(null);
//		testParam.setEndDate(new Date());
//		testParam.setIsVip(true);
//		
//		String lockKey = buildLockKeyByEntity(testParam);
//		System.out.println(lockKey);
//	}

}
