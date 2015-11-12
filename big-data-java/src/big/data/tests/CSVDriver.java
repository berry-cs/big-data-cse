package big.data.tests;


import big.data.*;
import big.data.util.*;


public class CSVDriver {

    public static void main(String[] args) {

        DataSource ds;
        String p;

        ds = DataSource.connect("https://data.wprdc.org/dataset/7b8896c5-867d-428d-a2e3-bdb732655846/resource/90e87a5c-0cdd-4534-ab84-0cc523b110f5/download/municpal-building-energy-use-2009-2014.csv");
        ds.load();
        //p = ds.fetchString("row/Property_Name");
        //System.out.println(p);

    }
}


