package Weather;
import java.io.IOException;
import java.io.IOException;

public class testMain {
	public static void main(String args[]) throws IOException
	{
		WeatherInformationTracker wt = new WeatherInformationTracker();
		//wt.setURLbyCity("서울", "강남구", "도곡동");
		wt.setURLbyCoordinate("37.2394667", "127.0838812");
		WeatherData wd = new WeatherData();
		wd = wt.getCurrentWeather();
		System.out.println(wd.city);
		System.out.println(wd.county);
		System.out.println(wd.skyName);
		System.out.println(wd.humidity);
		System.out.println(wd.stormYn);
		System.out.println(wd.tc);
		System.out.println(wd.tmax);
	}
}
