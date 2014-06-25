package jsonAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

public class HotelJson {

	public static void main(String[] args) {
		insertHotelData();
	}

	private static void insertHotelData() {
		int sum = 0;
		File directory = new File("E:\\Courseware\\senior3_2\\TravelProjectData\\hoteldata");
		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			try {
				if (!file.isFile()) // 判断是不是文件
					continue;
				parseJson(file);
				System.out.println(file.getName());
				sum++;
				System.out.println("已经完成纪录" + sum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void parseJson(File file)
			throws Exception {
		HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>();
		String result = "";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = "";
		while ((str = br.readLine()) != null) {
			result += str;
		}
		br.close();
		result = AppUtil.jsonFormatter(result);// 将结果转化为json格式文件
		JSONObject jsonobject = JSONObject.fromObject(result);
		JSONObject dataObj = jsonobject.getJSONObject("data");// 获取Json文件中的在“data”模块的数据对象

		/* *********************获得需要保存的数据库字段*************** */
		JSONObject queryObject = dataObj.getJSONObject("query");
		String nbx = queryObject.getString("nb_x");
		String nby = queryObject.getString("nb_y");
		JSONArray hotelList = dataObj.getJSONArray("hotels");
		for (int i = 0; i < hotelList.size(); i++) {
			JSONObject hotelObject = hotelList.getJSONObject(i);
			String uid = hotelObject.getString("uid");// 酒店id
			String hotelname = hotelObject.getString("hotel_name");
			String hoteladdress = hotelObject.getString("hotel_address");
			
			String phone = "";
			if (hotelObject.containsKey("phone")) {
				phone = hotelObject.getString("phone");
			}
			String hoteltype = hotelObject.getString("hotel_type");
			int price = hotelObject.getInt("price");
			int commentcount = hotelObject.getInt("comment_count");
			double commentscore = hotelObject.getDouble("comment_score");
			String specialservice = hotelObject.getString("special_service");
			int isgroup = hotelObject.getInt("is_group");
			String pic="";
			if (hotelObject.containsKey("pic")) {
				pic = hotelObject.getString("pic");
			}
			String pointx = hotelObject.getString("point_x");
			String pointy = hotelObject.getString("point_y");
			String tradingarea = hotelObject.getString("trading_area");
			int isrecommend = hotelObject.getInt("is_recommend");
			String reason=hotelObject.getString("reason");
			hashMap.put(1, uid);
			hashMap.put(2, hotelname);
			hashMap.put(3, hoteladdress);
			hashMap.put(4, phone);
			hashMap.put(5, hoteltype);
			hashMap.put(6, price);
			hashMap.put(7, commentcount);
			hashMap.put(8, commentscore);
			hashMap.put(9, specialservice);
			hashMap.put(10, isgroup);
			hashMap.put(11, pic);
			hashMap.put(12, pointx);
			hashMap.put(13, pointy);
			hashMap.put(14, tradingarea);
			hashMap.put(15, isrecommend);
			hashMap.put(16, reason);
			hashMap.put(17, nbx);
			hashMap.put(18, nby);
			String sid=file.getName().substring(0,file.getName().lastIndexOf("."));
			executeUpdate("insert into t_hotel(sid,uid,hotel_name,hotel_address,phone,hotel_type,price,comment_count,comment_score,special_service,is_group,pic,point_x,point_y,trading_area,is_recommend,reason,nb_x,nb_y)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new String[]{sid,hashMap.get(1).toString(),hashMap.get(2).toString(),(String)hashMap.get(3),(String)hashMap.get(4).toString(),hashMap.get(5).toString(),
					hashMap.get(6).toString(),hashMap.get(7).toString(),hashMap.get(8).toString(),hashMap.get(9).toString(),hashMap.get(10).toString(),hashMap.get(11).toString(),
					hashMap.get(12).toString(),hashMap.get(13).toString(),hashMap.get(14).toString(),hashMap.get(15).toString(),hashMap.get(16).toString(),hashMap.get(17).toString(),hashMap.get(18).toString()});
		}
	}

	public static Connection GetConnection() {
		Connection conn = null;
		try {
			Driver jdbcDriver = new Driver();
			DriverManager.registerDriver(jdbcDriver);
			// String dbUrl="jdbc:mysql://127.0.0.1:3306/travel";
			String dbUrl = "jdbc:mysql://127.0.0.1:3306/travel?characterEncoding=gbk";// 采用gbk编码方式读写数据库
			String dbUser = "root";
			String dbPwd = "";
			conn = (Connection) DriverManager.getConnection(dbUrl, dbUser,
					dbPwd);
		} catch (SQLException e) {
			System.out.println("连接服务器失败");
			e.printStackTrace();
		}
		return conn;
	}

	public static int executeUpdate(String sql, String[] params) {
		Connection conn = GetConnection();
		PreparedStatement preparedStatement = null;
		int result = -1;
		try {
			preparedStatement = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setString(i + 1, params[i]);
				}
				result = preparedStatement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStatement, conn);
		}
		return result;
	}

	public static void close() {
		close(null, null, GetConnection());
	}

	public static void close(ResultSet resultSet, PreparedStatement statement,
			Connection connection) {

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
