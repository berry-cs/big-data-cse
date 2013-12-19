package test;

 class Car1 {
	String make;
	String model;
	int mpgCity;
	
	Car1(String make, String model, int mpgCity) {
		super();
		this.make = make;
		this.model = model;
		this.mpgCity = mpgCity;
	}
	
	public String toString() {
		return "{Car: " + make + " - " + model + ". City MPG=" + mpgCity + "}";
	}	
}


