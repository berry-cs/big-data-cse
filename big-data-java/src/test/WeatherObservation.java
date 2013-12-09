package test;

public class WeatherObservation {
	private String datetime;
	private WeatherCondition cond;
	
	public WeatherObservation(String datetime, WeatherCondition cond) {
		this.datetime = datetime;
		this.cond = cond;
	}
	
	public WeatherObservation(String datetime, int temp, int wspeed) {
		this(datetime, new WeatherCondition(temp, wspeed));
	}
	
	public String toString() {
		return "Observation {" + datetime + ": " + cond + "}"; 
	}
}
