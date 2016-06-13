package test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import easy.data.*;

public class Quake2 {
    public static void main(String[] args) {
        int DELAY = 5;   // 5 minute cache delay

        DataSource ds = DataSource.connectJSON("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
        ds.setCacheTimeout(DELAY);		  

        ds.load();
        ds.printUsageString();

        HashSet<Earthquake> quakes = new HashSet<Earthquake>();

        while (true) {
            ds.load();
            List<Earthquake> latest = ds.fetchList("test.Earthquake",
                    "features/properties/title",
                    "features/properties/time",
                    "features/properties/mag",
                    "features/properties/url");
            for (Earthquake e : latest) {
                if (!quakes.contains(e)) {
                    System.out.println("New quake!... " + e.description + " (" + e.date() + ") info at: " + e.url);
                    quakes.add(e);
                }
            }
        }
    }
}


class Earthquake {
    String description;
    long timestamp;
    float magnitude;
    String url;

    public Earthquake(String description, long timestamp, float magnitude, String url) {
        this.description = description;
        this.timestamp = timestamp;
        this.magnitude = magnitude;
        this.url = url;
    }

    public Date date() {
        return new Date(timestamp);
    }

    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) 
            return false;
        Earthquake that = (Earthquake) o;
        return that.description.equals(this.description)
                && that.timestamp == this.timestamp
                && that.magnitude == this.magnitude;
    }

    // technically, hashCode() should be overridden if equals() is  
    public int hashCode() {
        return (int) (31 * (31 * this.description.hashCode()
                + this.timestamp)
                + this.magnitude);
    }
}
