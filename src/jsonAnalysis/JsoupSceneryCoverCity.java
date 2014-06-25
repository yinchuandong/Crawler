package jsonAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Remote;

import net.sf.json.JSONObject;

import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;

import net.sf.json.JSONArray;

public class JsoupSceneryCoverCity {

	public static void main(String[] args) {
		File directory=new File("E:\\CoverHtml");
		File[] files=directory.listFiles();
		
		for (File file : files) {
			try {
				if (!file.isFile()) // 判断是不是文件
					continue;
				Document document=Jsoup.parse(file, "UTF-8");
				JSONArray mainArray=JSONArray.fromObject("[]");
				String name2=file.getName();
				String nameString=name2.split("_")[1];
				Elements fiture = document.select(".scene-slide-holder figure");
				for (int i=0;i<fiture.size();i++) {
					Element element=fiture.get(i);
					JSONObject mainObject=JSONObject.fromObject("{}");
					Elements img = element.select(".center-pic-link meta");
					String imageString=img.attr("content");
					Elements tElements=element.select(".scene-slide-desc p.title");
					Element title=null;
					if (tElements!=null&&tElements.hasText()) {
						title=element.select(".scene-slide-desc p.title").first();
						mainObject.put("imgtitle",title.text().toString());
					}
					else {
						mainObject.put("imgtitle","");
					}
					Elements dElements=element.select(".scene-slide-desc p.ib");
					Element desc=null;
					if (dElements!=null && dElements.hasText()) {
						desc=element.select(".scene-slide-desc p.ib").first();
						mainObject.put("imgdescription", desc.text().toString());
					}
					else {
						mainObject.put("imgdescription", "");
					}
					mainObject.put("name", nameString);
//					if (img.hasText()) {
						mainObject.put("img", imageString);
//					}
//					else {
//						mainObject.put("img", "");
//					}
//					mainArray.put("main", mainObject);
					mainArray.add(mainObject);
//					System.out.println(title.text().toString()+desc.text().toString()+img.attr("content"));
				}
//				System.out.println(mainArray.getString(1));
				JSONArray recommendArray=JSONArray.fromObject("[]");
				Elements recommend=document.select("ul.unmissable-list li a");
				for (int i=0;i<recommend.size();i++) {
					JSONObject recommendObject=new JSONObject();
					Element element=recommend.get(i);
					Element r_title=element.select("span.unmissable-desc-tit").first();
					Element r_desc=null;
					Elements des = element.select("span.unmissable-desc-con");
					if (des!=null && des.hasText()) {
						 r_desc=element.select("span.unmissable-desc-con").first();
						 recommendObject.put("r_desc", r_desc.text().toString());
					}
					else {
						recommendObject.put("r_desc","");
					}
					Element a=element.select("img").get(0);
					String r_img=a.attr("src").toString();
					
					if (r_title.hasText()) {
						recommendObject.put("r_name", r_title.text().toString());
					}
					else {
						recommendObject.put("r_name", "");
					}
					
					if (!r_img.isEmpty()) {
						recommendObject.put("r_img", r_img);
					}
					else {
						recommendObject.put("r_img", "");
					}
					recommendArray.add(recommendObject);
//					System.out.println(r_title.text().toString()+r_desc.text().toString()+r_img);
				}
				JSONObject allObject=new JSONObject();
				allObject.put("errno", 0);
				allObject.put("msg","");
				allObject.put("main", mainArray);
				allObject.put("recommendation", recommendArray);
				System.out.println(mainArray.toString());
				String allString=allObject.toString();
//				System.out.println(allString);
				File jsonfile=new File("E:\\Courseware\\senior3_2\\Datamining for travel\\Crawler\\json\\3\\"+nameString+".json");
				FileWriter fw = null;
				try {
				    if(!jsonfile.exists()){
				    	jsonfile.createNewFile();
				    }
				    fw = new FileWriter(jsonfile);
				    BufferedWriter out = new BufferedWriter(fw);
				    out.write(allString);
				    out.flush();
				    out.close();
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
//				Elements recommend=document.select("ul.unmissable-list li");
//				for (int i=0;i<recommend.size()-1;i++) {
//					Element element=recommend.get(i);
//					Element title=element.select("span.unmissable-desc-tit").get(0);
//					System.out.println(title.text().toString());
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}



}
