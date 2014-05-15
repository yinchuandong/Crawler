package Main;

import java.net.URL;

import Model.WebPage;
import Util.PageUtil;

import Base.BaseCrawler;

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
		String filename = PageUtil.getFileNameByUrl(page.getUrl().toString());
		PageUtil.exportFile("web/"+filename+".txt", page.getPageContent());
		System.out.println(page.getUrl().toString());
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
