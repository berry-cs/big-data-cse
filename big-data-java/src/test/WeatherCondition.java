package test;

public class WeatherCondition {
	int temp;
	int wspeed;
	
	public WeatherCondition(String wth, int wspeed) {
		if (wth.equals("hot")) temp = 90;
		else temp = 70;
		this.wspeed = wspeed;
	}
	
	public WeatherCondition(int temp, int wspeed) {
		this.temp = temp;
		this.wspeed = wspeed;
	}
	

	
	public String toString() {
		return "Weather - t: " + temp + "; ws; " + wspeed;
	}
}