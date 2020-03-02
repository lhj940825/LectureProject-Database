package Weather;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class WeatherRssReader {

	// http://www.kma.go.kr/wid/queryDFS.jsp?gridx=59&gridy=125

	public List<Map<String, String>> parseXML() throws ParseException {

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		try {

			SAXBuilder parser = new SAXBuilder();
			parser.setIgnoringElementContentWhitespace(true);

			// URL url = new URL(rssFeed);
			// InputSource is = new InputSource(url.openStream());
			// Document doc = parser.build(is);

			String url = "http://www.kma.go.kr/weather/forecast/mid-term-rss3.jsp?stnId=108";
			Document doc = parser.build(url);
			Element root = doc.getRootElement();

			Element channel = root.getChild("channel");
			Element item = channel.getChild("item");
			Element description = item.getChild("description");
			Element body = description.getChild("body");
			List<Element> list = body.getChildren("location");
			System.out.println(list.size());
			for (int i = 0; i < list.size(); i++) {

				Element el = (Element) list.get(i);

				List<Element> temp = el.getChildren("data");
				for (int j = 0; j < temp.size(); j++) {
					Map<String, String> data = new LinkedHashMap<String, String>();
					data.put("city", el.getChildText("city"));
					//data.put("province", el.getChildText("province"));
					String tt = convert(temp.get(j).getChildText("tmEf"));
					data.put("date", tt);
					data.put("wf", temp.get(j).getChildText("wf"));
					data.put("tmn", temp.get(j).getChildText("tmn"));
					data.put("tmx", temp.get(j).getChildText("tmx"));
					data.put("reliability", temp.get(j).getChildText("reliability"));
				
					result.add(data);
				}
				
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	public String convert(String tmef) throws ParseException{
		final String OLD_FORMAT = "yyyy-MM-dd HH:mm";
		final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss";
		String oldDateString = tmef;
		String newDateString;
		//2017-05-25 00:00
		DateFormat formatter = new SimpleDateFormat(OLD_FORMAT);
		Date d = formatter.parse(oldDateString);
		
		((SimpleDateFormat) formatter).applyPattern(NEW_FORMAT);
		newDateString = formatter.format(d);
		//System.out.println(newDateString);

		Timestamp ts = Timestamp.valueOf(newDateString);
		//System.out.println(ts);
		return newDateString;
		
	}
}
