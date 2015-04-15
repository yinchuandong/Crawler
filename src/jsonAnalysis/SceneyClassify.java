package jsonAnalysis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SceneyClassify {

	//广 {广东，10}{广州，11}
	HashMap<String,HashMap<String, Integer>> allMap = new HashMap<String, HashMap<String,Integer>>();
	HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
	HashMap<String, Integer> tmpMap = null;
	@SuppressWarnings("null")
	public void test(){
		java.sql.ResultSet rSet=DbJson.executeQuery("SELECT sname,view_count from t_scenery ORDER BY ?", new String[]{"view_count"});
		try {
			while(rSet.next())
			{		
				String sname= rSet.getString("sname");
				int viewCount = rSet.getInt("view_count");
				String prefix = sname.substring(0,1);
				
				if (!allMap.containsKey(prefix)) {
					tmpMap = new HashMap<String, Integer>();
					tmpMap.put(sname, viewCount);
					allMap.put(prefix, tmpMap);
				}else{
					tmpMap = allMap.get(prefix);
					if (tmpMap.containsKey(sname)) {
						int curCount = tmpMap.get(sname);
						if(curCount < viewCount){
							tmpMap.put(sname, viewCount);
						}
					}else{
						tmpMap.put(sname, viewCount);
					}
				}	
			}
			java.sql.ResultSet rSet1=DbJson.executeQuery("SELECT ambiguity_sname,view_count from t_scenery where sname!=? ", new String[]{"ambiguity_sname"});
			try {
				while(rSet1.next())
				{	
					String ambiguitysname= rSet1.getString("ambiguity_sname");
					int viewCount2 = rSet1.getInt("view_count");
					String prefix2 = ambiguitysname.substring(0,1);
					if (!allMap.containsKey(prefix2)) {
						tmpMap.put(ambiguitysname, viewCount2);
						allMap.put(prefix2, tmpMap);
					}else{
						tmpMap = allMap.get(prefix2);
						if (tmpMap.containsKey(ambiguitysname)) {
							int curCount = tmpMap.get(ambiguitysname);
							if(curCount < viewCount2){
								tmpMap.put(ambiguitysname, viewCount2);
							}
						}else{
							tmpMap.put(ambiguitysname, viewCount2);
						}
					}
				}
				System.out.println("共"+allMap.size()+"纪录     ");
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			System.out.println("共"+allMap.size()+"纪录     ");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		String path = "D:\\wordfinal";
		File f = new File(path);
		if(!f.exists()){
		  f.mkdirs();
		} 
		int count = 0;
		Iterator<String> allIterator = allMap.keySet().iterator();
		while(allIterator.hasNext()){
			String allKey = allIterator.next();
			HashMap<String, Integer> tmpMap = allMap.get(allKey);
			String filename=allKey+".txt";
			File file = new File(f,filename);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				PrintWriter writer = new PrintWriter(file);
				Iterator<String> tmpIterator = tmpMap.keySet().iterator();
				while(tmpIterator.hasNext()){
					String tmpKey = tmpIterator.next();
					int viewCount = tmpMap.get(tmpKey);
					writer.write(tmpKey + " " + viewCount + "\r\n");
//					System.out.println(tmpKey + " " + viewCount);
					count ++;
				}
				writer.close();
			} catch (Exception e) {
			} finally{
			  }
			}
			System.out.println("all :" + count);
		}
		
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SceneyClassify sort = new SceneyClassify();
		sort.test();
		
	}
}
