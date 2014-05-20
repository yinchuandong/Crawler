package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import Model.WebPage;
import Util.AppUtil;
import Util.DbUtil;
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
	
	/**
	 * 每一页景点的数量
	 */
	private int listRows = 16;
	/**
	 * 当前的时间戳
	 */
	private long timestamp ;
	
	//-------------------------------------------
	public Baidu(){
		super();
		this.timestamp = System.currentTimeMillis();
		this.setDomain("http://lvyou.baidu.com");
		this.init();
	}
	
	private void init(){
		String url = this.generateUrl("guangzhou", 1);
		super.addWaitList(url);
		
		String uniqueKey = "guangzhou-1";
		super.addUnVisitPath(uniqueKey);
		
	}
	
	/**
	 * 生成爬取的Url
	 * @param surl city的surl;如番禺 panyu
	 * @param cid city的Id
	 * @param page
	 * @return
	 */
	public String generateUrl(String surl, int cid, int page){
		String url = "http://lvyou.baidu.com/destination/ajax/jingdian?format=ajax&";
		url  += "surl=" + surl+ "&cid=" + cid + "&pn=" + page + "&t=" + this.timestamp;
		return url;
	}
	
	/**
	 * 生成爬取的Url
	 * @param surl
	 * @param page
	 * @return
	 */
	public String generateUrl(String surl, int page){
		return this.generateUrl(surl, 1, page);
	}
	
	@Override
	public void loadWaitList() {
		ResultSet resultSet = DbUtil.executeQuery("select sname from t_crawled where isVisited=?", new String[]{"0"});
		try {
			while(resultSet.next()){
				String sname = resultSet.getString("sname");
				String[] arr =sname.split("-");
				if (arr.length >=2 ) {
					String surl = arr[0];
					int page = Integer.parseInt(arr[1]);
					String url = generateUrl(surl, page);
					addWaitList(url);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void exactor(WebPage page) {
		if (page == null) {
			System.err.println("exactor爬取信息为空");
			return;
		}
		String result = page.getPageContent();
		try {
			result = AppUtil.jsonFormatter(result);
			JSONObject jsonObj = JSONObject.fromObject(result); 
			JSONObject dataObj = jsonObj.getJSONObject("data");
			//----需要保存的数据库字段--------------------
			String sid = dataObj.getString("sid");
			String surl = dataObj.getString("surl");
			String sname = dataObj.getString("sname");
			String ambiguitySname = dataObj.getString("ambiguity_sname");
			String parentSid = dataObj.getString("parent_sid");
			String viewCount = dataObj.getString("view_count");
			String star = dataObj.getString("star");
			String sceneLayer = dataObj.getString("scene_layer");
			int goingCount = dataObj.getInt("going_count");
			int goneCount = dataObj.getInt("gone_count");
			double rating = dataObj.getDouble("rating");
			int ratingCount = dataObj.getInt("rating_count");
			JSONObject extObj = dataObj.getJSONObject("ext");
			String mapInfo = extObj.getString("map_info");//获得经纬度
			
			//------用于判断分页，构造url------------------
			int sceneTotal = dataObj.getInt("scene_total");
			int currentPage = dataObj.getInt("current_page");
			
			//--------------将url标记为已经访问----------------------------
			super.visitUrl(surl + "-" + currentPage);
			
			//取得页数
			int pageNums = (int) Math.ceil((double)sceneTotal / listRows);
			for(int i=currentPage+1; i<=pageNums; i++){
				//如果该url没有被访问过，则添加到未访问列表中
				String uniqueKey = surl + "-" + i;
				String uniqueSid = AppUtil.md5(uniqueKey);
				int count = DbUtil.count("select count(*) from t_crawled where sid=?", new String[]{uniqueSid});
				if (count < 1) {
					//添加到等待队列
					String tmpUrl = this.generateUrl(surl, i);
					addWaitList(tmpUrl);
					addUnVisitPath(uniqueKey);
				}
			}
			
			//-------解析景点列表-----------
			JSONArray sceneList = dataObj.getJSONArray("scene_list");
			this.parseSceneList(sceneList);
			
			//---------将json文件保存下来-------------------
			String filename = surl + "-" + currentPage + ".json";
			PageUtil.exportFile("E:\\web\\" + filename, AppUtil.jsonFormatter(result));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析景点列表，及json的scene_list部分
	 * @param sceneList
	 */
	private void parseSceneList(JSONArray sceneList){
		for(int i=0; i<sceneList.size(); i++){
			JSONObject sceneObj = sceneList.getJSONObject(i);
			String sid = sceneObj.getString("sid");
			String surl = sceneObj.getString("surl");
			String sname = sceneObj.getString("sname");
			String sceneLayer = sceneObj.getString("scene_layer");
			
			//如果该url没有被访问过，则添加到未访问列表中
			String uniqueKey = surl + "-" + 1;
			String uniqueSid = AppUtil.md5(uniqueKey);
			int count = DbUtil.count("select count(*) from t_crawled where sid=?", new String[]{uniqueSid});
			if (count < 1) {
				//添加到等待队列
				String tmpUrl = this.generateUrl(surl, 1);
				addWaitList(tmpUrl);
				addUnVisitPath(uniqueKey);
			}
			
			System.out.println(sid);
			System.out.println(surl);
			System.out.println(sname);
			System.out.println("------------------------------");
		}
		
	}

}
