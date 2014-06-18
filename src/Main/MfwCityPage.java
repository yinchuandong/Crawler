package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 通过城市的id获得城市的酒店pageNum
 * @author yinchuandong
 *
 */
public class MfwCityPage {
	
	private LinkedList<City> cityList;
	
	public MfwCityPage(){
		cityList = new LinkedList<City>();
	}
	
	/**
	 * 载入数据 如：广州 10088
	 * @param fileName
	 * @throws IOException
	 */
	public void loadData(String fileName) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String buff = "";
		while((buff = reader.readLine()) != null){
			String[] arr = buff.split("\\s");
			String cityName = arr[0];
			String cityId = arr[1];
			City city = new City();
			city.setId(cityId);
			city.setName(cityName);
			cityList.add(city);
		}
		reader.close();
	}
	
	public void runTask(final String fileName){
		new Thread(){
			@Override
			public void run(){
				try {
					PrintWriter writer = new PrintWriter(new File(fileName));
					while(cityList.size() != 0){
						City city = cityList.poll();
						String pageNum = getPageNum(city.getId());
						if (pageNum.equals("")) {
							System.out.println("错误：----" + city.getName() + " " + pageNum);
							continue;
						}
						String buff = city.getName() + " " + city.getId() + " " + pageNum + "\r\n";
						System.out.print(buff);
						writer.write(buff);
						writer.flush();
						Thread.sleep(500);
					}
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public String getPageNum(String cityId){
		String url = "http://www.mafengwo.cn/hotel/" + cityId + "/";
		String pageNum = "";
		try {
			Document document = Jsoup.connect(url).timeout(50000).get();
			Elements spans = document.select("#list_paginator .count span");
//			System.out.println(spans.get(0).text());
			if (spans.size() == 0) {
				pageNum = "1";
			}else{
				pageNum = spans.get(0).text();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pageNum;
	}
	
	/**
	 * 城市的实体
	 * @author yinchuandong
	 *
	 */
	public class City{
		private String name;
		private String id;
		private String pageNum;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPageNum() {
			return pageNum;
		}
		public void setPageNum(String pageNum) {
			this.pageNum = pageNum;
		}
		
		
	}
	
	public static void main(String[] args) throws IOException{
		MfwCityPage model = new MfwCityPage();
		model.loadData("./Seed/mfw-city-id.txt");
		model.runTask("./Seed/mfw-city-id-page.txt");
	}

}
