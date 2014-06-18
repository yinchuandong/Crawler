package Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Util.PageUtil;
import Util.UrlParmUtil;

import net.sf.json.JSONObject;

public class MfwParser {

	
	private void parse(String fileName) throws IOException{
		String content = PageUtil.readFile(new File(fileName));
		JSONObject jsonObject = JSONObject.fromObject(content);
		String html = jsonObject.getString("html");
		Document document = Jsoup.parse(html);
		Elements hItems = document.select(".h-item");
		for (Element hItem : hItems) {
			//名称和坐标
			String name = hItem.attr("data-name");
			String lat = hItem.attr("data-lat");
			String lng = hItem.attr("data-lng");
			//获得图片src
			Element img = hItem.select(".h-pic a img").get(0);
			String imgSrc = img.attr("src");
			
			//简介
			Elements hSummary = hItem.select(".h-summary");
			String summary = hSummary.get(0).text();
			
			//地址
			Element hAddress = hItem.select(".address").get(0);
			String address = hAddress.text().replaceAll("[\r\n]", "");
			
			//价格
			Elements hPriceBox = hItem.select(".btn-booking");
			for (Element priceBox : hPriceBox) {
				String dataUrl = priceBox.attr("data-url");
				if (dataUrl.equals("")) {
					continue;
				}
				dataUrl = "http://www.mafengwo.cn" + dataUrl;
				HashMap<String, String> params = UrlParmUtil.parseUrl(dataUrl);
				String jurl = params.get("j");
				jurl = URLDecoder.decode(jurl);
				String otaname = priceBox.select("em.t").get(0).text();
				String price = priceBox.select("em.p ._j_price").get(0).text();
				System.out.println(otaname + " " + price + " " + jurl);
			}
		}
//		System.out.println(html);
	}
	
	public static void main(String[] args) throws IOException{
		MfwParser model = new MfwParser();
		model.parse("E:\\hotel\\34888-1.txt");
	}
}
