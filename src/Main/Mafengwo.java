package Main;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class Mafengwo {

	/**
	 * 测试apache get方法打开Url
	 * 解决中文乱码
	 */
	@SuppressWarnings("deprecation")
	public static void getCityId(String cityName){
		String url = "http://www.mafengwo.cn/hotel/s.php?sKeyWord="+cityName+"&sCheckIn=2014-07-28&sCheckOut=2014-07-29";
		DefaultHttpClient client = new DefaultHttpClient();
		client.setRedirectStrategy(new RedirectStrategy() {
			@Override
			public HttpUriRequest getRedirect(HttpRequest arg0,
					HttpResponse arg1, HttpContext arg2)
					throws ProtocolException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isRedirected(HttpRequest arg0, HttpResponse arg1,
					HttpContext arg2) throws ProtocolException {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		HttpGet httpGet = new HttpGet(url);
//		httpGet.setHeader("Host", "www.mafengwo.cn");
		httpGet.setHeader("Referer", "http://www.mafengwo.cn/hotel/");
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36"); 
		try {
			HttpResponse response = client.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity());
				//解决中文乱码
//				String test = new String(result.getBytes("ISO-8859-1"), 0, result.length(), "utf-8");
//				String test = new String(result.getBytes("gbk"), 0, result.length(), "utf-8");

				Header[] headers = response.getAllHeaders();
				System.out.println(result);
				System.out.println("ok?");
			}else{
				System.out.println("页面没有返回");
				Header[] headers = response.getHeaders("Location");
				System.out.println(headers[0].getValue());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			httpGet.releaseConnection();
		}
	}
	
	
	public static void main(String[] args){
		getCityId("深圳");
	}
}
