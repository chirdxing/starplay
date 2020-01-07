package com.star.common.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 时间、字符串通用操作类
 * @date 2020年1月7日
 * @version 1.0
 */
public class CommonUtils {

	private static char[] numbersAndLetters = null;
	private static char[] numbersLetters = null;

	private static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999,
			9999999, 99999999, 999999999, Integer.MAX_VALUE };

	static {
		numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
				+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		numbersLetters = ("0123456789").toCharArray();
	}

	// 地球平均半径（单位：千米）
	private static final double EARTH_RADIUS = 6370.856;

	/**
	 * 转换Date类型为字符串类型
	 * 
	 * @param value
	 * @return
	 */
	public static String getSimpleDate(Date value) {
		return getSimpleDate(value, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获取当前的年份
	 * 
	 * @param value
	 * @return
	 */
	public static int getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * 获取当前的月份
	 * 
	 * @param value
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH);
	}
	
	/**
	 * 获取当前的日
	 * 
	 * @param value
	 * @return
	 */
	public static int getCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 转换Date类型为中文字符串类型
	 * 
	 * @param value
	 * @return
	 */
	public static String getChinieseDate(Date value) {
		return getSimpleDate(value, "yyyy年MM月dd日HH时mm分");
	}

	/**
	 * 转换Date类型为字符串类型
	 * 
	 * @param value
	 * @return
	 */
	public static String getSimpleDate(Date value, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(value);
	}

	/**
	 * 判断字符串是否空或空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否为非空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 返回trim后的字符串，如空字符串，则直接返回空。
	 * 
	 * @param str
	 * @return
	 */
	public static String trimToEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return null;
		}
		return str.trim();
	}

	/**
	 * 返回trim后的字符串，如果为null,则返回""
	 * 
	 * @param str
	 * @return
	 */
	public static String trimToEmptyForce(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}
		return str.trim();
	}

	/**
	 * 判断字符是否为数值型
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}

	}
	/**
	 * 文件大小单位换算
	 * @param fileSize 单位：KB
	 * @return
	 */
	public static String conFileSize(Integer fileSize) {
		String appFileSizeStr = "";
		float roundNum = 0;
		// 单位值
		String unit = "KB";
		if (fileSize < 1024) {
			appFileSizeStr = fileSize + unit;
			return appFileSizeStr;
		} else if (fileSize >= 1024 && fileSize < 1024 * 1024) {
			unit = "M";
			// 整数
		    roundNum = (float)fileSize / 1024;
			
		} else if (fileSize >= 1024 * 1024) {
			unit = "G";
			// 整数
			roundNum = (float)fileSize / (1024 * 1024);
		}
		String roundNumStr = String.valueOf(roundNum);
		// 前缀
		String prefixNum = roundNumStr.substring(0, String.valueOf(roundNum).lastIndexOf("."));
		// 后缀
		String suffixNum = roundNumStr.substring(String.valueOf(roundNum).lastIndexOf(".") + 1, roundNumStr.length());
		if (suffixNum.length() == 1) {
			appFileSizeStr = roundNum + unit;
			return appFileSizeStr;
		} else if (suffixNum.length() >= 2) {
			int suffixSecondNum = Integer.parseInt(suffixNum.charAt(1) + "");
			int suffixFirstNum = Integer.parseInt(suffixNum.charAt(0) + "");
			if (suffixSecondNum >= 5) {
				suffixFirstNum++;
			} 
			appFileSizeStr = prefixNum + "." + suffixFirstNum + unit;
			return appFileSizeStr;
		}
		return null;
	}
	
	/**
	 * 获取几年前的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date lastYear(Date d, int m) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - m);
		return now.getTime();
	}

	/**
	 * 获取几月后的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date nextMonth(Date d, int m) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) + m);
		return now.getTime();
	}
	
	/**
	 * 获取几月前的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date lastMonth(Date d, int m) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) - m);
		return now.getTime();
	}

	/**
	 * 获取几天后的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date nextDate(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}
	
	/**
	 * 获取几星期前的时间
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date lastWeek(Date d, int week) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.WEEK_OF_MONTH, now.get(Calendar.WEEK_OF_MONTH) - week);
		return now.getTime();
	}
	
	/**
	 * 获取几天前的时间
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date lastDay(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * 获取几小时后的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date nextHour(Date d, int hour) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) + hour);
		return now.getTime();
	}

	/**
	 * 获取几分钟后的时间
	 * 
	 * @param d
	 * @param minute
	 * @return
	 */
	public static Date nextMinute(Date d, int minute) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + minute);
		return now.getTime();
	}
	
	/**
	 * 获取几分钟后的时间
	 * 
	 * @param d
	 * @param minute
	 * @return
	 */
	public static Date nextSecond(Date d, int second) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.SECOND, now.get(Calendar.SECOND) + second);
		return now.getTime();
	}

	/**
	 * 获取两个日期的间隔天数
	 * 
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public static int dayInterval(Date startDay, Date endDay) {
		return (int) ((endDay.getTime() - startDay.getTime()) / (24 * 60 * 60 * 1000));
	}

	/**
	 * 转换String类型为Date类型
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static Date getSimpleDate(String value) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.parse(value);
	}

	/**
	 * 转换String类型为Date类型
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static Date getSimpleDateBy(String value, String pattern)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(value);
	}

	/**
	 * 转换String类型为Date类型
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static Date getSimpleDate2(String value) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.parse(value);
	}

	/**
	 * 把时分秒置为0
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateOnly(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 首字母大写
	 * 
	 * @param name
	 * @return
	 */
	public static String getUpperName(String name) {
		byte[] items = name.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	/**
	 * 判断字符串是否合法(不含有非法字符或中文) 若数组中其中一字符串含有非法字符，返回false，反之，返回true
	 * 
	 * @param sarray
	 * @return
	 */
	public static boolean judgeIllegalChar(String[] sarray) {
		boolean result = true;
		if (sarray != null) {
			Pattern pattern = Pattern.compile("^\\w+$");
			for (int i = 0; i < sarray.length; i++) {
				if (!pattern.matcher(sarray[i]).matches()) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * 判断字符串是否为合法邮箱
	 * 
	 * @param sarray
	 * @return
	 */
	public static boolean judgeEmail(String email) {
		boolean result = false;
		if (email != null) {
			Pattern pattern = Pattern
					.compile("^([a-z0-9A-Z._-])+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
			if (pattern.matcher(email).matches()) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 判断字符串是否合法(不含有非法字符，可含中文) 若数组中其中一字符串含有非法字符，返回false，反之，返回true
	 * 
	 * @param sarray
	 * @return
	 */
	public static boolean judgeIllegalCharAndChinese(String[] sarray) {
		boolean result = true;
		if (sarray != null) {
			Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9_-]+$");
			for (int i = 0; i < sarray.length; i++) {
				if (sarray[i] == null || !pattern.matcher(sarray[i]).matches()) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * 判断字符串是否合法(不含 ' 字符，可含中文) 若数组中其中一字符串含有非法字符，返回false，反之，返回true
	 * 
	 * @param sarray
	 * @return
	 */
	public static boolean judgePartIllegalCharAndChinese(String[] sarray) {
		boolean result = true;
		if (sarray != null) {
			Pattern pattern = Pattern.compile("[^\']+");
			for (int i = 0; i < sarray.length; i++) {
				if (sarray[i] == null || !pattern.matcher(sarray[i]).matches()) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	public static String apiEncode(Integer uid, Integer wuid) {
		String randString = UUID.randomUUID().toString();
		String startString = randString.substring(0, 4);
		String endString = randString.substring(0, 10);
		String midString = "C";
		wuid = wuid << 2;
		uid = uid << 3;
		return startString + uid + midString + wuid + endString;
	}

	public static Integer[] apiDecode(String key) {
		key = key.substring(0, key.length() - 10).substring(4);
		String[] array = key.split("C");
		Integer uid = Integer.parseInt(array[0]);
		uid = uid >> 3;
		Integer wuid = Integer.parseInt(array[1]);
		wuid = wuid >> 2;
		return new Integer[] { uid, wuid };
	}

	/**
	 * 分享链接编码
	 * 
	 * @param uid
	 * @return
	 */
	public static String shareEncode(Integer uid) {
		String randString = UUID.randomUUID().toString();
		String startString = randString.substring(0, 4);
		String endString = randString.substring(0, 10);
		String midString = "C";
		uid = uid << 3;
		return startString + uid + midString + endString;
	}

	/**
	 * 分享链接解码
	 * 
	 * @param key
	 * @return
	 */
	public static Integer shareDecode(String key) {
		key = key.substring(0, key.length() - 10).substring(4);
		String[] array = key.split("C");
		Integer uid = Integer.parseInt(array[0]);
		uid = uid >> 3;
		return uid;
	}

	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[new Random().nextInt(71)];
		}
		return new String(randBuffer);
	}

	public static final String randomNumber(int length) {
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersLetters[new Random().nextInt(10)];
		}
		return new String(randBuffer);
	}

	/**
	 * 截取左边max个字符,ascll码大于255算两个字符，字符过长以...结尾
	 * 
	 * @param s
	 * @param max
	 * @return
	 */
	public static String left(String s, int max) {
		char[] cs = s.toCharArray();
		int count = 0;
		int last = cs.length;
		for (int i = 0, len = last; i < len; i++) {
			if (cs[i] > 255) {
				count += 2;
			} else {
				count++;
			}
			if (count > max) {
				last = i + 1;
				break;
			}
		}
		if (count <= max) {
			return s;
		}
		max -= 3;
		for (int i = last - 1; i >= 0; i--) {
			if (cs[i] > 255) {
				count -= 2;
			} else {
				count--;
			}
			if (count <= max) {
				return s.substring(0, i) + "...";
			}
		}
		return "...";
	}

//	public static String base64Encode(String s) {
//		return Base64.encodeBase64String(s.getBytes());
//	}
//
//	public static String base64Decode(String s) {
//		return new String(Base64.decodeBase64(s));
//	}

	/**
	 * 从指定的时间截取年月日。将时分秒毫秒都设置为0
	 * 
	 * @param source
	 *            原始时间
	 * @return 将时分秒毫秒都设置为0的日期
	 */
	public static Date trimDate(Date source) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	/**
	 * 返回指定时间在当天的最后时间，便于查询
	 * @param source
	 * @return
	 */
	public static Date fillDate(Date source) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	/**
	 * 计算精度和维度之间的距离
	 * 
	 * @param a_x_point
	 * @param a_y_point
	 * @param b_x_point
	 * @param b_y_point
	 * @return
	 */
	public static long calDistance(Double a_x_point, Double a_y_point,
			Double b_x_point, Double b_y_point) {
		Double R = new Double(EARTH_RADIUS);
		Double dlat = (b_x_point - a_x_point) * Math.PI / 180;
		Double dlon = (b_y_point - a_y_point) * Math.PI / 180;
		Double aDouble = Math.sin(dlat / 2) * Math.sin(dlat / 2)
				+ Math.cos(a_x_point * Math.PI / 180)
				* Math.cos(b_x_point * Math.PI / 180) * Math.sin(dlon / 2)
				* Math.sin(dlon / 2);
		Double cDouble = 2 * Math.atan2(Math.sqrt(aDouble),
				Math.sqrt(1 - aDouble));
		long d = Math.round((R * cDouble) * 1000);
		return d;
	}

	/**
	 * 计算某个经纬度的周围某段距离的正方形的四个点
	 * 
	 * @return
	 */
	public static Map<SquarePoint, Point> calSquarePoint(Double longitude,
			Double latitude, Double distance) {
		Double longitudeRad = 2 * Math.asin(Math.sin(distance / (2 * EARTH_RADIUS))
				/ Math.cos(deg2rad(latitude)));
		Double longitudeDeg = rad2deg(longitudeRad);

		Double latitudeRad = distance / EARTH_RADIUS;
		Double latitudeDeg = rad2deg(latitudeRad);
		
		Map<SquarePoint, Point> squarePoint = new HashMap<SquarePoint, Point>(4, 1);
		squarePoint.put(SquarePoint.LEFT_TOP, new Point(longitude - longitudeDeg, latitude + latitudeDeg));
		squarePoint.put(SquarePoint.RIGHT_TOP, new Point(longitude + longitudeDeg, latitude + latitudeDeg));
		squarePoint.put(SquarePoint.LEFT_BOTTOM, new Point(longitude - longitudeDeg, latitude - latitudeDeg));
		squarePoint.put(SquarePoint.RIGHT_BOTTOM, new Point(longitude + longitudeDeg, latitude - latitudeDeg));
		return squarePoint;
	}
	
	public static enum SquarePoint {
		LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
	}
	
	public static class Point {
		/**
		 * 经度
		 */
		private Double longtitude;
		/**
		 * 纬度
		 */
		private Double latitude;
		
		public Point(Double longtitude, Double latitude) {
			super();
			this.longtitude = longtitude;
			this.latitude = latitude;
		}

		/**
		 * @see #longtitude
		 * @return the longtitude
		 */
		public Double getLongtitude() {
			return longtitude;
		}
		
		/**
		 * @see #longtitude
		 * @param longtitude the longtitude to set
		 */
		public void setLongtitude(Double longtitude) {
			this.longtitude = longtitude;
		}
		
		/**
		 * @see #latitude
		 * @return the latitude
		 */
		public Double getLatitude() {
			return latitude;
		}
		
		/**
		 * @see #latitude
		 * @param latitude the latitude to set
		 */
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		
	}

	/**
	 * 角度转弧度
	 * 
	 * @param deg
	 * @return
	 */
	public static Double deg2rad(Double deg) {
		return 2 * Math.PI * deg / 360;
	}

	/**
	 * 弧度转角度
	 * 
	 * @param rad
	 * @return
	 */
	public static Double rad2deg(Double rad) {
		return 360 * rad / (2 * Math.PI);
	}

	/**
	 * 获取字符串的长度，中文占一个字符,英文数字占半个字符
	 * 
	 * @param value
	 *            指定的字符串
	 * @return 字符串的长度
	 */
	public static double length(String value) {
		double valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 1;
			} else {
				valueLength += 0.5;
			}
		}
		return Math.ceil(valueLength);
	}

	/**
	 * 生成一个随机码
	 * 
	 * @return
	 */
	public static String getRandomCode() {
		SimpleDateFormat millSecondFormat = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		String prefix = millSecondFormat.format(new Date());
		Random random = new Random();
		int number = random.nextInt(100);
		return prefix + number;
	}

	/**
	 * 获取一个int常量的位数
	 * 
	 * @param source
	 * @return
	 */
	public static int sizeOfInt(int source) {
		for (int i = 0;; i++) {
			if (source <= sizeTable[i]) {
				return i + 1;
			}
		}
	}
	
	/**
	 * 计算两个时间之间的秒数，注意不能超过50年
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	public static int calSecondsBetween(Date start, Date end) {
		long times = (end.getTime() - start.getTime()) / 1000;
		return (int)times;
	}
	
	public static boolean contains(String target , String[] list){
		boolean contain = false;
		for(String item : list){
			if(item.equals(target)){
				contain = true;
				break;
			}
		}
		return contain;
	}
}
