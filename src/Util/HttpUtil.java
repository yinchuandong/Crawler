package Util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 采用apache
 * @author yinchuandong
 *
 */
public class HttpUtil {
	
	private String charset;
	
	public HttpUtil(){
		charset = "utf-8";
	}
	
	public void setCharset(String charset){
		this.charset = charset;
	}
	
	/**
	 * 通过get方法爬取
	 * @param url
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String get(String url){
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String result = "";
		try {
			HttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
				//解决中文乱码
				result = new String(result.getBytes("ISO-8859-1"), 0, result.length(), charset);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
