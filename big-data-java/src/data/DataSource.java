package data;

import data.xml.XMLDataSource;

public abstract class DataSource implements IDataSource {
	
	public static DataSource loadFrom(String path) {
		return new XMLDataSource(path);
	}
	
	public static DataSource loadFrom(String path, long timeout) {
		return new XMLDataSource(path, timeout);
	}
	
	public static DataSource airportStatus(String airportCode) {
		return new XMLDataSource("http://services.faa.gov/airport/status/" + airportCode + "?format=application/xml");
	}
	
	public static DataSource worldWeather(String zip, String WWOapikey) {
		//return new XMLDataSource("http://api.worldweatheronline.com/free/v1/weather.ashx?q=" + zip + "&format=xml&date=today&extra=localObsTime&fx=no&key=" + WWOapikey, 
		// 60*60*1000);
		
		URLPrepper up = new URLPrepper("api.worldweatheronline.com/free/v1/weather.ashx");
		up.addParams(new String[][] { {"q", zip}, {"format", "xml"}, {"date", "today"}, {"extra", "localObsTime"}, {"fx", "no"}, {"key", WWOapikey} });
		return up.getXMLDataSource(60*60*1000);
	}
	
}
