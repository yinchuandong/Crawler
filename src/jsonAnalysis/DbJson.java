package jsonAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import Util.AppUtil;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import jsonAnalysis.*;

public class DbJson {

	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		//		int num=count("select count(*) from t_crawled where isVisited=?", new String[]{"0"});
		//		System.out.println(num);
		//        System.out.println(sum);
		insertDbData();
        }	
	public static void	insertDbData()
	{
		int sum=0;
		File directory=new File("E:\\json");
        File[] fileList = directory.listFiles();
        for(File file : fileList){
                  try{
                	  if(!file.isFile())   //判断是不是文件
                          continue;
                	HashMap< Integer, Object> map=ParseJson(file);
              		int num=executeUpdate("insert into t_scenery(sid,surl,sname,ambiguity_sname,parent_sid,view_count,star,scene_layer,going_count,gone_count,rating,rating_count,map_info)values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
              								new String[]{map.get(1).toString(),map.get(2).toString(),(String)map.get(3),(String)map.get(4).toString(),map.get(5).toString(),
              								map.get(6).toString(),map.get(7).toString(),map.get(8).toString(),map.get(9).toString(),map.get(10).toString(),map.get(11).toString(),
              								map.get(12).toString(),map.get(13).toString()});
              			sum+=num;
              			System.out.println("已经完成纪录"+sum);
                  } catch (Exception e) {
                	  e.printStackTrace();
                  }
        }
	}
	public static HashMap<Integer, Object> ParseJson(File file) throws Exception
	{  
		HashMap< Integer, Object> hashMap=new HashMap<Integer, Object>();
          			String result="";
          			BufferedReader br = new BufferedReader(new FileReader(file));
          			String str="";
          			while ((str=br.readLine())!=null) {
          				result+=str;
          			}
          			//System.out.println(result);
          			br.close();
          			result=AppUtil.jsonFormatter(result);//将结果转化为json格式文件
          			JSONObject jsonobject=JSONObject.fromObject(result);
          		    JSONObject dataObj=jsonobject.getJSONObject("data");//获取Json文件中的在“data”模块的数据对象
          		    
          		    /* *********************获得需要保存的数据库字段****************/
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
          			/* ***************获得经纬度************************/
          			JSONObject extObj = dataObj.getJSONObject("ext");//获取Json文件中的在“ext”模块的数据对象
          			String mapInfo = extObj.getString("map_info");//获得经纬度
          			hashMap.put(1, sid);
          			hashMap.put(2, surl);
          			hashMap.put(3, sname);
          			hashMap.put(4, ambiguitySname);
          			hashMap.put(5, parentSid);
          			hashMap.put(6, viewCount);
          			hashMap.put(7, star);
          			hashMap.put(8, sceneLayer);
          			hashMap.put(9, goingCount);
          			hashMap.put(10, goneCount);
          			hashMap.put(11, rating);
          			hashMap.put(12, ratingCount);
          			hashMap.put(13, mapInfo);
          			return hashMap;
        }
	
	public  static Connection GetConnection()
	{
		Connection conn=null;
		try {
			Driver jdbcDriver=new Driver();
			DriverManager.registerDriver(jdbcDriver);
//			String dbUrl="jdbc:mysql://127.0.0.1:3306/travel";
			String dbUrl= "jdbc:mysql://127.0.0.1:3306/travel?characterEncoding=gbk";//采用gbk编码方式读写数据库
			String dbUser="root";
			String dbPwd="";
			conn=(Connection) DriverManager.getConnection(dbUrl, dbUser, dbPwd);
			} 
			catch (SQLException e) {
			System.out.println("连接服务器失败");
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * 关闭数据库连接，释放资源
	 * @param resultSet
	 * @param statement
	 * @param connection
	 */
	public static void close(ResultSet resultSet, PreparedStatement statement, Connection connection){
		
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 关闭数据库连接
	 * 直接关闭connection
	 */
	public static void close(){
		close(null, null, GetConnection());
	}
	public static int count(String sql,String[] params)
	{
		int num=-1;
		ResultSet resultSet=executeQuery(sql, params);
		try {
			while (resultSet.next()) {
				num=resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			close();
		}
		return num;
	}
	public static ResultSet executeQuery(String sql,String[] params)
	{
		Connection conn=GetConnection();
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try {
			preparedStatement=conn.prepareStatement(sql);
			if(params!=null)
			{
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setString(i+1, params[i]);
				}	
			}
			resultSet=preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			
		}
		return resultSet;
	}
	public static int executeUpdate(String sql, String[] params)
	{
		Connection conn = GetConnection();
		PreparedStatement preparedStatement = null;
		int result = -1;
		try {
			preparedStatement=conn.prepareStatement(sql);
			if(params!=null)
			{
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setString(i+1,params[i]);
				}
				result=preparedStatement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			close(null, preparedStatement, conn);
		}
		return result;
		
	}
}
