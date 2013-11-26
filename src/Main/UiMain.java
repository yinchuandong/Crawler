package Main;

import java.net.URL;

import Model.WebPage;
import Util.PageUtil;

import Base.BaseCrawler;

public class UiMain extends BaseCrawler{
	
	
	public static void main(String[] args){
		new UiMain();
	}

	@Override
	public void exactor(WebPage page) {
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
