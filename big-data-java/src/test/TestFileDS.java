package test;


import java.io.PrintWriter;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import big.data.*;
import big.data.field.FieldToXMLSpec;
import big.data.util.IOUtil;
import big.data.xml.*;
//import data.csv.*;


public class TestFileDS {
	public static void test1() {
		DataSource ds = DataSource.connectXML("vehicles.xml").load();
		System.out.println(ds.usageString());
		System.out.println("ds.size(): " + ds.size());

		Car c1 = ds.fetch("test.Car", "make", "model", "city08");
		System.out.println(c1);

		ArrayList<Car> cs = ds.fetchList(Car.class, "make", "model", "city08");
		System.out.println(cs.size());
		Car max = cs.get(0);
		for (Car c : cs) {
			if (c.mpgCity > max.mpgCity) max = c;
		}
		System.out.println(max);
	}
	
	public static void test2() {
		DataSource ds = DataSource.connect("src/big/data/tests/dsspec1.xml");
		System.out.println(ds.usageString());
		System.out.println(ds.readyToLoad());
		System.out.println(IOUtil.join(ds.missingParams().toArray(new String[] {}), ", "));
		//ds.load();
		ds.set("airportCode", "BOS").load();
		System.out.println("ds.size(): " + ds.size());
		String[] names = ds.fetchArray("String", "Name");
		System.out.println(names.length + " airports. First name: " + names[0]);
		
		ds = DataSource.connect("http://services.faa.gov/airport/status/BOS").set("format", "application/xml").load();
		System.out.println(ds.usageString());
		APStatus x = ds.fetch("test.APStatus", "Name", "State", "Delay", "Weather/Weather");
		System.out.println(x);
	}
	
	
	/*
	public static void test2() {
		String airportCode = "JFK";
		XMLDataSource ads = new XMLDataSource("Airport Status", "http://services.faa.gov/airport/status/" + airportCode + "?format=application/xml");
		APStatus x = ads.fetch("test.APStatus", "Name", "State", "Delay", "Weather/Weather");
		System.out.println(x + "\n");
		System.out.println(ads.usageString());
		System.out.println("---");
		
		XMLDataSource fds = new XMLDataSource("Foods", "Food_Display_Table.xml");
		ArrayList<FoodItem> fs = fds.fetchList(FoodItem.class, "Display_Name", "Calories", "Portion_Amount", "Portion_Display_Name", "Food_Code");
		System.out.println(fs.size() + " foods in table");
		
		for (int i = 0; i < 10; i++) {
			System.out.println(fs.get(i));
		}
		System.out.println("---");
		
		for (FoodItem f : fs) {
			if (f.code.startsWith("7514")) {
				System.out.println(f);
			}
		}
		System.out.println("---");
		
		ArrayList<String> names = fds.fetchList("String", "Display_Name");
		for (int i = 0; i < 10; i++) {
			System.out.println(names.get(i));
		}
		System.out.println("---");

		ArrayList<Double> cals = fds.fetchList("Double", "Calories");
		for (int i = 0; i < 10; i++) {
			System.out.println(cals.get(i));
		}
		System.out.println("---");
		
		Double[] dcals = fds.fetchArray("Double", "Calories");
	}
	*/
	
	public static class CodeCountry { String code; String ctry; String city; public CodeCountry(String a, String b, String c) { code = a; ctry = b; city = c; } }

	public static void test4() {
		DataSource ds = DataSource.connectCSV("Airline Data", "http://sourceforge.net/p/openflights/code/HEAD/tree/openflights/data/airlines.dat");
		ds.setOption("header", "\"ID\",\"Name\",\"Alias\",\"IATA\",\"ICAO\",\"Callsign\",\"Country\",Active");
		ds.set("format", "raw");
		System.out.println(ds.usageString());
		ds.load();
		System.out.println(ds.usageString());
		System.out.println("ds.size(): " + ds.size());
		String[] names = ds.fetchStringArray("Name");
		System.out.println(names.length + " airlines");
		System.out.println(names[100]);
		
		ds = DataSource.connect("src/big/data/tests/dsspec2.xml").load();
		System.out.println(ds.usageString());
		names = ds.fetchStringArray("Name");
		System.out.println(names.length + " airlines");
		System.out.println(names[100]);
		
		System.out.println("---");
		ds.getFieldSpec().apply(new FieldToXMLSpec()).write(new PrintWriter(System.out));
		
		ds = DataSource.connect("src/big/data/tests/dsspec3.xml").load();
		System.out.println(ds.usageString());
		names = ds.fetchStringArray("Country");
		System.out.println(IOUtil.join(ArrayUtils.subarray(names, 0, 20), ","));
	}
	
	/*
	public static void test3() {
		IDataSource ds = CSVDataSourceFactory.getDataSource("airports.dat");
		System.out.println(ds.usageString());


		CodeCountry[] codes = ds.fetchArray(CodeCountry.class, "IATA-FAA_Code", "Country", "City");
		for (CodeCountry airport : codes) {
			if (airport.ctry.equals("United States") && airport.city.equals("New York")) {
				String airportCode = airport.code;
				XMLDataSource ads = new XMLDataSource("http://services.faa.gov/airport/status/" + airportCode + "?format=application/xml");
				if (ads.dataAvailable()) {
					APStatus x = ads.fetch("test.APStatus", "Name", "State", "Delay", "Status/Reason");
					if (x.delay) {
						System.out.println("Delay at " + x.name + ". Reason: " + x.weather);
					} else {
						System.out.println(x.name + " good.");
					}
				}
			}
		}
		System.out.println("done");
	}
	
	*/
	
