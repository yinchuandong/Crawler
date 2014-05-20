package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import Util.AppUtil;

/**
 * 用来测试jsoup的解析
 * @author yinchuandong
 *
 */
public class TestJsoup {

	public static void main(String[] args){
		
//		testHttpGet();
//		testJsoupUrl();
		testParseJson();
	}
	
	
	/**
	 * 测试解析Json
	 */
	@SuppressWarnings("unused")
	public static void testParseJson(){
		String result = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("web/guangzhou-1.json"));
			String str ="";
			while((str = reader.readLine()) != null){
				result += str;
			}
//			System.out.println(AppUtil.jsonFormatter(result));
			result = AppUtil.jsonFormatter(result);
			JSONObject jsonObj = JSONObject.fromObject(result); 
			JSONObject dataObj = jsonObj.getJSONObject("data");
			//----需要保存的数据库字段--------------------
			String sid = dataObj.getString("sid");
			String surl = dataObj.getString("surl");
			String sname = dataObj.getString("sname");
			String ambiguitySname = dataObj.getString("ambiguity_sname");
			String parentSid = dataObj.getString("parent_sid");
			String viewCount = dataObj.getString("view_count");
			String star = dataObj.getString("star");
			String sceneLayer = dataObj.getString("scene_layer");
			int goingCount = dataObj.getInt("going_count");
			int goneCount = dataObj.getInt("gone_count");
			double rating = dataObj.getDouble("rating");
			int ratingCount = dataObj.getInt("rating_count");
			
			JSONObject extObj = dataObj.getJSONObject("ext");
			String mapInfo = extObj.getString("map_info");//获得经纬度
			
			//------用于判断分页，构造url------------------
			int sceneTotal = dataObj.getInt("scene_total");
			int currentPage = dataObj.getInt("current_page");
			int pageNums = (int) Math.ceil((double)sceneTotal / 16);
			
			//-------解析景点列表-----------
			JSONArray sceneList = dataObj.getJSONArray("scene_list");
			parseSceneList(sceneList);
			
			System.out.println("------------------------------");
			System.out.println(sceneTotal);
			System.out.println(rating);
			System.out.println(sid);
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 解析旅游json 的scene_list部分
	 * @param sceneList
	 */
	public static void parseSceneList(JSONArray sceneList){
		for(int i=0; i<sceneList.size(); i++){
			JSONObject dataObj = sceneList.getJSONObject(i);
			String sid = dataObj.getString("sid");
			String surl = dataObj.getString("surl");
			String sname = dataObj.getString("sname");
			String ambiguitySname = dataObj.getString("ambiguity_sname");
			String parentSid = dataObj.getString("parent_sid");
			String viewCount = dataObj.getString("view_count");
			String star = dataObj.getString("star");
			String sceneLayer = dataObj.getString("scene_layer");
			
			JSONObject extObj = dataObj.getJSONObject("ext");
			String mapInfo = extObj.getString("map_info");//获得经纬度
			int goingCount = extObj.getInt("going_count");
			int goneCount = extObj.getInt("gone_count");
			System.out.println(sid);
			System.out.println(surl);
			System.out.println(sname);
			System.out.println(generateUrl(surl, 1, 1));
			System.out.println("------------------------------");
		}
		
	}
	
	public static String generateUrl(String city, int cid, int page){
		String url = "http://lvyou.baidu.com/destination/ajax/jingdian?format=ajax&";
		url  += "surl=" + city+ "&cid=1&pn=" + page +"&t=1400550956830";
		return url;
	}
	
	/**
	 * 测试apache get方法打开Url
	 * 解决中文乱码
	 */
	@SuppressWarnings("deprecation")
	public static void testHttpGet(){
		String url = "http://lvyou.baidu.com/destination/ajax/jingdian?format=ajax&surl=panyu&cid=1&pn=1&t=" + System.currentTimeMillis();
		String url2 = "http://lvyou.baidu.com/guangzhoubaiyunshan/";
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url2);
		try {
			HttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity());
				//解决中文乱码
				String test = new String(result.getBytes("ISO-8859-1"), 0, result.length(), "utf-8");
				System.out.println(test);
			}else{
				System.out.println("页面没有返回");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试jsoup解析html格式的string
	 */
	public static void testJsoupString(){
		Document document = Jsoup.parse("<html><head><title>test for title</title></head><body><div id='main'>maindiv</div></body></html>");
		Element divMain= document.getElementById("main");
		System.out.println(divMain.text());
	}
	
	/**
	 * 测试jsoup打开url连接
	 */
	public static void testJsoupUrl(){
		try {
			
			Document document2 = Jsoup.parse(new URL("http://lvyou.baidu.com/guangzhoubaiyunshan/"), 5000);
			System.out.println(document2.toString());
			Element container = document2.getElementById("J_slide-holder");
			System.out.println(container.attr("class"));
			Elements sub = container.children();
			System.out.println(sub.size());
			for (int i=0; i<sub.size(); i++) {
				Element elem = sub.get(i);
				System.out.println(elem.select("figure").attr("data-url"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
