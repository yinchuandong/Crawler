package Main;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

import Model.WebPage;
import Util.PageUtil;

import Base.BaseCrawler;

/**
 * 爬取百度旅游通用页面的
 * @author yinchuandong
 *
 */
public class Baidu extends BaseCrawler{
	
	
	public static void main(String[] args){
		Baidu baidu = new Baidu();
		baidu.begin();
	}
	
	
	//-------------------------------------------
	public Baidu(){
		super();
		this.setDomain("http://lvyou.baidu.com");
		this.loadSeedsFromFile("Seed/baidu.txt");
	}

	@Override
	public void exactor(WebPage page) {
//		String filename = PageUtil.getFileNameByUrl(page.getUrl().toString());
		String filename;
		try {
//			filename = URLEncoder.encode(page.getUrl().toString(),"utf-8");
//			PageUtil.exportFile("web/"+filename+".txt", page.getPageContent());
//			System.out.println(page.getPageContent());
			JSONObject jsonObject = JSONObject.fromObject(page);
			System.out.println(jsonObject.get("layer"));
			System.out.println(jsonObject.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAllowVisit(URL url) {
		if (url.toString().indexOf("lvyou.baidu.com") != -1) {
			return true;
		}else{
			return false;
		}
	}

}
