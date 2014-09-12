package test;

// duplicating example from Bart's paper, Figure 2

import java.util.HashSet;
import java.util.List;
import big.data.*;

class Earthquake {
	String description;
	long timestamp;
	float magnitude;
	
	public Earthquake(String description, long timestamp, float magnitude) {
		this.description = description;
		this.timestamp = timestamp;
		this.magnitude = magnitude;
	}
	
	public int hashCode() {
		return (int) (31 * (31 * this.description.hashCode()
				+ this.timestamp)
				+ this.magnitude);
	}
	
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass()) 
			return false;
		Earthquake that = (Earthquake) o;
		return that.description.equals(this.description)
		    && that.timestamp == this.timestamp
		    && that.magnitude == this.magnitude;
	}
}

public class Quake {
	public static void main(String[] args) {
		int DELAY = 5 * 60 * 1000;   // 5 minute cache delay

		DataSource ds = DataSource.connectJSON("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
		ds.setCacheTimeout(DELAY);		  

		HashSet<Earthquake> quakes = new HashSet<Earthquake>();

		//while (true) {
			ds.load();
			List<Earthquake> latest = ds.fetchList("test.Earthquake",
									"features/properties/title",
									"features/properties/time",
									"features/properties/mag");
			for (Earthquake e : latest) {
				System.out.println(e.description);
				if (!quakes.contains(e)) {
					System.out.println("New quake!... " + e.description);
					quakes.add(e);
				}
			}
		//}
	}
	
	/*
	public static void main(String[] args) {
		int DELAY = 5 * 60 * 1000;   // 5 minute cache delay

		DataSource ds = DataSource.connectJSON("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
		ds.setCacheTimeout(DELAY);		  

		HashSet<String> quakes = new HashSet<String>();

		while (true) {
			ds.load();
			List<String> latest = ds.fetchStringList("features/properties/title");
			for (String t : latest) {
				if (!quakes.contains(t)) {
					System.out.println("New quake!... " + t);
					quakes.add(t);
				}
			}
		}
	}
	*/
}

//xml source: DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.quakeml");
//ds.printUsageString();
