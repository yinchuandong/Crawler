package Util;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class AppUtil {

	/**
	 * md5加密算法
	 * @param str
	 * @return
	 */
	static public String md5(String str) {
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (algorithm != null) {
			algorithm.reset();
			algorithm.update(str.getBytes());
			byte[] bytes = algorithm.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				hexString.append(Integer.toHexString(0xFF & b));
			}
			return hexString.toString();
		}
		return "";

	}
	
	/**
	 * 格式化json
	 * @param uglyJSONString
	 * @return
	 */
	public static String jsonFormatter(String uglyJSONString){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}
	
	/**
	 * 解析url，获得查询的参数，
	 * 如：http://www.baidu.com?name=123&year=2014
	 * 将name、year的值装入hashmap里面，直接通过get方法调用
	 * @param url
	 * @return
	 */
	public static HashMap<String, String> parseUrlQuey(URL url){
		HashMap<String, String> result = new HashMap<String, String>();
		String[] rowArr = url.getQuery().split("&");
		for (String param : rowArr) {
			String[] paramArr = param.split("=");
			if (paramArr.length == 2) {
				result.put(paramArr[0], paramArr[1]);
			}
		}
		return result;
	}
	

}
