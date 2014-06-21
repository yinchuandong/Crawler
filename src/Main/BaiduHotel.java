package Main;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import Base.BaseCrawler;
import Model.WebPage;
import Util.DbUtil;
import Util.PageUtil;
import Util.UrlParmUtil;

public class BaiduHotel extends BaseCrawler{
	

	public BaiduHotel(){
		init();
	}
	
	private void init(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("./Seed/baidu-scenery.txt")));
			String buff = null;
			while((buff = reader.readLine()) != null){
				String[] arr = buff.split("\\s");
				String sid = arr[0];
				double mapX = Double.parseDouble(arr[1]);
				double mapY = Double.parseDouble(arr[2]);
				String url = this.generateUrl(sid, mapX, mapY);
				String uniqueKey = sid + "-" + mapX + "-" + mapY;
				this.addUnVisitPath(uniqueKey);
				this.addWaitList(url);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成爬取的Url
	 * @param cid city的Id 如：10088
	 * @param page
	 * @return
	 */
	public String generateUrl(String sid, double mapX, double mapY){
		double r = 2000;
		double left = mapX - r;
		double right = mapX + r;
		double top = mapY - r;
		double bottom = mapY + r;
		String url = "http://lvyou.baidu.com/business/ajax/hotel/searcharound?sid=" +
				sid + "&wd=%E9%85%92%E5%BA%97&is_detail=0&nb_x=" +
				mapX + "&nb_y=" + mapY + "&r=" + r + "&b=(" + left + "," + top + ";" + right + "," + bottom + ")";
		return url;
	}

	@Override
	public void exactor(WebPage webPage) {
		HashMap<String, String> params = UrlParmUtil.parseUrl(webPage.getUrl().toString());
		String sid = params.get("sid");
		double mapX = Double.parseDouble(params.get("nb_x"));
		double mapY = Double.parseDouble(params.get("nb_y"));
		String uniqueKey = sid + "-" + mapX + "-" + mapY;
		this.visitUrl(uniqueKey);
		
		File dir = new File("E:\\traveldata\\baidu-hotel");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String path = "E:\\traveldata\\baidu-hotel\\" + sid + ".txt";
		PageUtil.exportFile(path, webPage.getPageContent());
	}

	@Override
	public void loadWaitList() {
		ResultSet resultSet = DbUtil.executeQuery("select sname from t_crawled where isVisited=?", new String[]{"0"});
		try {
			while(resultSet.next()){
				String sname = resultSet.getString("sname");
				String[] arr =sname.split("-");
				String sid = arr[0];
				double mapX = Double.parseDouble(arr[1]);
				double mapY = Double.parseDouble(arr[2]);
				String url = this.generateUrl(sid, mapX, mapY);
				String uniqueKey = sid + "-" + mapX + "-" + mapY;
				this.addUnVisitPath(uniqueKey);
				this.addWaitList(url);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		BaiduHotel hotel = new BaiduHotel();
		hotel.begin();
	}
	
	/**
	 * 从数据库中选择景点的大地坐标信息，作为种子
	 */
	public static void createSeeds(){
		String sql = "SELECT s.sid,s.map_x,s.map_y from t_scenery as s WHERE s.scene_layer=?";
		String[] params = {"6"};
		ResultSet resultSet = DbUtil.executeQuery(sql, params);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("./Seeds/baidu-scenery.txt"));
			while(resultSet.next()){
				String sid = resultSet.getString("sid");
				String mapX = resultSet.getString("map_x");
				String mapY = resultSet.getString("map_y");
				String buff = sid + " " + mapX + " " + mapY + "\r\n";
				writer.write(buff);
				writer.flush();
				System.out.print(buff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (writer != null) {
				writer.close();
			}
		}
	}

}
