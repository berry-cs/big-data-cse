package test;

public class APStatus {
	String name;
	String place;
	boolean delay;
	String weather;
	
	public APStatus(String name, String place, boolean delay, String weather) {
		super();
		this.name = name;
		this.place = place;
		this.delay = delay;
		this.weather = weather;
	}
	
	public String toString() {
		return name + " (" + place + ")" + (delay? " DELAY" : " no delay") + " / Weather: " + weather;
	}
}
