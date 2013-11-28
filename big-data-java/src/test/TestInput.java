package test;

import java.io.InputStream;
import datasrc.*;
import ext.*;

public class TestInput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String[] lines = IOUtil.loadStrings("http://www.processing.org");
		// for (String line : lines) System.out.println(line);

		String WWOapikey = "7kwg5bevqqvrv3s676kd4uwb";
		
		XML xml = IOUtil.loadXML("wwocache.xml");
		
		if (xml == null) {
			xml = IOUtil.loadXML("http://api.worldweatheronline.com/free/v1/weather.ashx?q=30165&format=xml&date=today&extra=localObsTime&fx=no&key=" + WWOapikey);
			IOUtil.saveXML(xml, "wwocache.xml");
		}
		
		System.out.println(xml);
		XMLDataField f1 = new XMLDataField(xml, "localTime", "current_condition/weatherIconUrl");
		System.out.println(f1.asString());
		
		XMLDataRecord r = new XMLDataRecord(xml);
		for (String k : new String[] { "localObsDateTime", "temp_F", "weatherDesc", "windspeedMiles", "winddirDegree", "humidity", "pressure"}) {
			r.addField("current_condition/" + k);
		}
		System.out.println(r);
	}

}
