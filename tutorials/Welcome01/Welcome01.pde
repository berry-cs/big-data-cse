import big.data.*; 

void setup() {
  DataSource.initializeProcessing(this);

  String id = "KATL";
  DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/" + id + ".xml"); 
  ds.setCacheTimeout(15);  
  ds.load();
  //ds.printUsageString();
  float temp = ds.fetchFloat("temp_f");
  String loc = ds.fetchString("location");
  System.out.println("The temperature at " + loc + " is " + temp + "F");
}

