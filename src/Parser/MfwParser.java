package Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
			
			//价格,默认第一个为最低价格
			Element priceBox = hItem.select(".btn-booking").get(0);
			String dataUrl = priceBox.attr("data-url");
			String jurl = "";
			String otaname = "";
			String price = "0";
			if (!dataUrl.equals("")) {
				dataUrl = "http://www.mafengwo.cn" + dataUrl;
				HashMap<String, String> params = UrlParmUtil.parseUrl(dataUrl);
				jurl = params.get("j");
				jurl = URLDecoder.decode(jurl);
				otaname = priceBox.select("em.t").get(0).text();
				price = priceBox.select("em.p ._j_price").get(0).text();
				System.out.println(otaname + " " + price + " " + jurl);
			}
			
			Hotel hotel = new Hotel();
			hotel.setName(name);
			hotel.setLat(lat);
			hotel.setLng(lng);
			hotel.setImgSrc(imgSrc);
			hotel.setSummary(summary);
			hotel.setAddress(address);
			hotel.setBookUrl(jurl);
			hotel.setOtaname(otaname);
			hotel.setPrice(price);
			
		}
//		System.out.println(html);
	}
	
	public class Hotel{
		private String name;
		private String lat;
		private String lng;
		private String summary;
		private String imgSrc;
		private String address;
		private String otaname;//预订的网站，如：携程网等
		private String price;
		private String bookUrl;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLat() {
			return lat;
		}
		public void setLat(String lat) {
			this.lat = lat;
		}
		public String getLng() {
			return lng;
		}
		public void setLng(String lng) {
			this.lng = lng;
		}
		public String getSummary() {
			return summary;
		}
		public void setSummary(String summary) {
			this.summary = summary;
		}
		public String getImgSrc() {
			return imgSrc;
		}
		public void setImgSrc(String imgSrc) {
			this.imgSrc = imgSrc;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getOtaname() {
			return otaname;
		}
		public void setOtaname(String otaname) {
			this.otaname = otaname;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getBookUrl() {
			return bookUrl;
		}
		public void setBookUrl(String bookUrl) {
			this.bookUrl = bookUrl;
		}
		
		
	}
	
	
	public static void main(String[] args) throws IOException{
		MfwParser model = new MfwParser();
		model.parse("E:\\hotel\\10088-1.txt");
	}
}
