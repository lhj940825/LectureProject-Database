package Weather;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WeatherData {
	
	private JSONObject json = new JSONObject();
	private JSONObject result = new JSONObject();
	private JSONObject common = new JSONObject();
	private JSONObject weather = new JSONObject();
	
	//JSONArray and JSONObject for weather
	private JSONArray hourly = new JSONArray(); 
	private JSONObject sky = new JSONObject();
	private JSONObject precipitation = new JSONObject();
	private JSONObject grid = new JSONObject();
	private JSONObject temperature = new JSONObject();
	private JSONObject wind = new JSONObject();
	
	public String code, requestUrl, message; //result
	public String alertYn, stormYn; //common
	public String skyCode, skyName, lightning, sinceOntime, type, city, latitude, county, 
	village, longitude, tmax, tmin, tc, humidity, timeRelease, wdir, wspd; //weather
	
	//private DataFormat form[] = new DataFormat[22];
	public String keyArr[] = new String[22];
	public String valueArr[] = new String[22];
	
	public void setJSONData(JSONObject input)
	{
		json = input;
		System.out.println(input.get("result"));
		result = (JSONObject) json.get("result");
		common = (JSONObject)json.get("common");
		weather = (JSONObject)json.get("weather");		
		//result
		//code = (String)result.get("code");	
		code = "9200"; //temp value(request code of successful data transmission)
		requestUrl = (String)result.get("requestUrl");
		message = (String)result.get("message");
		//result
		
		//common
		alertYn = (String)common.get("alertYn");
		stormYn = (String)common.get("stormYn");
		//common
		
		//weather
		hourly = (JSONArray)weather.get("hourly");
		weather = (JSONObject) hourly.get(0); //format change		
				 
		sky = (JSONObject) weather.get("sky");
		lightning = (String) weather.get("lighting");
		precipitation = (JSONObject) weather.get("precipitation");
		grid = (JSONObject) weather.get("grid");
		temperature = (JSONObject) weather.get("temperature");
		humidity = (String) weather.get("humidity");
		timeRelease = (String) weather.get("timeRelease");
		wind = (JSONObject) weather.get("wind");
		 
		skyCode = (String)sky.get("code");
		skyName = (String)sky.get("name");
		sinceOntime = (String)precipitation.get("sinceOntime");
		type = (String)precipitation.get("type");
		city = (String)grid.get("city");
		latitude = (String)grid.get("latitude");
		county = (String)grid.get("county");
		village = (String)grid.get("village");
		longitude = (String)grid.get("longitude");
		tmax = (String)temperature.get("tmax");
		tmin = (String)temperature.get("tmin");
		tc = (String)temperature.get("tc");
		wdir = (String)wind.get("wdir");
		wspd = (String)wind.get("wspd");
		//weather
		setDataArr();
		
	}
	
	public void setDataArr(){
		
		String tempArr[] = {"code", "requestUrl", "message","alertYn", "stormYn","skyCode", "skyName", "lightning", "sinceOntime", "type", "city", "latitude", "county", 
				"village", "longitude", "tmax", "tmin", "tc", "humidity", "timeRelease", "wdir", "wspd"};
		String tempArr2[] = {code, requestUrl, message, alertYn, stormYn,skyCode, skyName, lightning, sinceOntime, type, city, latitude, county, 
				village, longitude, tmax, tmin, tc, humidity, timeRelease, wdir, wspd};
		
		keyArr = tempArr;
		valueArr = tempArr2;
		
	}
	public String findDataByKeyWord(String keyWord){
		String find_result = null;
		for(int i=0; i<22; i++){
			if(keyWord.equals(keyArr[i]))
			{
				find_result = valueArr[i];
			}
		}
		
		return find_result;
	}
	
	
	public void printAll()
	{
		for(int i=0; i<22; i++){
			System.out.println(keyArr[i] + " : " + valueArr[i]);
		}
		
	}
}