	public static class Weather {
		String time;
		int temp;
		int vis;   // km
		
		public Weather(String time, int temp, int vis) {
			super();
			this.time = time;
			this.temp = temp;
			this.vis = vis;
		}
		
		public String toString() {
			return time + ": " + temp + " degrees; visibility: " + vis + "km";
		}
	}
	
	public static void test5() {
		DataSource ds = DataSource.connect("src/big/data/tests/dsspec4.xml");
		ds.set("q", "11746").set("key", "7kwg5bevqqvrv3s676kd4uwb").load();
		System.out.println(ds.usageString());
		
		//System.out.println("---");
		//ds.getFieldSpec().apply(new FieldToXMLSpec()).write(new PrintWriter(System.out));

		ds = DataSource.connect("src/big/data/tests/dsspec5.xml").set("q", "11746").set("key", "7kwg5bevqqvrv3s676kd4uwb").load();
		System.out.println(ds.usageString());

		Weather w = ds.fetch(Weather.class, "date&time", "temp", "visibility");
		System.out.println(w);
	}
	
	public static void test6() {
		DataSource ds = DataSource.connect("src/big/data/tests/dsspec3.xml").load();
		DataSourceIterator iter = ds.iterator();
		System.out.println(iter.usageString());
		int count = 0;
		while (iter.hasData() && count<10) {
			String name = iter.fetchString("Name");
			boolean active = iter.fetchBoolean("Active");
			String country = iter.fetch("String", "Country");
			if (!country.isEmpty() && active) {
				System.out.println(name + " (" + country + ")");
				count++;
			}
			iter.loadNext();
		}
	}

	public static void main(String[] args) {
		//test1();
		//test2();
		//test3();
		//test4();
		//test5();
		test6();
	}
}


/*


This XML file does not appear to have any style information associated with it. The document tree is shown below.
<vehicle>
<atvType>FFV</atvType>
<barrels08>21.974</barrels08>
<barrelsA08>7.4910000000000005</barrelsA08>
<charge120>0.0</charge120>
<charge240>0.0</charge240>
<city08>13</city08>
<city08U>13.1355</city08U>
<cityA08>9</cityA08>
<cityA08U>9.6009</cityA08U>
<cityCD>0.0</cityCD>
<cityE>0.0</cityE>
<cityUF>0.0</cityUF>
<co2>-1</co2>
<co2A>-1</co2A>
<co2TailpipeAGpm>629.5</co2TailpipeAGpm>
<co2TailpipeGpm>592.4666666666667</co2TailpipeGpm>
<comb08>15</comb08>
<comb08U>14.5028</comb08U>
<combA08>10</combA08>
<combA08U>10.5921</combA08U>
<combE>0.0</combE>
<combinedCD>0.0</combinedCD>
<combinedUF>0.0</combinedUF>
<cylinders>8</cylinders>
<displ>4.6</displ>
<drive>Rear-Wheel Drive</drive>
<emissionsList>
<emissionsInfo>
<efid>CFMXT04.65H9</efid>
<id>31873</id>
<salesArea>3</salesArea>
<score>2.0</score>
<scoreAlt>-1.0</scoreAlt>
<smartwayScore>-1</smartwayScore>
<standard>B8</standard>
<stdText>Bin 8</stdText>
</emissionsInfo>
<emissionsInfo>
<efid>CFMXT04.65H9</efid>
<id>31873</id>
<salesArea>7</salesArea>
<score>2.0</score>
<scoreAlt>-1.0</scoreAlt>
<smartwayScore>-1</smartwayScore>
<standard>B8</standard>
<stdText>Bin 8</stdText>
</emissionsInfo>
</emissionsList>
<engId>146</engId>
<eng_dscr>FFV</eng_dscr>
<evMotor/>
<feScore>-1</feScore>
<fuelCost08>3250</fuelCost08>
<fuelCostA08>4850</fuelCostA08>
<fuelType>Gasoline or E85</fuelType>
<fuelType1>Regular Gasoline</fuelType1>
<fuelType2>E85</fuelType2>
<ghgScore>-1</ghgScore>
<ghgScoreA>-1</ghgScoreA>
<guzzler/>
<highway08>17</highway08>
<highway08U>16.617</highway08U>
<highwayA08>12</highwayA08>
<highwayA08U>12.1217</highwayA08U>
<highwayCD>0.0</highwayCD>
<highwayE>0.0</highwayE>
<highwayUF>0.0</highwayUF>
<hlv>0</hlv>
<hpv>0</hpv>
<id>31873</id>
<lv2>0</lv2>
<lv4>0</lv4>
<make>Ford</make>
<mfrCode>FMX</mfrCode>
<model>E150 Van FFV</model>
<mpgData>N</mpgData>
<phevBlended>false</phevBlended>
<pv2>0</pv2>
<pv4>0</pv4>
<rangeA>330</rangeA>
<rangeCityA>0.0</rangeCityA>
<rangeHwyA>0.0</rangeHwyA>
<trans_dscr/>
<trany>Automatic 4-spd</trany>
<UCity>16.2</UCity>
<UCityA>11.7</UCityA>
<UHighway>22.9</UHighway>
<UHighwayA>16.6</UHighwayA>
<VClass>Vans, Cargo Type</VClass>
<year>2012</year>
<youSaveSpend>-5500</youSaveSpend>
<sCharger/>
<tCharger/>
</vehicle>

*/