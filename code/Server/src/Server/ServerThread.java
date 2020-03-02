package Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import Weather.WeatherData;
import Weather.WeatherInformationTracker;

import com.example.hojun.db_project.WeatherContainer;

import DataBase.*;

public class ServerThread extends Thread {
	private static List<ServerThread> threads = new ArrayList<ServerThread>();

	private ArrayList<WeatherContainer> weathers = new ArrayList<WeatherContainer>();
	Socket clientSocket = null;
	DataBase db = null;

	public ServerThread(Socket socket) throws SQLException {
		// TODO Auto-generated constructor stub
		super();
		this.clientSocket = socket;
		threads.add(this);
		for (int i = 0; i < threads.size() - 1; i++) {
			if (socket.getInetAddress().equals(threads.get(i).clientSocket.getInetAddress())) {
				System.out.println(threads.get(i).clientSocket.getInetAddress() + "Duplicated Access");
				System.out.println(threads.get(i).getName() + "Delete Thread");
				threads.remove(i);
			} else {
				System.out.println(threads.get(i).clientSocket.getInetAddress() + "Access");
			}
		}
		for (int i = 0; i < threads.size(); i++) {
			System.out.println(threads.get(i).getName() + "Activated Thread");
		}

		db = new DataBase();
		this.weathers = db.getWeatherContainer();
	}

	@SuppressWarnings("unchecked")
	public void run() {

		InputStream objectInBytes = null;
		ObjectInputStream ois = null;
		OutputStream sendingBytes = null;
		ObjectOutputStream oos = null;

		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			objectInBytes = clientSocket.getInputStream();
			ois = new ObjectInputStream(objectInBytes);
			sendingBytes = clientSocket.getOutputStream();
			oos = new ObjectOutputStream(sendingBytes);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			oos.writeObject(weathers);
			oos.reset();
			while (true) {
				Object obj = ois.readObject();
				if (obj instanceof String) {
					String request = (String) obj;
					String[] data = request.split(" ");
					if (data[0].equals("WeatherPlanet")) {
						oos.writeObject(getWeatherPlanetInfo(data).toJSONString());
						oos.reset();
					} else if (data[0].equals("recentWeather")) {
						oos.writeObject(getRecentWeatherInfo(data).toJSONString());
						oos.reset();
					} else if (data[0].equals("recentAllWeather")) {
						oos.writeObject(getRecentAllWeatherInfo().toJSONString());
						oos.reset();
					}

				} else if (obj instanceof Integer) { // this code is about

				}
			}
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JSONObject getRecentWeatherInfo(String[] data) throws SQLException {
		JSONObject json = db.getRecentWeatherInfo(data[1]);
		return json;
	}
	
	public JSONObject getRecentAllWeatherInfo() throws SQLException{
		JSONObject json = db.getRecentAllWeatherInfo();
		
		return json;
	}

	public JSONObject getWeatherPlanetInfo(String[] data) throws IOException, SQLException {
		WeatherInformationTracker wt = new WeatherInformationTracker();
		wt.setURLbyCoordinate(data[1], data[2]);
		WeatherData wd = new WeatherData();
		wd = wt.getCurrentWeather();
		JSONObject json = db.getAlertInfo(translateCityName(wd.city));
		json.put("sky", wd.skyName);
		json.put("place", wd.city + " " + wd.county + " " + wd.village);
		json.put("tmax", wd.tmax);
		json.put("tmin", wd.tmin);
		json.put("wspd", wd.wspd);
		json.put("humidity", wd.humidity);
		json.put("latitude", wd.latitude);
		json.put("longitude", wd.longitude);
		return json;
	}

	public String translateCityName(String city) {
		String temp = null;
		if (city.equals("경기"))
			temp = "경기도";
		else if (city.equals("서울"))
			temp = "서울";
		else if (city.equals("강원"))
			temp = "강원도";
		else if (city.equals("충남"))
			temp = "충청남도";
		else if (city.equals("충북"))
			temp = "충청북도";
		else if (city.equals("경북"))
			temp = "경상북도";
		else if (city.equals("강원"))
			temp = "강원도";
		else if (city.equals("전북"))
			temp = "전라북도";
		else if (city.equals("전남"))
			temp = "전라남도";
		else if (city.equals("경남"))
			temp = "경상남도";
		else if (city.equals("제주"))
			temp = "제주도";
		else
			temp = city;
		return temp;
	}

	private void sendMessageAll(String message) {
		// TODO Auto-generated method stub
		ServerThread thread;
		for (int i = 0; i < threads.size(); i++) {
			thread = threads.get(i);
			if (thread.isAlive())
				thread.sendMessage(this, message);
		}
	}

	public void sendMessage(ServerThread talker, String message) {
		try {
			OutputStream out = clientSocket.getOutputStream();
			byte[] buffer = message.getBytes("UTF-8");
			out.write(buffer);
			out.flush();
		} catch (IOException ioe) {
		}
	}

	public void sendNotification(String message) throws IOException {
		OutputStream sendingBytes2 = null;
		ObjectOutputStream oos2 = null;
		sendingBytes2 = clientSocket.getOutputStream();
		oos2 = new ObjectOutputStream(sendingBytes2);
		oos2.writeObject(message);
		oos2.reset();

	}
}
