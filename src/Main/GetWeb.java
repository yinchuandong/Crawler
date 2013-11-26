package Main;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Hashtable;
public class GetWeb {
private int webDepth = 2;//爬虫深度
private int intThreadNum = 10;//线程数
private String strHomePage = "";//主页地址
private String myDomain;//域名
private String fPath = "web";//储存网页文件的目录名
private ArrayList<String> arrUrls = new ArrayList<String>();//存储未处理URL
private ArrayList<String> arrUrl = new ArrayList<String>();//存储所有URL供建立索引
private Hashtable<String,Integer> allUrls = new Hashtable<String,Integer>();//存储所有URL的网页号
private Hashtable<String,Integer> deepUrls = new Hashtable<String,Integer>();//存储所有URL深度
private int intWebIndex = 0;//网页对应文件下标，从0开始
private String charset = "GB2312";
private String report = "";
private long startTime;
private int webSuccessed = 0;
private int webFailed = 0;


public GetWeb(String s)
{
   this.strHomePage = s;
}
/**
 * 定义深度和主页
 * @param s
 * @param i
 */
public GetWeb(String s,int i)
{
   this.strHomePage = s;
   this.webDepth = i;
}

public synchronized void addWebSuccessed()
{
   webSuccessed++;
}
public synchronized void addWebFailed()
{
   webFailed++;
}
/**
 * 将结果记录在d://reprot.txt
 * @param s
 */
public synchronized void addReport(String s)
{
   try
   {
    report += s;
    PrintWriter pwReport = new PrintWriter(new FileOutputStream("d://report.txt"));
    pwReport.println(report);
    pwReport.close();
   }
   catch(Exception e)
   {
    System.out.println("生成报告文件失败!");
   }
}
//依次取出list中的元素，线程安全
public synchronized String getAUrl()
{
   String tmpAUrl = arrUrls.get(0);
   arrUrls.remove(0);
   return tmpAUrl;
}
//依次取出list中的元素，线程安全
public synchronized String getUrl()
{
   String tmpUrl = arrUrl.get(0);
   arrUrl.remove(0);
   return tmpUrl;
}
public synchronized Integer getIntWebIndex()
{
   intWebIndex++;
   return intWebIndex;
}
/**
* @param args
*/
public static void main(String[] args)
{
	GetWeb gw = new GetWeb("http://lvyou.baidu.com/shenzhen/jingdian/");
	gw.getWebByHomePage();
//	//输入为空时，打印出No input
//   if (args.length == 0 || args[0].equals(""))
//   {
//    System.out.println("No input!");
//    System.exit(1);
//   }
//   //输入不为空且只有一个URL时判读输入的是否为正确的URL
//   else if(args.length == 1)
//   {
//	   //八输入的URL定义为主业
//    GetWeb gw = new GetWeb(args[0]);
//    //判断URL是否正确
//    gw.getWebByHomePage();
//   }
//   else
//    {
//    GetWeb gw = new GetWeb(args[0],Integer.parseInt(args[1]));
//    gw.getWebByHomePage();
//   }
}
public void getWebByHomePage()
{
   startTime = System.currentTimeMillis();
   this.myDomain = getDomain();
   if (myDomain == null)
   {
    System.out.println("Wrong input!");
    //System.exit(1);
   }
   //打印出主页
   System.out.println("Homepage = " + strHomePage);
   addReport("Homepage = " + strHomePage + "!\n");
   System.out.println("Domain = " + myDomain);
   addReport("Domain = " + myDomain + "!\n");
   //将URL存储到List中来处理
   arrUrls.add(strHomePage);
   arrUrl.add(strHomePage);
   allUrls.put(strHomePage,0);
   deepUrls.put(strHomePage,1);
   //判断存储网页的web文件是否存在，如果不存在就创建一个web目录
   File fDir = new File(fPath);
        if(!fDir.exists())
        {
        fDir.mkdir();
        }
   System.out.println("Start!");
   this.addReport("Start!\n");
   String tmp = getAUrl();
   //输出从URL连接流中得到的网页地址
   this.getWebByUrl(tmp,charset,allUrls.get(tmp)+"");
   int i = 0;
   //每次执行都为该当前对象创建一个线程
   for (i=0;i<intThreadNum;i++)
   {
    new Thread(new Processer(this)).start();
   }
   //向report.txt中记录信息
   while (true)
   {
    if(arrUrls.isEmpty() && Thread.activeCount() == 1)
    {
     long finishTime = System.currentTimeMillis();
     long costTime = finishTime-startTime;
     System.out.println("\n\n\n\n\nFinished!");
     addReport("\n\n\n\n\nFinished!\n");
     System.out.println("Start time = " + startTime + "   " + "Finish time = " + finishTime + "   " + "Cost time = " + costTime + "ms");
     addReport("Start time = " + startTime + "   " + "Finish time = " + finishTime + "   " + "Cost time = " + costTime + "ms" + "\n");
     System.out.println("Total url number = " + (webSuccessed+webFailed) + "   Successed: " + webSuccessed + "   Failed: " + webFailed);
     addReport("Total url number = " + (webSuccessed+webFailed) + "   Successed: " + webSuccessed + "   Failed: " + webFailed + "\n");
    
     String strIndex = "";
     String tmpUrl = "";
     while (!arrUrl.isEmpty())
     {
      tmpUrl = getUrl();
      strIndex += "Web depth:" + deepUrls.get(tmpUrl) + "   Filepath: " + fPath + "/web" + allUrls.get(tmpUrl) + ".htm" + "   url:" + tmpUrl + "\n\n";
     }
     System.out.println(strIndex);
     try
     {
      PrintWriter pwIndex = new PrintWriter(new FileOutputStream("fileindex.txt"));
      pwIndex.println(strIndex);
      pwIndex.close();
     }
     catch(Exception e)
     {
      System.out.println("生成索引文件失败!");
     }
     break;
    }
   }
}
/**
 * 将List中的每个URL目录下 的信息打印出
 * @param strUrl
 * @param charset
 * @param fileIndex
 */
public void getWebByUrl(String strUrl,String charset,String fileIndex)
{
   try
   {
	   //字符转码为utf8
    if(charset==null||"".equals(charset))charset="utf-8";
    System.out.println("Getting web by url: " + strUrl);
    addReport("Getting web by url: " + strUrl + "\n");
    URL url = new URL(strUrl);
    //与URL建立连接
    URLConnection conn = url.openConnection();
    //定义连接为可输出
    conn.setDoOutput(true);
    //得到此连接的输入
    InputStream is = null;
    is = url.openStream();
   
    String filePath = fPath + "/web" + fileIndex + ".htm";
   // System.out.println(filePath+"1111111111111111111111111");
    PrintWriter pw = null;
    FileOutputStream fos = new FileOutputStream(filePath);
    OutputStreamWriter writer = new OutputStreamWriter(fos);
    pw = new PrintWriter(writer);
    BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
    StringBuffer sb = new StringBuffer();
    String rLine = null;
    String tmp_rLine = null;
    //逐行输入输入流中的信息并将其输出到目录中去--在目录下建立名为web/webn.html的文件
    while ( (rLine = bReader.readLine()) != null)
    {
     tmp_rLine = rLine;
     int str_len = tmp_rLine.length();
     if (str_len > 0)
     {
      sb.append("\n" + tmp_rLine);
      pw.println(tmp_rLine);
      pw.flush();
      //如果爬虫深度大于网页深度，则网页深度+1，再同上
      if (deepUrls.get(strUrl) < webDepth)getUrlByString(tmp_rLine,strUrl);
     }
     tmp_rLine = null;
    }
    is.close();
    pw.close();
    System.out.println("Get web successfully! " + strUrl);
    addReport("Get web successfully! " + strUrl + "\n");
    addWebSuccessed();
   }
   catch (Exception e)
   {
    System.out.println("Get web failed!       " + strUrl);
    addReport("Get web failed!       " + strUrl + "\n");
    addWebFailed();
   }
}
/**
 * 判断URL是否合法，如果合法返回URL，如果不则返回null
 * @return
 */
public String getDomain()
{	
	//URL 的正则表达式
   String reg = "(?<=http\\://[a-zA-Z0-9]{0,100}[.]{0,1})[^.\\s]*?\\.(com|cn|net|org|biz|info|cc|tv)";
   Pattern p = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
   Matcher m = p.matcher(strHomePage);
   //判断输入的字符串是否包含定义的正则表达式
   boolean blnp = m.find();
   if (blnp == true)
   {
	   //返回匹配原字符串
    return m.group(0);
   }
   return null;
}

public void getUrlByString(String inputArgs,String strUrl)
{
   String tmpStr = inputArgs;
   String regUrl = "(?<=(href=)[\"]?[\']?)[http://][^\\s\"\'\\?]*(" + myDomain + ")[^\\s\"\'>]*";
   Pattern p = Pattern.compile(regUrl,Pattern.CASE_INSENSITIVE);
   Matcher m = p.matcher(tmpStr);
   boolean blnp = m.find();
   //int i = 0;
   while (blnp == true)
   {
    if (!allUrls.containsKey(m.group(0)))
    {
     System.out.println("Find a new url,depth:" + (deepUrls.get(strUrl)+1) + " "+ m.group(0));
     addReport("Find a new url,depth:" + (deepUrls.get(strUrl)+1) + " "+ m.group(0) + "\n");
     arrUrls.add(m.group(0));
     arrUrl.add(m.group(0));
     allUrls.put(m.group(0),getIntWebIndex());
     deepUrls.put(m.group(0),(deepUrls.get(strUrl)+1));
    }
    tmpStr = tmpStr.substring(m.end(),tmpStr.length());
    m = p.matcher(tmpStr);
    blnp = m.find();
   }
}
/**
 * 为当前的URL建立线程
 * @author xqh
 *
 */
class Processer implements Runnable
{
     GetWeb gw;
     public Processer(GetWeb g)
     {
         this.gw = g;
     }
     public void run()
     {
       //Thread.sleep(5000);
       while (!arrUrls.isEmpty())
       {
        String tmp = getAUrl();
        getWebByUrl(tmp,charset,allUrls.get(tmp)+"");
       }
     }
}
}