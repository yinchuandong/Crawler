package jsonAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.ResultSetMetaData;

public class WordProcess {
	
	
	public static void main(String[] args) throws Exception {
		ResultSet snameResultSet=GetSceneryName();
		ResultSetMetaData metaData=(ResultSetMetaData) snameResultSet.getMetaData();
		int columnnum=metaData.getColumnCount();
	     File f = new File("D://Data");
	     f.mkdirs();
	     f = new File("D://Data//SnamePinYin.txt");
	     f.createNewFile(); 
//		int rowCount = 0;
//		snameResultSet.last(); 
//		rowCount = snameResultSet.getRow();
//		System.out.println(rowCount);
		while (snameResultSet.next()) {
			for (int i = 1; i <= columnnum; i++) {
				String chinese=snameResultSet.getString(i);
				String SnamePinYin=new PinYin().HanyuToPinyin(chinese);
				WriteFile(SnamePinYin);
			}
		}
	}
	 
	public static ResultSet GetSceneryName() throws SQLException{
			java.sql.ResultSet rSet=DbJson.executeQuery("SELECT sname from t_scenery", null);
			return rSet;
	}
	public static void WriteFile(String contentString) throws IOException {
			FileWriter fileWriter=new FileWriter("D://Data//SnamePinYin.txt",true);
			fileWriter.write(contentString+"\r\n");
		    fileWriter.flush();
		    fileWriter.close();
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
