package Main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

/**
 * 用来测试jsoup的解析
 * @author yinchuandong
 *
 */
public class TestJsoup {

	public static void main(String[] args){
		
		testHttpGet();
//		testJsoupUrl();
		
//		System.out.println(System.currentTimeMillis());
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
