package DataBase;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import Weather.WeatherRssReader;

public class Main {

	public static void main(String[] args) throws SQLException, ParseException {
		// TODO Auto-generated method stub
		DataBase db = new DataBase();
		// load data from web and save those data to DB
		WeatherRssReader wrr = new WeatherRssReader();
		db.putWeatherData(wrr.parseXML());
		//db.getWeatherContainer();
		
	/*	 ArrayList<String> citys = db.getCityName();
		 System.out.println(citys);
		 db.putAlertData(citys);*/
	}

}
