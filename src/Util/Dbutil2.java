package Util;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.omg.PortableInterceptor.SUCCESSFUL;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.ResultSet;

public abstract class Dbutil2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getConnection();
	}
	/**
	 * @打开数据库连接
	 * @return
	 */
	public static Connection getConnection()
	{
		Connection conn = null;
		try {
			Driver jdbcDriver=new Driver();
			DriverManager.registerDriver(jdbcDriver);
			String url="jdbc:mysql://127.0.0.1:3306/travel";
			String dbUser="root";
			String dbPwd="";
			conn=(Connection) DriverManager.getConnection(url, dbUser, dbPwd);
			System.out.println("success connect to the database!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		}
		return  conn;
		
	}

	public static int executeUpdate(String sql,String[] pa) {
		
		return 1;
	}
}
