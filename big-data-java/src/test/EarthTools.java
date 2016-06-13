package test;

import java.util.Scanner;

import easy.data.*;

public class EarthTools {
  
  public static void main(String[] args) {
	 
	 
	  
     DataSource ds = DataSource.connect("http://www.earthtools.org/sun/34.26/-85.185/14/10/99/1");
     ds.load();
     ds.printUsageString();
     
  }
  
}

