package com.star.common.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * 地图服务相关的函数工具类
 * GPS坐标(mgs84)、火星坐标(gcj02)、百度坐标(bd09)相互转化函数
 * 已知两个经纬度的点求距离函数
 * @date 2020年1月7日
 * @version 1.0
 */
public class MapUtil {
	private static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	private static double PI = 3.1415926535897932384626;
	private static double a = 6378137.0;
	private static double ee = 0.00669342162296594323;
	

	/**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     * @param bd_lon
     * @param bd_lat
     * @returns
     */
    public static Map<String, String> bd09ToGcj02(double bd_lon, double bd_lat) {
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("lng", String.valueOf(gg_lng));
        resultMap.put("lat", String.valueOf(gg_lat));
        return resultMap;
    };
	
    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     * @param lng
     * @param lat
     * @returns
     */
    public static Map<String, String> gcj02ToBd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("lng", String.valueOf(bd_lng));
        resultMap.put("lat", String.valueOf(bd_lat));
        return resultMap;
    };

    /**
     * WGS84转GCj02
     * @param lng
     * @param lat
     * @returns
     */
    public static Map<String, String> wgs84ToGcj02(double lng, double lat) {
    	Map<String, String> resultMap = new HashMap<String, String>();
        if (outOfChina(lng, lat)) {
             resultMap.put("lng", String.valueOf(lng));
             resultMap.put("lat", String.valueOf(lat));
             return resultMap;
        } else {
        	double dlat = transformLat(lng - 105.0, lat - 35.0);
        	double dlng = transformLng(lng - 105.0, lat - 35.0);
        	double radlat = lat / 180.0 * PI;
        	double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            resultMap.put("lng", String.valueOf(mglng));
            resultMap.put("lat", String.valueOf(mglat));
            return resultMap;
        }
    };
    

    /**
     * GCJ02 转换为 WGS84
     * @param lng
     * @param lat
     * @returns
     */
    public static Map<String, String> gcj02ToWgs84(double lng, double lat) {
    	Map<String, String> resultMap = new HashMap<String, String>();
        if (outOfChina(lng, lat)) {
        	 resultMap.put("lng", String.valueOf(lng));
             resultMap.put("lat", String.valueOf(lat));
             return resultMap;
        } else {
            double dlat = transformLat(lng - 105.0, lat - 35.0);
            double dlng = transformLng(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            resultMap.put("lng", String.valueOf(mglng));
            resultMap.put("lat", String.valueOf(mglat));
            return resultMap;
        }
    };

    public static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    };

    public static double transformLng(double lng, double lat) {
    	double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    };

    /**
     * 判断是否在国内，不在国内则不做偏移
     * @param lng
     * @param lat
     * @returns
     */
    public static boolean outOfChina(double lng, double lat) {
        return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
    };

    /**
     * 转化为弧度(rad) 
     */
    public static double toRad(double d) {
    	return d * Math.PI / 180.0;
    }
    
    /** 
     * 基于余弦定理求两经纬度距离 
     * @param lng1 第一点的经度 
     * @param lat1 第一点的纬度 
     * @param lng2 第二点的经度 
     * @param lat3 第二点的纬度 
     * @return 返回的距离，单位m
     * */  
    public static double distByLngAndLat(double lng1, double lat1, double lng2, double lat2) {
    	double radLat1 = toRad(lat1);  
    	double radLat2 = toRad(lat2);  
  
    	double radlng1 = toRad(lng1);
    	double radlng2 = toRad(lng2);
  
        if (radLat1 < 0)  
            radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
        if (radLat1 > 0)  
            radLat1 = Math.PI / 2 - Math.abs(radLat1);// north  
        if (radlng1 < 0)  
            radlng1 = Math.PI * 2 - Math.abs(radlng1);// west  
        if (radLat2 < 0)  
            radLat2 = Math.PI / 2 + Math.abs(radLat2);// south  
        if (radLat2 > 0)  
            radLat2 = Math.PI / 2 - Math.abs(radLat2);// north  
        if (radlng2 < 0)  
            radlng2 = Math.PI * 2 - Math.abs(radlng2);// west
        double x1 = a * Math.cos(radlng1) * Math.sin(radLat1);  
        double y1 = a * Math.sin(radlng1) * Math.sin(radLat1);  
        double z1 = a * Math.cos(radLat1);
  
        double x2 = a * Math.cos(radlng2) * Math.sin(radLat2);  
        double y2 = a * Math.sin(radlng2) * Math.sin(radLat2);  
        double z2 = a * Math.cos(radLat2);  
  
        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)+ (z1 - z2) * (z1 - z2));  
        //余弦定理求夹角  
        double theta = Math.acos((a * a + a * a - d * d) / (2 * a * a));  
        double dist = theta * a;  
        return dist;
    }
    
}
