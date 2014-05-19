package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import Model.WebPage;
import Util.AppUtil;
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
	
	//-------------------------------------------
	public Baidu(){
		super();
		this.setDomain("http://lvyou.baidu.com");
//		this.loadSeedsFromFile("Seed/baidu.txt");
		this.init();
	}
	
	private void init(){
		String url = this.generateUrl("yangjiang", 1);
		super.addWaitList(url, 4);
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
		url  += "surl=" + surl+ "&cid=1&pn=" + page +"&t=" + System.currentTimeMillis();
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
			
			ConcurrentHashMap<String, Integer> urlDeeps = super.getUrlDeeps();
			//取得页数
			int pageNums = (int) Math.ceil((double)sceneTotal / listRows);
			for(int i=currentPage+1; i<=pageNums; i++){
				String tmpUrl = this.generateUrl(surl, i);
				//如果该url没有被访问过，则添加到未访问列表中
				if (!urlDeeps.containsKey(AppUtil.md5(tmpUrl))) {
					super.addWaitList(tmpUrl, Integer.parseInt(sceneLayer));
				}
			}
			
			//-------解析景点列表-----------
			JSONArray sceneList = dataObj.getJSONArray("scene_list");
			parseSceneList(sceneList);
			
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
		ConcurrentHashMap<String, Integer> urlDeeps = super.getUrlDeeps();
		for(int i=0; i<sceneList.size(); i++){
			JSONObject sceneObj = sceneList.getJSONObject(i);
			String sid = sceneObj.getString("sid");
			String surl = sceneObj.getString("surl");
			String sname = sceneObj.getString("sname");
			String sceneLayer = sceneObj.getString("scene_layer");
			
			String crawlUrl = this.generateUrl(surl, 1);
			//如果该url没有被访问过，则添加到未访问列表中
			if (!urlDeeps.containsKey(AppUtil.md5(crawlUrl))) {
				super.addWaitList(crawlUrl, Integer.parseInt(sceneLayer));
			}
			System.out.println(sid);
			System.out.println(surl);
			System.out.println(sname);
			System.out.println(generateUrl(surl, 1, 1));
			System.out.println("------------------------------");
		}
		
	}
	
	

}
