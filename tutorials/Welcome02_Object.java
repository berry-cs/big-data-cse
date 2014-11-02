
import big.data.*;

public class Welcome02_Object {
   public static void main(String[] args) {
      String id1 = "KATL";
      DataSource ds1 = DataSource.connect("http://weather.gov/xml/current_obs/" + id1 + ".xml"); 
      ds1.setCacheTimeout(15);  
      ds1.load();
      
      Observation ob1 = ds1.fetch("Observation", "weather", "temp_f", "wind_degrees");
      System.out.println(id1 + ": " + ob1);
      
      String id2 = "KSAV";
      DataSource ds2 = DataSource.connect("http://weather.gov/xml/current_obs/" + id2 + ".xml"); 
      ds2.setCacheTimeout(15);  
      ds2.load();
      
      Observation ob2 = ds2.fetch("Observation", "weather", "temp_f", "wind_degrees");
      System.out.println(id2 + ": " + ob2);
      
      if (ob1.colderThan(ob2)) {
         System.out.println("Colder at " + id1);
      } else {
         System.out.println("Colder at " + id2);
      }
   }
}


class Observation {
   float temp;
   int windDir;   // in degrees
   String description;
   
   Observation(String description, float temp, int windDir) {
      this.description = description;
      this.temp = temp;
      this.windDir = windDir;
   }
   
   public boolean colderThan(Observation that) {
      return this.temp < that.temp;
   }
   
   public String toString() {
      return (temp + " degrees; " + description + " (wind: " + windDir + " degrees)");
   }
}

