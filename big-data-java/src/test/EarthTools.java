package test;

// Data Source: http://xisbn.worldcat.org

import big.data.*;
/*
import big.data.field.FieldToXMLSpec;
import big.data.field.IDataField;
import big.data.util.XML;
*/

import java.util.Scanner;

public class EarthTools {
  
  public static void main(String[] args) {
	 
	 
	  
     DataSource ds = DataSource.connect("http://www.earthtools.org/sun/34.26/-85.185/14/10/99/1");
     ds.load();
     ds.printUsageString();
     
  }
  
}

