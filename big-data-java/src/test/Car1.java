package test;

public class Car1 {
	String make;
	String model;
	int mpgCity;
	
	public Car1(String make, String model, int mpgCity) {
		super();
		this.make = make;
		this.model = model;
		this.mpgCity = mpgCity;
	}
	
	public String toString() {
		return "{Car: " + make + " - " + model + ". City MPG=" + mpgCity + "}";
	}
	
}


