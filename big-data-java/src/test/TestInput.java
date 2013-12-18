package test;

import java.io.InputStream;

import data.xml.*;

import ext.*;
import static data.xml.PrimSig.*;

public class TestInput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String[] lines = IOUtil.loadStrings("http://www.processing.org");
		// for (String line : lines) System.out.println(line);

		String WWOapikey = "7kwg5bevqqvrv3s676kd4uwbx";
		
		XML xml = IOUtil.loadXML("wwocache.xml");
		
		if (xml == null) {
			xml = IOUtil.loadXML("http://api.worldweatheronline.com/free/v1/weather.ashx?q=30165&format=xml&date=today&extra=localObsTime&fx=no&key=" + WWOapikey);
			IOUtil.saveXML(xml, "wwocache.xml");
		}
		
		System.out.println(xml);

		CompSig<WeatherCondition> cs1 = new CompSig<WeatherCondition>(WeatherCondition.class);
		cs1.addField(WILDCARD_SIG, "Temperature");
		cs1.addField(WILDCARD_SIG, "Wind Speed");
		ListSig ls1 = new ListSig(cs1);
		System.out.println(ls1);
		
		CompSig<WeatherCondition> cs2 = new CompSig<WeatherCondition>(WeatherCondition.class);
		cs2.addField(INT_SIG, "Temperature");
		cs2.addField(INT_SIG, "Wind Speed");
		
		System.out.println(cs1.findConstructor());
		
		PrimField pf1 = new PrimField("current_condition/localObsDateTime");
		System.out.println(pf1.instantiate(xml, STRING_SIG));
		System.out.println(pf1.asInt(xml));
		
		CompField cf1 = new CompField("current_condition");
		cf1.addField("Temperature", new PrimField("temp_F"));
		cf1.addField("Wind Speed", new PrimField("windspeedMiles"));
		
		CompField cf2 = new CompField("current_condition");
		CompField cf3 = new CompField();
		cf2.addField("Date/Time", new PrimField("localObsDateTime"));
		cf2.addField("Condition", cf3);
		cf3.addField("Temperature", new PrimField("temp_F"));
		cf3.addField("Wind Speed", new PrimField("windspeedMiles"));
		
		//WeatherCondition w = cf1.asObject(xml, WeatherCondition.class, "Temperature", "Wind Speed");
		//System.out.println(w);
		WeatherCondition w1 = cf1.instantiate(xml, cs1);
		WeatherCondition w2 = cf1.instantiate(xml, cs2);
		System.out.println(w1);
		System.out.println(w2);
		
		CompSig<WeatherObservation> cs3 = new CompSig<WeatherObservation>(WeatherObservation.class);
		cs3.addField(STRING_SIG, "Date/Time");
		cs3.addField(cs2, "Condition");
		
		WeatherObservation wob = cf2.instantiate(xml, cs3);
		System.out.println(wob);
	}
	
	
	

}