package test;

// duplicating example from Bart's paper, Figure 2

import java.util.HashSet;
import java.util.List;

import easy.data.*;

public class Quake {
	public static void main(String[] args) {
		int DELAY = 5;   // 5 minute cache delay

		DataSource ds = DataSource.connectJSON("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
		ds.setCacheTimeout(DELAY);		  

		HashSet<String> quakes = new HashSet<String>();

		//while (true) {
			ds.load();
			
			List<Q> qs = ds.fetchList("test.Q", "features/properties/title");
			for (Q q : qs) System.out.println(q);
			
			List<String> latest = ds.fetchStringList("features/properties/title");
			for (String t : latest) {
				if (!quakes.contains(t)) {
					System.out.println("New quake!... " + t);
					quakes.add(t);
				}
			}
		//}
	}
}

class Q {
	String s;
	
	Q(String s) { this.s = s; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Q [s=" + s + "]";
	}
	
}
//xml source: DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.quakeml");
//ds.printUsageString();
