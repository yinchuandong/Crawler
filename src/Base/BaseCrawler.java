package Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import Model.WebPage;
import Util.PageUtil;

public abstract class BaseCrawler {

	private final static int TASK_NUM = 10;
	private HashMap<String, Integer> urlDeeps = new HashMap<String, Integer>();//链接的深度
	private ArrayList<String> waitList =  new ArrayList<String>();//等待的队列
	private ExecutorService taskPool = Executors.newCachedThreadPool();
	private String charset = "utf-8";
	private String domain = "";
	private int crawlerDeeps = 2;
	public BaseCrawler(){
		setDomain("http://lvyou.baidu.com");
		loadSeedsFromFile();
		process();
//		System.out.println(PageUtil.parseDomain("http://www.oschina.net/p/crawler4j"));
//		test();
	}
	
	private void process(){
//		waitList.add("http://www.baidu.com");
//		waitList.add("http://www.baidu.com/lv");
//		waitList.add("http://www.baidu.com/tao");
		for(int i=0; i<TASK_NUM; i++){
			taskPool.execute(new ProcessThread());
		}
		taskPool.shutdown();
	}
	
	public synchronized String popList(){
		String temp = waitList.get(0);
		waitList.remove(0);
		return temp;
	}
	
	public synchronized void addLink(String url){
		waitList.add(url);
	}
	
	private void loadSeedsFromFile(){
		try {
			File file = new File("Seed/seeds.txt");
			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String msg = null;
			while((msg = reader.readLine()) != null){
				waitList.add(msg);
				urlDeeps.put(msg, 1);//种子的深度为1
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 执行完爬虫之后的回调函数
	 * @param webPage
	 */
	public abstract void exactor(WebPage webPage);
	/**
	 * 设置url的过滤规则
	 * @param url
	 * @return
	 */
	public abstract boolean isAllowVisit(URL url);
	/**
	 * 设置字符集
	 * @param charset
	 */
	public void setCharset(String charset){
		this.charset = charset;
	}
	
	/**
	 * 设置域名
	 * @param domain
	 */
	public void setDomain(String domain){
		this.domain = domain;
	}
	
	/**
	 * 设置爬虫的深度，默认为2
	 * @param deeps
	 */
	public void setCrawlerDeeps(int deeps){
		if(deeps >= 0){
			this.crawlerDeeps = deeps;
		}
	}
	
	public void test(){
		String test = "<a href=\"/pictravel1/\" class=\"nav-link nslog\"> \r\n" +
				"<a href=\"/pictravel2/\" class=\"nav-link nslog\">";
		Pattern pattern = Pattern.compile("href=[\"|\']([^#]*?)[\"|\']",Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(test);
		while(matcher.find()) {
			System.out.println(matcher.group(1));
		}
	}
	
	/**
	 * 从页面中提取所有的url，并记录其deepth
	 * @param sourceUrl
	 * @param pageContent
	 */
	public synchronized void getAllUrlFromPage(String sourceUrl, String pageContent){
		Pattern pattern = Pattern.compile("href=[\"|\']([^#]*?)[\"|\']",Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(pageContent);
		while(matcher.find()) {
			String key = matcher.group(1);//匹配出url
			if (PageUtil.parseDomain(key) == null) {
				if(key.startsWith("/")){
					key = this.domain + key;
				}else{
					key = this.domain + "/" + key;
				}
			}
			if(!urlDeeps.containsKey(key)){
				urlDeeps.put(key, urlDeeps.get(sourceUrl) + 1);
				waitList.add(key);
				System.out.println(key);
			}
		}
	}
	
	
	/**
	 * 把inputstream转为string类型
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private String parse(InputStream inputStream) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String result = "";
		String msg = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,charset));
		while((msg = reader.readLine()) != null ){
			buffer.append(msg + "\r\n");
		}
		result = buffer.toString();
		return result;
	}
	
	
	public class ProcessThread implements Runnable{

		@Override
		public void run() {
			while(!waitList.isEmpty()){
				String urlstr = popList();
				try {
					URL url = new URL(urlstr);
					if(isAllowVisit(url)){
						if(urlDeeps.get(urlstr) <= crawlerDeeps){
							URLConnection conn = url.openConnection();
							InputStream input = conn.getInputStream();
							String pageContent = parse(input);
							getAllUrlFromPage(urlstr, pageContent);
							WebPage page = new WebPage(pageContent, url, urlDeeps.get(urlstr));
							String filename = PageUtil.getFileNameByUrl(urlstr);
							PageUtil.exportFile("web/"+filename+".txt", pageContent);
							exactor(page);
						}
					}
					System.out.println("正在处理："+urlstr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
