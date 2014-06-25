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

public class JsoupSceneryCover {

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
				Elements fiture = document.select(".scene-transition-slide-holder figure img");
				for (int i=0;i<fiture.size();i++) {
					Element element=fiture.get(i);
					String imgString=element.attr("src");
					JSONObject mainObject=JSONObject.fromObject("{}");
					mainArray.add(imgString);
			    }
					JSONObject allObject=new JSONObject();
					allObject.put("errno", 0);
					allObject.put("msg","");
					allObject.put("main", mainArray);
					System.out.println(mainArray.toString());
					String allString=allObject.toString();
					File jsonfile=new File("E:\\Courseware\\senior3_2\\Datamining for travel\\Crawler\\json\\4\\"+nameString+".json");
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
