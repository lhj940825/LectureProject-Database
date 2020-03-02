package DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.example.hojun.db_project.WeatherContainer;

public class DataBase {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private PreparedStatement pstmt;

	private String url = "jdbc:mysql://localhost:3306/DbPJ?useUnicode=true&characterEncoding=euckr";
	private String id = "DbPJ";
	private String password = "1234";

	public DataBase() throws SQLException {

		String driverName = "org.gjt.mm.mysql.Driver";
		try {
			Class.forName(driverName);
			getConnection();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getConnection() throws SQLException {
		con = DriverManager.getConnection(url, id, password);
	}

	public void createTable(String tableName) throws SQLException {
		stmt.executeUpdate("CREATE TABLE " + tableName + " (idx int unsigned not null, name varchar(100), address varchar(100))");
	}

	public void putWeatherData(List<Map<String, String>> parsedData) throws SQLException {
		List<Map<String, String>> data = parsedData;
		pstmt = con.prepareStatement("INSERT INTO weatherdata values(?, ?, ?, ?, ?, ? )");
		for (int i = 0; i < data.size(); i++) {
			pstmt.setString(1, data.get(i).get("city"));
			pstmt.setTimestamp(2, Timestamp.valueOf(data.get(i).get("date")));
			pstmt.setString(3, data.get(i).get("wf"));
			pstmt.setInt(4, Integer.parseInt(data.get(i).get("tmn")));
			pstmt.setInt(5, Integer.parseInt(data.get(i).get("tmx")));
			pstmt.setString(6, data.get(i).get("reliability"));
			pstmt.executeUpdate();
		}
		pstmt.close();

	}

	public void putAreaData(List<Map<String, String>> parsedData) throws SQLException {
		List<Map<String, String>> data = parsedData;
		// pstmt =
		// con.prepareStatement("INSERT INTO weatherdata values(?, ?, ?, ?, ?, ? )");
		pstmt = con.prepareStatement("INSERT INTO area values(?, ?,?, ?)");
		for (int i = 0; i < data.size(); i++) {
			pstmt.setString(1, data.get(i).get("province"));
			pstmt.setString(2, data.get(i).get("city"));
			pstmt.setString(3, "0");
			pstmt.setString(4, "0");
			pstmt.executeUpdate();
		}
		pstmt.close();

	}

	public void putAlertData(ArrayList<String> citys) throws SQLException {
		// pstmt =
		// con.prepareStatement("INSERT INTO weatherdata values(?, ?, ?, ?, ?, ? )");
		pstmt = con.prepareStatement("INSERT INTO alert values(?, ?, ?, ?, ?)");
		for (int i = 0; i < citys.size(); i++) {
			pstmt.setString(1, citys.get(i));
			pstmt.setInt(2, 35);
			pstmt.setDouble(3, 32.0D);
			pstmt.setDouble(4, 21.0D);
			pstmt.setInt(5, -15);
			pstmt.executeUpdate();
		}
		pstmt.close();

	}

	public void closeConnection() throws SQLException {
		con.close();
	}

	// select a.province, a.latitude, a.longitude, w.city, w.date, w.wf, w.tmn,
	// w.tmx, w.reliability from weatherdata w INNER JOIN area a ON (w.city =
	// a.city)where w.date = (select max(date) from weatherdata)
	public ArrayList<WeatherContainer> getWeatherContainer() throws SQLException {
		ArrayList<WeatherContainer> container = new ArrayList<WeatherContainer>();
		pstmt = con
				.prepareStatement(new String(
						"SELECT a.province, a.latitude, a.longitude, w.city, w.date, w.wf, w.tmn, w.tmx, w.reliability FROM weatherdata w INNER JOIN area a ON (w.city = a.city)WHERE w.date = (SELECT max(date) FROM weatherdata)"));
		rs = pstmt.executeQuery();
		String city, province, wf, reliability;
		Double latitude, longitude;
		int tmn, tmx;
		Timestamp date;

		while (rs.next()) {
			province = rs.getString("province");
			latitude = Double.parseDouble(rs.getString("latitude"));
			longitude = Double.parseDouble(rs.getString("longitude"));
			city = rs.getString("city");
			date = rs.getTimestamp("date");
			wf = rs.getString("wf");
			tmn = rs.getInt("tmn");
			tmx = rs.getInt("tmx");
			reliability = rs.getString("reliability");
			container.add(new WeatherContainer(province, latitude, longitude, city, date, wf, tmn, tmx, reliability));
		}
		rs.close();
		pstmt.close();
		return container;
	}

	public ArrayList<String> getCityName() throws SQLException {
		ArrayList<String> citys = new ArrayList<String>();
		pstmt = con.prepareStatement(new String("SELECT distinct city FROM `weatherdata`"));
		rs = pstmt.executeQuery();
		String cityName;
		while (rs.next()) {
			cityName = rs.getString("city");
			citys.add(cityName);
		}
		rs.close();
		pstmt.close();
		return citys;
	}

	public JSONObject getAlertInfo(String province) throws SQLException {
		JSONObject json = new JSONObject();
		json.put("tag", "WeatherPlanet");
		pstmt = con.prepareStatement(new String("SELECT mHeatWave, mHumidity, mWindSpeed, mColdwave FROM `alert` where province like '%" + province + "%'"));
		rs = pstmt.executeQuery();
		int mHeatWave = 0, mColdWave = 0;
		double mHumidity = 0, mWindSpeed = 0;
		while (rs.next()) {
			mHeatWave = rs.getInt("mHeatWave");
			mColdWave = rs.getInt("mColdwave");
			mHumidity = rs.getDouble("mHumidity");
			mWindSpeed = rs.getDouble("mWindSpeed");
		}
		json.put("mHeatWave", String.valueOf(mHeatWave));
		json.put("mColdWave", String.valueOf(mColdWave));
		json.put("mHumidity", String.valueOf(mHumidity));
		json.put("mWindSpeed", String.valueOf(mWindSpeed));
		return json;
	}

	public JSONObject getRecentWeatherInfo(String city) throws SQLException {
		String cityName, wf, reliability;
		Timestamp date;
		int tmn, tmx;
		pstmt = con.prepareStatement(new String("SELECT l.mHeatWave, l.mColdWave, a.province, a.latitude, a.longitude,w.date, w.city, w.date, w.wf, w.tmn, w.tmx, w.reliability FROM weatherdata w, area a, alert l where (w.city = a.city) and (a.province = l.province) and w.city = '"+city+"'"));
		rs = pstmt.executeQuery();
		JSONObject json = new JSONObject();
		json.put("tag", "recentWeather");
		JSONArray weatherInfoArray = new JSONArray();
		while (rs.next()) {
			JSONObject weatherInfo = new JSONObject();
			cityName = rs.getString("city");
			date = rs.getTimestamp("date");
			wf = rs.getString("wf");
			tmn = rs.getInt("tmn");
			tmx = rs.getInt("tmx");
			reliability = rs.getString("reliability");
			weatherInfo.put("city", cityName);
			weatherInfo.put("date", date.toString());
			weatherInfo.put("wf", wf);
			weatherInfo.put("tmn", String.valueOf(tmn));
			weatherInfo.put("tmx", String.valueOf(tmx));
			weatherInfo.put("reliability", reliability);
			weatherInfo.put("mHeatWave", String.valueOf(rs.getInt("mHeatWave")));
			weatherInfo.put("mColdWave", String.valueOf(rs.getInt("mColdWave")));
			weatherInfo.put("province", rs.getString("province"));
			weatherInfoArray.add(weatherInfo);
		}
		json.put("weatherInfoArray", weatherInfoArray);

		return json;
	}
	
	public JSONObject getRecentAllWeatherInfo() throws SQLException{
		pstmt = con.prepareStatement(new String("SELECT * FROM weatherdata"));
		rs = pstmt.executeQuery();
		JSONObject json = new JSONObject();
		json.put("tag", "recentAllWeather");
		JSONArray weatherInfoArray = new JSONArray();
		while(rs.next()){
			JSONObject weatherInfo = new JSONObject();
			weatherInfo.put("city", rs.getString("city"));
			weatherInfo.put("date", rs.getTimestamp("date").toString());
			weatherInfo.put("tmx", String.valueOf(rs.getInt("tmx")));
			weatherInfo.put("wf", (rs.getString("wf")));
			weatherInfoArray.add(weatherInfo);
		}	
		json.put("weatherInfoArray", weatherInfoArray);
		return json;
	}
}
