package test;

// duplicating example from Bart's paper, Figure 2

import java.util.HashSet;
import java.util.List;
import big.data.*;

public class Quake {
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
}

//xml source: DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.quakeml");
//ds.printUsageString();
