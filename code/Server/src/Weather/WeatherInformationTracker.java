package Weather;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherInformationTracker {

	private String longitude = "";
	private String latitude = "";
	private String city = "";
	private String county = "";
	private String village = "";

	private String weather_url = null;

	public WeatherData getCurrentWeather() throws IOException {
		InputStream is = null;
		WeatherData data = new WeatherData();
		weather_url = "http://apis.skplanetx.com/weather/current/hourly?lon=" + longitude + "&lat=" + latitude + "&village=" + village + "&county=" + county + "&lat=" + latitude + "&city=" + city
				+ "&version=1";
		JSONParser parser = new JSONParser();
		try {

			URL url = new URL(weather_url);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setRequestMethod("GET");
			// urlCon.setRequestProperty("x-skpop-userId", "kong2983");
			urlCon.setRequestProperty("Content-Type", "application/json");
			urlCon.setRequestProperty("Accept", "application/json");

			urlCon.setRequestProperty("Accept-Language", "en");
			// urlCon.setRequestProperty("access_token", "");
			// urlCon.setRequestProperty("Host", "apis.skplanetx.com");
			urlCon.setRequestProperty("appKey", "75dac6b1-0d2a-3596-b277-d36d6b8df60f");
			// urlCon.setRequestProperty("Accept-Charset", "UTF-8");
			System.out.println(urlCon.getRequestProperties()); // ?���?
																// ?��?��?��
																// properties 출력
			/*
			 * if (urlCon.getResponseCode() >= 400) { is =
			 * urlCon.getErrorStream(); } else { is = urlCon.getInputStream(); }
			 */
			is = urlCon.getInputStream();

			// //// ?��?��?��?��
			System.out.println(urlCon.getRequestProperty("Accept"));

			System.out.println("getRequestMethod():" + urlCon.getRequestMethod());
			System.out.println("getContentType():" + urlCon.getContentType());
			System.out.println("getResponseCode():" + urlCon.getResponseCode());
			System.out.println("getResponseMessage():" + urlCon.getResponseMessage());

			for (Map.Entry<String, List<String>> header : urlCon.getHeaderFields().entrySet()) {
				for (String value : header.getValue()) {
					System.out.println(header.getKey() + " : " + value);
				}
			}

			// ////?��?��?��보확?��

			/*
			 * InputStream temp = is; String body =
			 * getStringFromInputStream(temp); System.out.println(body);
			 */
			JSONObject json = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				json = (JSONObject) jsonParser.parse(new InputStreamReader(is, "UTF-8"));

			} catch (ParseException e) {
				// TODO Auto-generated catch blockd
				e.printStackTrace();
			}

			data.setJSONData(json);

			// System.out.println("JSON 출력!!!!!!!!!!");
			/*
			 * System.out.println(json.toString());
			 * System.out.println("=========================");
			 * 
			 * Scanner scan = new Scanner(System.in);
			 * 
			 * System.out.println("�??�� ?��?��?��? -> "); String keyWord =
			 * scan.nextLine();
			 * 
			 * if(keyWord.equals("all")){ data.printAll(); } else { String
			 * findResult = data.findDataByKeyWord(keyWord); if(findResult ==
			 * null){ System.out.println("Cannot find data"); } else {
			 * System.out.println(findResult); } }
			 */

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	public void setURLbyCoordinate(String _latitude, String _longitude) {
		latitude = _latitude;
		longitude = _longitude;
	}

	public void setURLbyCity(String _city, String _county, String _village) {
		city = _city;
		county = _county;
		village = _village;
	}
}