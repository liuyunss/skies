package com.skies.utils;

import java.util.Map;

/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @author Administrator
 * @version 2014年6月10日 下午6:53:38
 */
public class MapUtil {

	public static String getValue(Map map, String key){

		String value = "";

		try {
			value = map.get(key).toString();
			if("height".equals(key)){
                boolean matches = value.matches("[0-9]+");
                if(!matches){
                    value = "0";
                }

            }
		} catch (Exception e) {
			value = "";
		}
		return value;

	}

}
