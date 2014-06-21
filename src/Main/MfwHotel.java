package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.jws.WebParam.Mode;
import javax.print.DocFlavor.STRING;

import Base.BaseCrawler;
import Model.WebPage;
import Util.AppUtil;
import Util.DbUtil;
import Util.PageUtil;
import Util.UrlParmUtil;

public class MfwHotel extends BaseCrawler{
	
	public MfwHotel(){
		super();
		init();
	}
	
	private void init(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("./Seed/mfw-city-id-page.txt")));
			String buff = null;
			while((buff = reader.readLine()) != null){
				String[] arr = buff.split("\\s");
				int pageNum =  Integer.parseInt(arr[2]);
				for (int i = 1; i <= pageNum; i++) {
					String url = this.generateUrl(arr[1], i);
					super.addWaitList(url);
					super.addUnVisitPath(arr[1] + "-" + i);
					System.out.println(url);
				}
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
	public String generateUrl(String cid, int page){
		String url = "http://www.mafengwo.cn/hotel/ajax.php?sAction=getPoiList2&iMddId=";
		url += cid + "&sKeyWord=&sCheckIn=2014-07-29&sCheckOut=2014-07-30&iPage="+page+"&sTags=&iPriceMin=0&iPriceMax=&sSortType=comment&sSortFlag=DESC";
		return url;
	}

	@Override
	public void exactor(WebPage webPage) {
		// TODO Auto-generated method stub
		String content = webPage.getPageContent();
//		String filename = webPage.getUrl();
		HashMap<String, String> params = UrlParmUtil.parseUrl(webPage.getUrl().toString());
		String cityId = params.get("iMddId");
		String page = params.get("iPage");
		
		//标记为已经访问
		super.visitUrl(cityId + "-" + page);
		
		String filename = cityId + "-" + page + ".txt";
		PageUtil.exportFile("E:\\hotel\\" + filename, content);
		System.out.println(filename + " : " + content);
		
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
	
	public static void main(String[] args){
		MfwHotel model = new MfwHotel();
		model.begin();
	}

}
