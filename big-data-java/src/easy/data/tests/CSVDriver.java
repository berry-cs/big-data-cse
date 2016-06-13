package easy.data.tests;


import easy.data.*;
import easy.data.util.*;


public class CSVDriver {

    public static void main(String[] args) {

        DataSource ds;
        String p;

        //ds = DataSource.connect("https://data.wprdc.org/dataset/7b8896c5-867d-428d-a2e3-bdb732655846/resource/90e87a5c-0cdd-4534-ab84-0cc523b110f5/download/municpal-building-energy-use-2009-2014.csv");
        //ds.load();
        //p = ds.fetchString("row/Property_Name");
        //System.out.println(p);

        long a = System.currentTimeMillis();
        ds = DataSource.connectCSV("https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat");
        ds.setOption("header", "Airline,ID,Source,SourceID,Dest,DestID,Codeshare,Stops,Equip");
        ds.setCacheTimeout(0);
        ds.load();
        ds.printUsageString();
        System.out.println(System.currentTimeMillis() - a);
        
        System.out.println(DataCacher.DEFAULT_CACHE_DIR);

    }
}


