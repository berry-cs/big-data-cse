package test;

import big.data.DataSource;

public class Quake {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.quakeml");
		  //DataSource ds = DataSource.connectJSON("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
		  ds.load();
		  ds.printUsageString();


	}

}
