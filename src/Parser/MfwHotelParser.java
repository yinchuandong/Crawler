package Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mysql.jdbc.Field;

import Util.DbUtil;
import Util.PageUtil;
import Util.UrlParmUtil;

import net.sf.json.JSONObject;

public class MfwHotelParser {

	private HashMap<String, String> cityMap;
	
	public MfwHotelParser(){
		cityMap = new HashMap<String, String>();
	}
	
	/**
	 * 从文件中加载cityId-cityName的映射
	 * @param fileName
	 * @throws IOException
	 */
	public void loadCity(String fileName) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String buff = null;
		while((buff = reader.readLine()) != null){
			String arr[] = buff.split("\\s");
			String cityName = arr[0];
			String cityId = arr[1];
			cityMap.put(cityId, cityName);
		}
		reader.close();
	}
	
	public void runTask(String dirPath) throws IOException{
		PrintWriter writer = new PrintWriter(new File("./mfwparser-log.txt"));
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				parse(file);
				System.out.println("---------处理完:"+ i +"个------------");
			} catch (Exception e) {
				String error = "第 " + i + " 个错误:" + file.getName();
				error += "\r\n" + e.getMessage()+"\r\n";
				error += "------------------------------------------------\r\n";
				System.out.print(error);
				writer.write(error);
				writer.flush();
				e.printStackTrace();
			}
		}
		writer.close();
		
	}
	
	private void parse(File file) throws IOException{
		String content = PageUtil.readFile(file);
		JSONObject jsonObject = JSONObject.fromObject(content);
		String html = jsonObject.getString("html");
		Document document = Jsoup.parse(html);
		Elements hItems = document.select(".h-item");
		
		String[] extArr = file.getName().split("-");
		String cityId = extArr[0];
		String cityName = cityMap.get(cityId);
		
		for (Element hItem : hItems) {
			//名称和坐标
			String name = hItem.attr("data-name");
			String lat = hItem.attr("data-lat");
			String lng = hItem.attr("data-lng");
			
			//获得酒店id
			String id = hItem.select(".h-pic a").get(0).attr("href").replaceAll("(/\\w*?/)|(\\.(\\w)+)", "");
			
			//获得图片src
			Element img = hItem.select(".h-pic a img").get(0);
			String imgSrc = img.attr("src");
			
			//简介
			Elements hSummary = hItem.select(".h-summary");
			String summary = hSummary.get(0).text();
			
			//地址
			Element hAddress = hItem.select(".address p").get(0);
			String address = hAddress.attr("title");
			
			//价格,默认第一个为最低价格
			String jurl = "";
			String otaname = "";
			String price = "0";
			
			Elements hPriceBox = hItem.select(".btn-booking");
			if (hPriceBox != null && hPriceBox.size() != 0) {
				Element priceBox = hPriceBox.get(0);
				String dataUrl = priceBox.attr("data-url");
				if (!dataUrl.equals("")) {
					dataUrl = "http://www.mafengwo.cn" + dataUrl;
					HashMap<String, String> params = UrlParmUtil.parseUrl(dataUrl);
					jurl = params.get("j");
					jurl = URLDecoder.decode(jurl);
					otaname = priceBox.select("em.t").get(0).text();
					price = priceBox.select("em.p ._j_price").get(0).text();
				}
			}
			
			System.out.println(otaname + " " + price + " " + jurl);
			
			Hotel hotel = new Hotel();
			hotel.setId(id);
			hotel.setName(name);
			hotel.setLat(lat);
			hotel.setLng(lng);
			hotel.setImgSrc(imgSrc);
			hotel.setSummary(summary);
			hotel.setAddress(address);
			hotel.setBookUrl(jurl);
			hotel.setOtaname(otaname);
			hotel.setPrice(price);
			hotel.setCityId(cityId);
			hotel.setCityName(cityName);
			saveToDb(hotel);
			
		}
//		System.out.println(html);
	}
	
	/**
	 * 将结果保存到数据库
	 * @param hotel
	 * @return
	 */
	private int saveToDb(Hotel hotel){
		String[] params = {
				hotel.getId(),
				hotel.getName(),
				hotel.getLat(),
				hotel.getLng(),
				hotel.getImgSrc(),
				hotel.getSummary(),
				hotel.getAddress(),
				hotel.getBookUrl(),
				hotel.getOtaname(),
				hotel.getPrice(),
				hotel.getCityId(),
				hotel.getCityName()
		};
		String sql = "insert into t_hotel (id, name, lat, lng, imgSrc, summary, address, bookUrl, otaname, price, cityId, cityName) values (?,?,?,?,?,?,?,?,?,?,?,?)";
		return DbUtil.executeUpdate(sql, params);
	}
	
	public class Hotel{
		private String id;
		private String name;
		private String lat;
		private String lng;
		private String summary;
		private String imgSrc;
		private String address;
		private String otaname;//预订的网站，如：携程网等
		private String price;
		private String bookUrl;
		private String cityId;
		private String cityName;
		
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
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
		public String getCityId() {
			return cityId;
		}
		public void setCityId(String cityId) {
			this.cityId = cityId;
		}
		public String getCityName() {
			return cityName;
		}
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}
		
		
		
		
	}
	
	
	public static void main(String[] args) throws IOException{
		MfwHotelParser model = new MfwHotelParser();
		model.loadCity("./Seed/mfw-city-id.txt");
//		model.parse(new File("E:\\hotel\\10088-1.txt"));
		model.runTask("E:\\traveldata\\hotel");
	}
}
