package Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import Model.WebPage;
import Util.AppUtil;
import Util.HttpUtil;
import Util.PageUtil;

/**
 * 爬取通用html页面的基类
 * @author yinchuandong
 *
 */
public abstract class BaseCrawler {

	private final static int TASK_NUM = 10;
	/**
	 * 保存爬过的Url和深度，key是url的md5值，value是深度值
	 */
	private ConcurrentHashMap<String, Integer> urlDeeps;
	/**
	 * 等待的队列
	 */
	private LinkedList<String> waitList;
	/**
	 * 线程池
	 */
	private ExecutorService taskPool;
	/**
	 * 网页的字符编码
	 */
	private String charset = "utf-8";
	/**
	 * 网页的域名，如：http://lvyou.baidu.com
	 */
	private String domain = "";
	/**
	 * 爬虫最大的深度
	 */
	private int crawlerDeeps = 2;
	/**
	 * 延时时间
	 */
	private int delay = 500;
	public BaseCrawler(){
		urlDeeps = new ConcurrentHashMap<String, Integer>();
		waitList =  new LinkedList<String>();
		taskPool = Executors.newCachedThreadPool();
	}
	
	/**
	 * 开始爬取，由外部调用
	 */
	public void begin(){
		taskPool.shutdown();
		new Thread(){
			@Override
			public void run(){
				while(!waitList.isEmpty()){
					String url = popWaitList();
					taskPool.execute(new ProcessThread(url));
				}
			}
		}.start();
	}
	
	/**
	 * 将waitList的头结点弹出
	 * @return
	 */
	public synchronized String popWaitList(){
		String temp = waitList.poll();
		return temp;
	}
	
	/**
	 * 添加一个url到waitList
	 * @param url
	 * @param deeps
	 */
	public synchronized void addWaitList(String url, int deeps){
		waitList.offer(url);
		String key = AppUtil.md5(url);
		urlDeeps.put(key, deeps);
	}
	
	
	/**
	 * 获得已经爬取过的url深度列表，key是url的md5值，value是深度值
	 * @return
	 */
	public ConcurrentHashMap<String, Integer> getUrlDeeps(){
		return this.urlDeeps;
	}
	
	/**
	 * 添加爬虫起始种子
	 * @param path
	 */
	protected void loadSeedsFromFile(String path){
		try {
			File file = new File(path);
			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String msg = null;
			while((msg = reader.readLine()) != null){
				waitList.add(msg);
				urlDeeps.put(AppUtil.md5(msg), 1);//种子的深度为1
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
	 * 设置字符集
	 * @param charset
	 */
	public void setCharset(String charset){
		this.charset = charset;
	}
	
	/**
	 * 设置域名, 解决相对地址问题
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
	
	/**
	
	
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
	
	
	/**
	 * 具体爬取的线程
	 * @author yinchuandong
	 *
	 */
	public class ProcessThread implements Runnable{

		private String url;
		public ProcessThread(String url){
			this.url = url;
		}
		
		@Override
		public void run() {
			HttpUtil httpUtil = new HttpUtil();
			httpUtil.setCharset(charset);
			String pageContent = httpUtil.get(url);
			WebPage webPage;
			try {
				webPage = new WebPage(pageContent, new URL(url), urlDeeps.get(AppUtil.md5(url)));
				exactor(webPage);
			} catch (MalformedURLException e) {
				exactor(null);
				e.printStackTrace();
			}
		}
	}
	
}
