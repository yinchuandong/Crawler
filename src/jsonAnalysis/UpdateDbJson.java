package jsonAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import Util.AppUtil;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;

public class UpdateDbJson {

	public static void main(String[] args) {
		File directory=new File("G:\\web\\webAll");
	     File[] fileList = directory.listFiles();
	        for(File file : fileList){
	                  try{
	                	  if(!file.isFile())   //判断是不是文件
	                          continue;
	                		  updateDbjson(file);
	                  }
	         catch (Exception e) {
          	  e.printStackTrace();
            }
	        }    
	}
	public static void updateDbjson(File file) throws IOException
	{
	          			String result="";
	          			BufferedReader br = new BufferedReader(new FileReader(file));
	          			String str="";
	          			while ((str=br.readLine())!=null) {
	          				result+=str;
	          			}
	          			br.close();
	          			int sum=0;
	          			result=AppUtil.jsonFormatter(result);//将结果转化为json格式文件
	          			JSONObject jsonObj = JSONObject.fromObject(result); 
	        			JSONObject dataObj = jsonObj.getJSONObject("data");
	          		    JSONArray sceneList = dataObj.getJSONArray("scene_list");//获取scene_list里面的数组内容
	           for (int j = 0; j < sceneList.size(); j++) {
		          		JSONObject sceneObj = sceneList.getJSONObject(j);
		          		JSONObject dataObject=sceneObj.getJSONObject("ext");
		          		String sid=sceneObj.getString("sid");//子景点ID
						String absDesc=dataObject.getString("abs_desc");
						String moreDesc=dataObject.getString("more_desc");
						JSONObject dataObjectCover=sceneObj.getJSONObject("cover");
						String fullUrl=dataObjectCover.getString("full_url");
						int num=executeUpdate("UPDATE t_test SET abs_desc=?,more_desc=?,full_url=? WHERE sid=?", new String[]{absDesc,moreDesc,fullUrl,sid});
						sum+=num;
	           }
	          	System.out.println(sum);	    
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
		public static void close(){
			close(null, null, GetConnection());
	}
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
}
