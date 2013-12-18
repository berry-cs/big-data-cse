package test;

public class FoodItem {
	String name;
	double calories;
	double portionSize;
	String portionUnits;
	String code;
	
	public FoodItem(String name, double calories, double portionSize,
			String portionUnits, String code) {
		this.name = name;
		this.calories = calories;
		this.portionSize = portionSize;
		this.portionUnits = portionUnits;
		this.code = code;
	}
	
	public String toString() {
		return name + ": " + calories + " calories (" + portionSize + " " + portionUnits + ") #: " + code;
	}
	
}
